package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class SymbolTableCreatorBuilderDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  public SymbolTableCreatorBuilderDecorator(final GlobalExtensionManagement glex,
                                            final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }


  protected static final String SCOPE_STACK_TEMPLATE = "this." + SCOPE_STACK_VAR + "%s;\n" +
      "return this;";

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String symbolTableCreator = symbolTableService.getSymbolTableCreatorSimpleName();
    String symbolTableCreatorBuilder = symbolTableCreator + BUILDER_SUFFIX;
    String scopeInterface = symbolTableService.getScopeInterfaceFullName();
    ASTMCBasicGenericType dequeType = getMCTypeFacade().createBasicGenericTypeOf(DEQUE_TYPE, scopeInterface);
    ASTMCQualifiedType builderType = getMCTypeFacade().createQualifiedType(symbolTableCreatorBuilder);

    ASTCDAttribute scopeStackAttribute = createScopeStackAttribute(dequeType);

    return CD4AnalysisMill.cDClassBuilder()
        .setName(symbolTableCreatorBuilder)
        .setModifier(PUBLIC.build())
        .addCDConstructor(createConstructor(symbolTableCreatorBuilder))
        .addCDMethod(createBuildMethod(symbolTableCreator))
        .addCDAttribute(scopeStackAttribute)
        .addCDMethod(createSetScopeStackMethod(dequeType, builderType))
        .addCDMethod(createAddToScopeStackMethod(scopeInterface, builderType))
        .addCDMethod(createRemoveFromScopeStackMethod(scopeInterface, builderType))
        .addCDMethod(createGetScopeStackMethod(dequeType))
        .build();
  }

  protected ASTCDConstructor createConstructor(String symTabCreatorBuilder) {
    return getCDConstructorFacade().createConstructor(PROTECTED.build(), symTabCreatorBuilder);
  }

  protected ASTCDMethod createBuildMethod(String symTabCreator) {
    ASTCDMethod buildMethod = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createQualifiedType(symTabCreator), BUILD_METHOD);
    this.replaceTemplate(EMPTY_BODY, buildMethod, new
        StringHookPoint("return new " + symTabCreator + "(" + SCOPE_STACK_VAR + ");"));
    return buildMethod;
  }

  protected ASTCDAttribute createScopeStackAttribute(ASTMCType dequeType) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PROTECTED, dequeType, SCOPE_STACK_VAR);
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= new java.util.ArrayDeque<>()"));
    return scopeStack;
  }

  protected ASTCDMethod createSetScopeStackMethod(ASTMCType dequeType, ASTMCType builderType) {
    ASTCDParameter dequeParam = getCDParameterFacade().createParameter(dequeType, SCOPE_STACK_VAR);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, builderType, "setScopeStack", dequeParam);
    String methodCall = " = " + SCOPE_STACK_VAR;
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(String.format(SCOPE_STACK_TEMPLATE, methodCall)));
    return method;
  }

  protected ASTCDMethod createAddToScopeStackMethod(String scopeInterface, ASTMCType builderType) {
    ASTCDParameter dequeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, builderType, "addToScopeStack", dequeParam);
    String methodCall = ".add(" + SCOPE_VAR + ")";
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(String.format(SCOPE_STACK_TEMPLATE, methodCall)));
    return method;
  }

  protected ASTCDMethod createRemoveFromScopeStackMethod(String scopeInterface, ASTMCType builderType) {
    ASTCDParameter dequeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, builderType, "removeFromScopeStack", dequeParam);
    String methodCall = ".remove(" + SCOPE_VAR + ")";
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(String.format(SCOPE_STACK_TEMPLATE, methodCall)));
    return method;
  }

  protected ASTCDMethod createGetScopeStackMethod(ASTMCType dequeType) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, dequeType, "getScopeStack");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(
        "return this." + SCOPE_STACK_VAR + ";"));
    return method;
  }
}
