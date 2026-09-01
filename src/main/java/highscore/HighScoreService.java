package highscore;

import game.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class HighScoreService {
    private static final int MAX_ENTRIES = 10;

    private static final Comparator<HighScoreEntry>
            ENTRY_COMPARATOR =
            Comparator.comparingLong(
                            HighScoreEntry::durationMilis
                    )
                    .thenComparing(
                            HighScoreEntry::achievedAt
                    );

    private final HighScoreRepository repository;

    private List<HighScoreEntry> entries;

    public HighScoreService() {
        this(
                new HighScoreRepository(
                        HighScoreRepository.getDefaultFilePath()
                )
        );
    }

    public HighScoreService(
            HighScoreRepository repository
    ) {
        this.repository = repository;

        this.entries = new ArrayList<>(
                repository.load()
        );

        this.entries.sort(ENTRY_COMPARATOR);

        if (this.entries.size() > MAX_ENTRIES) {
            this.entries =
                    new ArrayList<>(
                            this.entries.subList(
                                    0,
                                    MAX_ENTRIES
                            )
                    );
        }
    }

    public synchronized List<HighScoreEntry>
    getEntries() {
        return List.copyOf(entries);
    }

    public synchronized HighScoreSaveResult record(
            GameResult result
    ) {
        if (!isEligible(result)) {
            return HighScoreSaveResult.NOT_ELIGIBLE;
        }

        PlayerConfig winner = result.winner();

        String normalizedName =
                normalizeName(
                        winner.playerName()
                );

        HighScoreEntry existingEntry =
                findByNormalizedName(
                        normalizedName
                );

        if (existingEntry != null
                && existingEntry.durationMilis()
                <= result.durationMillis()) {

            return HighScoreSaveResult.NOT_IMPROVED;
        }

        HighScoreEntry candidate =
                new HighScoreEntry(
                        winner.playerName(),
                        result.durationMillis(),
                        result.mode(),
                        winner.character(),
                        Instant.now()
                );

        List<HighScoreEntry> updatedEntries =
                new ArrayList<>();

        for (HighScoreEntry entry : entries) {
            if (!normalizeName(entry.playerName())
                    .equals(normalizedName)) {
                updatedEntries.add(entry);
            }
        }

        updatedEntries.add(candidate);
        updatedEntries.sort(ENTRY_COMPARATOR);

        if (updatedEntries.size() > MAX_ENTRIES) {
            updatedEntries =
                    new ArrayList<>(
                            updatedEntries.subList(
                                    0,
                                    MAX_ENTRIES
                            )
                    );
        }

        boolean candidateIsIncluded =
                updatedEntries.stream()
                        .anyMatch(entry ->
                                normalizeName(
                                        entry.playerName()
                                ).equals(normalizedName)
                        );

        if (!candidateIsIncluded) {
            return HighScoreSaveResult.NOT_IN_TOP_TEN;
        }

        if (!repository.save(updatedEntries)) {
            return HighScoreSaveResult.SAVED_FAILD;
        }

        entries = updatedEntries;

        return HighScoreSaveResult.SAVED;
    }

    public static String formatDuration(
            long durationMillis
    ) {
        long minutes =
                durationMillis / 60_000L;

        long seconds =
                (durationMillis / 1_000L) % 60L;

        long milliseconds =
                durationMillis % 1_000L;

        return String.format(
                Locale.ROOT,
                "%02d:%02d.%03d",
                minutes,
                seconds,
                milliseconds
        );
    }

    private boolean isEligible(
            GameResult result
    ) {
        if (result.outcome()
                != GameOutcome.WIN) {
            return false;
        }

        if (!result.winnerCausedElimination()) {
            return false;
        }

        if (result.mode()
                == GameMode.SINGLE_PLAYER) {
            return false;
        }

        if (result.winner() == null) {
            return false;
        }

        return result.winner().controllerType()
                != ControllerType.AI;
    }

    private HighScoreEntry findByNormalizedName(
            String normalizedName
    ) {
        for (HighScoreEntry entry : entries) {
            if (normalizeName(entry.playerName())
                    .equals(normalizedName)) {
                return entry;
            }
        }

        return null;
    }

    private String normalizeName(String name) {
        return name.trim()
                .toLowerCase(Locale.ROOT);
    }
}
