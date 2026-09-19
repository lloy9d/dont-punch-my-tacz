<p>
  <img src="icon.png" alt="Don't Punch My TACZ" width="128" height="128">
</p>

# Don't Punch My TACZ

<p>
  <a href="https://modrinth.com/mod/dont-punch-my-tacz">
    <img alt="Modrinth" src="https://img.shields.io/badge/Modrinth-download-1bd96a?style=flat-square">
  </a>
  <img alt="Modrinth downloads" src="https://img.shields.io/modrinth/dt/dont-punch-my-tacz?label=downloads&color=8f8264&style=flat-square">
  <img alt="Minecraft versions" src="https://img.shields.io/badge/Minecraft-1.20.1%20%E2%80%A2%201.21.1%20%E2%80%A2%201.21.11%20%E2%80%A2%2026.x-0ca2a8?style=flat-square">
  <img alt="Client only" src="https://img.shields.io/badge/Client-only-4a90d9?style=flat-square">
  <img alt="License" src="https://img.shields.io/badge/license-GPL%20v3.0-6c757d?style=flat-square">
</p>

![Title Photo](https://i.imgur.com/3ayCSpf.png)
![Divider](https://i.imgur.com/jZOMuHG.png)

<p align="center">
  <a href="https://modrinth.com/mod/dont-punch-my-tacz">
    <img alt="Modrinth" src="https://i.imgur.com/TK2RwfB.png">
  </a>
  <a href="https://www.curseforge.com/minecraft/mc-mods/dont-punch-my-tacz">
    <img alt="CurseForge" src="https://i.imgur.com/ORkc3F0.png">
  </a>
</p>

## What Is This?

Punchy animates the item in your hand. Some mods draw their own first-person model for their items too, and when both happen at once the item **glitches**, doubles or fights the animation

Despite the name this is not just a TACZ mod. TACZ is just what it was first made for. It covers every mod in the list below, **guns or not**

Punchy already has a fix: press F8 and blacklist the item manually. This mod just does that step for you **automatically**, so players don't have to set it up themselves

## Which File Do I Download?

| File | Mod Loader | Minecraft |
|---|---|---|
| `dont-punch-my-tacz-vX.Y-1.20.1-fabric.jar` | **Fabric** | 1.20.1 |
| `dont-punch-my-tacz-vX.Y-1.21.x-fabric.jar` | **Fabric** | 1.21.1, 1.21.11 |
| `dont-punch-my-tacz-vX.Y-26.x-fabric.jar` | **Fabric** | 26.1, 26.1.1, 26.1.2, 26.2, 26.3 |
| `dont-punch-my-tacz-vX.Y-1.20.1-forge.jar` | **Forge** | 1.20.1 |
| `dont-punch-my-tacz-vX.Y-1.21.1-neo.jar` | **NeoForge** | 1.21.1 |

On Modrinth, pick the file that matches your mod loader and Minecraft version. On GitHub, all files are on the same [release](https://github.com/lloy9d/dont-punch-my-tacz/releases)

## How to Install

1. Make sure you have the matching **Punchy** build for your loader and Minecraft version
2. Download the file from the table above and put it in your `mods` folder
3. That's it. **Every player** needs this mod, the server does **not**

![Compatibility Photo](https://i.imgur.com/gTAKX5s.png)

## Which Items Are Covered?

All of these are **optional**, the mod only touches items from mods you actually have installed:

- **TACZ** → all guns
- **SuperbWarfare** → all guns, grenades, and a few special items
- **LRTactical / LesRaisins** → melee weapons, throwables, consumables
- **Create** → potato cannon, extendo grip, handheld worldshaper
- **Create Simulated Aeronautics** → plunger launcher
- **Power Grid** → portable saw, portable drill, electrozapper
- **Create: Gunsmithing** → flintlock, revolver, shotgun, nailgun, gatling, blazegun, launcher, pneumatic hammer, frag grenade
- **Create Diesel Generators** → chemical sprayer, chemical sprayer lighter
- **Exposure** → camera
- **Minecraft** → brush, so its sweeping animation shows properly again

## And Beyond the Item List

These are not about a specific item, they fix Punchy itself. All of them are **optional** too and only ever touch the feature they describe

- **Quick Charge crossbows** → with Quick Charge 1, 2 or 3 the crossbow charges faster but Punchy's charge animation kept playing at its old speed, so it ran behind the shot and snapped at the end. now the animation speeds up with the enchantment (×1.25 / ×1.67 / ×2.5) and lines up with the release. without Quick Charge nothing changes
- **Invisibility** → while the invisibility effect is on, your first-person arm is no longer drawn
- **Chestplate arm in third person (1.20.1)** → when Punchy draws your armor arms in first person, the chestplate's arm piece could detach and float in front of the camera in third person. now it goes back to its normal pose, so you can leave Punchy's armor arms on. fixed on Forge and Fabric
- **Trimmed chestplate in first person (26.3)** → the sleeve of a trimmed chestplate showed up black, now it renders the trim again with the correct pattern and colours. no extra mods needed, and you can leave Punchy's armor arms on. on 26.1 and 26.2 it never happened, so nothing changes there

## Carry On

Carrying a chest, barrel or animal with **Carry On** has one extra fix built in. While you carry something, Punchy's first-person hand is handed back to vanilla for that frame, so you see the object you are carrying instead of a bare hand floating next to it

Carry On is completely **optional**. If you don't have it installed, nothing changes at all

## What Does It Actually Change?

It adds the items above to Punchy's **F8 first-person blacklist**, so you never have to do that part by hand. Everything else only kicks in for the situations listed above and is skipped completely otherwise

![Divider](https://i.imgur.com/jZOMuHG.png)
