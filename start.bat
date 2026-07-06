@echo off
chcp 65001 >nul 2>&1
REM ============================================================
REM mblog 博客系统一键启动脚本（Windows 批处理版）
REM 本脚本调用 start.ps1 完成启动流程
REM ============================================================

cd /d "%~dp0"

REM 检查 PowerShell 执行策略，如受限则临时绕过
powershell -Command "if ((Get-ExecutionPolicy -Scope CurrentUser) -eq 'Restricted') { Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned -Force }"

REM 调用 PowerShell 启动脚本
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start.ps1" %*

REM 如果 PowerShell 退出码非 0，暂停以查看错误
if %ERRORLEVEL% neq 0 (
    echo.
    echo [启动失败] 请查看上方错误信息
    pause
)
