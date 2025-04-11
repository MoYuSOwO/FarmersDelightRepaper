# FarmersDelightRePaper - 农夫乐事 Paper 重置版

![License](https://img.shields.io/badge/License-GPLv3-blue)
![Status](https://img.shields.io/badge/Status-Alpha-red)

[English](README.md)

一个基于 PaperAPI 和客户端资源包实现的 [农夫乐事mod](https://modrinth.com/mod/farmers-delight) 复刻项目，完全通过服务端技术实现原版模组功能。

## ✨ 核心特性

- **纯服务端实现**：仅需客户端资源包 + Paper 服务端插件
- **协议层魔法**：利用数据包和区块操作欺骗客户端渲染自定义方块
- **轻量无依赖**：原生使用区块存储，无需外部数据库
- **完美兼容性**：方块行为层基于 PaperAPI 开发，不依赖 NMS，兼容性好
- **模块化架构**：易于扩展新作物和厨具系统

## ⚙️ 安装指南

1. 将插件放入 `plugins/` 文件夹
2. 正确配置资源包
3. 重启服务器
4. 确保在 `server.properties` 中启用资源包：
   ```properties
   require-resource-pack=true
   resource-pack-sha1=你的资源包SHA1值
   ```

## 🛠️ 开发架构

### 核心模块
| 模块                      | 功能         |
|-------------------------|------------|
| **BlockPacketHandler**  | 协议层伪装自定义方块 |
| **CustomBlockBehavior** | 作物生长阶段控制   |
| **CustomBlockStorage**  | 自定义方块储存    |

## 📜 协议说明

本项目采用 **GNU General Public License v3.0** 开源协议:
- 允许自由使用和修改
- 要求衍生作品开源
- 禁止作为闭源商业软件的一部分分发

完整协议见 [LICENSE](LICENSE) 文件。

## 🚧 开发进度

**当前版本功能**:
- [x] 基础作物系统 (洋葱)
- [x] 方块和物品管理
- [x] 对应客户端资源包
- [x] 部分合成表
- [x] 基于sqlite的自定义方块数据持久化储存

**TODO List**:
- [ ] 添加原mod的多个物品与物品合成表
- [ ] 完善作物系统
- [ ] 开发刀具&厨具交互逻辑
- [ ] ~~找bug~~ 编写单元测试用例

> ⚠️ 警告：本项目仍处于开发阶段，请勿在生产环境使用！

## 🤝 参与贡献

欢迎通过以下方式参与：
1. 提交 Pull Request （请先通过 `./gradlew check` 代码审查）
2. 在 Issues 讨论技术方案
3. 帮助完善文档

推荐开发环境：
- JDK 21+
- Paper 1.21.4+
- IntelliJ IDEA

## 💬 交流方式

欢迎提交 Issue 或发送邮件给我一起讨论： [MoYuOwO@outlook.com](mailto:MoYuOwO@outlook.com)