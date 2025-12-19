# UmoiftNG (Universal Mod of Infinite Flame Team Next Generation)

## 项目简介

**UmoiftNG**（无限火焰团队通用下一代模组）是一个基于 Minecraft Forge 1.20.1 的综合性模组项目，旨在为 Minecraft 带来丰富的功能扩展和游戏体验改进。

### 名称含义
- **Umoift**: Universal Mod of Infinite Flame Team（无限火焰团队的通用模组）
- **NG**: Next Generation（下一代）
- 完整含义：无限火焰团队的通用下一代模组

## 主要功能

### 已实现功能

#### 1. 彩色灯具系统 (ColoredLampNext)
- 16种不同颜色的灯具方块
- 支持红石信号控制开关
- 每种颜色都有独特的光照效果
- 配方合成系统

#### 2. 对话框系统
- `/dialog` 命令 - 高版本风格的对话框功能
- 支持自定义对话内容
- 网络同步机制

#### 3. 领地保护系统
- `/claim` 命令 - 完整的领地管理功能
- 支持领地创建、删除、权限管理
- 防止未授权玩家破坏和交互

#### 4. 创造模式标签页
- 独立的模组物品标签页
- 专属图标和分类展示

### 开发中功能
- 数据命令扩展
- 蹲下机制改进
- 跳跃时蹲下功能
- 多语言支持增强

### 计划功能
完整的开发计划详见 [`功能与计划(UmoiftNG).txt`](功能与计划(UmoiftNG).txt)

## 技术架构

### 开发环境
- **Minecraft 版本**: 1.20.1
- **Forge 版本**: 47.4.10
- **Java 版本**: 17
- **构建工具**: Gradle 6.0+
- **映射表**: Official Mojang Mappings

### 项目结构
```
src/main/java/org/infiniteflameteam/umoiftng/
├── Main.java                    # 主模组类
├── blocks/                      # 方块相关类
│   ├── AbstractColoredLampNextBlock.java
│   └── [颜色]LampNextBlock.java  # 16种颜色灯具
├── claim/                       # 领地系统
│   ├── ClaimManager.java
│   ├── ClaimCommand.java
│   └── ClaimProtectionHandler.java
├── commands/                    # 命令系统
│   ├── DialogCommand.java
│   └── ClaimCommand.java
├── dialog/                      # 对话框系统
│   └── OfficialDialogManager.java
├── network/                     # 网络通信
│   └── DialogNetworkHandler.java
└── localization/               # 本地化支持
    └── LanguageManager.java

src/main/resources/
├── META-INF/mods.toml          # 模组元数据
├── assets/umoiftng/            # 资源文件
│   ├── blockstates/            # 方块状态定义
│   ├── models/                 # 模型文件
│   ├── textures/               # 纹理贴图
│   └── lang/                   # 语言文件
└── data/umoiftng/              # 数据文件
    ├── recipes/                # 合成配方
    └── loot_tables/            # 掉落表
```

## 开发团队

### Infinite Flame Team 成员
- **DeepSeek AI**: 开发、代码编写
- **Hei_wan_Feng**: 开发、代码编写、调试、资源管理
- **Ean游戏**: 美术设计、资源管理
- **[合作]dhjs0000**: 美学设计、资源管理、版本控制

## 构建与安装

### 开发环境搭建
1. 克隆项目到本地
2. 确保已安装 Java 17
3. 运行 `./gradlew genEclipseRuns` 或 `./gradlew genIntellijRuns`
4. 导入到 IDE 中

### 构建模组
```bash
./gradlew build
```

构建完成后，模组文件将位于 `build/libs/` 目录下。

### 安装模组
1. 安装 Minecraft Forge 1.20.1
2. 将构建好的 `.jar` 文件放入 `mods` 文件夹
3. 启动游戏

## 配置说明

### 模组配置
模组的主要配置位于 [`gradle.properties`](gradle.properties)：
- `mod_version`: 模组版本号
- `mod_name`: 模组显示名称
- `mod_authors`: 作者信息
- `mod_description`: 模组描述

### 版本更新
更新版本时需要修改的文件详见 [`更新版本时需要更新的文件(UmoiftNG).txt`](更新版本时需要更新的文件(UmoiftNG).txt)

## 核心功能详解

### 彩色灯具系统
每种颜色的灯具都有对应的方块类和物品类，支持红石控制，亮度等级为15。

### 领地系统
- 使用命令 `/claim create <名称>` 创建领地
- 支持权限管理和保护机制
- 数据持久化存储

### 对话框系统
- 支持自定义对话内容
- 客户端-服务器网络同步
- 支持重载配置

## 开发规范

### 代码规范
- 使用 UTF-8 编码
- 遵循 Java 命名规范
- 添加适当的注释和文档

### 资源规范
- 纹理文件使用 PNG 格式
- 模型文件遵循 Minecraft 模型规范
- 语言文件支持简体中文、繁体中文等

## 已知问题

**注意**: 部分功能可能尚未经过充分测试，如发现问题请提交 Issue。

## 许可证

本项目采用 **All Rights Reserved** 许可证，详见 [`LICENSE`](LICENSE) 文件。

## 相关链接

- [Minecraft Forge 官方文档](https://docs.minecraftforge.net/)
- [Minecraft 官方映射表](https://parchmentmc.org/)
- [Forge 版本下载](https://files.minecraftforge.net/)

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 GitHub Issue
- 联系开发团队成员

---

**UmoiftNG** - 为 Minecraft 带来无限可能的模组体验！