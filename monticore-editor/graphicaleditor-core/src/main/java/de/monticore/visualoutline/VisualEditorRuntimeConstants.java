package de.monticore.visualoutline;

public class VisualEditorRuntimeConstants {
	/**
	 * Identifies the rule in the outline description concept this AST node matches 
	 */
	public static final String NODE_RULE_IDENTIFIER_ANNOTATION = "visualeditor_rule_identifier";
	
	/**
	 * Name of the AST annotation in the runtime that contains the connections the AST node is a source of  
	 */
	public static final String SOURCE_CONNECTIONS_ANNOTATION = "visualeditor_source_connections";
	
	/**
	 * Name of the AST annotation in the runtime that contains the connections the AST node is a target of  
	 */
	public static final String TARGET_CONNECTIONS_ANNOTATION = "visualeditor_target_connections";
	
	/**
	 * Name of the AST annotation in the runtime that contains a list of model elements that are to treat as additional children of that AST node
	 */
	public static final String ADDITIONAL_MODEL_CHILDREN_ANNOTATION = "visualeditor_additional_model_children";
	
	/**
	 * Name of the AST annotation in the runtime that contains a list of model elements that should be added as children to the same AST node this AST node is added as a child 
	 */
	public static final String ADDITIONAL_PARENT_MODEL_CHILDREN_ANNOTATION = "visualeditor_additional_parent_model_children";
}
