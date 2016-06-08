package de.monticore.visualoutline.runtime;

import java.util.ArrayList;
import java.util.List;

import de.monticore.ast.ASTNode;
import de.monticore.visualoutline.helper.IStringBuilder;

/**
 * The StringBuilder decides at runtime which of the supplied string builder
 * implementations to use to translate AST nodes to strings.
 * 
 * @author Dennis Birkholz
 */
public class StringBuilder {
	
	/**
	 * List of string builder classes to use
	 */
	private List<IStringBuilder> builders = new ArrayList<IStringBuilder>();
	
	/**
	 * Add a string builder class to use
	 * String builders are stored in inverse order so string builders defined later have higher priority than string builders defined first  
	 */
	public void add(IStringBuilder builder) {
		this.builders.add(0, builder);
	}
	
	/**
	 * Find the best possible string builder for the supplied object and return the generated string.
	 * 
	 * @param obj Object to print
	 */
	public String buildString(Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (obj instanceof String) {
			return (String)obj;
		}
		
		if (obj instanceof ASTNode) {
			ASTNode node = (ASTNode)obj;
			for (IStringBuilder builder : builders) {
				if (builder.handlesASTType(node)) {
					return builder.buildString(node);
				}
			}
			return node.toString();
		}
		
		return "";
	}
}
