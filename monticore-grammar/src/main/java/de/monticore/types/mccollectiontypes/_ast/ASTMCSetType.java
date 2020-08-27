/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import com.google.common.collect.Lists;

import java.util.List;

public class ASTMCSetType extends ASTMCSetTypeTOP {

  protected List<String> names = Lists.newArrayList("Set");

  protected List<ASTMCTypeArgument> typeArguments = Lists.newArrayList();

  @Override
  public void setMCTypeArgument(ASTMCTypeArgument mCTypeArgument) {
    super.setMCTypeArgument(mCTypeArgument);
    typeArguments = Lists.newArrayList(mCTypeArgument);
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
