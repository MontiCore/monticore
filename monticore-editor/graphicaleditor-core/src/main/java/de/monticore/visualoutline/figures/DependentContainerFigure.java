package de.monticore.visualoutline.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;

import de.monticore.genericgraphics.view.figures.layout.ToolbarLayoutWithMinimumSize;

/**
 * Default figure for all containers.
 * 
 * @author Dennis Birkholz
 */
public class DependentContainerFigure extends Figure {
	public DependentContainerFigure(Object model) {
		super();
		
		ToolbarLayout layout = new ToolbarLayoutWithMinimumSize();
		layout.setStretchMinorAxis(true);
	    this.setLayoutManager(layout);
	    
	    setOpaque(true);
	}
}
