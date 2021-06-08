/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.mcfullgenerictypes._ast;

import com.google.common.collect.Lists;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.List;

public class ASTMCMultipleGenericType extends ASTMCMultipleGenericTypeTOP {

  protected ASTMCMultipleGenericType() {
  }

  public String printWithoutTypeArguments() {
    // from a.B<C>D.E.<F>.G ist will return a.B.D.E.G
    String firstGenericType = getMCBasicGenericType().printWithoutTypeArguments();
    String innerTypes = getMCInnerTypeList()
        .stream()
        .map(ASTMCInnerType::getName)
        .reduce((a, b) -> a + "." + b).get();
    return firstGenericType + "." + innerTypes;
  }

  @Override
  public List<ASTMCTypeArgument> getMCTypeArgumentList() {
    return getMCInnerType(sizeMCInnerTypes()-1).getMCTypeArgumentList();
  }

  @Override
  public List<String> getNameList() {
    return Lists.newArrayList(printWithoutTypeArguments().split("."));
  }

}
