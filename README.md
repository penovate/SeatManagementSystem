# SeatSystem 員工座位管理系統

這是一個專為人資部門設計的「員工座位安排系統」。透過視覺化地圖介面，人資人員可以直觀地查看樓層座位狀態，並輕鬆完成員工座位的分配與異動。

---

## 主要功能

### 1. 視覺化座位圖

分樓層顯示，用顏色區分狀態。

- 白色/空位：沒人坐，點擊可以選擇。
- 深灰色：有人坐了，點擊可以彈出視窗釋放座位。
- 玉山綠：選中位置後的預覽顏色。

### 2. 座位分配邏輯

- 支援下拉選單篩選 5 碼固定員編之員工。
- 點擊空位會先顯示預覽資訊（員工姓名與編號）。
- 自動處理「換位子」。如果員工原本有位子，換新位子時舊位子會自動清空。

### 3. 防錯機制

- 使用 SweetAlert2 做確認彈窗，避免點錯直接改到資料。

---

## 技術架構

- 前端：Vue 3 (Vite), Axios, SweetAlert2
- 後端：Java 21, Spring Boot, Spring Data JPA
- 資料庫：MS SQL Server
- 管理工具：Maven, NPM

---

## 技術重點

- 預存程序 (Stored Procedure)：核心的座位異動邏輯寫在 SQL 的 UpdateEmployeeSeat 裡，透過 Transaction 確保「清空舊位」跟「佔用新位」是一起完成的。
- 資料安全（後端）：後端不使用字串拼接，全部走參數化查詢。
- 資料安全（前端）：前端利用 Vue 內建機制處理 XSS。
- 資料一致性：在資料庫層級有設定關聯約束，確保資料正確。

---

## 如何跑起來

### 1. 資料庫 DB

進到 /DB 資料夾：

1.  先執行 DDL.sql 產表，建立 SeatDB 及其資料表與索引。
2.  再執行 DML.sql 匯入測試資料跟預存程序（UpdateEmployeeSeat）。

### 2. 後端 Backend

1. 版本須為 Java 21。
2. 請先修改 src/main/resources/application.properties 裡的資料庫帳密。

### 3. 前端 Frontend

環境要求：需安裝 Node.js。

```bash
cd frontend
npm install
npm run dev
```

> 預設訪問地址：http://localhost:5173

## 檔案結構

```plaintext
SeatManagementSystem/
├── backend/    # Spring Boot 服務端 (Java 21)
├── frontend/   # Vue 3 用戶端專案
└── DB/         # 資料庫定義與初始化腳本 (DDL/DML)
```

## 環境版本檢查

### Java 版本校驗

由於本系統利用了 **Java 21** 的特性，請在執行前確認 pom.xml 中的配置是否如下：

```xml
<properties>
    <java.version>21</java.version>
</properties>
```
