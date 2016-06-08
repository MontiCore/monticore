package de.monticore.visualoutline.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.InlineFlow;
import org.eclipse.draw2d.text.TextFlow;

import de.monticore.visualoutline.runtime.InstanceRegistry;

public class TextFigure extends Figure {
	protected InstanceRegistry instanceRegistry;
	protected InlineFlow textContent;
	
	public TextFigure(InstanceRegistry ir) {
		this.instanceRegistry = ir;
		
		this.setLayoutManager(new StackLayout());
		
		FlowPage page = new FlowPage();
		page.setOpaque(false);
		this.add(page);
		
		textContent = new InlineFlow();
		page.add(textContent);
	}
	
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		if (figure instanceof TextFlow) {
			this.textContent.add(figure);
		}
		
		else {
			super.add(figure, constraint, index);
		}
	}
}
