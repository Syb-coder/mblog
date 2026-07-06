<#
.SYNOPSIS
    mblog 博客系统一键启动脚本（Windows PowerShell 版）
.DESCRIPTION
    本脚本完成以下工作：
      1. 检查并切换 JDK 17 运行环境
      2. 检查 Docker 守护进程，启动 MySQL 5.7 容器
      3. 等待 MySQL 健康检查通过
      4. 创建站点存储目录
      5. 使用 Maven 编译项目
      6. 以 dev profile 启动 Spring Boot 应用
.NOTES
    使用方式：在项目根目录执行 .\start.ps1
    或双击 start.bat 调用本脚本
#>

param(
    # 跳过 Docker 启动（当数据库已运行时使用）
    [switch]$SkipDocker,
    # 跳过 Maven 编译（当已编译完成时使用）
    [switch]$SkipBuild,
    # 传入 Spring Boot 额外参数
    [string]$SpringArgs = ""
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

# 控制台输出工具函数
function Write-Step([string]$msg) { Write-Host "`n[步骤] $msg" -ForegroundColor Cyan }
function Write-Ok([string]$msg)   { Write-Host "  [成功] $msg" -ForegroundColor Green }
function Write-Warn([string]$msg) { Write-Host "  [警告] $msg" -ForegroundColor Yellow }
function Write-Err([string]$msg)  { Write-Host "  [错误] $msg" -ForegroundColor Red }

# ============================================================
# 步骤 1：检查并切换 JDK 17
# 项目 pom.xml 中配置 <java.version>17</java.version>，
# 系统默认 JDK 25 与 Lombok 注解处理器存在兼容性问题，
# 因此必须显式切换到 JDK 17。
# ============================================================
Write-Step "检查 JDK 17 环境"

$Jdk17Path = "C:\010\jdk17"
if (-not (Test-Path "$Jdk17Path\bin\java.exe")) {
    Write-Err "未找到 JDK 17（路径：$Jdk17Path）"
    Write-Err "请安装 JDK 17 或修改脚本中的 `$Jdk17Path 变量"
    exit 1
}

$env:JAVA_HOME = $Jdk17Path
$env:PATH = "$Jdk17Path\bin;$env:PATH"
$javaVersion = & java -version 2>&1 | Select-Object -First 1
Write-Ok "已切换到 $javaVersion"

# ============================================================
# 步骤 2：检查 Maven
# ============================================================
Write-Step "检查 Maven 环境"
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvnCmd) {
    Write-Err "未找到 mvn 命令，请确认 Maven 已安装并加入 PATH"
    exit 1
}
$mvnVersion = & mvn -version 2>&1 | Select-Object -First 1
Write-Ok "Maven：$mvnVersion"

# ============================================================
# 步骤 3：启动 Docker MySQL 容器
# ============================================================
if (-not $SkipDocker) {
    Write-Step "检查 Docker 守护进程"

    $dockerInfo = docker info 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Warn "Docker 守护进程未运行，正在尝试启动 Docker Desktop..."
        $dockerExe = "C:\Program Files\Docker\Docker\Docker Desktop.exe"
        if (Test-Path $dockerExe) {
            Start-Process $dockerExe
            $maxWait = 30
            for ($i = 1; $i -le $maxWait; $i++) {
                Start-Sleep -Seconds 3
                docker info 2>&1 | Out-Null
                if ($LASTEXITCODE -eq 0) { break }
                Write-Host "  等待 Docker 启动... ($i/$maxWait)"
            }
        }
        docker info 2>&1 | Out-Null
        if ($LASTEXITCODE -ne 0) {
            Write-Err "Docker 守护进程启动失败，请手动启动 Docker Desktop 后重试"
            Write-Warn "如数据库已运行，可使用 -SkipDocker 参数跳过此步骤"
            exit 1
        }
    }
    Write-Ok "Docker 守护进程已就绪"

    # 启动 MySQL 容器
    Write-Step "启动 MySQL 5.7 容器"
    Set-Location $ProjectRoot
    docker compose up -d mysql 2>&1 | Out-Null
    if ($LASTEXITCODE -ne 0) {
        # 容器名冲突时先移除再重启
        Write-Warn "容器启动失败，尝试移除旧容器后重试..."
        docker rm -f mblog-mysql 2>&1 | Out-Null
        docker compose up -d mysql 2>&1 | Out-Null
    }
    Write-Ok "MySQL 容器已启动"

    # 等待 MySQL 健康检查通过
    Write-Step "等待 MySQL 就绪"
    $maxRetry = 30
    $ready = $false
    for ($i = 1; $i -le $maxRetry; $i++) {
        Start-Sleep -Seconds 2
        $result = docker exec mblog-mysql mysqladmin ping -uroot -proot 2>&1
        if ($result -match "mysqld is alive") {
            $ready = $true
            break
        }
        Write-Host "  等待 MySQL 初始化... ($i/$maxRetry)"
    }
    if (-not $ready) {
        Write-Err "MySQL 启动超时，请检查容器日志：docker logs mblog-mysql"
        exit 1
    }
    Write-Ok "MySQL 已就绪，数据库 db_mblog 可用"
} else {
    Write-Warn "已跳过 Docker 启动步骤"
}

# ============================================================
# 步骤 4：确保站点存储目录存在
# application.yml 中 site.location 指向此目录，
# 用于存储上传文件、Lucene 索引、FreeMarker 模板等。
# ============================================================
Write-Step "检查站点存储目录"
$storageDir = "C:\020-docker-data\mblog-storage"
if (-not (Test-Path $storageDir)) {
    New-Item -ItemType Directory -Force -Path $storageDir | Out-Null
    Write-Ok "已创建存储目录：$storageDir"
} else {
    Write-Ok "存储目录已存在：$storageDir"
}

# ============================================================
# 步骤 5：Maven 编译
# ============================================================
if (-not $SkipBuild) {
    Write-Step "Maven 编译项目"
    Set-Location $ProjectRoot
    & mvn clean compile -DskipTests -Pdev 2>&1 | Select-Object -Last 15
    if ($LASTEXITCODE -ne 0) {
        Write-Err "Maven 编译失败"
        exit 1
    }
    Write-Ok "编译成功"
} else {
    Write-Warn "已跳过 Maven 编译步骤"
}

# ============================================================
# 步骤 6：启动 Spring Boot 应用
# 使用 spring-boot:run 以 dev profile 启动，
# 端口 8080，控制台实时输出日志。
# ============================================================
Write-Step "启动 Spring Boot 应用"
Set-Location $ProjectRoot
Write-Host "  应用地址：http://localhost:8080" -ForegroundColor Green
Write-Host "  按 Ctrl+C 停止应用" -ForegroundColor Yellow
Write-Host ""

$runArgs = @("spring-boot:run", "-Pdev")
if ($SpringArgs) {
    $runArgs += "-Dspring-boot.run.arguments=$SpringArgs"
}
& mvn @runArgs
