#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
安全删除所有 Java/JS 文件中的 @author 行和 mtons 版权块。
使用 Python 处理 UTF-8 文件，避免 PowerShell 编码问题。
"""

import os
import re

PROJECT_ROOT = r'c:\000\code\mblog'


def remove_author_and_copyright(content):
    """
    安全删除 @author 行和 mtons 版权块。
    """
    lines = content.split('\n')
    result = []
    i = 0
    skip_block = False

    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        # 检测 mtons 版权块开始（文件头部的 /* ... mtons ... */ 块）
        if i < 5 and stripped.startswith('/*') and not stripped.startswith('/**'):
            # 收集整个块
            block_lines = [line]
            j = i + 1
            while j < len(lines):
                block_lines.append(lines[j])
                if '*/' in lines[j]:
                    break
                j += 1

            block_text = '\n'.join(block_lines)
            # 检查是否是 mtons 版权块
            if 'mtons' in block_text.lower() or 'Mblog' in block_text or 'Mtons' in block_text:
                # 跳过整个版权块
                i = j + 1
                continue
            else:
                # 不是版权块，保留
                result.extend(block_lines)
                i = j + 1
                continue

        # 检测 @author 或 Created by 行（在 javadoc 注释中）
        if stripped.startswith('*') and ('@author' in stripped or 'Created by' in stripped or 'created by' in stripped):
            i += 1
            continue

        result.append(line)
        i += 1

    # 清理 javadoc 中连续的空 * 行
    text = '\n'.join(result)
    # 移除连续的空 * 行（最多保留一行）
    text = re.sub(r'(\s*\*\s*\n){2,}', ' *\n', text)

    # 清理连续空行（最多保留2个）
    lines = text.split('\n')
    cleaned = []
    empty_count = 0
    for line in lines:
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


def process_files(root):
    """处理所有 Java 和 JS 文件"""
    stats = {'total': 0, 'modified': 0, 'skipped': 0}

    for dirpath, dirnames, filenames in os.walk(root):
        # 跳过第三方目录
        skip_dirs = {'.git', 'target', 'node_modules', '.idea', '.vscode', 'vendors'}
        dirnames[:] = [d for d in dirnames if d not in skip_dirs]

        for fname in filenames:
            if not (fname.endswith('.java') or fname.endswith('.js')):
                continue

            fpath = os.path.join(dirpath, fname)
            stats['total'] += 1

            try:
                with open(fpath, 'r', encoding='utf-8') as f:
                    content = f.read()
            except (UnicodeDecodeError, OSError) as e:
                stats['skipped'] += 1
                continue

            # 检查是否含 @author 或 mtons 版权块
            has_author = '@author' in content or 'Created by' in content or 'created by' in content
            has_mtons = False

            # 检查文件头部是否有 mtons 版权块
            first_500 = content[:500]
            if 'mtons' in first_500.lower() or ('Mblog' in first_500 and 'Copyright' in first_500):
                has_mtons = True

            if not has_author and not has_mtons:
                continue

            cleaned = remove_author_and_copyright(content)

            if cleaned != content:
                with open(fpath, 'w', encoding='utf-8', newline='\n') as f:
                    f.write(cleaned)
                stats['modified'] += 1
                rel = os.path.relpath(fpath, PROJECT_ROOT)
                print(f'[OK] {rel}')

    return stats


if __name__ == '__main__':
    src_dir = os.path.join(PROJECT_ROOT, 'src')
    print('开始处理...')
    print('=' * 60)

    stats = process_files(src_dir)

    print('=' * 60)
    print(f'总文件数: {stats["total"]}')
    print(f'已修改: {stats["modified"]}')
    print(f'已跳过: {stats["skipped"]}')
