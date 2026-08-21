# Don't Punch My TACZ

<p>
  <img alt="NeoForge" src="https://img.shields.io/badge/NeoForge-1.21.1-e04e14?style=flat-square">
  <img alt="Minecraft" src="https://img.shields.io/badge/MC-1.21.1-62b47a?style=flat-square">
  <img alt="Client" src="https://img.shields.io/badge/Client-only-4a90d9?style=flat-square">
  <img alt="License" src="https://img.shields.io/badge/license-MIT-6c757d?style=flat-square">
</p>

Punchy is great. Punchy holding a TACZ gun is two guns. Yeah.

The Punchy guy already told us the fix: press **F8**, blacklist the guns. That works. This jar just does the click so your players don't have to.

Not official. Not made by Punchy or TACZ. Just a tiny helper.

## Install

Drop it in `mods`.

Everyone who plays needs it. The server does not.

You need:

- Minecraft **1.21.1**
- **NeoForge**
- **Punchy**
- **TACZ**

## What it does

It adds TACZ to Punchy's blacklist. Same list as F8, plus `tacz:.*` so new guns get caught too.

Your other Punchy settings stay put.

## Build

GitHub builds it when you push. Or on your machine, Java 21:

```bash
./gradlew build
```

Jar lands in `build/libs/`.

## License

MIT. Go wild.
