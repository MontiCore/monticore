package de.monticore.visualoutline.runtime.model;

import org.eclipse.gef.EditPartFactory;

/**
 * Generated custom model elements use this class, do not use it on your own.
 * 
 * The supplied (generated) edit part factory that handles all edit part creation for the visual outline
 * can identify the type of edit part to create based on the stored identifier.
 * 
 * @author Dennis Birkholz
 */
final public class CustomModelWithIdentifier extends DefaultCustomModelWithFactory {
	private String identifier;
	
	public CustomModelWithIdentifier(Object model, String identifier, EditPartFactory factory) {
		super(model, factory);
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
}
