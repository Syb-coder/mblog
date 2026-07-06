#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
分析本次会话修改的文件，分离「代码变更」与「注释/空白变更」。
仅关注非注释行的改动，用于判断功能是否一致。
"""
import subprocess
import sys
import re

FILES = [
    "src/main/java/com/mtons/mblog/modules/entity/Post.java",
    "src/main/java/com/mtons/mblog/modules/data/PostVO.java",
    "src/main/java/com/mtons/mblog/modules/data/CommentVO.java",
    "src/main/java/com/mtons/mblog/modules/data/UserVO.java",
    "src/main/java/com/mtons/mblog/modules/data/AccountProfile.java",
    "src/main/java/com/mtons/mblog/web/controller/site/auth/RegisterController.java",
    "src/main/java/com/mtons/mblog/web/controller/site/user/SettingsController.java",
    "src/main/java/com/mtons/mblog/web/controller/site/comment/CommentController.java",
    "src/main/java/com/mtons/mblog/web/controller/site/user/UsersController.java",
]

def is_comment_or_blank(line):
    """判断一行是否为注释、空白或 import 行（import 视为非功能性代码）"""
    s = line.strip()
    if not s:
        return True
    if s.startswith("//") or s.startswith("*") or s.startswith("/*"):
        return True
    if s.startswith("/**") or s.startswith("*/"):
        return True
    return False

def is_import(line):
    s = line.strip()
    return s.startswith("import ") or s.startswith("package ")

for f in FILES:
    try:
        out = subprocess.check_output(
            ["git", "diff", "HEAD", "--", f],
            cwd=r"c:\000\code\mblog",
            stderr=subprocess.DEVNULL,
            text=True,
            encoding="utf-8",
            errors="replace"
        )
    except Exception as e:
        print(f"[ERR] {f}: {e}")
        continue

    if not out:
        print(f"\n=== {f} === (no changes)")
        continue

    print(f"\n=== {f} ===")
    # 提取 +/- 开头的行（排除 +++/--- 文件头）
    code_changes = []
    for line in out.split("\n"):
        if not line:
            continue
        if line.startswith("+++") or line.startswith("---"):
            continue
        if not (line.startswith("+") or line.startswith("-")):
            continue
        # 取实际内容
        content = line[1:].strip()
        # 跳过注释、空白、import、package
        if is_comment_or_blank(content):
            continue
        if is_import(content):
            continue
        # 跳过 diff 头
        if line.startswith("@@"):
            continue
        code_changes.append(line)

    if not code_changes:
        print("  [仅注释/空白/import 变更，无功能性代码变更]")
    else:
        print(f"  [代码变更 {len(code_changes)} 行]:")
        for c in code_changes:
            print(f"    {c}")
