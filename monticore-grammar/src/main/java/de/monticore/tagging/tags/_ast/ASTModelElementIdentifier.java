/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging.tags._ast;

import de.monticore.ast.ASTNode;

public interface ASTModelElementIdentifier extends ASTModelElementIdentifierTOP {
  /**
   * @return which production is identified and thus may be tagged by a tag-type
   */
  String getIdentifies();

  default boolean isDefaultIdentifier() {
    return false;
  }

  ASTNode getIdentifiesElement();

}
