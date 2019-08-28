/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import java.util.List;

public interface ASTMCObjectType extends ASTMCObjectTypeTOP  {

  // TODO RE: Entfernbar, weil sowieso reingeerbt?
  // Damit kann die ganze Klasse entfernt werden?
  public List<String> getNameList();
}
