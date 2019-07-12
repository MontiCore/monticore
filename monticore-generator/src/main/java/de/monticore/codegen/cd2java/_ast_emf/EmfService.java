package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.StringTransformations;

import static de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator.LITERALS_SUFFIX;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.PACKAGE_IMPL_SUFFIX;

public class EmfService extends AbstractService {

  private static final String ABSTRACT = "IS_ABSTRACT";

  public EmfService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public EmfService(CDSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return ASTConstants.AST_PACKAGE;
  }

  @Override
  protected ASTService createService(CDSymbol cdSymbol) {
    return createEmfService(cdSymbol);
  }


  public static ASTService createEmfService(CDSymbol cdSymbol) {
    return new ASTService(cdSymbol);
  }

  public String getQualifiedPackageImplName() {
    return getQualifiedPackageImplName(getCDSymbol());
  }

  public String getQualifiedPackageImplName(CDSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getSimplePackageImplName(cdSymbol));
  }

  public String getSimplePackageImplName() {
    return getSimplePackageImplName(getCDSymbol());
  }

  public String getSimplePackageImplName(CDSymbol cdSymbol) {
    return cdSymbol.getName() + PACKAGE_IMPL_SUFFIX;
  }

  public String getSimplePackageImplName(String cdSymbolQualifiedName) {
    return getSimplePackageImplName(resolveCD(cdSymbolQualifiedName));
  }

  public boolean isExternal(ASTCDAttribute attribute) {
    return getNativeAttributeType(attribute.getType()).endsWith("Ext");
  }

  //fo InitializePackageContents template
  public String getClassPackage(CDTypeSymbol cdTypeSymbol) {
    if (cdTypeSymbol.getModelName().toLowerCase().equals(getQualifiedCDName().toLowerCase())) {
      return "this";
    } else {
      return StringTransformations.uncapitalize(getSimplePackageImplName(cdTypeSymbol.getModelName()));
    }
  }

  //fo InitializePackageContents template
  public String determineListInteger(ASTType astType) {
    if (DecorationHelper.isListType(TypesPrinter.printType(astType))) {
      return "-1";
    } else {
      return "1";
    }
  }

  //fo InitializePackageContents template
  public String determineAbstractString(ASTCDClass cdClass) {
    if (cdClass.isPresentModifier() && cdClass.getModifier().isAbstract()) {
      return ABSTRACT;
    } else {
      return "!" + ABSTRACT;
    }
  }

  //fo InitializePackageContents template
  public String determineGetEmfMethod(ASTCDAttribute attribute, ASTCDDefinition astcdDefinition) {
    DecorationHelper decorationHelper = new DecorationHelper();
    if (isExternal(attribute)) {
      return "theASTENodePackage.getENode";
    } else if (isPrimitive(attribute.getType()) || isString(attribute.getType())) {
      return "ecorePackage.getE" + StringTransformations.capitalize(getSimpleNativeAttributeType(attribute.getType()));
    } else if (decorationHelper.isSimpleAstNode(attribute) || decorationHelper.isListAstNode(attribute) || decorationHelper.isOptionalAstNode(attribute)) {
      String grammarName = StringTransformations.uncapitalize(getGrammarFromClass(astcdDefinition, attribute));
      return grammarName + ".get" + StringTransformations.capitalize(getSimpleNativeAttributeType(attribute.getType()));
    } else {
      return "this.get" + StringTransformations.capitalize(getSimpleNativeAttributeType(attribute.getType()));
    }
  }

  public boolean isPrimitive(ASTType type) {
    return type instanceof ASTPrimitiveType;
  }

  public boolean isString(ASTType type){
   return getSimpleNativeAttributeType(type).equals("String");
  }

  public boolean isLiteralsEnum(ASTCDEnum astcdEnum, String definitionName){
    return astcdEnum.getName().equals(definitionName + LITERALS_SUFFIX);
  }
}
