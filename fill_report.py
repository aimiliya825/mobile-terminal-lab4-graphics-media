# -*- coding: utf-8 -*-
"""把实验四的代码、截图和思考题答案填入答题卡模板，保留模板原有版式。

用法：python fill_report.py
输出：report\\实验四、图形图像和多媒体技术-202402323-胡俊峰.docx
"""
import os
import sys

from docx import Document
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Pt
from docx.text.paragraph import Paragraph

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import content_lab4 as C  # noqa: E402

BASE = r"E:\deepseek_project\mobile-lab4"
DOCX_IN = os.path.join(BASE, "docs", "答题卡.docx")
DOCX_OUT = os.path.join(BASE, "report", "实验四、图形图像和多媒体技术-202402323-胡俊峰.docx")
IMG = os.path.join(BASE, "screenshots", "crops")

BODY_FONT = "宋体"
ASCII_FONT = "Times New Roman"
CODE_ASCII = "Consolas"
CODE_EAST = "宋体"

doc = Document(DOCX_IN)
body_parent = doc.paragraphs[0]._parent


# ---------------------------------------------------------------- helpers
def set_run_font(run, ascii_name, east_name, size_pt, bold=False):
    run.font.name = ascii_name
    run.font.size = Pt(size_pt)
    run.font.bold = bold
    rpr = run._element.get_or_add_rPr()
    rfonts = rpr.find(qn("w:rFonts"))
    if rfonts is None:
        rfonts = OxmlElement("w:rFonts")
        rpr.insert(0, rfonts)
    rfonts.set(qn("w:ascii"), ascii_name)
    rfonts.set(qn("w:hAnsi"), ascii_name)
    rfonts.set(qn("w:eastAsia"), east_name)
    rfonts.set(qn("w:cs"), ascii_name)


def delete_el(el):
    parent = el.getparent()
    if parent is not None:
        parent.remove(el)


def clean_blank_after(p):
    """删除锚点段落之后连续的空白段落（含仅有分页符的空白段落）。"""
    el = p._p.getnext()
    while el is not None:
        if el.tag == qn("w:p"):
            text = "".join(t.text or "" for t in el.iter(qn("w:t")))
            if text.strip() == "":
                nxt = el.getnext()
                delete_el(el)
                el = nxt
                continue
        break


def new_para_after(after_el, text="", ascii_font=ASCII_FONT, east_font=BODY_FONT,
                   size=10.5, bold=False, indent=21.0, line=1.3,
                   before=0.0, after=0.0, align=None):
    p_el = OxmlElement("w:p")
    after_el.addnext(p_el)
    p = Paragraph(p_el, body_parent)
    pf = p.paragraph_format
    pf.first_line_indent = Pt(indent) if indent else Pt(0)
    pf.line_spacing = line
    pf.space_before = Pt(before)
    pf.space_after = Pt(after)
    if align is not None:
        pf.alignment = align
    if text:
        run = p.add_run(text)
        set_run_font(run, ascii_font, east_font, size, bold)
    return p


def add_text_block(after_el, lines, **kw):
    cur = after_el
    for line in lines:
        p = new_para_after(cur, line, **kw)
        cur = p._p
    return cur


def add_code_block(after_el, lines):
    cur = after_el
    n = len(lines)
    for i, line in enumerate(lines):
        p_el = OxmlElement("w:p")
        cur.addnext(p_el)
        p = Paragraph(p_el, body_parent)
        pPr = p_el.get_or_add_pPr()
        shd = OxmlElement("w:shd")
        shd.set(qn("w:val"), "clear")
        shd.set(qn("w:color"), "auto")
        shd.set(qn("w:fill"), "F2F5F9")
        pPr.append(shd)
        snap = OxmlElement("w:snapToGrid")
        snap.set(qn("w:val"), "0")
        pPr.append(snap)
        spacing = OxmlElement("w:spacing")
        spacing.set(qn("w:before"), "0")
        spacing.set(qn("w:after"), "0")
        spacing.set(qn("w:line"), "230")
        spacing.set(qn("w:lineRule"), "auto")
        pPr.append(spacing)
        ind = OxmlElement("w:ind")
        ind.set(qn("w:firstLine"), "85")
        ind.set(qn("w:left"), "113")
        pPr.append(ind)
        run = p.add_run(line if line else " ")
        set_run_font(run, CODE_ASCII, CODE_EAST, 7.5, False)
        cur = p_el
    return cur


