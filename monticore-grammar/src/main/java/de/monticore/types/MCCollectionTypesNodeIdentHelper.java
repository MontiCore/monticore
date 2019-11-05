/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;

import java.util.Arrays;
import java.util.List;

public class MCCollectionTypesNodeIdentHelper extends MCBasicTypesNodeIdentHelper {

  public String getIdent(ASTMCGenericType a) {
    StringBuilder name = new StringBuilder();
    List<String> nameList = Arrays.asList(a.printWithoutTypeArguments().split("\\."));
    int nameListSize = nameList.size();
    for (int i = 0; i < nameListSize; i++) {
      name.append(nameList.get(i));
      if (i != nameListSize - 1) {
        name.append(".");
      }
    }
    return format(name.toString(), Layouter.nodeName(a));
  }

}
