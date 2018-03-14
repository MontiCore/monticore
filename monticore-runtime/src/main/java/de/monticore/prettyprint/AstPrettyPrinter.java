/* (c)  https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;

public interface AstPrettyPrinter<T extends ASTNode> {
  
  String prettyPrint(T node);

}
