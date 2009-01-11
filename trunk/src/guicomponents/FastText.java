package guicomponents;

import processing.core.PApplet;
import processing.core.PImage;

/* FastText - fast text for processing.
original author: Glen Murphy (glenmurphy.com)

added features by toxi@toxi.co.uk:
 * vertical text
 * set clipping region
 * target image selection
 * compute pixel width of string
 * (word)wrap string to pixel width

 */

class FastText {
	int characters;
	int charWidth[] = new int[255];
	int charXOffset[] = new int[255];
	int charHeight;
	int chars[][];
	int lineHeight;
	int maxWidth;
	int col;
	int width, height;
	int wh;
	int[] pixels;
	int clipX1, clipY1, clipX2, clipY2, clipOffset1, clipOffset2;
	PImage img;
	PApplet parent;

	FastText(PApplet p, String fontFile) {
		parent = p;
		// set applet's pixel buffer as default target surface
		//setTarget(p.g);

		loadFont(fontFile);
		charHeight = img.height;
		lineHeight = charHeight;
		// find the characters' endpoints.
		int currWidth = 0;
		maxWidth = 0;
		for (int i = 0; i < img.width; i++) {
			currWidth++;
			if (img.pixels[i] == 0xffff0000) {
				charWidth[characters++] = currWidth;
				charXOffset[characters] = i;
				if (currWidth > maxWidth)
					maxWidth = currWidth;
				currWidth = 0;
			}
		}
		// create the character sprites.
		chars = new int[characters][maxWidth * charHeight];
		int indent = 0;
		for (int i = 0; i < characters; i++) {
			for (int u = 0; u < charWidth[i] * charHeight; u++) {
				chars[i][u] = img.pixels[indent + (u / charWidth[i]) * img.width + (u % charWidth[i])];
			}
			indent += charWidth[i];
		}
	}

	public void setLineHeight(int h) {
		lineHeight = h;
	}

	public void setColor(int c) {
		col = c;
	}

	public void loadFont(String name) {
		System.out.println("loading font: " + name);
		img = parent.loadImage(name);
	}

	// set target image object to be used
	public void setTarget(PImage t) {
		pixels = t.pixels;
		width = t.width;
		height = t.height;
		wh = width * height;
		setClipping(0, 0, width, height);
	}

	// set clipping rectangle (left,top,right,bottom)
	public void setClipping(int x1, int y1, int x2, int y2) {
		clipX1 = x1;
		clipY1 = y1;
		clipOffset1 = clamp(y1 * width + x1, 0, wh);
		clipX2 = x2 - 1;
		clipY2 = y2 - 1;
		clipOffset2 = clamp(clipY2 * width + clipX2, clipOffset1, wh);
	}

	void putcharH(int c, int x, int y) {
		y *= width;
		for (int i = 0; i < charWidth[c] * charHeight; i++) {
			int xpos = x + i % charWidth[c];
			int pos = xpos + y + (i / charWidth[c]) * width;
			if (chars[c][i] == 0xff000000 && xpos < clipX2 && pos >= clipOffset1 && pos < clipOffset2) {
				pixels[pos] = col;
			}
		}
	}

	public void writeH(String text, int x, int y, int wrap) {
		int indent = 0;
		for (int i = 0; i < text.length(); i++) {
			int c = (int)text.charAt(i);
			if (c < 32 || (wrap > 0 && indent > wrap)) {
				indent = 0;
				y += lineHeight;
			}
			if (c > 31) {
				putcharH(c - 32, x + indent, y);
				indent += charWidth[c - 32];
			}
		}
	}
	public void writeH(String text, int x, int y) {
		writeH(text, x, y, clipX2 - x);
	}

	void putcharV(int c, int x, int y) {
		int[] cData = chars[c];
		int xpos = x;
		int i = 0;
		y *= width;
		for (int xx = 0; xx < charHeight; xx++) {
			for (int yy = 0; yy < charWidth[c]; yy++) {
				int pos = y + xpos - yy * width;
				if (cData[i] == 0xff000000 && xpos < clipX2 && pos > clipOffset1 && pos < clipOffset2) {
					pixels[pos] = col;
				}
				i++;
			}
			xpos++;
		}
	}

	public void writeV(String text, int x, int y, int wrap) {
		int indent = 0;
		for (int i = 0; i < text.length(); i++) {
			int c = (int)text.charAt(i);
			if (c < 32 || (wrap > 0 && indent > wrap)) {
				indent = 0;
				x += lineHeight;
			}
			if (c > 31) {
				putcharV(c - 32, x, y - indent);
				indent += charWidth[c - 32];
			}
		}
	}

	public void writeV(String text, int x, int y) {
		writeV(text, x, y, -1);
	}

	public int getPixelWidth(String s) {
		int len = 0;
		int sl = s.length();
		for (int i = 0; i < sl; i++) {
			int c = (int)s.charAt(i);
			if (c >= 32)
				len += charWidth[c - 32];
		}
		return len;
	}

	// return word wrapped substring for pixel width
	// if wordWrap is "false", the max.number of chars is used
	public String getSubstringForWidth(String s, int w, boolean wordWrap) {
		int i, len = 0, lastWS = 0, sl = s.length();
		for (i = 0;(i < sl && len < w); i++) {
			int c = (int)s.charAt(i);
			if (c >= 32) {
				len += charWidth[c - 32];
				if (c <= 32)
					lastWS = i;
			}
		}
		if (wordWrap) {
			return s.substring(0, (lastWS > 0 && len >= w) ? lastWS : i);
		}
		else {
			return s.substring(0, i);
		}
	}

	private int clamp(int a, int b, int c) {
		return (a < b ? b : (a > c ? c : a));
	}
} 