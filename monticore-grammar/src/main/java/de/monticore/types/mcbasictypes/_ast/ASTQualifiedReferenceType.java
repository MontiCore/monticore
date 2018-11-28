package de.monticore.types.mcbasictypes._ast;

import java.util.List;

public class ASTQualifiedReferenceType extends ASTQualifiedReferenceTypeTOP {

  public ASTQualifiedReferenceType() {
  }

  public ASTQualifiedReferenceType(ASTQualifiedName qualifiedName) {
    super(qualifiedName);
  }

  public List<String> getNameList() {
    return this.getQualifiedName().getPartList();
  }
}