def set_tbl_borders_none(tbl_pr):
    borders = OxmlElement("w:tblBorders")
    for edge in ("top", "left", "bottom", "right", "insideH", "insideV"):
        el = OxmlElement("w:" + edge)
        el.set(qn("w:val"), "nil")
        borders.append(el)
    tbl_pr.append(borders)


def add_image_row(after_el, items):
    """items: list of (filename, width_cm, caption)。生成无边框表格并居中排列。"""
    cols = len(items)
    table = doc.add_table(rows=1, cols=cols)
    tbl_el = table._tbl
    after_el.addnext(tbl_el)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    tbl_pr = tbl_el.tblPr
    set_tbl_borders_none(tbl_pr)
    grid_cols = tbl_el.tblGrid.findall(qn("w:gridCol"))
    cell_width = int(9200 / cols)
    for gc in grid_cols:
        gc.set(qn("w:w"), str(cell_width))
    row = table.rows[0]
    tr_pr = row._tr.get_or_add_trPr()
    tr_pr.append(OxmlElement("w:cantSplit"))
    for idx, (fname, width_cm, caption) in enumerate(items):
        cell = row.cells[idx]
        cell.width = Pt(cell_width / 20.0)
        p_img = cell.paragraphs[0]
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(2)
        p_img.paragraph_format.space_after = Pt(0)
        p_img.paragraph_format.line_spacing = 1.0
        run = p_img.add_run()
        run.add_picture(os.path.join(IMG, fname), width=Cm(width_cm))
        p_cap = cell.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_before = Pt(1)
        p_cap.paragraph_format.space_after = Pt(2)
        p_cap.paragraph_format.line_spacing = 1.0
        for j, line in enumerate(caption.split("\n")):
            if j > 0:
                run2 = p_cap.add_run()
                run2.add_break()
            r = p_cap.add_run(line)
            set_run_font(r, BODY_FONT, BODY_FONT, 8.5, False)
    return tbl_el


def spacer(after_el, size=6):
    p_el = OxmlElement("w:p")
    after_el.addnext(p_el)
    p = Paragraph(p_el, body_parent)
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(0)
    p.paragraph_format.line_spacing = 1.0
    run = p.add_run(" ")
    run.font.size = Pt(size)
    return p_el


def find_para(prefix):
    for p in doc.paragraphs:
        if p.text.strip().startswith(prefix):
            return p
    raise SystemExit("找不到段落：%r" % prefix)


# ---------------------------------------------------------------- 0. 封面压缩
# 模板封面在 LibreOffice 下可能溢出一行（审核人/日期跑到第 2 页），压小“实验指导书”与“制定人”之间的空行
COVER_TIGHT_IDX = [15, 17, 18, 19, 20]
for idx in COVER_TIGHT_IDX:
    if idx >= len(doc.paragraphs):
        continue
    p = doc.paragraphs[idx]
    if p.text.strip() != "":
        continue
    pf = p.paragraph_format
    pf.space_before = Pt(0)
    pf.space_after = Pt(0)
    pf.line_spacing = Pt(6)
    for r in p.runs:
        r.font.size = Pt(6)

# ---------------------------------------------------------------- 1. 页眉信息
for sec in doc.sections:
    if sec.header.is_linked_to_previous:
        continue
    for hp in sec.header.paragraphs:
        if "姓名" in hp.text and "日期" in hp.text:
            runs = hp.runs
            if len(runs) >= 14:
                runs[1].text = " 胡俊峰          "
                runs[5].text = " 计算机249        "
                runs[9].text = " 202402323       "
                runs[13].text = " " + C.DATE + "      "

# ---------------------------------------------------------------- 2. 程序1
anchor = find_para("（1）写出自定义View的onDraw方法")
clean_blank_after(anchor)
cur = new_para_after(anchor._p, C.P1_NOTE, indent=21.0, line=1.3)._p
cur = add_code_block(cur, C.P1_CODE.split("\n"))
anchor = find_para("（2）粘贴绘图结果截图")
clean_blank_after(anchor)
cur = add_image_row(anchor._p, C.P1_IMAGES)
cur = spacer(cur)

# ---------------------------------------------------------------- 3. 程序2
anchor = find_para("（1）写出利用Matrix")
clean_blank_after(anchor)
cur = new_para_after(anchor._p, C.P2_NOTE, indent=21.0, line=1.3)._p
cur = add_code_block(cur, C.P2_CODE.split("\n"))
anchor = find_para("（2）粘贴原图与各类变换")
clean_blank_after(anchor)
cur = add_image_row(anchor._p, C.P2_IMAGES)
cur = spacer(cur)

