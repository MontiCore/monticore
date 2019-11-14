/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;

/**
 * NodeIdentHelper for MCFullGenericTypes, mainly used for Reporting
 */
public class MCFullGenericTypesNodeIdentHelper extends MCSimpleGenericTypesNodeIdentHelper {
  public String getIdent(ASTMCMultipleGenericType a){
    return format(a.printWithoutTypeArguments(), Layouter.nodeName(a));
  }

  public String getIdent(ASTMCArrayType a){
    return format(a.printTypeWithoutBrackets(), Layouter.nodeName(a));
  }
}
