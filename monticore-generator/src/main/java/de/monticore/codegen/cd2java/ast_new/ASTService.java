package de.monticore.codegen.cd2java.ast_new;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.symboltable.CDSymbol;

public class ASTService extends AbstractService<ASTService> {

  public ASTService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public ASTService(CDSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  protected String getSubPackage() {
    return ASTConstants.AST_PACKAGE;
  }

  @Override
  protected ASTService createService(CDSymbol cdSymbol) {
    return createASTService(cdSymbol);
  }

  public static ASTService createASTService(CDSymbol cdSymbol) {
    return new ASTService(cdSymbol);
  }

  public String getASTBaseInterfaceSimpleName() {
    return ASTConstants.AST_PREFIX + getCDName() + ASTConstants.NODE_SUFFIX;
  }

  public String getASTBaseInterfaceFullName() {
    return String.join(".", getPackage(), getASTBaseInterfaceSimpleName());
  }

  public ASTReferenceType getASTBaseInterface() {
    return getCDTypeFactory().createSimpleReferenceType(getASTBaseInterfaceFullName());
  }

  public String getSimpleTypeName(ASTCDType type) {
    return type.getName().startsWith(ASTConstants.AST_PREFIX) ? type.getName().substring(ASTConstants.AST_PREFIX.length()) : type.getName();
  }

  public String getASTSimpleTypeName(ASTCDType type) {
    return ASTConstants.AST_PREFIX + type.getName();
  }

  public String getASTFullTypeName(ASTCDType type) {
    return String.join(".", getPackage(), getASTSimpleTypeName(type));
  }

  public ASTType getASTType(ASTCDType type) {
    return getCDTypeFactory().createSimpleReferenceType(getASTFullTypeName(type));
  }

  public ASTReferenceType getASTNodeInterfaceType() {
    return getCDTypeFactory().createSimpleReferenceType(ASTNode.class);
  }
}
