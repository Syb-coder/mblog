"""
调试: 检查 Post.java 的实际字节, 确定损坏使用了什么编码
"""
import sys

filepath = r'c:\000\code\mblog\src\main\java\com\mtons\mblog\modules\entity\Post.java'

with open(filepath, 'rb') as f:
    raw = f.read()

# 找到损坏区域 (around "鏂囩珷" which is the corruption of "文章")
# 先找 UTF-8 编码的 "鏂囩珷"
garbled = "鏂囩珷".encode('utf-8')
pos = raw.find(garbled)
print(f"Found garbled '鏂囩珷' at byte position: {pos}")

if pos >= 0:
    # 显示前后字节
    start = max(0, pos - 20)
    end = min(len(raw), pos + len(garbled) + 20)
    print(f"Bytes around corruption (hex):")
    print(raw[start:end].hex(' '))
    print(f"Bytes around corruption (repr):")
    print(repr(raw[start:end]))

# 现在尝试反向: 这些字节读为UTF-8得到 "鏂囩珷"
# 然后用不同编码尝试还原
print("\n=== Trying different encodings to reverse ===")
test_str = "鏂囩珷"
print(f"Garbled string: {test_str}")
print(f"Original should be: 文章")

for enc in ['gbk', 'gb2312', 'gb18030', 'big5', 'cp950', 'cp936', 'latin1', 'iso-8859-1', 'windows-1252']:
    try:
        encoded = test_str.encode(enc)
        print(f"  {enc}: {encoded.hex(' ')}", end='')
        # 尝试解码为UTF-8
        try:
            decoded = encoded.decode('utf-8')
            print(f" -> UTF-8: {decoded}")
        except UnicodeDecodeError:
            print(f" -> not valid UTF-8")
    except (UnicodeEncodeError, LookupError) as e:
        print(f"  {enc}: cannot encode ({e})")

# 额外检查: 文章的UTF-8字节
print(f"\n'文章' in UTF-8: {'文章'.encode('utf-8').hex(' ')}")
print(f"'文章' in GBK: {'文章'.encode('gbk').hex(' ')}")

# 检查 鏂囩珷 的 Unicode 码点
for ch in "鏂囩珷":
    print(f"  '{ch}' = U+{ord(ch):04X}")
