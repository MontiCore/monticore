/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;

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

  /*
  create base interface name e.g. ASTAutomataNode
   */
  public String getASTBaseInterfaceSimpleName() {
    return AST_PREFIX + getCDName() + ASTConstants.NODE_SUFFIX;
  }

  public String getASTBaseInterfaceSimpleName(CDDefinitionSymbol cdSymbol) {
    return AST_PREFIX + cdSymbol.getName() + ASTConstants.NODE_SUFFIX;
  }

  public String getASTBaseInterfaceFullName(CDDefinitionSymbol cdDefinitionSymbol) {
    return String.join(".", getPackage(), getASTBaseInterfaceSimpleName());
  }

  public String getASTBaseInterfaceFullName() {
    return String.join(".", getPackage(), getASTBaseInterfaceSimpleName());
  }

  public ASTMCQualifiedType getASTBaseInterface() {
    return getCDTypeFacade().createQualifiedType(getASTBaseInterfaceFullName());
  }

  /*
constant class names g.g. ASTConstantsAutomata
 */
  public String getASTConstantClassSimpleName() {
    return getASTConstantClassSimpleName(getCDSymbol());
  }

  public String getASTConstantClassSimpleName(CDDefinitionSymbol cdSymbol) {
    return ASTConstants.AST_CONSTANTS + cdSymbol.getName();
  }

  public String getASTConstantClassFullName() {
    return getASTConstantClassFullName(getCDSymbol());
  }

  public String getASTConstantClassFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getASTConstantClassSimpleName(cdSymbol);
  }

  /*
ast class names g.g. ASTAutomaton
 */
  public String getASTSimpleName(ASTCDType type) {
    return type.getName().startsWith(AST_PREFIX) ? type.getName() : AST_PREFIX + type.getName();
  }

  public String getASTFullName(ASTCDType type) {
    return String.join(".", getPackage(), getASTSimpleName(type));
  }

  public String getASTFullName(ASTCDType type, CDDefinitionSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getASTSimpleName(type));
  }

  public ASTMCType getASTType(ASTCDType type) {
    return getCDTypeFacade().createQualifiedType(getASTFullName(type));
  }
}
