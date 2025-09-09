/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;


import com.google.common.collect.Lists;
import de.monticore.symboltable.ISymbol;

import java.util.List;
import java.util.Optional;

public class ASTMCMapType extends ASTMCMapTypeTOP {

  protected ISymbol definingSymbol;

  @Override
  public Optional<ISymbol> getDefiningSymbol() {
    return Optional.ofNullable(this.definingSymbol);
  }

  @Override
  public void setDefiningSymbol(ISymbol symbol) {
    this.definingSymbol = symbol;
  }

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
