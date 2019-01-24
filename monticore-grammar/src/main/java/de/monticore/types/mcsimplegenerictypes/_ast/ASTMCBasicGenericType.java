package de.monticore.types.mcsimplegenerictypes._ast;

import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.List;

public class ASTMCBasicGenericType extends ASTMCBasicGenericTypeTOP {

  public ASTMCBasicGenericType() {
  }

  public ASTMCBasicGenericType(List<String> names, List<ASTMCTypeArgument> mCTypeArguments) {
    super(names, mCTypeArguments);
  }

  @Override
  public String getBaseName() {
    return getName(-1);
  }
}
