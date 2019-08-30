/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcsimplegenerictypes._ast;

import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.List;
import java.util.Optional;

public class ASTMCBasicGenericType extends ASTMCBasicGenericTypeTOP {

  public ASTMCBasicGenericType() {
  }

  public ASTMCBasicGenericType( List<ASTMCTypeArgument> mCTypeArguments, List<String> names) {
    super(mCTypeArguments, names);
  }

  @Override
  public String getBaseName() {
    return getName(sizeNames()-1);
  }

}
