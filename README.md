# AdminBot – crackscout.de

Ein privater administrativer **TeamSpeak3-Server-Query-Bot** für den TeamSpeak-Server von [crackscout.de](https://crackscout.de). Der Bot übernimmt automatisierte Moderations- und Verwaltungsaufgaben (AFK-Verschiebung, Selbst-Kick, Whitelist-Verwaltung, Wortfilter mit Regex-Support, Statistik- und Debug-Ausgaben sowie experimentelle „Troll"-Funktionen) und wird über private TeamSpeak-Chatnachrichten (`!command`) gesteuert.

> ⚠️ **Hinweis:** Dieses Projekt ist primär für den internen Gebrauch auf crackscout.de entwickelt. Stand: `1.0`. Manche Funktionen (insbesondere `!trollmove`) sind weiterhin als „in development" markiert.

***

## Inhaltsverzeichnis

- [Funktionsumfang](#funktionsumfang)
- [Verwendete Technologien](#verwendete-technologien)
- [Projektstruktur](#projektstruktur)
- [Voraussetzungen](#voraussetzungen)
- [Setup & Installation](#setup--installation)
- [Konfiguration](#konfiguration)
- [Starten des Bots](#starten-des-bots)
- [Nutzung & Befehle](#nutzung--befehle)
- [Deployment / Hosting (Pterodactyl)](#deployment--hosting-pterodactyl)
- [Logging](#logging)
- [Sicherheits- und Wartungshinweise](#sicherheits--und-wartungshinweise)
- [Lizenz](#lizenz)
- [Autor](#autor)

***

## Funktionsumfang

**Befehle (per privater Nachricht an den Bot):**

| Befehl        | Auth nötig | Beschreibung |
|---------------|:----------:|--------------|
| `!ping`       | nein       | Antwortet mit `pong` (Verbindungstest). |
| `hello`       | nein       | Begrüßt den Absender persönlich. |
| `!kickme`     | nein       | Trägt den Aufrufer in die KickMe-Liste ein – der `KickCollector` kickt den Client beim nächsten Durchlauf und entfernt die ID danach korrekt aus der Liste. |
| `!stay`       | ja         | Fügt den Aufrufer der Whitelist hinzu bzw. entfernt ihn wieder (ausgenommen von AFK-Move und KickMe). |
| `!clear`      | ja         | Leert die `kickMeList` und die `whitelistedUsers`-Liste. |
| `!stats`      | nein       | Zeigt Debug-Status, Auth-Status und Größen der internen Listen. |
| `!trollmove`  | ja         | *(WIP)* Schiebt ein Ziel-Client wiederholt zwischen Channels (Dauer `s`/`m`/`h` oder fester Anzahl). Laufende Threads sind jetzt abbrechbar. |
| `!troll`      | ja         | *(WIP / Platzhalter)* Sammelpunkt für Troll-Befehle. |

**Hintergrundprozesse (Collectors / Threads):**

- **AfkCollector** – verschiebt Clients automatisch in den AFK-Channel, wenn die Idle-Zeit den konfigurierten Schwellwert überschreitet. Channel-ID, Idle-Zeit und weiteres sind über `config.properties` konfigurierbar.
- **KickCollector** – kickt zyklisch alle Clients, die per `!kickme` in der Liste stehen, und entfernt die ID danach korrekt.
- **WordFilterEvents** – registriert TS3-Listener für Join-Events und überprüft Nicknamen beim Beitritt sowie alle 30 Sekunden per Polling-Thread (da die TS3-API kein Rename-Event liefert).
- **StackedEvents / Disconnect** – registrieren TS3-Listener für weitere Events; `Disconnect` bereinigt beim Leave-Event alle stale IDs aus `whitelistedUsers` und `kickMeList`.

**Wortfilter:**

- `WordFilterManager` prüft Nicknamen gegen die Datei `AdminBot/blacklisted_words.app`.
- Unterstützt zwei Modi:
  - **Normales Matching** (Zeileneinträge ohne Präfix): Word-Boundary-Matching via `(?i)\\b<wort>\\b` — verhindert False Positives durch Teilstring-Treffer.
  - **Regex-Matching** (Einträge mit `regex:`-Präfix): vollständige Regex-Ausdrücke, case-insensitive.
- Die Kick-Nachricht ist über `messages.properties` konfigurierbar.

***

## Verwendete Technologien

- **Sprache:** Java (Eclipse JDT Projekt, JRE-Container)
- **Build/IDE:** Eclipse (`.project`, `.classpath`) sowie VS Code Java-Extension (`.vscode/launch.json`)
- **TeamSpeak-API:** [TheHolyWaffle/TeamSpeak-3-Java-API](https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API) – Version `1.3.1-with-dependencies`
- **Logging:** `Debug`-Klasse für Konsolen-Ausgaben; `java.util.logging`-basiertes File-Logging ist aktuell deprecated und wird in einem zukünftigen Release überarbeitet

***

## Projektstruktur

```
.
├── AdminBot/
│   ├── auth.app                 # Auth-Konfiguration + erlaubte UIDs (zeilenweise)
│   ├── blacklisted_words.app    # Wortfilter-Blacklist (normales & Regex-Matching)
│   ├── config.properties        # Zentrale Konfiguration (AFK, Trollmove, Bot-Settings)
│   └── messages.properties      # Alle konfigurierbaren Bot-Nachrichten
├── src/de/crackscout/
│   ├── AdminBot/Main.java       # Einstiegspunkt, Bootstrapping
│   ├── Collectors/              # AfkCollector, KickCollector, StatCollector
│   ├── Commands/                # Ping, KickMe, Stay, Clear, Stats
│   │   └── TrollCommands/       # Troll, Trollmove (WIP)
│   ├── Events/                  # WordFilterEvents, Disconnect, StackedEvents
│   ├── Logging/                 # Logging (deprecated), MyFormatter, Utils
│   └── Managers/                # AuthManager, ConfigManager, MessageManager,
│                                # SettingsManager, WordFilterManager, TimeHandler,
│                                # Debug, Utils
├── .classpath / .project        # Eclipse-Konfiguration
├── .vscode/launch.json          # VS Code Launch-Konfiguration (mainClass: de.crackscout.AdminBot.Main)
├── LICENSE                      # GNU GPL v3
└── README.md
```

***

## Voraussetzungen

- **Java 11+** (JDK; im `.classpath` ist ein modul-fähiger JRE-Container konfiguriert)
- **TeamSpeak-3-Server** mit aktivierter ServerQuery-Schnittstelle und einem Query-Account
- Die folgende JAR-Abhängigkeit lokal verfügbar (siehe `.classpath`):
  - `teamspeak3-api-1.3.1-with-dependencies.jar`
- Schreibrechte im Arbeitsverzeichnis (Ordner `AdminBot/` und `AdminBot/logs/` werden zur Laufzeit erstellt/beschrieben)

***

## Setup & Installation

1. **Repository klonen**

   ```bash
   git clone https://github.com/crackscout123/AdminBot---crackscout.de.git
   cd AdminBot---crackscout.de
   ```

2. **Abhängigkeiten bereitstellen**

   Die im `.classpath` referenzierten Pfade sind Windows-spezifisch (`D:/Dev/...`). Vor dem Bauen sollten die JARs lokal abgelegt und ggf. die Pfade im `.classpath` angepasst werden, z. B.:

   ```xml
   <classpathentry kind="lib" path="libs/teamspeak3-api-1.3.1-with-dependencies.jar"/>
   ```

3. **Projekt importieren / bauen**

   - **Eclipse:** *File → Import → Existing Projects into Workspace*, anschließend Build via JDT (Output-Ordner `bin`).
   - **VS Code:** Projekt öffnen, *Run/Debug*-Konfiguration „Main\" aus `.vscode/launch.json` benutzen.
   - **Kommandozeile:** Aus den kompilierten Klassen + JAR-Abhängigkeiten ein ausführbares JAR (`adminbot.jar`) bauen.

***

## Konfiguration

Beim ersten Start legt der Bot das Verzeichnis `AdminBot/` und alle benötigten Konfigurationsdateien mit Standardwerten an (`ConfigManager`, `AuthManager`, `WordFilterManager`).

### `AdminBot/config.properties`

Zentrale Konfigurationsdatei für alle Bot-Features:

```properties
# Bot Settings
bot.nickname=AdminBot
bot.debug=false

# AFK Collector
afk.channel.id=24
afk.silent.group.id=18
afk.music.channel.id=22
afk.sleep.ms=60000
afk.max.idle.ms=600000

# Troll Move – ignorierte Channel-, Client- und Gruppen-IDs (kommagetrennt)
trollmove.ignored.channels=1,2,3
trollmove.ignored.clients=4,5,6
trollmove.ignored.groups=7,8,9

# WordFilter
wordfilter.enabled=true
```

> Alle Werte werden beim Start automatisch aus dieser Datei geladen. Anpassungen erfordern **keinen** Neucompile mehr.

### `AdminBot/messages.properties`

Konfigurierbare Bot-Nachrichten:

```properties
msg.afk.moved=Du wurdest in den AFK-Channel verschoben!
msg.wordfilter.kick=Blacklisted name! Please change it!
msg.stay.added=Du wurdest zur Whitelist hinzugefügt.
msg.stay.removed=Du wurdest von der Whitelist entfernt.
```

### `AdminBot/auth.app`

```
<TS3-UniqueIdentifier-1>=
<TS3-UniqueIdentifier-2>=
```

- `ignoreAuth=true` – jeder darf authentisierte Befehle (`!stay`, `!clear`, `!trollmove`) ausführen.
- `ignoreAuth=false` – nur die eingetragenen TS3-UIDs sind berechtigt.

### `AdminBot/blacklisted_words.app`

```
# Normales Wort-Matching (Word-Boundary, case-insensitive)
badword

# Regex-Matching (case-insensitive, volles Regex)
regex:f+[u\\*ü0]+c+k+
```

- Zeilen mit `#` werden als Kommentare ignoriert.
- Einträge **ohne** Präfix: Word-Boundary-Matching — `"ass"` trifft nicht `"class"`.
- Einträge **mit `regex:`-Präfix**: vollständige Regex-Ausdrücke.

***

## Starten des Bots

Der Bot erwartet seine Verbindungsdaten als **Kommandozeilen-Argumente**:

```
java -jar adminbot.jar "<hostname>" "<serverID>" "<query-user>:<query-password>" [-debug]
```

**Beispiel:**

```bash
java -jar adminbot.jar "ts.crackscout.de" "1" "serveradmin:GeheimesPasswort" -debug
```

Argumente:

1. **hostname** – ServerQuery-Host (z. B. `ts.example.com`)
2. **serverID** – virtuelle Server-ID (`selectVirtualServerById`)
3. **user:pass** – ServerQuery-Login (durch `:` getrennt)
4. **(optional) `-debug`** – aktiviert das Communications-Logging der TS3-API

Nach erfolgreichem Start verbindet sich der Bot, setzt den Nicknamen aus `config.properties` und gibt `done.` auf STDOUT aus – dieses Signal wird vom Pterodactyl-Install-Skript ausgewertet.

***

## Nutzung & Befehle

Alle Befehle werden **per privater TeamSpeak-Nachricht** an den Bot gesendet. Befehle, die mit *Auth* markiert sind, prüfen die UID des Absenders gegen `auth.app` (sofern `ignoreAuth=false`).

| Befehl                                   | Beispiel                   | Wirkung |
|------------------------------------------|----------------------------|---------|
| `!ping`                                  | `!ping`                    | `pong` |
| `hello`                                  | `hello`                    | „Hello \\<name\\>!\" |
| `!kickme`                                | `!kickme`                  | Setzt den Aufrufer auf die Kick-Liste |
| `!stay`                                  | `!stay`                    | Whitelist toggeln (AFK-/Kick-immun) |
| `!clear`                                 | `!clear`                   | Kick- und Whitelist-Listen leeren |
| `!stats`                                 | `!stats`                   | Debug-/Auth-Status + Listengrößen |
| `!trollmove <id\\|name> <anzahl\\|dauer>`  | `!trollmove JohnDoe 10s`   | *(WIP)* Ziel-Client wiederholt verschieben |

***

## Deployment / Hosting (Pterodactyl)

Das Projekt ist für ein **Pterodactyl-Panel** ausgelegt:

- `Main.java` gibt nach abgeschlossenem Startup `System.out.println("done.");` aus – dieses Token kann als „Done\"-Indikator im Pterodactyl-Egg verwendet werden.

Empfohlene Startparameter im Egg:

```
java -jar adminbot.jar "{{TS_HOSTNAME}}" "{{TS_SERVER_ID}}" "{{TS_QUERY_USER}}:{{TS_QUERY_PASS}}"
```

***

## Logging

- Zusätzliche Konsolen-Ausgaben über die `Debug`-Klasse (`Debug.info(...)`, `Debug.err(...)`).
- Das `java.util.logging`-basierte File-Logging (`AdminBot/logs/<datum>.log`) ist aktuell **deprecated** und wird in einem zukünftigen Release durch ein vollwertiges Logging-System ersetzt.
- `.gitignore` schließt `*.log` und `*.lck` aus der Versionierung aus.

***

## Sicherheits- und Wartungshinweise

- **Standard `ignoreAuth=true`** – in Produktion **unbedingt auf `false` setzen** und nur vertrauenswürdige TS3-UIDs in `auth.app` eintragen.
- **Query-Credentials im Klartext** über CLI-Argumente – auf gemeinsam genutzten Systemen sichtbar in der Prozessliste. Stattdessen ggf. via Pterodactyl-Egg-Variablen befüllen.
- **`!trollmove` ist WIP** – `!trollmove stop` zum manuellen Abbruch laufender Threads ist noch nicht implementiert.
- **`auth.app` und `blacklisted_words.app`** sind in `.gitignore` aufgeführt und sollen **nicht** im Repository landen, sondern lokal/produktiv gepflegt werden.
- **Externe JARs nicht versioniert** – die TeamSpeak-API-Bibliothek muss separat bereitgestellt werden; der absolute Windows-Pfad im `.classpath` ist vor dem Build anzupassen.

***

## Lizenz

Dieses Projekt steht unter der **GNU General Public License v3.0** – siehe [`LICENSE`](LICENSE).

***

## Autor

**Joel Rzepka** – [rzepka.me](https://rzepka.me)
