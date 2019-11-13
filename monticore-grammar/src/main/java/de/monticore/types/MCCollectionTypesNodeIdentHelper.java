/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.Joiners;

import java.util.Arrays;
import java.util.List;

public class MCCollectionTypesNodeIdentHelper extends MCBasicTypesNodeIdentHelper {

  public String getIdent(ASTMCGenericType a) {
    String name = Joiners.DOT.join(a.getNameList());
    return format(name.toString(), Layouter.nodeName(a));
  }

}
