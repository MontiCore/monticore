package de.monticore.types.mcfullgenerictypes._ast;

import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;

import java.util.List;

public class ASTMCMultipleGenericType extends ASTMCMultipleGenericTypeTOP {
  public ASTMCMultipleGenericType() {
  }

  public ASTMCMultipleGenericType(List<ASTMCBasicGenericType> mCBasicGenericTypes, List<String> names, List<ASTMCTypeArgument> mCTypeArguments) {
    super(mCBasicGenericTypes, names, mCTypeArguments);
  }

  @Override
  public String getBaseName() {
    return getNameList().get(getNameList().size()-1);
  }
}
