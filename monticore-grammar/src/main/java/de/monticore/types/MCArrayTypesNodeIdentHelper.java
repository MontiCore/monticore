/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;

/**
 * NodeIdentHelper for MCArrayTypes, mainly used for Reporting
 */
public class MCArrayTypesNodeIdentHelper extends MCBasicTypesNodeIdentHelper {

  public String getIdent(ASTMCArrayType a){
    return format(a.printTypeWithoutBrackets(), Layouter.nodeName(a));
  }

}
