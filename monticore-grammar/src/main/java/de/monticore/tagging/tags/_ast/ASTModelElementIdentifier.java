/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging.tags._ast;

public interface ASTModelElementIdentifier extends ASTModelElementIdentifierTOP {
  /**
   * @return which production is identified and thus may be tagged by a tag-type
   */
  public String getIdentifies();

}
