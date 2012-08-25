package guicomponents;

import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class Location {
	public int charInText, charOnLine, lineNo;
	public float cursorX, cursorY, cursorHeight; // do not take scrolling into account
	public boolean valid;

	public Location(){
		charInText = charOnLine = lineNo = -1;
		cursorX = cursorY = cursorHeight = 0;
		valid = false;
	}
	
	public boolean calculateFromCharNo(int charNumber, int textLength, LinkedList<TextLayout> lines){
		valid = false;
		if(charNumber < 0 || charNumber > textLength){
			charInText = charOnLine = lineNo = -1;
			cursorX = cursorY = cursorHeight = 0;
		} 
		else {
			valid = true;
			charInText = charNumber;
			lineNo = -1;
			cursorY = 0;
			for(TextLayout line : lines){
				if(charNumber > line.getCharacterCount()){
					charNumber -= line.getCharacterCount();
					charOnLine = charNumber;
					cursorY += getHeight(line);
					lineNo ++;
				}
				else {
					cursorHeight = line.getAscent() + line.getDescent();
					cursorY += cursorHeight;
				}
			}
		}
		return valid;
	}

	
	// px and py represent the value over the text so must be adjusted for scrolling
	public boolean calculateFromXY(float px, float py, float breakWidth, LinkedList<TextLayout> lines){
		valid = false;
		float yLine = 0, layoutHeight;
		if(py >= 0 && px >= 0 || px < breakWidth) {
			lineNo = 0;
			TextLayout line = null;
			do {
				line = lines.get(lineNo);
				layoutHeight = getHeight(line);
				if(py < layoutHeight)
					break;
				else {
					charInText += line.getCharacterCount();
					yLine += layoutHeight;
					py -= layoutHeight;
					lineNo++;
				}
			} while(lineNo < lines.size());
			if(lineNo < lines.size()){
				TextHitInfo thi = line.hitTestChar(px,py);
				charOnLine = thi.getCharIndex();
				if(!thi.isLeadingEdge())
					charOnLine++;
				charInText += charOnLine;
				Point2D caretPos = new Point2D.Float();
				line.hitToPoint(thi, caretPos);
				cursorX = (float) caretPos.getX();
				//cursorY = (float) line.getBaseline() + yLine;
				cursorY = getHeight(line) + yLine;
				//cursorY += cursorHeight;
				cursorHeight = line.getAscent() + line.getDescent();
				valid = true;
			}
		} 
		if(!valid) {
			charInText = charOnLine = lineNo = -1;
			cursorX = cursorY = cursorHeight = 0;
		}
		return valid;
	}

	public float getHeight(TextLayout layout){
		return layout.getAscent() +layout.getDescent() + layout.getLeading();
	}

	public String toString(){
		StringBuilder sb = new StringBuilder("Curser in text " + charInText);
		sb.append("@ Line " + lineNo);
		sb.append("  character " + charOnLine);
		sb.append("  Cursor [" + cursorX + ", " + cursorY + "]");
		return sb.toString();
	}
}
