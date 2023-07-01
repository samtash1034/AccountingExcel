記帳Excel管理器
記帳Excel管理器是一個網頁應用程式，允許使用者導入記帳Excel檔案，根據特定條件篩選數據，並將篩選後的數據下載為新的Excel檔案。該應用程式使用Java和Spring框架實現，利用Apache POI庫進行Excel檔案處理。

功能
導入Excel：使用者可以使用網頁表單上傳一個記帳Excel檔案。
數據篩選：應用程式讀取上傳的Excel檔案，根據特定條件（如記錄類型和月份）對數據進行篩選。
選擇列：使用者可以選擇在篩選後的數據中保留的列。
動態月份選擇：使用者可以輸入要篩選數據的月份。
導出Excel：篩選後的數據將以新的Excel檔案形式導出，用戶可以下載該檔案。
前提條件
Java開發工具包（JDK）8或更高版本
Apache Maven
快速入門
複製存儲庫：git clone https://github.com/your-accounting-excel-manager.git
進入專案目錄：cd accounting-excel-manager
構建專案：mvn clean install
運行應用程式：mvn spring-boot:run
在網頁瀏覽器中訪問應用程式：http://localhost:8080
