package de.monticore.visualoutline.figures;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;

/**
 * Default figure for all containers.
 * 
 * @author Dennis Birkholz
 */
public class DefinitionFigure extends FreeformLayer {
	public DefinitionFigure(Object model) {
	    FreeformLayout layout = new FreeformLayout();
	    layout.setPositiveCoordinates(true);
	    
	    this.setLayoutManager(layout);
	}
}
