# Spring Boot 容器診斷腳本

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Spring Boot 應用診斷工具" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 1. 檢查容器狀態
Write-Host "[1] 檢查容器運行狀態..." -ForegroundColor Yellow
docker ps --filter name=online-shop-backend --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# 2. 檢查 Tomcat 是否運行
Write-Host "`n[2] 檢查 Tomcat 進程..." -ForegroundColor Yellow
docker exec online-shop-backend ps aux | Select-String "java"

# 3. 檢查 WAR 部署狀態
Write-Host "`n[3] 檢查 WAR 文件部署..." -ForegroundColor Yellow
docker exec online-shop-backend ls -lh /usr/local/tomcat/webapps/ | Select-String "app"

# 4. 查找 Spring Boot 啟動日誌
Write-Host "`n[4] 搜尋 Spring Boot 啟動日誌..." -ForegroundColor Yellow
$logs = docker logs online-shop-backend 2>&1 | Select-String -Pattern "Started DemoApplication|Spring|ApplicationContext|Bean"
if ($logs) {
    Write-Host "找到 Spring Boot 日誌:" -ForegroundColor Green
    $logs | Select-Object -First 10
} else {
    Write-Host "⚠️  未找到 Spring Boot 啟動日誌 - 應用可能未啟動！" -ForegroundColor Red
}

# 5. 檢查錯誤日誌
Write-Host "`n[5] 檢查錯誤日誌..." -ForegroundColor Yellow
$errors = docker logs online-shop-backend 2>&1 | Select-String -Pattern "Exception|Error|Failed|Cannot|refused" -CaseSensitive:$false
if ($errors) {
    Write-Host "發現錯誤:" -ForegroundColor Red
    $errors | Select-Object -First 20
} else {
    Write-Host "未發現明顯錯誤" -ForegroundColor Green
}

# 6. 檢查數據庫連接
Write-Host "`n[6] 測試數據庫連接..." -ForegroundColor Yellow
Write-Host "檢查本機 SQL Server..." -ForegroundColor Gray
$sqlRunning = netstat -an | Select-String "1433.*LISTENING"
if ($sqlRunning) {
    Write-Host "✓ SQL Server 正在本機運行" -ForegroundColor Green
} else {
    Write-Host "✗ SQL Server 未在本機運行" -ForegroundColor Red
}

# 7. 測試容器到主機的連接
Write-Host "`n測試容器到主機的連接..." -ForegroundColor Gray
try {
    $result = docker exec online-shop-backend bash -c "timeout 2 bash -c '</dev/tcp/host.docker.internal/1433' 2>&1"
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ 容器可以連接到 SQL Server" -ForegroundColor Green
    } else {
        Write-Host "✗ 容器無法連接到 SQL Server" -ForegroundColor Red
        Write-Host "  原因：SQL Server 可能未配置允許遠程連接" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ 連接測試失敗" -ForegroundColor Red
}

# 8. 檢查 Redis 連接
Write-Host "`n檢查 Redis..." -ForegroundColor Gray
$redisRunning = docker ps --filter name=redis --format "{{.Names}}\t{{.Status}}"
if ($redisRunning) {
    Write-Host "✓ Redis 容器正在運行: $redisRunning" -ForegroundColor Green
} else {
    Write-Host "✗ Redis 容器未運行" -ForegroundColor Red
}

# 9. 測試應用端點
Write-Host "`n[7] 測試應用端點..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/app" -TimeoutSec 3 -ErrorAction Stop
    Write-Host "✓ 應用正常響應，狀態碼: $($response.StatusCode)" -ForegroundColor Green
} catch {
    $errorMsg = $_.Exception.Message
    if ($errorMsg -like "*404*") {
        Write-Host "✗ 返回 404 - Spring Boot 應用未啟動" -ForegroundColor Red
    } else {
        Write-Host "✗ 無法訪問應用: $errorMsg" -ForegroundColor Red
    }
}

# 10. 總結和建議
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "診斷總結" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`n可能的問題:" -ForegroundColor Yellow
Write-Host "1. Spring Boot 應用因無法連接數據庫而啟動失敗" -ForegroundColor White
Write-Host "2. 數據庫連接配置中的 localhost 需要改為 host.docker.internal" -ForegroundColor White
Write-Host "3. SQL Server 需要配置允許遠程連接" -ForegroundColor White

Write-Host "`n建議解決方案:" -ForegroundColor Yellow
Write-Host "1. 停止容器並使用正確的環境變量重啟:" -ForegroundColor White
Write-Host '   docker stop online-shop-backend && docker rm online-shop-backend' -ForegroundColor Gray
Write-Host '   docker run -d -p 8080:8080 --name online-shop-backend \' -ForegroundColor Gray
Write-Host '     -e SPRING_DATASOURCE_URL="jdbc:sqlserver://host.docker.internal:1433;databaseName=onlineShopDB;encrypt=true;trustServerCertificate=true;" \' -ForegroundColor Gray
Write-Host '     -e SPRING_DATA_REDIS_HOST="host.docker.internal" \' -ForegroundColor Gray
Write-Host '     online-shop:latest' -ForegroundColor Gray
Write-Host "`n2. 或使用 Docker Compose 管理所有服務" -ForegroundColor White
Write-Host "" -ForegroundColor White

