/* (c) https://github.com/MontiCore/monticore */
package mc.basegrammars.kotlincommongenerictypes._ast;

import com.google.common.collect.Lists;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.List;
import java.util.Optional;


public class ASTMCArrayGenericType extends ASTMCArrayGenericTypeTOP {

  protected ISymbol definingSymbol;

  @Override
  public Optional<ISymbol> getDefiningSymbol() {
    return Optional.ofNullable(this.definingSymbol);
  }

  public void setDefiningSymbol(ISymbol symbol) {
    this.definingSymbol = symbol;
  }

  protected List<String> names = Lists.newArrayList("Array");

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
