// (c) https://github.com/MontiCore/monticore

package de.monticore.types.mcfullgenerictypes._ast;

import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;

import java.util.List;

public class ASTMCMultipleGenericType extends ASTMCMultipleGenericTypeTOP {

  /* generated by template core.Constructor*/
  protected  ASTMCMultipleGenericType()  {
    /* generated by template core.EmptyBody*/
// empty body

  }

  public ASTMCMultipleGenericType(ASTMCBasicGenericType mCBasicGenericType, List<ASTMCInnerType> mCInnerTypes, List<ASTMCTypeArgument> mCTypeArguments, List<String> names) {
    super(mCBasicGenericType, mCInnerTypes, mCTypeArguments, names);
  }

  @Override
  public String getBaseName() {
    return null;
  }

  @Override
  public String printType() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }
}
