@echo off
REM ============================================================
REM mblog 博客系统一键启动脚本（Windows 批处理入口）
REM 本脚本调用 start.ps1 完成启动流程
REM ============================================================

cd /d "%~dp0"

REM 检查 PowerShell 执行策略，如受限则临时放宽
powershell -NoProfile -Command "if ((Get-ExecutionPolicy -Scope CurrentUser) -eq 'Restricted') { Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned -Force }"

REM 调用 PowerShell 启动脚本，传递所有命令行参数
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start.ps1" %*
set EXITCODE=%ERRORLEVEL%

REM 无论成功失败都暂停，避免窗口立即关闭导致看不到输出
echo.
if %EXITCODE% equ 0 (
    echo [启动结束] 应用已停止运行
) else (
    echo [启动失败] 退出码 %EXITCODE%，请查看上方错误信息
)
pause
exit /b %EXITCODE%
