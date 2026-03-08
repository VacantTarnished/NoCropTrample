# No Crop Trample (Fabric)

![Fabric 1.21+](https://img.shields.io/badge/Fabric-1.21%2B-blue?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

[![Demo Video](https://img.shields.io/badge/Demo-YouTube-red?style=flat-square)](https://youtu.be/ypxASh8R1tI)

**No Crop Trample** is a lightweight, server-side utility mod for Minecraft **Fabric 1.21+** that prevents crops (farmland) from being trampled by players or mobs.

## ✨ Features

- **Prevent Empty Farmland Trampling:** Stop empty farmland trampling for whichever entities you have it enabled for (will always trample empty farmlands when set to off).
- **Prevent Player Trampling:** Stop players from ruining crops by jumping on them.
- **Prevent Mob Trampling:** Stop passive or hostile mobs from destroying farmland.
- **Configurable:** Toggle protection for players and mobs independently via commands or config file.
- **Featherlight:** Minimal performance impact, uses efficient Mixins.
- **Server-Side Only:** Works without being installed on the client (though client installation is recommended for config menus).

## 📥 Installation

1. Download the latest `.jar` file from [Modrinth](https://modrinth.com/mod/nocroptrample) or [GitHub Releases](https://github.com/Murqin/NoCropTrample/releases).
2. Install [Fabric Loader](https://fabricmc.net/use/).
3. Install [Fabric API](https://modrinth.com/mod/fabric-api).
4. Drop `nocroptrample-x.y.z.jar` into your `mods` folder.

## ⚙️ Configuration & Commands

The mod works out of the box with trampling prevention **enabled** for everyone.

### Commands
All commands require Operator (OP) permission.

- `/nocroptrample status` - Check current protection status.
- `/nocroptrample empty <on|off>` - Enable/Disable empty farmland trampling.
- `/nocroptrample player <on|off>` - Enable/Disable player trampling on farmland.
- `/nocroptrample mob <on|off>` - Enable/Disable mob trampling on farmland.
- `/nocroptrample reload` - Reload configuration from file.

### Config File
Located at `config/nocroptrample.json`.
```json
{
  "preventEmptyTrampling": true,
  "preventPlayerTrampling": true,
  "preventMobTrampling": true
}
```

## 🛠️ Build from Source

Requirements: Java 21 (JDK 21)

```bash
git clone https://github.com/Murqin/NoCropTrample.git
cd NoCropTrample
./gradlew build
```

The output jar will be in `build/libs/`.

## 📝 License

Licensed under the MIT License.
