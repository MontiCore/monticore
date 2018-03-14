/* (c) https://github.com/MontiCore/monticore */
package mc.feature.hwc.tagging2._ast;

import java.util.List;

import mc.grammar.types.ittesttypes._ast.ASTImportStatement;
import mc.grammar.types.ittesttypes._ast.ASTQualifiedName;

public class ASTTaggingUnit extends ASTTaggingUnitTOP {

  private String name;

  public ASTTaggingUnit() {
    super();
  }

  public ASTTaggingUnit(List<String> r__package, List<ASTImportStatement> importStatements,
      List<ASTQualifiedName> qualifiedNames, ASTTagBody tagBody) {
    super(r__package, importStatements, qualifiedNames, tagBody);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
