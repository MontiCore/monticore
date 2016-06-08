package de.monticore.visualoutline.runtime.gef;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.AbstractFlowBorder;
import org.eclipse.draw2d.text.FlowFigure;
import org.eclipse.swt.graphics.Color;

/**
 * Make text from TextFlow elements be underlined, overlined or striked out.
 * 
 * GEF itself does not allow text decorations that are not part of the system font.
 * To add underline, overline and strike out text decorations,
 * the required lines are drawn directly on the graphics of that TextFlow element.
 * 
 * @author Dennis Birkholz
 */
final public class TextLineDecoration extends AbstractFlowBorder {
	final public static int UNDERLINE = 1;
	final public static int OVERLINE = 2;
	final public static int STRIKE = 4;
	
	private int borderType;
	private int borderWidth;
	private Color borderColor;
	
	/**
	 * Create a new TextLineDecoration
	 * 
	 * @param type Combination of UNDERLINE, OVERLINE and STRIKE
	 * @param color Color to draw lines in
	 * @param width Width of the line
	 */
	public TextLineDecoration(int type, Color color, int width) {
		this.borderType = type;
		this.borderColor = color;
		this.borderWidth = width;
	}
	
	public void addDecoration(int type) {
		if ((this.borderType & type) == 0) {
			this.borderType += type;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.draw2d.text.AbstractFlowBorder#paint(org.eclipse.draw2d.text.FlowFigure, org.eclipse.draw2d.Graphics, org.eclipse.draw2d.geometry.Rectangle, int)
	 */
	public void paint(FlowFigure figure, Graphics g, Rectangle where, int sides) {
		if ((where.width == 0) || (where.height == 0)) {
			return;
		}
		
		Color oldColor = g.getForegroundColor();
		int oldWidth = g.getLineWidth();
		g.setForegroundColor(this.borderColor);
		g.setLineWidth(this.borderWidth);
		
		int x1 = where.x;
		int x2 = where.x + where.width;
		
		if ((this.borderType & UNDERLINE) >0) {
			int y = where.y + where.height - this.borderWidth;
			g.drawLine(x1, y, x2, y);
		}
		
		if ((this.borderType & OVERLINE) >0) {
			int y = where.y + 1;
			g.drawLine(x1, y, x2, y);
		}
		
		if ((this.borderType & STRIKE) >0) {
			int y = where.y + ((where.height - this.borderWidth) / 2);
			g.drawLine(x1, y, x2, y);
		}
		
		g.setForegroundColor(oldColor);
		g.setLineWidth(oldWidth);
		
	}
}
