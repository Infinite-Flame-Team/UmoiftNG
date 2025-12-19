# UmoiftNG (Universal Mod of Infinite Flame Team Next Generation)

## 📖 語言版本

- [简体中文](README.md)
- [繁體中文（香港）](README_zh_hk.md)
- [繁體中文（台灣）](README_zh_tw.md) (當前)
- [文言文（華夏）](README_lzh.md)

## 專案簡介

**UmoiftNG**（無限火焰團隊通用下一代模組）是一個基於 Minecraft Forge 1.20.1 的綜合性模組專案，旨在為 Minecraft 帶來豐富的功能擴展和遊戲體驗改進。

### 名稱含義
- **Umoift**: Universal Mod of Infinite Flame Team（無限火焰團隊的通用模組）
- **NG**: Next Generation（下一代）
- 完整含義：無限火焰團隊的通用下一代模組

## 主要功能

### 已實現功能

#### 1. 彩色燈具系統 (ColoredLampNext)
- 16種不同顏色的燈具方塊
- 支援紅石訊號控制開關
- 每種顏色都有獨特的光照效果
- 配方合成系統

#### 2. 對話框系統
- `/dialog` 命令 - 高版本風格的對話框功能
- 支援自訂對話內容
- 網路同步機制

#### 3. 領地保護系統
- `/claim` 命令 - 完整的領地管理功能
- 支援領地建立、刪除、權限管理
- 防止未授權玩家破壞和互動

#### 4. 創造模式標籤頁
- 獨立的模組物品標籤頁
- 專屬圖示和分類展示

### 開發中功能
- 資料命令擴展
- 蹲下機制改進
- 跳躍時蹲下功能
- 多語言支援增強

### 計劃功能
完整的開發計劃詳見 [`功能與計劃(UmoiftNG).txt`](功能與計劃(UmoiftNG).txt)

## 技術架構

### 開發環境
- **Minecraft 版本**: 1.20.1
- **Forge 版本**: 47.4.10
- **Java 版本**: 17
- **建構工具**: Gradle 6.0+
- **映射表**: Official Mojang Mappings

### 專案結構
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
├── network/                     # 網路通訊
│   └── DialogNetworkHandler.java
└── localization/               # 本地化支援
    └── LanguageManager.java

src/main/resources/
├── META-INF/mods.toml          # 模組元資料
├── assets/umoiftng/            # 資源檔案
│   ├── blockstates/            # 方塊狀態定義
│   ├── models/                 # 模型檔案
│   ├── textures/               # 紋理貼圖
│   └── lang/                   # 語言檔案
└── data/umoiftng/              # 資料檔案
    ├── recipes/                # 合成配方
    └── loot_tables/            # 掉落表
```

## 開發團隊

### Infinite Flame Team 成員
- **DeepSeek AI**: 開發、程式碼編寫
- **Hei_wan_Feng**: 開發、程式碼編寫、調試、資源管理
- **Ean遊戲**: 美術設計、資源管理
- **[合作]dhjs0000**: 美學設計、資源管理、版本控制

## 建構與安裝

### 開發環境搭建
1. 複製專案到本地
2. 確保已安裝 Java 17
3. 執行 `./gradlew genEclipseRuns` 或 `./gradlew genIntellijRuns`
4. 匯入到 IDE 中

### 建構模組
```bash
./gradlew build
```

建構完成後，模組檔案將位於 `build/libs/` 目錄下。

### 安裝模組
1. 安裝 Minecraft Forge 1.20.1
2. 將建構好的 `.jar` 檔案放入 `mods` 資料夾
3. 啟動遊戲

## 配置說明

### 模組配置
模組的主要配置位於 [`gradle.properties`](gradle.properties)：
- `mod_version`: 模組版本號
- `mod_name`: 模組顯示名稱
- `mod_authors`: 作者資訊
- `mod_description`: 模組描述

### 版本更新
更新版本時需要修改的檔案詳見 [`更新版本時需要更新的文件(UmoiftNG).txt`](更新版本時需要更新的文件(UmoiftNG).txt)

## 核心功能詳解

### 彩色燈具系統
每種顏色的燈具都有對應的方塊類和物品類，支援紅石控制，亮度等級為15。

### 領地系統
- 使用命令 `/claim create <名稱>` 建立領地
- 支援權限管理和保護機制
- 資料持久化儲存

### 對話框系統
- 支援自訂對話內容
- 客戶端-伺服器網路同步
- 支援重載配置

## 開發規範

### 程式碼規範
- 使用 UTF-8 編碼
- 遵循 Java 命名規範
- 添加適當的註解和文件

### 資源規範
- 紋理檔案使用 PNG 格式
- 模型檔案遵循 Minecraft 模型規範
- 語言檔案支援簡體中文、繁體中文等

## 已知問題

**注意**: 部分功能可能尚未經過充分測試，如發現問題請提交 Issue。

## 授權條款

本專案採用 **All Rights Reserved** 授權條款，詳見 [`LICENSE`](LICENSE) 檔案。

## 相關連結

- [Minecraft Forge 官方文件](https://docs.minecraftforge.net/)
- [Minecraft 官方映射表](https://parchmentmc.org/)
- [Forge 版本下載](https://files.minecraftforge.net/)

## 聯絡方式

如有問題或建議，請透過以下方式聯絡：
- 提交 GitHub Issue
- 聯絡開發團隊成員

---

**UmoiftNG** - 為 Minecraft 帶來無限可能的模組體驗！