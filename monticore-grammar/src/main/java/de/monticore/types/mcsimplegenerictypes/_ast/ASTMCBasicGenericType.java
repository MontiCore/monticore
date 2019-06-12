package de.monticore.types.mcsimplegenerictypes._ast;

import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.List;
import java.util.Optional;

public class ASTMCBasicGenericType extends ASTMCBasicGenericTypeTOP {

  public ASTMCBasicGenericType() {
  }

  public ASTMCBasicGenericType(List<String> names, List<ASTMCTypeArgument> mCTypeArguments) {
    super(names, mCTypeArguments);
  }

  @Override
  public String getBaseName() {
    return getName(sizeNames()-1);
  }

}
