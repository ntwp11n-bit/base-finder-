# Deep Base Finder (Fabric 1.21.11)

A client-side utility mod for Minecraft Java 1.21.11 with Fabric Loader. Press **O** to scan from the world's bottom Y-level to its top Y-level in a 24-block horizontal radius, then print nearby chest, trapped chest, barrel, and shulker-box coordinates in chat.

## Important limitations

- The mod only reads chunks and block states already available to your client. It does not reveal unloaded chunks or bypass server anti-x-ray / hidden-block protections.
- It reports storage block coordinates; it does not currently provide through-wall ESP, room-shape classification, or guaranteed base detection. Storage blocks can be legitimate and natural player builds can use other containers.
- A full-height scan is deliberately on-demand (press O), not continuous, to avoid constant performance cost.

## Build

1. Install JDK 21.
2. Open this folder in IntelliJ IDEA or run `gradle build` with Gradle 9.x / a compatible Gradle installation.
3. The mod JAR will be in `build/libs/`.
4. Install Fabric API for Minecraft 1.21.11, then place the built JAR in `.minecraft/mods/`.

Keybind: **Options → Controls → Key Binds → Deep Base Finder → Scan** (default O).
