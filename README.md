# BombIt

BombIt ist ein mit Processing und Java entwickelter Bomberman-Klon. Die Spieler bewegen sich durch eine kachelbasierte Arena, platzieren Bomben, zerstören Wände und sammeln Power-ups. Ziel ist es, den Gegner zu besiegen, ohne von einer Explosion getroffen zu werden.

## Funktionen

- Trainingsmodus für einen Spieler
- Lokaler Mehrspielermodus für zwei Spieler
- Spiel gegen einen computergesteuerten Gegner
- Zufällig verteilte zerstörbare Wände und Power-ups
- Bomben, Explosionen und Kettenreaktionen
- Drei Power-ups für Geschwindigkeit, Explosionsreichweite und zusätzliche Bomben
- Charakterauswahl
- Explosionssound
- Lokale Highscore-Liste mit der besten Zeit pro Spieler
- Skalierte Fullscreen-Darstellung mit Processing `P2D`

## Spielmodi

| Modus | Beschreibung |
| --- | --- |
| Single Player | Trainingsmodus ohne Gegner und Highscore-Wertung |
| Local Multiplayer | Zwei menschliche Spieler treten an einer Tastatur gegeneinander an |
| VS AI | Ein menschlicher Spieler tritt gegen die KI an |

## Steuerung

| Aktion | Spieler 1 | Spieler 2 |
| --- | --- | --- |
| Bewegung | `W`, `A`, `S`, `D` | Pfeiltasten |
| Bombe platzieren | `Leertaste` | `Enter` |

Die Menüs können mit der Maus oder der Tastatur bedient werden. `A` und `D` beziehungsweise die Pfeiltasten wechseln die Auswahl. Mit `Leertaste` oder `Enter` wird bestätigt. Über `H` kann die Highscore-Liste geöffnet werden.

## Voraussetzungen

- Java 21
- Maven
- OpenGL-Unterstützung
- Eine funktionierende Audioausgabe

Für NixOS ist eine Entwicklungsumgebung in `flake.nix` enthalten. Sie stellt Java, Maven sowie die benötigten Grafik- und Audio-Bibliotheken bereit.

```bash
nix develop "path:."
```

Auf anderen Systemen kann das Projekt als Maven-Projekt in IntelliJ IDEA importiert werden.

## Starten

1. Das Projekt in IntelliJ IDEA öffnen.
2. Maven-Abhängigkeiten laden lassen.
3. `src/main/java/BombIt.java` öffnen.
4. Die Methode `BombIt.main()` ausführen.

Unter NixOS sollte IntelliJ aus der zuvor geöffneten Nix-Entwicklungsumgebung gestartet werden, damit die nativen OpenGL- und Audio-Bibliotheken verfügbar sind.

## Tests

```bash
mvn test
```

Unter NixOS können die Tests direkt innerhalb der Entwicklungsumgebung ausgeführt werden:

```bash
nix develop "path:." --command mvn test
```

## Highscores

Highscores werden lokal als JSON-Datei gespeichert:

```text
~/.bombit/highscores.json
```

Gewertet werden menschliche Siege im lokalen Mehrspielermodus und im Modus gegen die KI. Trainingsrunden, Siege der KI, Unentschieden und Selbstmorde werden nicht gespeichert.

## Dokumentation

Die Projektdokumentation mit Vorgehen, Schwierigkeiten, Lösungswegen und Reflexion befindet sich in [`reflexion.adoc`](reflexion.adoc).
