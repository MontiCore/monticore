/* (c) https://github.com/MontiCore/monticore */
package mc.basegrammars.kotlincommongenerictypes._ast;

import com.google.common.collect.Lists;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.List;


public   class ASTMCArrayGenericType extends ASTMCArrayGenericTypeTOP {

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
