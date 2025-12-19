# UmoiftNG (Universal Mod of Infinite Flame Team Next Generation)

## 📖 語言版本

- [简体中文](README.md)
- [繁體中文（香港）](README_zh_hk.md) (當前)
- [繁體中文（台灣）](README_zh_tw.md)
- [文言文（華夏）](README_lzh.md)

## 項目簡介

**UmoiftNG**（無限火焰團隊通用下一代模組）係一個基於 Minecraft Forge 1.20.1 嘅綜合性模組項目，旨在為 Minecraft 帶來豐富嘅功能擴展同遊戲體驗改進。

### 名稱含義
- **Umoift**: Universal Mod of Infinite Flame Team（無限火焰團隊嘅通用模組）
- **NG**: Next Generation（下一代）
- 完整含義：無限火焰團隊嘅通用下一代模組

## 主要功能

### 已實現功能

#### 1. 彩色燈具系統 (ColoredLampNext)
- 16種唔同顏色嘅燈具方塊
- 支持紅石信號控制開關
- 每種顏色都有獨特嘅光照效果
- 配方合成系統

#### 2. 對話框系統
- `/dialog` 命令 - 高版本風格嘅對話框功能
- 支持自定義對話內容
- 網絡同步機制

#### 3. 領地保護系統
- `/claim` 命令 - 完整嘅領地管理功能
- 支持領地創建、刪除、權限管理
- 防止未授權玩家破壞同交互

#### 4. 創造模式標籤頁
- 獨立嘅模組物品標籤頁
- 專屬圖標同分類展示

### 開發中功能
- 數據命令擴展
- 蹲下機制改進
- 跳躍時蹲下功能
- 多語言支持增強

### 計劃功能
完整嘅開發計劃詳見 [`功能與計劃(UmoiftNG).txt`](功能與計劃(UmoiftNG).txt)

## 技術架構

### 開發環境
- **Minecraft 版本**: 1.20.1
- **Forge 版本**: 47.4.10
- **Java 版本**: 17
- **構建工具**: Gradle 6.0+
- **映射表**: Official Mojang Mappings

### 項目結構
```
src/main/java/org/infiniteflameteam/umoiftng/
├── Main.java                    # 主模組類
├── blocks/                      # 方塊相關類
│   ├── AbstractColoredLampNextBlock.java
│   └── [顏色]LampNextBlock.java  # 16種顏色燈具
├── claim/                       # 領地系統
│   ├── ClaimManager.java
│   ├── ClaimCommand.java
│   └── ClaimProtectionHandler.java
├── commands/                    # 命令系統
│   ├── DialogCommand.java
│   └── ClaimCommand.java
├── dialog/                      # 對話框系統
│   └── OfficialDialogManager.java
├── network/                     # 網絡通信
│   └── DialogNetworkHandler.java
└── localization/               # 本地化支持
    └── LanguageManager.java

src/main/resources/
├── META-INF/mods.toml          # 模組元數據
├── assets/umoiftng/            # 資源文件
│   ├── blockstates/            # 方塊狀態定義
│   ├── models/                 # 模型文件
│   ├── textures/               # 紋理貼圖
│   └── lang/                   # 語言文件
└── data/umoiftng/              # 數據文件
    ├── recipes/                # 合成配方
    └── loot_tables/            # 掉落表
```

## 開發團隊

### Infinite Flame Team 成員
- **DeepSeek AI**: 開發、代碼編寫
- **Hei_wan_Feng**: 開發、代碼編寫、調試、資源管理
- **Ean遊戲**: 美術設計、資源管理
- **[合作]dhjs0000**: 美學設計、資源管理、版本控制

## 構建與安裝

### 開發環境搭建
1. 克隆項目到本地
2. 確保已安裝 Java 17
3. 運行 `./gradlew genEclipseRuns` 或 `./gradlew genIntellijRuns`
4. 導入到 IDE 中

### 構建模組
```bash
./gradlew build
```

構建完成後，模組文件將位於 `build/libs/` 目錄下。

### 安裝模組
1. 安裝 Minecraft Forge 1.20.1
2. 將構建好嘅 `.jar` 文件放入 `mods` 文件夾
3. 啟動遊戲

## 配置說明

### 模組配置
模組嘅主要配置位於 [`gradle.properties`](gradle.properties)：
- `mod_version`: 模組版本號
- `mod_name`: 模組顯示名稱
- `mod_authors`: 作者信息
- `mod_description`: 模組描述

### 版本更新
更新版本時需要修改嘅文件詳見 [`更新版本時需要更新的文件(UmoiftNG).txt`](更新版本時需要更新的文件(UmoiftNG).txt)

## 核心功能詳解

### 彩色燈具系統
每種顏色嘅燈具都有對應嘅方塊類同物品類，支持紅石控制，亮度等級為15。

### 領地系統
- 使用命令 `/claim create <名稱>` 創建領地
- 支持權限管理同保護機制
- 數據持久化存儲

### 對話框系統
- 支持自定義對話內容
- 客戶端-服務器網絡同步
- 支持重載配置

## 開發規範

### 代碼規範
- 使用 UTF-8 編碼
- 遵循 Java 命名規範
- 添加適當嘅註釋同文檔

### 資源規範
- 紋理文件使用 PNG 格式
- 模型文件遵循 Minecraft 模型規範
- 語言文件支持簡體中文、繁體中文等

## 已知問題

**注意**: 部分功能可能尚未經過充分測試，如發現問題請提交 Issue。

## 許可證

本項目採用 **All Rights Reserved** 許可證，詳見 [`LICENSE`](LICENSE) 文件。

## 相關鏈接

- [Minecraft Forge 官方文檔](https://docs.minecraftforge.net/)
- [Minecraft 官方映射表](https://parchmentmc.org/)
- [Forge 版本下載](https://files.minecraftforge.net/)

## 聯繫方式

如有問題或建議，請通過以下方式聯繫：
- 提交 GitHub Issue
- 聯繫開發團隊成員

---

**UmoiftNG** - 為 Minecraft 帶來無限可能嘅模組體驗！