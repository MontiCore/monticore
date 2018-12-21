package de.monticore.types.mcbasictypes._ast;

import java.util.List;

public class ASTMCQualifiedReferenceType extends ASTMCQualifiedReferenceTypeTOP {

  public ASTMCQualifiedReferenceType() {
  }

  public ASTMCQualifiedReferenceType(ASTMCQualifiedName qualifiedName) {
    super(qualifiedName);
  }

  public List<String> getNameList() {
    return this.getMCQualifiedName().getPartList();
  }
}
