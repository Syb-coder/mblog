"""
定位 Post.java 中阻止编码修复的具体位置
"""
filepath = r'c:\000\code\mblog\src\main\java\com\mtons\mblog\modules\entity\Post.java'

with open(filepath, 'rb') as f:
    raw = f.read()

content = raw.decode('utf-8')
gbk_bytes = content.encode('gb18030')

# 尝试严格UTF-8解码, 定位失败位置
try:
    fixed = gbk_bytes.decode('utf-8')
    print("Decoding succeeded! File should be fixable.")
except UnicodeDecodeError as e:
    print(f"UTF-8 decode failed at byte position {e.start}: {e}")
    # 显示出错位置的字节
    start = max(0, e.start - 30)
    end = min(len(gbk_bytes), e.start + 30)
    print(f"Bytes around error (hex): {gbk_bytes[start:end].hex(' ')}")
    print(f"Problematic bytes: {gbk_bytes[e.start:e.end].hex(' ')}")

    # 反向定位: 这些字节对应原始文件的哪个位置
    # gbk_bytes 是 content.encode('gb18030'), 所以我们可以找到对应的字符
    # 先解码 gbk_bytes 到出错位置为止
    prefix = gbk_bytes[:e.start].decode('gb18030')
    char_pos = len(prefix)
    print(f"Corresponding char position in garbled content: {char_pos}")
    if char_pos < len(content):
        problem_char = content[char_pos]
        print(f"Problematic garbled char: '{problem_char}' (U+{ord(problem_char):04X})")
        # 这个字符的 GB18030 编码是什么
        print(f"Its GB18030 encoding: {problem_char.encode('gb18030').hex(' ')}")
        # 它在 GBK 中对应什么字节
        try:
            gbk_enc = problem_char.encode('gbk')
            print(f"Its GBK encoding: {gbk_enc.hex(' ')}")
        except UnicodeEncodeError:
            print("Its GBK encoding: NOT IN GBK (4-byte GB18030)")

    # 检查是否是正常的UTF-8中文(未被损坏的内容)
    print(f"\nContext around problem (garbled): {repr(content[max(0,char_pos-20):char_pos+20])}")
