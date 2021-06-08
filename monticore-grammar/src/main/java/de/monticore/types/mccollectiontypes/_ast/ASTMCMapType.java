/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;


import com.google.common.collect.Lists;

import java.util.List;

public class ASTMCMapType extends ASTMCMapTypeTOP {


  protected List<String> names = Lists.newArrayList("Map");

  protected List<ASTMCTypeArgument> typeArguments = Lists.newArrayList();

  @Override
  public void setKey(ASTMCTypeArgument key) {
    super.setKey(key);
    typeArguments = Lists.newArrayList(getKey(), getValue());
  }

  @Override
  public void setValue(ASTMCTypeArgument value) {
    super.setValue(value);
    typeArguments = Lists.newArrayList(getKey(), getValue());
  }

  @Override
  public List<ASTMCTypeArgument> getMCTypeArgumentList() {
    return Lists.newArrayList(typeArguments);
  }

  @Override
  public List<String> getNameList() {
    return Lists.newArrayList(names);
  }
}
