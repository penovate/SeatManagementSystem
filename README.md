# SeatSystem 員工座位管理系統

這是一個專為人資部門設計的「員工座位安排系統」。透過視覺化地圖介面，人資人員可以直觀地查看樓層座位狀態，並輕鬆完成員工座位的分配與異動。

---

## ✨ 核心功能

### 1. 樓層地圖視覺化

系統動態渲染各樓層座位分布，並透過色彩直觀標示狀態：

- <kbd>灰色</kbd> **空位**：目前無人佔用，點擊即可選取。
- <kbd>紅色</kbd> **已佔用**：顯示該位置員工編號與姓名，點擊可進行釋放。
- <kbd>亮綠色</kbd> **已選擇**：選取員工後的即時預覽狀態，確保安排正確。

### 2. 座位調整與智慧預覽

- **精準選取**：支援下拉選單篩選 5 碼固定員編之員工。
- **即時反饋**：點擊空位後，座位上立即顯示預計安排之「姓名與編號」，提供預覽感。
- **快速釋放**：點擊已佔用座位，即可觸發釋放流程，恢復為空位狀態。

### 3. 嚴謹的互動機制

- **防呆彈窗**：整合 `SweetAlert2` 實作二次確認邏輯，避免任何誤操作。
- **業務約束**：系統層級限制每位員工僅能佔用單一座位，自動處理「搬家」與「舊位釋放」。

---

## 🛠️ 技術架構

| 層級                  | 技術棧                                              |
| :-------------------- | :-------------------------------------------------- |
| **前端 (Frontend)**   | Vue.js, Vite, Axios, SweetAlert2                    |
| **後端 (Backend)**    | Java 21, Spring Boot, Spring Data JPA (RESTful API) |
| **資料庫 (Database)** | Microsoft SQL Server                                |
| **建構工具**          | Maven, NPM                                          |

---

## 🛡️ 安全性與技術亮點

- **Transaction 交易管理**：
  透過 Spring `@Transactional` 結合資料庫 **Stored Procedure**，確保「釋放舊位」與「佔用新位」在同一個事務中完成，保證資料一致性。
- **預防 SQL Injection**：
  全系統拒絕字串拼接，完全採用 **Prepared Statements** 與參數化 Stored Procedure 存取資料。
- **預防 XSS 攻擊**：
  利用 Vue 內建的模板自動轉義機制處理輸出，並於後端進行嚴格的資料格式校驗。
- **效能優化**：
  資料庫實作條件式 **Unique Index**，在 DB 層級加強資料正確性並優化查詢效能。

---

## 📦 快速啟動指南

### 1. 資料庫建置

請至 `/DB` 資料夾下，依序執行腳本：

1.  執行 `DDL.sql`：建立 `SeatDB` 及其資料表與索引。
2.  執行 `DML.sql`：插入初始化測試資料並建立 `UpdateEmployeeSeat` 預儲程序。

### 2. 後端啟動 (Backend)

```bash
cd backend
# 請先於 src/main/resources/application.properties 修改資料庫連線資訊
mvn spring-boot:run
```

### 3. 前端啟動 (Frontend)

環境要求：需安裝 Node.js。

```bash
cd frontend
npm install
npm run dev
```

> 預設訪問地址：http://localhost:5173

## 📂 目錄結構說明

```plaintext
SeatManagementSystem/
├── backend/    # Spring Boot 服務端 (Java 21)
├── frontend/   # Vue 3 用戶端專案
└── DB/         # 資料庫定義與初始化腳本 (DDL/DML)
```

## 💡 環境版本檢查

### Java 版本校驗

由於本系統利用了 **Java 21** 的特性，請在執行前確認 `backend/pom.xml` 中的配置是否如下：

```xml
<properties>
    <java.version>21</java.version>
</properties>
```
