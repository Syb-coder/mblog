# 用 .NET codepage 936 反转编码损坏 (与 PowerShell Get-Content 使用的编码完全一致)
# 跳过往返检查, 用宽松 UTF-8 解码 (U+FFFD 替换无效字节)

param(
    [switch]$DryRun
)

$root = 'c:\000\code\mblog\src'
$files = Get-ChildItem -Path $root -Recurse -Include '*.java','*.js' -ErrorAction SilentlyContinue

# 使用 codepage 936 (与 PowerShell Get-Content 默认编码一致)
$gbk = [System.Text.Encoding]::GetEncoding(936)
# UTF-8 无BOM
$utf8 = New-Object System.Text.UTF8Encoding($false)
# UTF-8 无BOM, 宽松解码 (用 U+FFFD 替换无效字节)
$utf8Lenient = New-Object System.Text.UTF8Encoding($false, $false)

# GBK 编码器, 用异常fallback (检测无法编码的字符)
$gbkEncoder = $gbk.GetEncoder()
$gbkEncWithFallback = [System.Text.Encoding]::GetEncoding(936, [System.Text.EncoderReplacementFallback]::new([char]0x3F), [System.Text.DecoderReplacementFallback]::new([char]0xFFFD))

$fixed = 0
$skipped = 0
$nochange = 0

foreach ($f in $files) {
    $bytes = [System.IO.File]::ReadAllBytes($f.FullName)
    # 步骤1: 读取当前内容为UTF-8字符串（损坏的）
    $content = $utf8.GetString($bytes)

    # 步骤2: 编码为GBK(codepage 936), 用'?'替换无法编码的字符
    $gbkBytes = $gbkEncWithFallback.GetBytes($content)

    # 步骤3: 宽松解码为UTF-8 (用 U+FFFD 替换无效字节)
    $fixedContent = $utf8Lenient.GetString($gbkBytes)

    # 步骤4: 检查是否有改善 (替换字符数量减少)
    $origFFFD = ($content.ToCharArray() | Where-Object { $_ -eq [char]0xFFFD }).Count
    $fixedFFFD = ($fixedContent.ToCharArray() | Where-Object { $_ -eq [char]0xFFFD }).Count
    $origGarbled = ([regex]::Matches($content, '[\u9300-\u9FFF\u5600-\u57FF\u7300-\u74FF]')).Count
    $fixedGarbled = ([regex]::Matches($fixedContent, '[\u9300-\u9FFF\u5600-\u57FF\u7300-\u74FF]')).Count

    # 只有修复后乱码减少才应用
    if ($fixedGarbled -lt $origGarbled -or ($origGarbled -eq 0 -and $fixedContent -ne $content)) {
        if ($fixedContent -ne $content) {
            $fixed++
            if (-not $DryRun) {
                [System.IO.File]::WriteAllText($f.FullName, $fixedContent, $utf8)
            } else {
                Write-Host "[DRY] $($f.FullName): garbled $origGarbled -> $fixedGarbled"
            }
        } else {
            $nochange++
        }
    } else {
        $nochange++
    }
}

Write-Host ""
Write-Host "===== .NET CP936 Fix Summary ====="
Write-Host "Fixed:      $fixed"
Write-Host "No change:  $nochange"
if ($DryRun) { Write-Host "(DRY RUN)" }
