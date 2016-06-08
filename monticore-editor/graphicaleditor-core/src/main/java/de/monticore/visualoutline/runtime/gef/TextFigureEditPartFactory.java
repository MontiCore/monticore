package de.monticore.visualoutline.runtime.gef;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.monticore.visualoutline.runtime.InstanceRegistry;

public class TextFigureEditPartFactory implements EditPartFactory {
	final private InstanceRegistry instanceRegistry;
	final private String style;
	
	public TextFigureEditPartFactory(InstanceRegistry ir, String style) {
		this.instanceRegistry = ir;
		this.style = style;
	} 
	
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart ep = new TextFigureEditPart(this.instanceRegistry, style);
		ep.setModel(model);
		return ep;
	}
}
