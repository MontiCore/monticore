package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class ASTService extends AbstractService<ASTService> {

  public ASTService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public ASTService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return ASTConstants.AST_PACKAGE;
  }

  @Override
  protected ASTService createService(CDDefinitionSymbol cdSymbol) {
    return createASTService(cdSymbol);
  }

  public static ASTService createASTService(CDDefinitionSymbol cdSymbol) {
    return new ASTService(cdSymbol);
  }

  public String getASTBaseInterfaceSimpleName() {
    return ASTConstants.AST_PREFIX + getCDName() + ASTConstants.NODE_SUFFIX;
  }

  public String getASTBaseInterfaceFullName() {
    return String.join(".", getPackage(), getASTBaseInterfaceSimpleName());
  }

  public ASTMCQualifiedType getASTBaseInterface() {
    return getCDTypeFactory().createQualifiedType(getASTBaseInterfaceFullName());
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

  public ASTMCType getASTType(ASTCDType type) {
    return getCDTypeFactory().createQualifiedType(getASTFullTypeName(type));
  }

  public ASTMCQualifiedType getASTNodeInterfaceType() {
    return getCDTypeFactory().createQualifiedType(ASTNode.class);
  }

  public String getASTConstantClassName() {
    return getPackage() + "." + ASTConstants.AST_CONSTANTS + getCDName();
  }
}
