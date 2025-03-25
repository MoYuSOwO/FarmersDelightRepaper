# FarmersDelightRepaper —— 基于 Ignite 的农夫乐事复刻项目

[English](README.md)

> 这是一个基于 Ignite 框架开发的 Paper 服务端 Mod，旨在通过纯服务端方案复刻[《农夫乐事》](https://modrinth.com/mod/farmers-delight)模组的核心功能，无需客户端安装任何模组。

## 🌟 项目特性

- **零客户端依赖**：所有功能均通过服务端实现，兼容原版客户端
- **混合架构**：结合 Paper 插件与 Mixin 技术实现高效开发
- **动态数据存储**：劫持原版方块状态存储作物数据，无需依赖 NBT
- **可扩展框架**：原理简便，扩展潜力高

## 🛠️ 技术实现

### 开发环境
- **Ignite + Paper**：基于 [Ignite](https://github.com/vectrix-space/ignite) 搭建 Mixin 开发环境
- **混合开发模式**：
  - `/plugin` - Paper 插件（物品注册/事件处理）
  - `/Ignite-mod` - Mixin 核心模块
- **Gradle 构建**：
  ```bash
  # 全项目构建
  ./gradlew build

  # 单独构建模块
  ./gradlew :plugin:build
  ./gradlew :Ignite-mod:build
  ```

### 核心机制
- **洋葱作物实现**：
    - 使用 `BlockState` 存储生长阶段与成熟度
    - 通过 Mixin 重写随机刻和作物逻辑实现原生级别的骨粉催熟和作物生长
    - 通过插件部分重写部分行为实现完整物理效果
    - 配合本地资源包支持实现完整的视觉效果

## 📌 当前进度

| 模块   | 完成度    | 说明                |
|------|--------|-------------------|
| 物品系统 | 🚧 50% | 实现了一些物品的注册，未实现合成表 |
| 作物系统 | 🚧 50% | 已实现洋葱种植/生长逻辑      |
| 烹饪交互 | ⭕ 0%   | 尚未开始实现            |

## 🚧 协作开发

欢迎贡献者加入！以下是需要重点推进的方向：

**TODO List**:
- [ ] 添加原mod的多个物品与物品合成表
- [ ] 完善作物系统
- [ ] 开发厨具交互逻辑
- [ ] ~~找bug~~ 编写单元测试用例

**开发须知**：
1. 是新手开发者一枚，很多 Mixin 代码都存在优化空间 ~~（是个只会Overwrite的屑）~~
2. 提交 PR 前请通过 `./gradlew check` 代码审查

## 📜 许可证

本项目采用 **[GNU General Public License v3.0](LICENSE)** 开源协议，您有义务：
- 保持衍生项目的开源性
- 明确标注原始作者信息
- 共享修改后的源代码

## 💬 交流方式

欢迎提交 Issue 或发送邮件给我一起讨论： [MoYuOwO@outlook.com](mailto:MoYuOwO@outlook.com)