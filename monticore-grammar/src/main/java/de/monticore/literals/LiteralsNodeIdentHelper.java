/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals;

import static de.monticore.generating.templateengine.reporting.commons.Layouter.nodeName;

import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.literals.literals._ast.ASTBooleanLiteral;
import de.monticore.literals.literals._ast.ASTCharLiteral;
import de.monticore.literals.literals._ast.ASTDoubleLiteral;
import de.monticore.literals.literals._ast.ASTFloatLiteral;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTLongLiteral;
import de.monticore.literals.literals._ast.ASTNullLiteral;
import de.monticore.literals.literals._ast.ASTStringLiteral;

/**
 * @author MB
 */
public class LiteralsNodeIdentHelper extends ASTNodeIdentHelper {
  
  public String getIdent(ASTBooleanLiteral ast) {
    return format(Boolean.toString(ast.getValue()), nodeName(ast));
  }
  
  public String getIdent(ASTCharLiteral ast) {
    return format(Character.toString(ast.getValue()), nodeName(ast));
  }
  
  public String getIdent(ASTDoubleLiteral ast) {
    return format(Double.toString(ast.getValue()), nodeName(ast));
  }
  
  public String getIdent(ASTFloatLiteral ast) {
    return format(Float.toString(ast.getValue()), nodeName(ast));
  }
  
  public String getIdent(ASTIntLiteral ast) {
    return format(Integer.toString(ast.getValue()), nodeName(ast));
  }
  
  public String getIdent(ASTLongLiteral ast) {
    return format(Long.toString(ast.getValue()), nodeName(ast));
  }
  
  public String getIdent(ASTNullLiteral ast) {
    return format("null", nodeName(ast));
  }
  
  public String getIdent(ASTStringLiteral ast) {
    // return a regular "Name"
    String name = ast.getValue();
    
    // Replace all special characters by _
    name = name.replaceAll("[^a-zA-Z0-9_$\\-+]", "_");
    if (name.matches("[0-9].*")) {
      // if the name starts with a digit ...
      name = "_".concat(name);
    }
    return format(name, nodeName(ast));
  }
  
}
