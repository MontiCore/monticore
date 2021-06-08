/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mccollectiontypes._ast.*;

import java.util.Arrays;
import java.util.List;

/**
 * NodeIdentHelper for MCCollectionTypes, mainly used for Reporting
 */
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

  public String getIdent(ASTMCListType a){
    return format("List",Layouter.nodeName(a));
  }

  public String getIdent(ASTMCSetType a){
    return format("Set",Layouter.nodeName(a));
  }

  public String getIdent(ASTMCMapType a){
    return format("Map", Layouter.nodeName(a));
  }

  public String getIdent(ASTMCOptionalType a){
    return format("Optional", Layouter.nodeName(a));
  }

  public String getIdent(ASTMCBasicTypeArgument a){
    return format(a.getMCQualifiedType().getMCQualifiedName().getQName(), Layouter.nodeName(a));
  }

  public String getIdent(ASTMCPrimitiveTypeArgument a){
    return format(a.getMCPrimitiveType().printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()),Layouter.nodeName(a));
  }

}
