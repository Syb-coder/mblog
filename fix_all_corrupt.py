#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
全面扫描并修复所有因 PowerShell GBK->UTF-8 导致编码损坏的文件。
策略：
1. 扫描所有 .java, .js, .ftl 文件，检测损坏模式
2. 对每个损坏文件，尝试反向编码修复
3. 对修复失败的文件，从 git HEAD 恢复，再重新应用作者信息删除
"""

import os
import re
import subprocess
import sys

PROJECT_ROOT = r'c:\000\code\mblog'

# 损坏字符特征：UTF-8 中文三字节被 GBK 两两组合后产生的常见乱码首字节
# 更可靠的方法：检查是否包含 CJK 统一表意文字但语义不通
# 这里用一组常见的损坏模式字符作为初筛
CORRUPT_CHARS = set('鏂囩珷鏁版嵁鐢ㄦ埛璇勮棰戦亾鏍囬鎽樿鐘舵€佸垱寤洪棿鎺ュ彛鍒嗙被鏍囩浣滆€呭唴瀹归摼鎺ョ郴缁熼厤缃繃婊ゆ煡璇㈠垹闄や慨鏀规帴鍙ｉ偖浠舵秷鎭€氱煡鍚姩鍒楄〃璇︽儏淇濆瓨鎻愪氦楠岃瘉寮傚父閿欒澶辫触鎴愬姛瀹屾垚寮€濮嬬粨鏉熻繑鍥炵浉鍏抽儴鍒嗛粯璁ゅ弬鏁扮被鍨嬪悕绉版弿杩伴〉闈㈠姞蹇垽鏂垎閴存帓搴忕粺璁″垎浜敹钘忕偣璧斿洖澶嶅彂甯冧笂绾夸笅绾垮鏍搁€氳繃鎷掔粷鑽夌ǹ姝ｅ父绂佺敤鍚敤鍋滄鏆傚仠缁х画鍙栨秷纭畾搴旂敤澶勭悊鐩戞帶鏃ュ織璁板綍瀛樺偍鏂囦欢鐩綍璺緞鏍煎紡瀹瑰櫒澶у皬闀垮害瀹藉害楂樺害浣嶇疆鍧愭爣棰滆壊瀛椾綋鑳屾櫙杈规闃村奖鍦嗚閫忔槑鏃嬭浆缂╂斁绉诲姩鐐瑰嚮鍙屽嚮鎸夐挳杈撳叆杈撳嚭澶嶅埗绮樿创鍓垏鎾斁閲嶆柊鍏抽棴鎵撳紑淇濆瓨鍙︾敤棰勮瀹℃牳閫氳繃鎷掔粷杩斿洖鎵归噺鑷姩鎵嬪姩瀹炴椂瀹氭椂鍛ㄦ湡绉掑埢灏忔椂澶╂湀骞存棩鏈熸椂闂存椂鍒�')

# 常见中文（用于验证修复成功）
COMMON_CHINESE = set('的一是不了在有和人这中大上个来国到说们为子你他她它就而要去也出得那对会学着可下多天能好过年些自与没家只从如都又并还等当自己作所二已及表样事最说力里定三此其本进学下将法高长实所')


def has_corrupt_chars(text):
    """检测文本是否含损坏字符"""
    count = sum(1 for c in text if c in CORRUPT_CHARS)
    return count >= 2  # 至少2个损坏字符才认为是损坏文件


def find_corrupt_files(root):
    """递归查找所有损坏文件"""
    corrupt = []
    for dirpath, dirnames, filenames in os.walk(root):
        # 跳过第三方目录
        skip_dirs = {'vendors', 'tinymce', '.git', 'target', 'node_modules', '.idea', '.vscode', 'dist'}
        # 但 dist/js/modules 下的文件需要检查
        dirnames[:] = [d for d in dirnames if d not in skip_dirs or 'dist' in dirpath]
        for fname in filenames:
            if not any(fname.endswith(ext) for ext in ('.java', '.js', '.ftl', '.html', '.xml', '.yml', '.yaml', '.properties', '.sql', '.json')):
                continue
            fpath = os.path.join(dirpath, fname)
            try:
                with open(fpath, 'r', encoding='utf-8') as f:
                    content = f.read()
                if has_corrupt_chars(content):
                    corrupt.append(fpath)
            except (UnicodeDecodeError, OSError):
                pass
    return corrupt


def try_reverse_fix(fpath):
    """
    尝试反向编码修复：UTF-8(当前) -> GB18030(编码回字节) -> UTF-8(解码原文)
    返回 (success, fixed_text_or_none, message)
    """
    try:
        with open(fpath, 'rb') as f:
            raw = f.read()
    except OSError as e:
        return False, None, f'读取失败: {e}'

    try:
        current = raw.decode('utf-8')
    except UnicodeDecodeError as e:
        return False, None, f'UTF-8解码失败: {e}'

    # 无非ASCII字符，无需修复
    if all(ord(c) < 128 for c in current):
        return False, None, '无非ASCII字符'

    # 反向编码
    try:
        gbk_bytes = current.encode('gb18030')
    except UnicodeEncodeError as e:
        return False, None, f'GB18030编码失败: {e}'

    # 用严格 UTF-8 解码
    try:
        fixed = gbk_bytes.decode('utf-8')
    except UnicodeDecodeError as e:
        return False, None, f'反向UTF-8解码失败: {e}'

    # 验证1：修复后不应再含大量损坏字符
    corrupt_count = sum(1 for c in fixed if c in CORRUPT_CHARS)
    if corrupt_count > 5:
        return False, None, f'修复后仍有{corrupt_count}个损坏字符'

    # 验证2：修复后应包含常见中文
    chinese_count = sum(1 for c in fixed if c in COMMON_CHINESE)
    if chinese_count < 3:
        return False, None, f'修复后中文太少({chinese_count})'

    return True, fixed, '修复成功'


def git_show_head(rel_path):
    """从 git HEAD 获取文件内容"""
    result = subprocess.run(
        ['git', 'show', f'HEAD:{rel_path}'],
        cwd=PROJECT_ROOT,
        capture_output=True,
    )
    if result.returncode != 0:
        return None
    # 尝试多种编码
    for enc in ('utf-8', 'gbk', 'gb18030', 'latin-1'):
        try:
            return result.stdout.decode(enc)
        except UnicodeDecodeError:
            continue
    return None


def remove_author_and_copyright(text):
    """
    安全地移除 @author 行和 mtons 版权块。
    返回处理后的文本。
    """
    lines = text.split('\n')
    result_lines = []
    in_copyright_block = False
    block_buffer = []

    i = 0
    while i < len(lines):
        line = lines[i]

        # 检测版权块开始（文件头部的 /* ... mtons ... */ 块）
        if i < 3 and line.strip().startswith('/*') and not in_copyright_block:
            # 收集到块结束
            block_lines = [line]
            j = i + 1
            found_mtons = 'mtons' in line.lower()
            while j < len(lines) and '*/' not in block_lines[-1]:
                block_lines.append(lines[j])
                if 'mtons' in lines[j].lower():
                    found_mtons = True
                j += 1
            if found_mtons:
                # 跳过整个版权块
                i = j
                continue

        # 检测 @author 或 Created by 行
        stripped = line.strip()
        if stripped.startswith('*') and ('@author' in stripped or 'Created by' in stripped or 'created by' in stripped):
            i += 1
            continue

        # 检测空行前的多余空行（清理删除@author后留下的空行）
        result_lines.append(line)
        i += 1

    # 清理连续的空 * 行（删除 @author 行后可能留下的空 javadoc 行）
    cleaned = '\n'.join(result_lines)
    # 移除 javadoc 中连续的空 * 行（最多保留一行空 * 行）
    cleaned = re.sub(r'(\s*\*\s*\n){2,}', ' *\n', cleaned)
    # 清理开头的空 * 行（如 /** 后直接跟 * @author 被删除后的情况）
    # 不移除，因为这是正常的 javadoc 结构

    return cleaned


if __name__ == '__main__':
    src_dir = os.path.join(PROJECT_ROOT, 'src')
    corrupt_files = find_corrupt_files(src_dir)

    print(f'找到 {len(corrupt_files)} 个损坏文件')
    print('=' * 60)

    reverse_fixed = []
    git_restored = []
    failed = []

    for fpath in corrupt_files:
        rel = os.path.relpath(fpath, PROJECT_ROOT)
        success, fixed_text, msg = try_reverse_fix(fpath)

        if success:
            # 写回修复后的内容
            with open(fpath, 'w', encoding='utf-8', newline='\n') as f:
                f.write(fixed_text)
            reverse_fixed.append(rel)
            print(f'[FIX]  {rel}')
        else:
            # 尝试从 git HEAD 恢复
            head_content = git_show_head(rel)
            if head_content is not None:
                # 从 git 恢复后，再移除作者信息
                cleaned = remove_author_and_copyright(head_content)
                with open(fpath, 'w', encoding='utf-8', newline='\n') as f:
                    f.write(cleaned)
                git_restored.append(rel)
                print(f'[GIT]  {rel} - {msg}')
            else:
                failed.append((rel, msg))
                print(f'[FAIL] {rel} - {msg}')

    print('=' * 60)
    print(f'反向编码修复: {len(reverse_fixed)}')
    print(f'Git恢复+清理: {len(git_restored)}')
    print(f'失败: {len(failed)}')
    for f, m in failed:
        print(f'  - {f}: {m}')
