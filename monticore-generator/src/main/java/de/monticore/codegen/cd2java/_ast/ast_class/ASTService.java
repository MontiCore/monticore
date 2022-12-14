/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;

/**
 * helper class for the AST package generation
 */
public class ASTService extends AbstractService<ASTService> {

  public ASTService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public ASTService(DiagramSymbol cdSymbol) {
    super(cdSymbol);
  }

  /**
   * overwrite methods of AbstractService to add the correct '_ast' package for ast generation
   */
  @Override
  public String getSubPackage() {
    return ASTConstants.AST_PACKAGE;
  }

  @Override
  protected ASTService createService(DiagramSymbol cdSymbol) {
    return createASTService(cdSymbol);
  }

  public static ASTService createASTService(DiagramSymbol cdSymbol) {
    return new ASTService(cdSymbol);
  }

  /**
   * create base interface name e.g. ASTAutomataNode
   */
  public String getASTBaseInterfaceSimpleName() {
    return AST_PREFIX + getCDName() + ASTConstants.NODE_SUFFIX;
  }

  public String getASTBaseInterfaceSimpleName(DiagramSymbol cdSymbol) {
    return AST_PREFIX + cdSymbol.getName() + ASTConstants.NODE_SUFFIX;
  }

  public String getASTBaseInterfaceFullName(DiagramSymbol cdDefinitionSymbol) {
    return String.join(".", getPackage(), getASTBaseInterfaceSimpleName());
  }

  public String getASTBaseInterfaceFullName() {
    return String.join(".", getPackage(), getASTBaseInterfaceSimpleName());
  }

  public ASTMCQualifiedType getASTBaseInterface() {
    return getMCTypeFacade().createQualifiedType(getASTBaseInterfaceFullName());
  }

  /**
   * constant class names g.g. ASTConstantsAutomata
   */
  public String getASTConstantClassSimpleName() {
    return getASTConstantClassSimpleName(getCDSymbol());
  }

  public String getASTConstantClassSimpleName(DiagramSymbol cdSymbol) {
    return ASTConstants.AST_CONSTANTS + cdSymbol.getName();
  }

  public String getASTConstantClassFullName() {
    return getASTConstantClassFullName(getCDSymbol());
  }

  public String getASTConstantClassFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getASTConstantClassSimpleName(cdSymbol);
  }

  /**
   * ast class names g.g. ASTAutomaton
   */
  public String getASTSimpleName(ASTCDType type) {
    return type.getName().startsWith(AST_PREFIX) ? type.getName() : AST_PREFIX + type.getName();
  }

  public String getASTFullName(ASTCDType type) {
    return String.join(".", getPackage(), getASTSimpleName(type));
  }

  public String getASTFullName(ASTCDType type, DiagramSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getASTSimpleName(type));
  }

  public ASTMCType getASTType(ASTCDType type) {
    return getMCTypeFacade().createQualifiedType(getASTFullName(type));
  }

  /**
   * returns true if the ast defines a symbol and no name attribute and no getName method are already defined
   */
  public boolean isSymbolWithoutName(ASTCDType type) {
    return hasSymbolStereotype(type.getModifier())
        && type.getCDAttributeList().stream().noneMatch(a -> "name".equals(a.getName()))
        && type.getCDMethodList().stream().noneMatch(m -> "getName".equals(m.getName()));
  }

  /**
   * abstract getName method only generated when isSymbolWithoutName evaluates true for the ast class
   */
  public ASTCDMethod createGetNameMethod() {
    return CDMethodFacade.getInstance().createMethod(PUBLIC_ABSTRACT.build(), getMCTypeFacade().createStringType(), "getName");
  }

  public String removeASTPrefix(ASTCDType clazz) {
    // normal symbol name calculation from
    return removeASTPrefix(clazz.getName());
  }

  public String removeASTPrefix(String clazzName) {
    // normal symbol name calculation from
    if (clazzName.startsWith(AST_PREFIX)) {
      return clazzName.substring(AST_PREFIX.length());
    } else {
      return clazzName;
    }
  }

}
