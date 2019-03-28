package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

public class ASTService extends AbstractService {

  public ASTService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  @Override
  protected String getSubPackage() {
    return ASTConstants.AST_PACKAGE;
  }

  public ASTReferenceType getASTBaseInterface() {
    return getCDTypeFactory().createSimpleReferenceType(ASTConstants.AST_PREFIX + getCDName() + ASTConstants.NODE_SUFFIX);
  }
}
