package de.monticore.visualoutline.runtime.model;

import org.eclipse.gef.EditPartFactory;

import de.monticore.ast.ASTNode;
import de.monticore.genericgraphics.controller.selection.IHasLineNumbers;
import de.se_rwth.commons.SourcePosition;

/**
 * Convenience default implementation.
 * Provides line synchronization if wrapped model element is AST node
 * 
 * @author Dennis Birkholz
 */
public class DefaultCustomModelWithFactory implements ICustomModelWithFactory, IHasLineNumbers {
	private Object model;
	private EditPartFactory factory;
	
	public DefaultCustomModelWithFactory(Object model, EditPartFactory factory) {
		this.model = model;
		this.factory = factory;
	}
	
	/*
	 * (non-Javadoc)
	 * @see mc.visualoutline.runtime.model.ICustomModelWithFactory#getFactory()
	 */
	@Override
	public EditPartFactory getFactory() {
		return this.factory;
	}
	
	/*
	 * (non-Javadoc)
	 * @see mc.visualoutline.runtime.model.ICustomModelWithFactory#getModel()
	 */
	@Override
	public Object getModel() {
		return model;
	}
	
	@Override
	public int getEndLine() {
		if (this.getModel() instanceof ASTNode) {
			ASTNode model = (ASTNode)this.getModel();
			SourcePosition end = model.get_SourcePositionEnd();
			if (end != null) {
				return end.getLine();
			}
		}
		return 1;
	}
	
	@Override
	public int getEndOffset() {
		if (this.getModel() instanceof ASTNode) {
			ASTNode model = (ASTNode)this.getModel();
			SourcePosition end = model.get_SourcePositionEnd();
			if (end != null) {
				return end.getColumn();
			}
		}
		return 1;
	}

	@Override
	public int getStartLine() {
		if (this.getModel() instanceof ASTNode) {
			ASTNode model = (ASTNode)this.getModel();
			SourcePosition start = model.get_SourcePositionStart();
			if (start != null) {
				return start.getLine();
			}
		}
		return 1;
	}

	@Override
	public int getStartOffset() {
		if (this.getModel() instanceof ASTNode) {
			ASTNode model = (ASTNode)this.getModel();
			SourcePosition start = model.get_SourcePositionStart();
			if (start != null) {
				return start.getColumn();
			}
		}
		return 1;
	}

}
