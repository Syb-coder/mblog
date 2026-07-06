"""
修复双编码损坏: 原始UTF-8 -> PowerShell(GBK默认)误读 -> 重写为UTF-8
反向转换: 读取当前UTF-8 -> 编码为GBK(还原原始字节) -> 严格解码为UTF-8
对ASCII代码是no-op, 对损坏的中文是修复, 对正常UTF-8中文会因无效UTF-8序列而跳过
"""
import os
import sys

root = r'c:\000\code\mblog\src'
dry_run = '--dry-run' in sys.argv

fixed = 0
skipped = 0
nochange = 0
fixed_files = []

for dirpath, dirnames, filenames in os.walk(root):
    for filename in filenames:
        if not (filename.endswith('.java') or filename.endswith('.js')):
            continue
        filepath = os.path.join(dirpath, filename)

        try:
            with open(filepath, 'rb') as f:
                raw_bytes = f.read()

            # 步骤1: 读取当前内容为UTF-8字符串（可能是损坏的）
            content = raw_bytes.decode('utf-8')

            # 步骤2: 编码为GB18030(GBK超集, 能编码所有Unicode), 还原原始字节
            try:
                gbk_bytes = content.encode('gb18030')
            except UnicodeEncodeError:
                skipped += 1
                continue

            # 步骤3: 严格解码为UTF-8(如果原始字节不是有效UTF-8, 会抛异常)
            try:
                fixed_content = gbk_bytes.decode('utf-8')
            except UnicodeDecodeError:
                # 不是有效UTF-8, 跳过(说明文件未被损坏, 或损坏不可逆)
                skipped += 1
                continue

            # 步骤4: 只有内容实际改变才写入
            if fixed_content == content:
                nochange += 1
                continue

            fixed += 1
            fixed_files.append(filepath)
            if not dry_run:
                with open(filepath, 'w', encoding='utf-8', newline='') as f:
                    f.write(fixed_content)
            else:
                print(f"[DRY] {filepath}")

        except Exception:
            skipped += 1

print()
print("===== Encoding Fix Summary (Python) =====")
print(f"Fixed (corrupted):   {fixed}")
print(f"No change (ASCII):   {nochange}")
print(f"Skipped (non-UTF8):  {skipped}")
if dry_run:
    print("(DRY RUN - no files modified)")
