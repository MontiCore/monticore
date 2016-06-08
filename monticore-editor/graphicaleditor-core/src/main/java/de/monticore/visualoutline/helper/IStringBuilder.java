package de.monticore.visualoutline.helper;

import de.monticore.ast.ASTNode;

/**
 * Interface each string builder used to build a string for a ast node for visualization purposes has to implement
 * 
 * @author Schulze
 *
 */
public interface IStringBuilder {
	/**
	 * @param ast ast node a string shall be build for
	 * 
	 * @return string representing input ast node
	 */
	public String buildString(ASTNode ast);
	
	
	/**
	 * @param ast ast node a string shall be build for
	 * @return if this string builder supports input ast node type
	 */
	public boolean handlesASTType(ASTNode ast);
}