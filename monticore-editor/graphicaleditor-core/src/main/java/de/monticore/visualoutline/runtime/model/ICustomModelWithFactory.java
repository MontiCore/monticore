package de.monticore.visualoutline.runtime.model;

import org.eclipse.gef.EditPartFactory;

/**
 * Interface custom model elements that to not represent links must implement.
 * 
 * The graphical outlines of some language may need additional model elements so they can be displayed properly.
 * An example is the State Chart language where a start state is visualized by an additional state (without text)
 * and a link from that new state to the original state.
 * Both the new state and the link need their own (custom) model element to work with the framework.
 * 
 * Models that do not represent a link need a possibility to create an edit part     
 * 
 * @author Dennis Birkholz
 */
public interface ICustomModelWithFactory {
	public EditPartFactory getFactory();
	public Object getModel();
}
