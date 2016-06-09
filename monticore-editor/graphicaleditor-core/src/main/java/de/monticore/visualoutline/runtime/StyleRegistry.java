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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import de.monticore.visualoutline.runtime.gef.TextLineDecoration;

/**
 * This class handles all text styling.
 * 
 * Styles are defined globally and then only referred by name.
 * They are registered 
 * 
 * @author Dennis Birkholz
 */
public class StyleRegistry {
	private Map<String, TextStyle> styles;
	
	public StyleRegistry() {
		this.styles = new HashMap<String, TextStyle>();
	}
	
	/**
	 * Register a style by name so it can be used afterwards
	 * 
	 * @param styleName
	 * @param style
	 */
	public void register(String styleName, TextStyle style) {
		this.styles.put(styleName, style);
	}
	
	/**
	 * Retrieve a style by name
	 * 
	 * @param name
	 * @return
	 */
	public TextStyle getStyle(String name) {
		return this.styles.get(name);
	}
	
	/**
	 * Apply the style to a TextFlow object
	 * 
	 * @param styleName
	 * @param text
	 */
	public void apply(String styleName, TextFlow text) {
		TextStyle style = this.getStyle(styleName);
		
		int fontStyle = SWT.NORMAL;
		if (style.bold) {
			fontStyle += SWT.BOLD;
		}
		if (style.italic) {
			fontStyle += SWT.ITALIC;
		}
		Font font = new Font(null, style.font, style.size, fontStyle);
		text.setFont(font);
		
		text.setForegroundColor(style.color);
		
		if (style.underline || style.overline || style.strike) {
			TextLineDecoration lineDeco = new TextLineDecoration(0, style.color, 1);
			
			if (style.underline) {
				lineDeco.addDecoration(TextLineDecoration.UNDERLINE);
			}
			if (style.overline) {
				lineDeco.addDecoration(TextLineDecoration.OVERLINE);
			}
			if (style.strike) {
				lineDeco.addDecoration(TextLineDecoration.STRIKE);
			}
			text.setBorder(lineDeco);
		}
	}
	
}
