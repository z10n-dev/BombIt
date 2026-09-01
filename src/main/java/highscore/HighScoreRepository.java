package highscore;

import game.GameMode;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import player.Character;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class HighScoreRepository {

  private static final int SCHEMA_VERSION = 1;
  private final Path filePath;

  public HighScoreRepository(Path filePath) {
    this.filePath = filePath;
  }

  public static Path getDefaultFilePath() {
    return Path.of(System.getProperty("user.home"), ".bombit", "highscores.json");
  }

  public List<HighScoreEntry> load() {
    if (!Files.exists(filePath)) {
      return List.of();
    }

    try {
      String jsonText = Files.readString(filePath, StandardCharsets.UTF_8);

      JSONObject root = JSONObject.parse(jsonText);

      if (root == null) {
        throw new IllegalArgumentException("Invalid JSON format");
      }

      int schemaVersion = root.getInt("schemaVersion", -1);

      if (schemaVersion != SCHEMA_VERSION) {
        throw new IllegalArgumentException("Unsupported schema version: " + schemaVersion);
      }

      JSONArray jsonEntries = root.getJSONArray("entries");

      List<HighScoreEntry> entries = new ArrayList<>();

      for (int index = 0; index < jsonEntries.size(); index++) {
        JSONObject jsonEntry = jsonEntries.getJSONObject(index);
        entries.add(fromJson(jsonEntry));
      }
      return List.copyOf(entries);
    } catch (Exception exception) {
      System.err.println("Failed to load high scores: " + exception.getMessage());

      preserveCorruptedFile();

      return List.of();
    }
  }

  public boolean save(List<HighScoreEntry> entries) {
    Path temporaryPath = filePath.resolveSibling(filePath.getFileName() + ".tmp");

    try {
      Path parent = filePath.getParent();

      if (parent != null) {
        Files.createDirectories(parent);
      }

      JSONObject root = new JSONObject();

      root.setInt("schemaVersion", SCHEMA_VERSION);

      JSONArray jsonEntries = new JSONArray();

      for (HighScoreEntry entry : entries) {
        jsonEntries.append(toJson(entry));
      }

      root.setJSONArray("entries", jsonEntries);

      Files.writeString(
          temporaryPath,
          root.format(2),
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE);

      moveTemporaryFile(temporaryPath);

      return true;
    } catch (Exception exception) {
      System.err.println("Failed to save high scores: " + exception.getMessage());

      try {
        Files.deleteIfExists(temporaryPath);
      } catch (IOException ignored) {
      }

      return false;
    }
  }

  private JSONObject toJson(HighScoreEntry entry) {
    JSONObject jsonEntry = new JSONObject();
    jsonEntry.setString("playerName", entry.playerName());
    jsonEntry.setLong("durationMillis", entry.durationMilis());
    jsonEntry.setString("mode", entry.mode().name());
    jsonEntry.setString("character", entry.character().name());
    jsonEntry.setString("achievedAt", entry.achievedAt().toString());

    return jsonEntry;
  }

  private HighScoreEntry fromJson(JSONObject jsonEntry) {
    return new HighScoreEntry(
        jsonEntry.getString("playerName"),
        jsonEntry.getLong("durationMillis"),
        GameMode.valueOf(jsonEntry.getString("mode")),
        Character.valueOf(jsonEntry.getString("character")),
        Instant.parse(jsonEntry.getString("achievedAt")));
  }

  private void moveTemporaryFile(Path temporaryPath) throws IOException {
    try {
      Files.move(
          temporaryPath,
          filePath,
          StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING);
    } catch (AtomicMoveNotSupportedException exception) {
      // Fallback to non-atomic move if atomic move is not supported
      Files.move(temporaryPath, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  private void preserveCorruptedFile() {
    if (!Files.exists(filePath)) {
      return;
    }

    String backupName = filePath.getFileName() + ".corrupted." + System.currentTimeMillis();

    Path backupPath = filePath.resolveSibling(backupName);

    try {
      Files.move(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException exception) {
      System.err.println("Failed to preserve corrupted high score file: " + exception.getMessage());
    }
  }
}
