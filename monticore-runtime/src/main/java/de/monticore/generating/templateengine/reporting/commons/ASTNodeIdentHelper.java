/* (c)  https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting.commons;

import de.monticore.ast.ASTNode;

public class ASTNodeIdentHelper implements IASTNodeIdentHelper {
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IASTNodeIdentHelper#getIdent(de.monticore.ast.ASTNode)
   */
  @Override
  public String getIdent(ASTNode ast) {
    String name;
    if (ast.getSymbolOpt().isPresent()) {
      name = ast.getSymbolOpt().get().getName();
    } else {
      name = ast.getClass().getSimpleName();
      if (name.startsWith("AST")) {
        name = name.substring(3);
      }
    }
    return format(name, "ASTNode");
  }
  
}
