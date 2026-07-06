# 批量清除 mtons 版权声明块（Java + JS 文件头部）
# 匹配块特征: 以 /* 开头，含 "mtons" 和 "Copyright"，以 */ 结尾
# 用法: powershell -File strip_copyright.ps1 [-DryRun]

param(
    [switch]$DryRun
)

$root = 'c:\000\code\mblog\src'
$javaFiles = Get-ChildItem -Path $root -Recurse -Include '*.java'
$jsFiles = Get-ChildItem -Path "$root\main\resources\static\dist\js\modules" -Recurse -Include '*.js' -ErrorAction SilentlyContinue
$files = @($javaFiles) + @($jsFiles)

$totalRemoved = 0
$modifiedFiles = 0

# 匹配文件头部 mtons 版权块:
# - 以 /* 开始 (允许前导空白)
# - 块内含 "mtons" 且含 "Copyright" (或 "mtons.com")
# - 以 */ 结束
# - 支持单行或多行块
$blockPattern = '(?s)^\s*/\*[\s\S]*?mtons[\s\S]*?\*/\r?\n?'

foreach ($f in $files) {
    $content = Get-Content $f.FullName -Raw
    if (-not $content) { continue }

    # 必须同时含 mtons 和 Copyright 才算版权块（避免误删普通注释）
    $blockMatch = [regex]::Match($content, $blockPattern)
    if (-not $blockMatch.Success) { continue }
    if ($blockMatch.Value -notmatch 'Copyright') { continue }

    $totalRemoved++
    $modifiedFiles++

    if ($DryRun) {
        Write-Host "[DRY] $($f.FullName)"
        continue
    }

    $newContent = [regex]::Replace($content, $blockPattern, '')
    $utf8NoBom = New-Object System.Text.UTF8Encoding $false
    [System.IO.File]::WriteAllText($f.FullName, $newContent, $utf8NoBom)
}

Write-Host ""
Write-Host "===== Summary ====="
Write-Host "Total files scanned:     $($files.Count)"
Write-Host "Files with mtons block:  $modifiedFiles"
if ($DryRun) { Write-Host "(DRY RUN - no files modified)" }
