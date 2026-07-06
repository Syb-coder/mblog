#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
删除所有含损坏字符的中文注释行，保留代码逻辑。
检测方法：用 GBK 反向验证 - 如果文本用 GBK 编码后能形成有效 UTF-8 中文，则为损坏。
"""

import os
import re

PROJECT_ROOT = r'c:\000\code\mblog'


def is_corrupt_char(c):
    """
    判断字符是否为损坏字符。
    损坏字符 = UTF-8 中文被 GBK 误读后产生的字符。
    检测策略：
    1. 不能用 GB2312 编码的 CJK 字符是罕见字符（可能是损坏字符）
    2. CJK 扩展A区、私用区、兼容区的字符
    3. 能用 GBK 编码但字节模式匹配 UTF-8 中文模式的字符（需排除常用字）
    """
    cp = ord(c)
    # ASCII 字符不损坏
    if cp < 128:
        return False
    # CJK 扩展A区（U+3400-U+4DBF）在正常中文中极少使用
    if 0x3400 <= cp <= 0x4DBF:
        return True
    # 私用区字符
    if 0xE000 <= cp <= 0xF8FF:
        return True
    # CJK 兼容表意文字（U+F900-U+FAFF）
    if 0xF900 <= cp <= 0xFAFF:
        return True
    # 非 CJK 字符不判断
    if not (0x4E00 <= cp <= 0x9FFF):
        return False
    # GB2312 检测：不能用 GB2312 编码的 CJK 字符是罕见字符
    # GB2312 覆盖 6763 个常用汉字，正常中文几乎都在这个范围内
    try:
        c.encode('gb2312')
        # 能用 GB2312 编码，是常用字，不是损坏字符
        return False
    except UnicodeEncodeError:
        # 不能用 GB2312 编码，是罕见字
        # 进一步检查：GBK 编码字节是否匹配 UTF-8 中文模式
        try:
            gbk_bytes = c.encode('gbk')
            if len(gbk_bytes) == 2:
                b1, b2 = gbk_bytes[0], gbk_bytes[1]
                # 模式1：E4-E9 80-BF（UTF-8 三字节的前两字节）
                if 0xE4 <= b1 <= 0xE9 and 0x80 <= b2 <= 0xBF:
                    return True
                # 模式2：80-BF E4-E9（UTF-8 续字节+下一字符首字节）
                if 0x80 <= b1 <= 0xBF and 0xE4 <= b2 <= 0xE9:
                    return True
        except UnicodeEncodeError:
            pass
        return False


def has_corrupt_chars(text, threshold=2):
    """检测文本是否含损坏字符"""
    count = sum(1 for c in text if is_corrupt_char(c))
    return count >= threshold


def is_comment_line(line):
    """判断是否为注释行"""
    stripped = line.strip()
    return (stripped.startswith('*') or
            stripped.startswith('//') or
            stripped.startswith('/*') or
            stripped.startswith('/**') or
            stripped.startswith('*/'))


def remove_corrupt_comments(content):
    """
    删除所有含损坏字符的注释行，保留代码逻辑。
    """
    lines = content.split('\n')
    result = []
    in_javadoc = False
    javadoc_buffer = []

    i = 0
    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        # 检测 javadoc 块开始
        if stripped.startswith('/**'):
            in_javadoc = True
            javadoc_buffer = [line]
            i += 1
            continue

        # javadoc 块内
        if in_javadoc:
            javadoc_buffer.append(line)
            if '*/' in stripped:
                in_javadoc = False
                block_text = '\n'.join(javadoc_buffer)
                if has_corrupt_chars(block_text, threshold=1):
                    # 删除块中含损坏字符的行
                    clean_lines = []
                    for bl in javadoc_buffer:
                        if has_corrupt_chars(bl, threshold=1) and is_comment_line(bl):
                            continue
                        clean_lines.append(bl)
                    # 如果清理后只剩 /** 和 */，删除整个块
                    non_empty = [l for l in clean_lines if l.strip() not in ('/**', '*/', '*', '')]
                    if not non_empty:
                        pass  # 跳过整个 javadoc
                    else:
                        result.extend(clean_lines)
                else:
                    result.extend(javadoc_buffer)
                javadoc_buffer = []
            i += 1
            continue

        # 单行注释含损坏字符，删除
        if is_comment_line(line) and has_corrupt_chars(line, threshold=1):
            i += 1
            continue

        # 代码行但含损坏字符（行尾注释），只删除注释部分
        if has_corrupt_chars(line, threshold=1) and not is_comment_line(line):
            # 查找 // 行尾注释
            comment_idx = -1
            in_string = False
            string_char = None
            j = 0
            while j < len(line):
                c = line[j]
                if in_string:
                    if c == '\\':
                        j += 2
                        continue
                    if c == string_char:
                        in_string = False
                else:
                    if c == '"' or c == "'":
                        in_string = True
                        string_char = c
                    elif c == '/' and j + 1 < len(line) and line[j + 1] == '/':
                        comment_idx = j
                        break
                j += 1

            if comment_idx >= 0:
                code_part = line[:comment_idx].rstrip()
                comment_part = line[comment_idx:]
                if has_corrupt_chars(comment_part, threshold=1):
                    if code_part:
                        result.append(code_part)
                    i += 1
                    continue

            # 无法分离，保留整行
            result.append(line)
            i += 1
            continue

        result.append(line)
        i += 1

    # 清理连续空行（最多保留2个）
    cleaned = []
    empty_count = 0
    for line in result:
        if line.strip() == '':
            empty_count += 1
            if empty_count <= 2:
                cleaned.append(line)
        else:
            empty_count = 0
            cleaned.append(line)

    # 清理开头的空行
    while cleaned and cleaned[0].strip() == '':
        cleaned.pop(0)

    # 清理结尾的空行
    while cleaned and cleaned[-1].strip() == '':
        cleaned.pop()

    return '\n'.join(cleaned) + '\n'


def find_corrupt_files(root):
    """递归查找所有损坏文件"""
    corrupt = []
    for dirpath, dirnames, filenames in os.walk(root):
        skip_dirs = {'.git', 'target', 'node_modules', '.idea', '.vscode'}
        dirnames[:] = [d for d in dirnames if d not in skip_dirs]
        for fname in filenames:
            if not any(fname.endswith(ext) for ext in ('.java', '.js', '.ftl', '.html', '.xml', '.yml', '.yaml', '.properties', '.sql', '.json')):
                continue
            fpath = os.path.join(dirpath, fname)
            try:
                with open(fpath, 'r', encoding='utf-8') as f:
                    content = f.read()
                if has_corrupt_chars(content, threshold=2):
                    corrupt.append(fpath)
            except (UnicodeDecodeError, OSError):
                pass
    return corrupt


if __name__ == '__main__':
    src_dir = os.path.join(PROJECT_ROOT, 'src')
    corrupt_files = find_corrupt_files(src_dir)

    print(f'找到 {len(corrupt_files)} 个损坏文件')
    print('=' * 60)

    fixed = []
    warned = []

    for fpath in corrupt_files:
        rel = os.path.relpath(fpath, PROJECT_ROOT)
        try:
            with open(fpath, 'r', encoding='utf-8') as f:
                content = f.read()
        except (UnicodeDecodeError, OSError) as e:
            warned.append((rel, str(e)))
            print(f'[SKIP] {rel} - {e}')
            continue

        cleaned = remove_corrupt_comments(content)

        # 检查是否还有损坏字符
        remaining = sum(1 for c in cleaned if is_corrupt_char(c))
        if remaining > 0:
            warned.append((rel, f'清理后仍有 {remaining} 个损坏字符'))
            print(f'[WARN] {rel} - 清理后仍有 {remaining} 个损坏字符')
            with open(fpath, 'w', encoding='utf-8', newline='\n') as f:
                f.write(cleaned)
        else:
            with open(fpath, 'w', encoding='utf-8', newline='\n') as f:
                f.write(cleaned)
            fixed.append(rel)
            print(f'[OK]   {rel}')

    print('=' * 60)
    print(f'修复成功: {len(fixed)}')
    print(f'需关注: {len(warned)}')
    for f, m in warned:
        print(f'  - {f}: {m}')
