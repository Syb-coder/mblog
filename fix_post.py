#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
尝试用宽松模式修复 Post.java 的中文损坏。
对于无法用 UTF-8 严格解码的字节，用替换字符代替，然后手动检查。
"""

import os

fpath = r'c:\000\code\mblog\src\main\java\com\mtons\mblog\modules\entity\Post.java'

with open(fpath, 'rb') as f:
    raw_bytes = f.read()

current_text = raw_bytes.decode('utf-8')

# 反向：用 gb18030 编码
try:
    gbk_bytes = current_text.encode('gb18030')
except UnicodeEncodeError as e:
    print(f'GB18030 编码失败: {e}')
    exit(1)

print(f'当前文件大小: {len(raw_bytes)} 字节')
print(f'反向编码后大小: {len(gbk_bytes)} 字节')

# 用宽松模式解码
fixed_text = gbk_bytes.decode('utf-8', errors='replace')

# 统计替换字符数
replacement_count = fixed_text.count('\ufffd')
print(f'替换字符数 (U+FFFD): {replacement_count}')

# 找到替换字符附近的内容
if replacement_count > 0:
    print('\n替换字符附近的内容:')
    pos = 0
    for i in range(min(replacement_count, 20)):
        idx = fixed_text.find('\ufffd', pos)
        if idx == -1:
            break
        start = max(0, idx - 30)
        end = min(len(fixed_text), idx + 30)
        context = fixed_text[start:end].replace('\n', '\\n')
        print(f'  位置 {idx}: ...{context}...')
        pos = idx + 1

# 写回（先不写，先看结果）
print('\n--- 修复后的前 60 行 ---')
lines = fixed_text.split('\n')
for i, line in enumerate(lines[:60]):
    print(f'{i+1:3d}: {line}')