# ---------------------------------------------------------------- 4. 程序3
anchor = find_para("（1）写出animation-list")
clean_blank_after(anchor)
cur = new_para_after(anchor._p, C.P3_NOTE, indent=21.0, line=1.3)._p
cur = add_code_block(cur, C.P3_CODE.split("\n"))
anchor = find_para("（2）粘贴相册播放")
clean_blank_after(anchor)
cur = add_image_row(anchor._p, C.P3_IMAGES)
cur = spacer(cur)

# ---------------------------------------------------------------- 5. 程序4
anchor = find_para("（1）写出透明度")
clean_blank_after(anchor)
cur = new_para_after(anchor._p, C.P4_NOTE, indent=21.0, line=1.3)._p
cur = add_code_block(cur, C.P4_CODE.split("\n"))
anchor = find_para("（2）粘贴动画开始")
clean_blank_after(anchor)
cur = add_image_row(anchor._p, C.P4_IMAGES)
cur = spacer(cur)
cur = add_image_row(cur, C.P4_IMAGES_EXTRA)
cur = spacer(cur)

# ---------------------------------------------------------------- 6. 程序5
anchor = find_para("（1）写出SoundPool")
clean_blank_after(anchor)
cur = new_para_after(anchor._p, C.P5_NOTE, indent=21.0, line=1.3)._p
cur = add_code_block(cur, C.P5_CODE.split("\n"))
anchor = find_para("（2）粘贴音效播放")
clean_blank_after(anchor)
cur = add_image_row(anchor._p, C.P5_IMAGES)
cur = spacer(cur)

# ---------------------------------------------------------------- 7. 程序6
anchor = find_para("（1）写出MediaPlayer加载音频")
clean_blank_after(anchor)
cur = new_para_after(anchor._p, C.P6_NOTE, indent=21.0, line=1.3)._p
cur = add_code_block(cur, C.P6_CODE.split("\n"))
anchor = find_para("（2）粘贴播放、暂停和停止状态")
clean_blank_after(anchor)
cur = add_image_row(anchor._p, C.P6_IMAGES)
cur = spacer(cur)

# ---------------------------------------------------------------- 8. 程序7
anchor = find_para("（1）写出视频源设置")
clean_blank_after(anchor)
cur = new_para_after(anchor._p, C.P7_NOTE, indent=21.0, line=1.3)._p
cur = add_code_block(cur, C.P7_CODE.split("\n"))
anchor = find_para("（2）粘贴视频播放与暂停")
clean_blank_after(anchor)
cur = add_image_row(anchor._p, C.P7_IMAGES)
cur = spacer(cur)

# ---------------------------------------------------------------- 9. 思考题
Q_ANCHORS = [
    ("1.如何设置画笔的颜色", C.Q1),
    ("2. 请描述Matrix", C.Q2),
    ("3. 如何制作一个帧动画", C.Q3),
    ("4.如何制作一个补间动画", C.Q4),
    ("5.使用SoundPool", C.Q5),
    ("6. 如何使用MediaPlayer播放音频", C.Q6),
    ("7. 如何使用VideoView", C.Q7),
    ("8. 如何使用MediaPlayer+SurfaceView", C.Q8),
]
for prefix, answer in Q_ANCHORS:
    anchor = find_para(prefix)
    clean_blank_after(anchor)
    cur = add_text_block(anchor._p, answer, indent=21.0, line=1.35)
    spacer(cur)

# ---------------------------------------------------------------- 10. 实验小结
for prefix, answer in [
    ("1 编程遇到的问题及解决方案", C.S1),
    ("2 实验收获及体会", C.S2),
    ("3 不足之处及下一步需改进的地方", C.S3),
]:
    anchor = find_para(prefix)
    clean_blank_after(anchor)
    cur = add_text_block(anchor._p, answer, indent=21.0, line=1.35)
    spacer(cur)

# ---------------------------------------------------------------- 11. 去掉模板中重复的“2.2实验小结”行
dups = [p for p in doc.paragraphs if p.text.strip() == "2.2实验小结"]
if len(dups) > 1:
    def is_bold(p):
        return any(r.font.bold for r in p.runs if r.text.strip())
    keep = next((p for p in dups if is_bold(p)), dups[0])
    for p in dups:
        if p is not keep:
            delete_el(p._p)
            break

os.makedirs(os.path.dirname(DOCX_OUT), exist_ok=True)
doc.save(DOCX_OUT)
print("saved", DOCX_OUT)
