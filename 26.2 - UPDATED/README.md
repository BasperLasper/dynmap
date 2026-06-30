# Dynmap Paper 26.2 Compatibility Build

By Basper

Support server: https://discord.gg/DMuGVPfvr

Experimental Dynmap `3.9-SNAPSHOT` build for Paper `26.2`.

This is not an official Dynmap release. It is a compatibility fork/build that lets Dynmap start, serve the web map, and render tiles on Paper 26.2 while official Spigot/Paper 26.2 mappings are not available.

## Status

Tested on:

```text
Paper 26.2 build 31 alpha
Dynmap 3.9-SNAPSHOT-Dev
Java 21
```

Confirmed working:

```text
Dynmap enables successfully
Web server starts successfully
/dynmap fullrender world renders tiles
/dynmap stats shows processed/rendered/updated increasing
```

## Download

The ready-to-use plugin jar is in this folder:

```text
Dynmap-3.9-SNAPSHOT-spigot.jar
```

Optional checksum:

```text
Dynmap-3.9-SNAPSHOT-spigot.jar.MD5
```

## Install

1. Stop the server.
2. Remove any old Dynmap jar from `plugins/`.
3. Copy `Dynmap-3.9-SNAPSHOT-spigot.jar` into `plugins/`.
4. Start the server once so Dynmap can create or update its config.
5. If needed, edit:

```text
plugins/dynmap/configuration.txt
```

Set a free webserver port, for example:

```yaml
webserver-port: 3913
```

6. Restart the server.

Open the map at:

```text
http://your-server-ip:3913
```

## Rendering

Start with the overworld:

```text
/dynmap fullrender world
```

Check progress:

```text
/dynmap stats
```

Healthy output should show numbers increasing:

```text
processed=N, rendered=N, updated=N
Active render jobs: world
```

For large worlds, fullrender can take a long time. Let one world finish before starting the next:

```text
/dynmap fullrender world_nether
/dynmap fullrender world_the_end
```

## Important Notes

Spigot BuildTools currently reports that `1.26.2` does not exist, so this build does not use official `v1_26_R1` CraftBukkit/NMS classes.

Instead, this fork uses a Bukkit API fallback helper:

```text
org.dynmap.bukkit.helper.v26_2
```

The fallback includes:

```text
Paper-safe main-thread chunk loading
Modern chunk snapshot block lookup
Synthetic block states for Dynmap model compatibility
```

This means rendering works, but some block models may be approximate until proper Paper/Spigot 26.2 mappings are available.

## Port Conflicts

If Dynmap logs something like:

```text
Failed to start WebServer on address 0.0.0.0:3061
```

another plugin is already using that port. In testing, `ZorvynCore` Votifier was using `3061`.

Fix it by changing either Dynmap's webserver port or the other plugin's port.

Recommended Dynmap config:

```yaml
webserver-port: 3913
```

## Build From Source

The Gradle module is:

```text
:bukkit-helper-26-2
```

Its project directory is:

```text
26.2-chatgpt/bukkit-helper-26-2
```

Build the Spigot/Paper jar with Java 21:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11'
.\gradlew.bat :spigot:shadowJar
```

The built jar is produced at:

```text
target/Dynmap-3.9-SNAPSHOT-spigot.jar
```

## Fork/Release Label

Suggested release wording:

```text
Experimental Dynmap 3.9-SNAPSHOT build for Paper 26.2.
Tested on Paper 26.2 build 31 alpha. Webserver and fullrender confirmed working.
This is a Bukkit API fallback compatibility build, not an official upstream Dynmap release.
```
