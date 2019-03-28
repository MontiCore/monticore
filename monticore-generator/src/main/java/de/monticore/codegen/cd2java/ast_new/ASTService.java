package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

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

  public String getASTSimpleTypeName(ASTCDType type) {
    return ASTConstants.AST_PREFIX + type.getName();
  }

  public ASTType getASTType(CDTypeSymbol type) {
    //FIXME: missing CD name in package name, e.g., results in 'de.monticore.codegen._ast.ASTFoo', but should be 'de.monticore.codegen.ast._ast.ASTFoo'
    return getCDTypeFactory().createSimpleReferenceType(String.join(".", type.getPackageName(), ASTConstants.AST_PACKAGE) +
        getASTSimpleTypeName((ASTCDType) type.getAstNode().get()));
  }
}
