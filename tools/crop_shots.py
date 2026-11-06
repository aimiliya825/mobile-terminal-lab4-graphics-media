# -*- coding: utf-8 -*-
"""裁剪实验四运行截图：去掉状态栏与底部导航条，并裁掉多余空白。

为避免模拟器屏幕圆角/手势条边缘的深色像素干扰自动裁剪，
自动搜索时忽略左右各 60px 的边缘区域，并优先使用给定的目标底边。
"""
import os

from PIL import Image

SRC = r"E:\deepseek_project\mobile-lab4\screenshots"
DST = os.path.join(SRC, "crops")
os.makedirs(DST, exist_ok=True)

STATUS_BAR = 136      # 状态栏高度
MARGIN = 20           # 内容下方保留空白

# 名称: 期望底边（None 表示自动搜索到屏幕底部导航条上方）
BOTTOMS = {
    "s_main": 1430,
    "s_paint": 2330,
    "s_matrix": 2200,
    "s_frame1": 1600,
    "s_frame2": 1600,
    "s_tween1": 1680,
    "s_tween2": 1680,
    "s_tween3": 1680,
    "s_sound": 1700,
    "s_audio_play": 1000,
    "s_audio_pause": 1000,
    "s_audio_stop": 1000,
    "s_video_play": 1720,
    "s_video_pause": 1720,
}


def row_has_content(px, y, w):
    """忽略左右边缘 60px，判断该行是否有非背景色内容。"""
    for x in range(60, w - 60, 2):
        r, g, b = px[x, y][:3]
        if r < 240 or g < 240 or b < 240:
            return True
    return False


for name, expect_bottom in BOTTOMS.items():
    src = os.path.join(SRC, name + ".png")
    im = Image.open(src).convert("RGB")
    w, h = im.size
    px = im.load()
    limit = expect_bottom if expect_bottom else h - 63
    last = STATUS_BAR
    for y in range(limit - 1, STATUS_BAR, -1):
        if row_has_content(px, y, w):
            last = y
            break
    bottom = min(limit, last + MARGIN)
    out = im.crop((0, STATUS_BAR, w, bottom))
    path = os.path.join(DST, name + ".png")
    out.save(path, optimize=True)
    print("%-14s %s -> %s" % (name, im.size, out.size))
print("DONE")
