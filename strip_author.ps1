# 批量清除 Java 文件中的 @author / Created by 署名行
# 用法: powershell -File strip_author.ps1 [-DryRun]

param(
    [switch]$DryRun
)

$root = 'c:\000\code\mblog\src'
$files = Get-ChildItem -Path $root -Recurse -Include '*.java'
$totalMatches = 0
$modifiedFiles = 0

# 匹配 Javadoc 行: * @author xxx / * Created by xxx / * created by xxx
$pattern = '(?m)^\s*\*\s*(@author|Created by|created by).*?$'

foreach ($f in $files) {
    $content = Get-Content $f.FullName -Raw
    if (-not $content) { continue }
    $matches = [regex]::Matches($content, $pattern)
    if ($matches.Count -eq 0) { continue }

    $totalMatches += $matches.Count
    $modifiedFiles++

    if ($DryRun) {
        Write-Host "[DRY] $($f.FullName): $($matches.Count) matches"
        continue
    }

    $newContent = [regex]::Replace($content, $pattern, '')
    # 清理因删除行留下的连续空行（仅限 Javadoc 内的连续空行）
    $newContent = [regex]::Replace($newContent, '(?m)^\s*\*\s*$\r?\n(\s*\*\s*$\r?\n){1,}', '')
    # 使用 .NET 写入 UTF8 无 BOM，保留原换行风格
    $utf8NoBom = New-Object System.Text.UTF8Encoding $false
    [System.IO.File]::WriteAllText($f.FullName, $newContent, $utf8NoBom)
}

Write-Host ""
Write-Host "===== Summary ====="
Write-Host "Total Java files scanned: $($files.Count)"
Write-Host "Files with author tags:   $modifiedFiles"
Write-Host "Total author lines removed: $totalMatches"
if ($DryRun) { Write-Host "(DRY RUN - no files modified)" }
