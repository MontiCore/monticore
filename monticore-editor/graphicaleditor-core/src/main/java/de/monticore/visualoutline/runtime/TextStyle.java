/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.monticore.visualoutline.runtime;

import org.eclipse.swt.graphics.Color;

/**
 * Simple container that holds all possible text stylings.
 * 
 * @author Dennis Birkholz
 */
public class TextStyle {
	public String font;
	public Integer size;
	public Color color;
	
	public Boolean bold;
	public Boolean italic;
	public Boolean underline;
	public Boolean overline;
	public Boolean strike;
	
	/**
	 * Create a new text style. All null values will be initialized with from the defaults: Arial 10px black, no decorations
	 * 
	 * @param font
	 * @param size
	 * @param color
	 * @param bold
	 * @param italic
	 * @param underline
	 * @param overline
	 * @param strike
	 */
	public TextStyle(String font, Integer size, Color color, Boolean bold, Boolean italic, Boolean underline, Boolean overline, Boolean strike) {
		this.font = (font != null ? font : "Arial");
		this.size = (size != null ? size : 10);
		this.color = (color != null ? color : new Color(null, 0, 0, 0));
		this.bold = (bold != null ? bold : false);
		this.italic = (italic != null ? italic : false);
		this.underline = (underline != null ? underline : false);
		this.overline = (overline != null ? overline : false);
		this.strike = (strike != null ? strike : false);
	}
	
	/**
	 * Create a new text style. All null values will be initialized from supplied parent style.
	 * 
	 * @param parent
	 * @param font
	 * @param size
	 * @param color
	 * @param bold
	 * @param italic
	 * @param underline
	 * @param overline
	 * @param strike
	 */
	public TextStyle(TextStyle parent, String font, Integer size, Color color, Boolean bold, Boolean italic, Boolean underline, Boolean overline, Boolean strike) {
		this.font = (font != null ? font : parent.font);
		this.size = (size != null ? size : parent.size);
		this.color = (color != null ? color : parent.color);
		this.bold = (bold != null ? bold : parent.bold);
		this.italic = (italic != null ? italic : parent.italic);
		this.underline = (underline != null ? underline : parent.underline);
		this.overline = (overline != null ? overline : parent.overline);
		this.strike = (strike != null ? strike : parent.strike);
	}
}
