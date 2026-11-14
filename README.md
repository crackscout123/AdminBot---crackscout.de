# AdminBot – crackscout.de

Ein privater administrativer **TeamSpeak3-Server-Query-Bot** für den TeamSpeak-Server von [crackscout.de](https://crackscout.de). Der Bot übernimmt automatisierte Moderations- und Verwaltungsaufgaben (AFK-Verschiebung, Selbst-Kick, Whitelist-Verwaltung, Wortfilter mit Regex-Support, Wasser-Erinnerungen, Statistik- und Debug-Ausgaben sowie experimentelle „Troll"-Funktionen) und wird über private TeamSpeak-Chatnachrichten (`!command`) gesteuert.

> ⚠️ **Hinweis:** Dieses Projekt ist primär für den internen Gebrauch auf crackscout.de entwickelt. Aktueller Stand: `dev-1.2` (**Pre-Release**, Branch `dev`). Die letzte stabile Version ist `stable-1.0`. Manche Funktionen (insbesondere `!trollmove`) sind weiterhin als „in development" markiert.

***

## Inhaltsverzeichnis

- [Funktionsumfang](#funktionsumfang)
- [Changelog](#changelog)
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
| `!help`       | nein       | Sendet eine Übersicht aller Befehle per privater Nachricht. |
| `!kickme`     | nein       | Trägt den Aufrufer in die KickMe-Liste ein – der `KickCollector` kickt den Client beim nächsten Durchlauf und entfernt die ID danach korrekt aus der Liste. |
| `!stay`       | ja         | Fügt den Aufrufer der Whitelist hinzu bzw. entfernt ihn wieder (ausgenommen von AFK-Move, KickMe und Wasser-Erinnerung). |
| `!clear`      | ja         | Leert die `kickMeList` und die `whitelistedUsers`-Liste. |
| `!stats`      | nein       | Zeigt Debug-Status, Auth-Status und Größen der internen Listen. |
| `!trollmove`  | ja         | *(WIP)* Schiebt ein Ziel-Client wiederholt zwischen Channels (Dauer `s`/`m`/`h` oder fester Anzahl). Mit `!trollmove stop <client>` lässt sich ein laufender Trollmove manuell abbrechen. |
| `!troll`      | ja         | *(WIP / Platzhalter)* Sammelpunkt für Troll-Befehle. |

**Hintergrundprozesse (Collectors / Threads):**

- **AfkCollector** – verschiebt Clients automatisch in den AFK-Channel, wenn die Idle-Zeit den konfigurierten Schwellwert überschreitet. Channel-ID, Idle-Zeit und weiteres sind über `config.properties` konfigurierbar.
- **KickCollector** – kickt zyklisch alle Clients, die per `!kickme` in der Liste stehen, und entfernt die ID danach korrekt.
- **WasserCollector** – erinnert Clients, die sich länger als die konfigurierte Zeit in einem der ausgewählten Channels aufhalten, per Poke oder privater Nachricht ans Trinken. Whitelist-User und ignorierte Servergruppen werden übersprungen.
- **WordFilterEvents** – registriert TS3-Listener für Join-Events und überprüft Nicknamen beim Beitritt sowie alle 30 Sekunden per Polling-Thread (da die TS3-API kein Rename-Event liefert).
- **Connect** – registriert TS3-Listener für Join-, Move- und Leave-Events und versorgt den `WasserCollector` mit den Channel-Zeiten.
- **StackedEvents / Disconnect** – registrieren TS3-Listener für weitere Events; `Disconnect` bereinigt beim Leave-Event alle stale IDs aus `whitelistedUsers` und `kickMeList`.

**Wortfilter:**

- `WordFilterManager` prüft Nicknamen gegen die Datei `AdminBot/blacklisted_words.app`.
- Unterstützt zwei Modi:
  - **Normales Matching** (Zeileneinträge ohne Präfix): Word-Boundary-Matching via `(?i)\b<wort>\b` — verhindert False Positives durch Teilstring-Treffer.
  - **Regex-Matching** (Einträge mit `regex:`-Präfix): vollständige Regex-Ausdrücke, case-insensitive.
- Die Kick-Nachricht ist über `messages.properties` konfigurierbar.

***

## Changelog

### dev-1.2

- **WasserCollector** – neue Hydration-Erinnerung inkl. `Connect`-Events und eigener Config-Parameter (`wasser.*`).
- **`!trollmove stop <client>`** – laufende Trollmove-Threads lassen sich jetzt manuell abbrechen.
- **Logging-Konsistenz** – `AfkCollector` und `ConfigManager` nutzen jetzt `Debug` statt `System.out.println`.
- **Shutdown-Hook erweitert** – stoppt zusätzlich den `WasserCollector`.
- **`ConfigManager.loadBool()`** – neue Hilfsmethode für Boolean-Werte aus Properties-Dateien.

### dev-1.1

- **`!help`** – Befehlsübersicht zentral in `MessageManager.helpText()`.
- **Logging-Rework** – File-Logging wieder aktiv (Singleton mit täglicher Rotation), `Debug` schreibt auf Konsole **und** in die Log-Datei.
- **Graceful Shutdown** – Shutdown-Hook stoppt Collector-Threads und trennt die Query-Verbindung sauber.
- **Thread-Safety** – `validChannels` in `Trollmove` ist jetzt eine `CopyOnWriteArrayList`.

### stable-1.0

Erster stabiler Release – siehe [Release-Notes](https://github.com/crackscout123/AdminBot---crackscout.de/releases/tag/stable-1.0).

***

## Verwendete Technologien

- **Sprache:** Java (Eclipse JDT Projekt, JRE-Container)
- **Build/IDE:** Eclipse (`.project`, `.classpath`) sowie VS Code Java-Extension (`.vscode/launch.json`)
- **TeamSpeak-API:** [TheHolyWaffle/TeamSpeak-3-Java-API](https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API) – Version `1.3.1-with-dependencies`
- **Logging:** `Debug`-Klasse (Konsole + Datei) auf Basis von `java.util.logging` mit täglicher Log-Rotation

***

## Projektstruktur

```
.
├── AdminBot/
│   ├── auth.app                 # Auth-Konfiguration + erlaubte UIDs (zeilenweise)
│   ├── blacklisted_words.app    # Wortfilter-Blacklist (normales & Regex-Matching)
│   ├── config.properties        # Zentrale Konfiguration (AFK, Trollmove, Wasser, Bot-Settings)
│   └── messages.properties      # Alle konfigurierbaren Bot-Nachrichten
├── logs/                        # Tägliche Log-Dateien (YYYY-MM-DD.log), zur Laufzeit erzeugt
├── src/de/crackscout/
│   ├── AdminBot/Main.java       # Einstiegspunkt, Bootstrapping, Shutdown-Hook
│   ├── Collectors/              # AfkCollector, KickCollector, WasserCollector, StatCollector (Stub)
│   ├── Commands/                # Ping, KickMe, Stay, Clear, Stats, Help
│   │   └── TrollCommands/       # Troll, Trollmove (WIP)
│   ├── Events/                  # Connect, WordFilterEvents, Disconnect, StackedEvents
│   ├── Logging/                 # Logging (Singleton, tägliche Rotation)
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
- Die folgende JAR-Abhängigkeit lokal verfügbar (siehe `.classpath`), sofern du selbst baust:
  - `teamspeak3-api-1.3.1-with-dependencies.jar`
- Schreibrechte im Arbeitsverzeichnis (Ordner `AdminBot/` und `logs/` werden zur Laufzeit erstellt/beschrieben)

***

## Setup & Installation

### Variante A: Fertige JAR (empfohlen)

Die kompilierte JAR (`AdminBot_dev-1.2.jar`) gibt es im [Pre-Release `dev-1.2`](https://github.com/crackscout123/AdminBot---crackscout.de/releases). Für die letzte stabile Version siehe Release `stable-1.0`.

### Variante B: Selbst bauen

1. **Repository klonen** (Branch `dev` für den aktuellen Pre-Release-Stand)

   ```bash
   git clone -b dev https://github.com/crackscout123/AdminBot---crackscout.de.git
   cd AdminBot---crackscout.de
   ```

2. **Abhängigkeiten bereitstellen**

   Die im `.classpath` referenzierten Pfade sind Windows-spezifisch (`D:/Dev/...`). Vor dem Bauen sollten die JARs lokal abgelegt und ggf. die Pfade im `.classpath` angepasst werden, z. B.:

   ```xml
   <classpathentry kind="lib" path="libs/teamspeak3-api-1.3.1-with-dependencies.jar"/>
   ```

3. **Projekt importieren / bauen**

   - **Eclipse:** *File → Import → Existing Projects into Workspace*, anschließend Build via JDT (Output-Ordner `bin`).
   - **VS Code:** Projekt öffnen, *Run/Debug*-Konfiguration „Main" aus `.vscode/launch.json` benutzen.
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

# Wasser-Erinnerung
wasser.selected.channels=34,
wasser.ignored.groups=18,
wasser.sleep.ms=60000
wasser.max.idle.s=300
wasser.notify.seconds=3600
wasser.poke.enabled=true

# WordFilter
wordfilter.enabled=true
```

| Parameter | Bedeutung |
|-----------|-----------|
| `wasser.selected.channels` | Channel-IDs, in denen die Aufenthaltszeit gezählt wird (kommagetrennt). |
| `wasser.ignored.groups` | Servergruppen-IDs, die nie erinnert werden. |
| `wasser.sleep.ms` | Prüfintervall des `WasserCollector` in Millisekunden. |
| `wasser.max.idle.s` | Kurze Channel-Wechsel innerhalb dieser Zeitspanne setzen den Timer nicht zurück. Dient außerdem als Fallback, falls `wasser.notify.seconds` nicht gesetzt ist. |
| `wasser.notify.seconds` | Aufenthaltsdauer in Sekunden, nach der die Erinnerung verschickt wird. |
| `wasser.poke.enabled` | `true` = Poke, `false` = private Nachricht. |

> Alle Werte werden beim Start automatisch aus dieser Datei geladen. Anpassungen erfordern **keinen** Neucompile mehr.

### `AdminBot/messages.properties`

Konfigurierbare Bot-Nachrichten:

```properties
msg.afk.moved=Du wurdest in den AFK-Channel verschoben!
msg.wordfilter.kick=Blacklisted name! Please change it!
msg.stay.added=Du wurdest zur Whitelist hinzugefügt.
msg.stay.removed=Du wurdest von der Whitelist entfernt.
wasser.notify.msg=Stay hydrated!
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
regex:f+[u\*ü0]+c+k+
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
| `hello`                                  | `hello`                    | „Hello \<name\>!" |
| `!help`                                  | `!help`                    | Befehlsübersicht per privater Nachricht |
| `!kickme`                                | `!kickme`                  | Setzt den Aufrufer auf die Kick-Liste |
| `!stay`                                  | `!stay`                    | Whitelist toggeln (AFK-/Kick-/Wasser-immun) |
| `!clear`                                 | `!clear`                   | Kick- und Whitelist-Listen leeren |
| `!stats`                                 | `!stats`                   | Debug-/Auth-Status + Listengrößen |
| `!trollmove <id\|name> <anzahl\|dauer>`  | `!trollmove JohnDoe 10s`   | *(WIP)* Ziel-Client wiederholt verschieben |
| `!trollmove stop <id\|name>`             | `!trollmove stop JohnDoe`  | Laufenden Trollmove für den Ziel-Client abbrechen |

***

## Deployment / Hosting (Pterodactyl)

Das Projekt ist für ein **Pterodactyl-Panel** ausgelegt:

- `Main.java` gibt nach abgeschlossenem Startup `System.out.println("done.");` aus – dieses Token kann als „Done"-Indikator im Pterodactyl-Egg verwendet werden.
- Der Shutdown-Hook fängt `SIGTERM`/`SIGINT` ab (z. B. beim Stoppen über das Panel), stoppt die Collector-Threads und meldet den Bot sauber vom Server ab.

Empfohlene Startparameter im Egg:

```
java -jar adminbot.jar "{{TS_HOSTNAME}}" "{{TS_SERVER_ID}}" "{{TS_QUERY_USER}}:{{TS_QUERY_PASS}}"
```

***

## Logging

- `Debug.info(...)`, `Debug.warn(...)` und `Debug.err(...)` schreiben auf die Konsole **und** in die Log-Datei.
- Das File-Logging ist aktiv: `Logging` ist ein Singleton und legt täglich eine neue Datei `logs/YYYY-MM-DD.log` an (Append-Modus, ein Neustart überschreibt nichts).
- Beim Shutdown wird die Log-Datei sauber geflusht und geschlossen.
- `.gitignore` schließt `*.log` und `*.lck` aus der Versionierung aus.

***

## Sicherheits- und Wartungshinweise

- **Standard `ignoreAuth=true`** – in Produktion **unbedingt auf `false` setzen** und nur vertrauenswürdige TS3-UIDs in `auth.app` eintragen.
- **Query-Credentials im Klartext** über CLI-Argumente – auf gemeinsam genutzten Systemen sichtbar in der Prozessliste. Stattdessen ggf. via Pterodactyl-Egg-Variablen befüllen.
- **`!trollmove` ist WIP** – `!trollmove stop` ist zwar vorhanden, das Feature gilt aber weiterhin als experimentell.
- **`StatCollector` ist noch ein Stub** – `!stats` zeigt nur interne Listen-Größen, keine echten Server-Statistiken.
- **`bot.nickname`** ist im Entwicklungsstand auf `AdminBot DEV` gesetzt – vor produktivem Einsatz auf `AdminBot` zurücksetzen.
- **`auth.app` und `blacklisted_words.app`** sind in `.gitignore` aufgeführt und sollen **nicht** im Repository landen, sondern lokal/produktiv gepflegt werden.
- **Externe JARs nicht versioniert** – die TeamSpeak-API-Bibliothek muss separat bereitgestellt werden; der absolute Windows-Pfad im `.classpath` ist vor dem Build anzupassen (nicht nötig, wenn die fertige JAR aus dem Release genutzt wird).

***

## Lizenz

Dieses Projekt steht unter der **GNU General Public License v3.0** – siehe [`LICENSE`](LICENSE).

***

## Autor

**Joel Rzepka** – [rzepka.me](https://rzepka.me)
