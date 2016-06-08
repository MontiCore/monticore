package de.monticore.visualoutline.runtime.gef;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.text.TextFlow;

import de.monticore.genericgraphics.controller.editparts.defaults.AbstractSelectableMCGraphicalEditPart;
import de.monticore.visualoutline.runtime.InstanceRegistry;
import de.monticore.visualoutline.runtime.model.ICustomModelWithFactory;
import de.se_rwth.commons.logging.Finding;

public class TextFigureEditPart extends AbstractSelectableMCGraphicalEditPart {
	final private InstanceRegistry instanceRegistry;
	final private String style;
	
	public TextFigureEditPart(InstanceRegistry ir, String style) {
		this.instanceRegistry = ir;
		this.style = style;
	}
	
	@Override
	protected IFigure createFigure() {
		ICustomModelWithFactory wrapperModel = (ICustomModelWithFactory)this.getModel(); 
		String text = (String)wrapperModel.getModel();
		
		TextFlow t = new TextFlow(text);
		this.instanceRegistry.getStyleRegistry().apply(this.style, t);
		return t;
	}
	
	@Override
	public void setProblems(List<Finding> reports) {
		// don't set errors
	}
}
