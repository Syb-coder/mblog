#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复因 PowerShell Get-Content(GBK) -> WriteAllText(UTF-8) 导致的中文双重编码损坏。
原理：当前文件是用 GBK 读取的 UTF-8 字节，再以 UTF-8 写出的结果。
修复：读取当前 UTF-8 文本 -> 用 GB18030 编码回字节 -> 用 UTF-8 解码得到原文。
对于无法完整还原的文件（含 GB18030 4字节序列），使用 git 恢复后再重新处理。
"""

import os
import re
import subprocess
import sys

PROJECT_ROOT = r'c:\000\code\mblog'

# 常见损坏字符（对应中文常用字的 UTF-8 字节被误读为 GBK 的结果）
CORRUPT_PATTERNS = [
    '鏂囩珷', '鏁版嵁', '鐢ㄦ埛', '璇勮', '棰戦亾', '鏍囬', '鎽樿',
    '鐘舵', '鍒涘缓', '鏃堕', '鍒嗙', '鏍囩', '浣滃', '鍐呭',
    '閾炬', '绯荤', '閰嶇', '杩囨', '鏌ヨ', '鍒犻', '淇敼',
    '鎺ュ', '璇锋', '鍝嶅', '鐧诲', '娉ㄥ', '瀵嗙', '淇欢',
    '娑堟', '閫氱', '鍚姩', '鍒楄', '璇︽', '淇濆', '鎻愪',
    '楠璇', '寮傚', '閿欒', '澶辨', '鎴愬', '瀹屾', '寮€濮',
    '缁撴', '杩斿', '鐩稿', '鍏ㄩ', '閮ㄥ', '榛樿', '鍙傛',
    '绫诲', '鍚嶇', '鎻忚', '椤甸', '鍔犺', '鍒锋', '鍒嗛',
    '鎺掑', '缁熻', '鏀惰', '鐐硅', '鍥炲', '鍙戝', '涓婄',
    '涓嬬', '瀹℃', '閫氳', '鎷掔', '鑽夌', '姝ｅ', '绂佺',
    '鍋滅', '鏆傚', '缁х', '鍙栨', '搴旂', '澶勭', '鐩戠',
    '鏃ュ', '璁板', '瀛樺', '鏂囦', '鐩綍', '璺寰', '鏍煎',
    '瀹瑰', '澶у', '闀垮', '瀹藉', '楂樺', '浣嶇', '鍧愭',
    '棰滆', '瀛椾', '鑳屾', '杈规', '闃村', '鍦嗚', '閫忔',
    '鏃嬭', '缂╂', '绉诲', '鐐瑰', '鍙屾', '鎸夐', '杈撳',
    '澶嶅', '绮樿', '鍓垏', '鎾ら', '閲嶅', '棰勮', '瀹℃',
    '鎵归', '鑷姩', '鎵嬪', '瀹炴', '瀹氭', '鍛ㄦ', '绉掓',
    '灏忔', '澶╂', '鏈堟', '骞存', '骞翠', '鏃ユ', '鏃堕',
    '鏃跺', '鏈嶅', '涓氬', '鍟嗗', '鐞嗚', '瑙ｆ',
]


def find_corrupt_files(root):
    """递归查找含损坏字符的文件"""
    corrupt_files = []
    for dirpath, dirnames, filenames in os.walk(root):
        # 跳过 vendors 和 tinymce 等第三方目录
        dirnames[:] = [d for d in dirnames if d not in ('vendors', 'tinymce', '.git', 'target')]
        for fname in filenames:
            if not (fname.endswith('.java') or fname.endswith('.js') or fname.endswith('.ftl')):
                continue
            fpath = os.path.join(dirpath, fname)
            try:
                with open(fpath, 'r', encoding='utf-8') as f:
                    content = f.read()
                for pat in CORRUPT_PATTERNS[:20]:  # 只检查前20个常见模式
                    if pat in content:
                        corrupt_files.append(fpath)
                        break
            except (UnicodeDecodeError, OSError):
                pass
    return corrupt_files


def try_fix_file(fpath):
    """
    尝试反向编码修复。
    返回 (success, message)
    """
    try:
        with open(fpath, 'rb') as f:
            raw_bytes = f.read()
    except OSError as e:
        return False, f'读取失败: {e}'

    try:
        current_text = raw_bytes.decode('utf-8')
    except UnicodeDecodeError as e:
        return False, f'UTF-8 解码失败: {e}'

    # 检查是否含非ASCII字符
    if all(ord(c) < 128 for c in current_text):
        return False, '无非ASCII字符'

    # 反向转换：把当前文本用 GB18030 编码，再用 UTF-8 解码
    try:
        gbk_bytes = current_text.encode('gb18030')
    except UnicodeEncodeError as e:
        return False, f'GB18030 编码失败: {e}'

    try:
        fixed_text = gbk_bytes.decode('utf-8')
    except UnicodeDecodeError as e:
        return False, f'反向 UTF-8 解码失败: {e}'

    # 验证：修复后的文本不应再含损坏模式
    has_corrupt = any(p in fixed_text for p in CORRUPT_PATTERNS[:10])
    if has_corrupt:
        return False, '修复后仍含损坏字符'

    # 验证：修复后应含至少一些常见中文
    common_chinese = ['的', '是', '了', '在', '和', '有', '与', '为', '将', '对', '不', '及', '等', '将', '该', '其', '此', '以', '中', '一']
    chinese_count = sum(fixed_text.count(c) for c in common_chinese)
    if chinese_count == 0:
        return False, '修复后无中文，可能不是损坏文件'

    # 写回
    with open(fpath, 'w', encoding='utf-8', newline='') as f:
        f.write(fixed_text)

    return True, '修复成功'


def restore_from_git(fpath):
    """从 git HEAD 恢复文件"""
    rel_path = os.path.relpath(fpath, PROJECT_ROOT).replace('\\', '/')
    result = subprocess.run(
        ['git', 'checkout', 'HEAD', '--', rel_path],
        cwd=PROJECT_ROOT,
        capture_output=True,
        text=True
    )
    return result.returncode == 0, result.stderr or result.stdout


def has_prev_session_changes(fpath):
    """检查文件是否有前次会话的修改（featured/weight 等删除）"""
    rel_path = os.path.relpath(fpath, PROJECT_ROOT).replace('\\', '/')
    result = subprocess.run(
        ['git', 'show', f'HEAD:{rel_path}'],
        cwd=PROJECT_ROOT,
        capture_output=True,
    )
    if result.returncode != 0:
        return False
    try:
        original = result.stdout.decode('utf-8')
    except UnicodeDecodeError:
        try:
            original = result.stdout.decode('gbk')
        except UnicodeDecodeError:
            return False

    # 检查是否包含前次会话删除的关键字段
    prev_session_markers = ['featured', 'weight', 'messageService', 'MessageService',
                           'roleService', 'RoleService', 'mailService', 'MailService',
                           'oauth', 'OAuth', 'themeService', 'ThemeService']
    marker_count = sum(1 for m in prev_session_markers if m.lower() in original.lower())

    # 如果 HEAD 版本含 2 个以上已删除的标记，说明前次会话对该文件有修改
    return marker_count >= 2


if __name__ == '__main__':
    src_dir = os.path.join(PROJECT_ROOT, 'src')
    corrupt_files = find_corrupt_files(src_dir)

    print(f'找到 {len(corrupt_files)} 个含损坏字符的文件')
    print('=' * 60)

    fixed = []
    need_git_restore = []
    failed = []

    for fpath in corrupt_files:
        rel = os.path.relpath(fpath, PROJECT_ROOT)
        success, msg = try_fix_file(fpath)
        if success:
            fixed.append(rel)
            print(f'[OK]   {rel}')
        else:
            # 检查是否需要 git 恢复
            has_changes = has_prev_session_changes(fpath)
            if not has_changes:
                # 前次会话未修改该文件，直接 git restore
                ok, git_msg = restore_from_git(fpath)
                if ok:
                    fixed.append(rel + ' (git restored)')
                    print(f'[GIT]  {rel} - git checkout 恢复')
                else:
                    failed.append((rel, msg + ' + git失败: ' + git_msg))
                    print(f'[FAIL] {rel} - {msg} - git也失败')
            else:
                need_git_restore.append(rel)
                print(f'[SKIP] {rel} - 有前次会话修改，需手动处理: {msg}')

    print('=' * 60)
    print(f'修复成功: {len(fixed)}')
    print(f'需手动处理（有前次会话修改）: {len(need_git_restore)}')
    for f in need_git_restore:
        print(f'  - {f}')
    print(f'失败: {len(failed)}')
    for f, m in failed:
        print(f'  - {f}: {m}')
