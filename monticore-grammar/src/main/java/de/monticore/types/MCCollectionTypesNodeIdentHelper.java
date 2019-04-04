package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;

public class MCCollectionTypesNodeIdentHelper extends MCBasicTypesNodeIdentHelper {
  public String getIdent(ASTMCGenericType a) {
    StringBuilder name = new StringBuilder();
    for (int i = 0; i < a.getNameList().size(); i++) {
      name.append(a.getNameList().get(i));
      if (i != a.getNameList().size() - 1) {
        name.append(".");
      }
    }
    return format(name.toString(), Layouter.nodeName(a));
  }
}
