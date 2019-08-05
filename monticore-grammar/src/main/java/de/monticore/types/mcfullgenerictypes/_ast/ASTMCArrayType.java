package de.monticore.types.mcfullgenerictypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.List;
import java.util.Optional;

public class ASTMCArrayType extends ASTMCArrayTypeTOP {
  public ASTMCArrayType() {
  }

  public ASTMCArrayType(int dimensions, ASTMCType mCType) {
    super(dimensions, mCType);
  }

  @Override
  public List<String> getNameList() {
    return getMCType().getNameList();
  }

  @Override
  public String getBaseName() {
    return getMCType().getBaseName();
  }

}
