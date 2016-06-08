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
