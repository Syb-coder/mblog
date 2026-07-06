# 修复双编码损坏: UTF-8 原始 → 被 PowerShell(GBK默认) 误读 → 重写为 UTF-8
# 反向转换: 读取当前UTF-8 → 编码为GBK(还原原始字节) → 严格解码为UTF-8

param(
    [switch]$DryRun
)

$root = 'c:\000\code\mblog\src'
$files = Get-ChildItem -Path $root -Recurse -Include '*.java','*.js' -ErrorAction SilentlyContinue

$gbk = [System.Text.Encoding]::GetEncoding(936)
$utf8 = New-Object System.Text.UTF8Encoding($false)
$utf8Strict = New-Object System.Text.UTF8Encoding($false, $true)

$fixed = 0
$skipped = 0
$nochange = 0

foreach ($f in $files) {
    $shouldSkip = $false
    $fixedContent = $null

    try {
        $bytes = [System.IO.File]::ReadAllBytes($f.FullName)
        $content = $utf8.GetString($bytes)

        $gbkBytes = $gbk.GetBytes($content)
        $roundTrip = $gbk.GetString($gbkBytes)
        if ($roundTrip -ne $content) {
            $shouldSkip = $true
        }

        if (-not $shouldSkip) {
            $fixedContent = $utf8Strict.GetString($gbkBytes)
        }
    } catch {
        $shouldSkip = $true
    }

    if ($shouldSkip) {
        $skipped++
    } elseif ($fixedContent -eq $null) {
        $skipped++
    } elseif ($fixedContent -eq $content) {
        $nochange++
    } else {
        $fixed++
        if (-not $DryRun) {
            [System.IO.File]::WriteAllText($f.FullName, $fixedContent, $utf8)
        } else {
            Write-Host "[DRY] $($f.FullName)"
        }
    }
}

Write-Host ""
Write-Host "===== Encoding Fix Summary ====="
Write-Host "Total files scanned: $($files.Count)"
Write-Host "Fixed (corrupted):   $fixed"
Write-Host "No change (ASCII):   $nochange"
Write-Host "Skipped (non-UTF8):  $skipped"
if ($DryRun) { Write-Host "(DRY RUN - no files modified)" }
