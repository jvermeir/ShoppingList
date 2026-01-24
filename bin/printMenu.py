#!/usr/bin/env python3
import sys
import os
import subprocess

# Supported colors and their RTF codes
COLOR_TABLE = {
    'red': r'\\red255\\green0\\blue0;',
    'green': r'\\red0\\green128\\blue0;',
    'blue': r'\\red0\\green0\\blue255;',
    'black': r'\\red0\\green0\\blue0;'
}

MENU_DIR = os.path.expanduser('~/dropbox/1menu')

def usage():
    print('Usage: uv run printMenu.py <filename> [color]')
    print('  <filename>: file in ${HOME}/dev/prive/ShoppingListData/menus')
    print('  [color]: red, blue, green, black - default is black')
    sys.exit(1)

def main():
    if len(sys.argv) < 2:
        usage()
    filename = sys.argv[1]
    color = sys.argv[2].lower() if len(sys.argv) > 2 else 'black'
    filepath = os.path.join(MENU_DIR, filename)
    if not os.path.isfile(filepath):
        print(f'File not found: {filepath}')
        sys.exit(2)
    if color not in COLOR_TABLE:
        print(f'Unsupported color: {color}. Supported: red, blue, green, black.')
        sys.exit(3)
    # Map color names to RGB for reportlab
    COLOR_RGB = {
        'red': (1, 0, 0),
        'green': (0, 0.5, 0),
        'blue': (0, 0, 1),
        'black': (0, 0, 0)
    }
    pdf_color = COLOR_RGB[color]
    tmp_base = f'/tmp/printmenu.{os.getpid()}'
    tmp_pdf = tmp_base + '.pdf'

    try:
        from reportlab.lib.pagesizes import A4
        from reportlab.pdfgen import canvas
        from reportlab.lib.units import cm
        from reportlab.lib.colors import Color
        from reportlab.pdfbase import pdfmetrics
        from textwrap import wrap
    except ImportError:
        print('The reportlab library is required. Install it with: pip install reportlab')
        sys.exit(4)

    # Read menu lines
    with open(filepath) as menu:
        lines = [line.rstrip() for line in menu]

    # Create PDF
    c = canvas.Canvas(tmp_pdf, pagesize=A4)
    width, height = A4
    font_name = "Courier"
    font_size = 6
    margin_left = 1*cm
    margin_right = 1*cm
    margin_top = 1*cm
    margin_bottom = 1*cm
    max_width = width - margin_left - margin_right
    c.setFont(font_name, font_size)
    c.setFillColor(Color(*pdf_color))
    y = height - margin_top
    for line in lines:
        avg_char_width = pdfmetrics.stringWidth("M", font_name, font_size)
        max_chars = int(max_width // avg_char_width)
        if line.strip() == "":
            if y < margin_bottom:
                break  # Stop printing when first page is full
            y -= 0.4*cm  # Add blank line for empty input
            continue
        wrapped_lines = wrap(line, max_chars)
        for wline in wrapped_lines:
            if y < margin_bottom:
                break  # Stop printing when first page is full
            c.drawString(margin_left, y, wline)
            y -= 0.4*cm  # Reduced line spacing
        if y < margin_bottom:
            break  # Stop printing when first page is full
    c.save()
    subprocess.run(['open', tmp_pdf])

if __name__ == '__main__':
    main()
