# FarmersDelightRePaper - Farmers Delight Paper Edition

![License](https://img.shields.io/badge/License-GPLv3-blue)
![Status](https://img.shields.io/badge/Status-Alpha-red)

[中文](README_ZH.md)

A recreation project of [Farmers Delight mod](https://modrinth.com/mod/farmers-delight) based on PaperAPI and client resource packs, fully implementing original mod features through server-side technology.

## ✨ Core Features

- **Pure Server-side Implementation**: Only requires client resource pack + Paper server plugin
- **Protocol Layer Magic**: Uses data packets and chunk operations to trick clients into rendering custom blocks
- **Lightweight & Dependency-free**: Natively uses chunk storage without external databases
- **Perfect Compatibility**: Block behavior layer developed on PaperAPI without NMS dependencies
- **Modular Architecture**: Easy to expand with new crops and cooking systems

## ⚙️ Installation Guide

1. Place the plugin in `plugins/` folder
2. Config the resource pack correctly
3. Restart the server
4. Ensure resource packs are enabled in `server.properties`:
   ```properties
   require-resource-pack=true
   resource-pack-sha1=your_resource_pack_SHA1_value
   ```

## 🛠️ Development Architecture

### Core Modules
| Module                  | Functionality                        |
|-------------------------|--------------------------------------|
| **BlockPacketHandler**  | Protocol-layer custom block disguise |
| **CustomBlockBehavior** | Crop growth stage control            |
| **CustomBlockStorage**  | Custom block storage                 |

## 📜 License Notice

This project adopts **GNU General Public License v3.0**:
- Allows free use and modification
- Requires derivative works to be open source
- Prohibits distribution as part of closed-source commercial software

Full license at [LICENSE](LICENSE).

## 🚧 Development Progress

**Current Features**:
- [x] Basic crop system (Onion)
- [x] Block and item management
- [x] Corresponding client resource pack
- [x] Partial recipes

**TODO List**:
- [ ] Add more items and recipes from original mod
- [ ] Improve crop system
- [ ] Develop knife & cooking tool interaction logic
- [ ] ~~Find bugs~~ Write unit test cases

> ⚠️ Warning: This project is still in development, do not use in production!

## 🤝 Contributing

Welcome to contribute through:
1. Submitting Pull Requests (pass `./gradlew check` first)
2. Discussing technical solutions in Issues
3. Improving documentation

Recommended development environment:
- JDK 21+
- Paper 1.21.4+
- IntelliJ IDEA

## 💬 Communication

Feel free to submit Issues or email me: [MoYuOwO@outlook.com](mailto:MoYuOwO@outlook.com)