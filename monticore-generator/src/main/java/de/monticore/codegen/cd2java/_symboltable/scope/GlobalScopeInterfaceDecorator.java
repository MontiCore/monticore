/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._symboltable.scope.GlobalScopeClassDecorator.LOAD;

/**
 * creates a globalScope class from a grammar
 */
public class GlobalScopeInterfaceDecorator
    extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  protected static final String ADAPTED_RESOLVER = "adapted%sResolver";

  protected static final String TEMPLATE_PATH = "_symboltable.iglobalscope.";


  /**
   * flag added to define if the GlobalScope interface was overwritten with the TOP mechanism
   * if top mechanism was used, must use setter to set flag true, before the decoration
   * is needed for different getRealThis method implementations
   */
  protected boolean isGlobalScopeInterfaceTop = false;

  protected static final String PUTSYMBOLDESER = "put%sSymbolDeSer";

  public GlobalScopeInterfaceDecorator(final GlobalExtensionManagement glex,
                                       final SymbolTableService symbolTableService,
                                       final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
    this.accessorDecorator = methodDecorator.getAccessorDecorator();
    this.mutatorDecorator = methodDecorator.getMutatorDecorator();
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String globalScopeInterfaceName = symbolTableService.getGlobalScopeInterfaceSimpleName();

    List<ASTCDType> symbolProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    List<ASTCDType> symbolClasses = symbolTableService
        .getSymbolDefiningProds(input.getCDDefinition());

    List<ASTCDMethod> resolverMethods = createAllResolverAttributes(symbolClasses)
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    adjustResolverMethodsForInterface(resolverMethods);

    String definitionName = input.getCDDefinition().getName();
    String globalScopeName = symbolTableService.getGlobalScopeSimpleName();

    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(globalScopeInterfaceName)
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder()
                .addAllSuperclass(getSuperGlobalScopeInterfaces())
                .addSuperclass(symbolTableService.getScopeInterfaceType()).build())
        .addAllCDMembers(createCalculateModelNameMethods(symbolClasses))
        .addAllCDMembers(resolverMethods)
        .addAllCDMembers(createResolveAdaptedMethods(symbolClasses))
        .addAllCDMembers(createResolveAdaptedSuperMethods())
        .addAllCDMembers(createResolveMethods(symbolClasses, definitionName))
        .addAllCDMembers(createSuperProdResolveMethods(definitionName))
        .addCDMember(createLoadFileForModelNameMethod())
        .addAllCDMembers(createEnclosingScopeMethods(globalScopeName))
        .addCDMember(createGetNameMethod(globalScopeName))
        .addCDMember(createIsPresentNameMethod())
        .addCDMember(creatCheckIfContinueAsSubScopeMethod())
        .addAllCDMembers(createDeSerMapMethods())
        .addAllCDMembers(createPutDeSerMethods(symbolProds))
        .addCDMember(createGetRealThisMethod(globalScopeInterfaceName))
        .build();
  }

  protected void adjustResolverMethodsForInterface(List<ASTCDMethod> resolverMethods) {
    for (ASTCDMethod method : resolverMethods) {
      if (method.getName().startsWith("set") && method.sizeCDParameters() == 1) {
        // simple list setter e.g. setXList(list: List<X>)
        method.getModifier().setAbstract(true);
      } else if (method.getName().startsWith("get") && method.isEmptyCDParameters()) {
        // simple list getter e.g. getXList(): List<X>
        method.getModifier().setAbstract(true);
      }
    }
  }

  protected List<ASTMCObjectType> getSuperGlobalScopeInterfaces() {
    return getSuperGlobalScopeInterfaces(symbolTableService.getCDSymbol());
  }

  protected List<ASTMCObjectType> getSuperGlobalScopeInterfaces(DiagramSymbol symbol){
    List<ASTMCObjectType> result = new ArrayList<>();
    for (DiagramSymbol superGrammar : symbolTableService.getSuperCDsDirect(symbol)) {
      if (!superGrammar.isPresentAstNode()) {
        Log.error("0xA4323 Unable to load AST of '" + superGrammar.getFullName()
            + "' that is supergrammar of '" + symbolTableService.getCDName() + "'.");
        continue;
      }
      result.add(symbolTableService.getGlobalScopeInterfaceType(superGrammar));
    }
    if (result.isEmpty()) {
      result.add(getMCTypeFacade().createQualifiedType(I_GLOBAL_SCOPE_TYPE));
    }
    return result;
  }

  protected List<ASTCDAttribute> createAllResolverAttributes(List<ASTCDType> symbolProds) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (ASTCDType symbolProd : symbolProds) {
      Optional<String> simpleName = symbolTableService.getDefiningSymbolSimpleName(symbolProd);
      if (simpleName.isPresent()) {
        String attrName = String.format(ADAPTED_RESOLVER, simpleName.get());
        String symbolResolverInterfaceTypeName = symbolTableService.
            getSymbolResolverInterfaceFullName(symbolProd, symbolTableService.getCDSymbol());
        ASTMCType listType = getMCTypeFacade().createListTypeOf(symbolResolverInterfaceTypeName);
        ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), listType, attrName);
        attributeList.add(attribute);
      }
    }
    return attributeList;
  }

  protected List<ASTCDMethod> createCalculateModelNameMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolProd : symbolProds) {
      String simpleName = symbolTableService.removeASTPrefix(symbolProd);
      ASTMCSetType setTypeOfString = getMCTypeFacade().createSetTypeOf(String.class);
      ASTCDParameter nameParam = getCDParameterFacade().createParameter(String.class, NAME_VAR);
      ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), setTypeOfString,
          String.format("calculateModelNamesFor%s", simpleName), nameParam);
      this.replaceTemplate(EMPTY_BODY, method,
          new TemplateHookPoint(TEMPLATE_PATH + "CalculateModelNamesFor"));
      methodList.add(method);
    }
    return methodList;
  }

  /**
   * resolve adapted methods
   */

  protected List<ASTCDMethod> createResolveAdaptedMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);

    for (ASTCDType symbolProd : symbolProds) {
      methodList.add(createResolveAdaptedMethod(symbolProd, symbolTableService.getCDSymbol(), foundSymbolsParameter, nameParameter,
          accessModifierParameter));
    }
    return methodList;
  }

  protected ASTCDMethod createResolveAdaptedMethod(ASTCDType symbolProd, DiagramSymbol cdDefinitionSymbol,
                                                   ASTCDParameter foundSymbolsParameter, ASTCDParameter nameParameter,
                                                   ASTCDParameter accessModifierParameter) {
    String symbolFullName = symbolTableService.getSymbolFullName(symbolProd, cdDefinitionSymbol);
    String symbolSimpleName = symbolTableService.getSymbolSimpleName(symbolProd);
    String symbolResolverInterfaceFullName = symbolTableService.getSymbolResolverInterfaceFullName(symbolProd, cdDefinitionSymbol);
    ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(
        getMCTypeFacade().createBasicGenericTypeOf(PREDICATE, symbolFullName), PREDICATE_VAR);
    String methodName = String.format(RESOLVE_ADAPTED, symbolTableService.removeASTPrefix(symbolProd.getName()));

    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createListTypeOf(symbolFullName), methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);

    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(TEMPLATE_PATH + "ResolveAdapted", symbolFullName, symbolResolverInterfaceFullName, symbolSimpleName));
    return method;
  }

  protected List<ASTCDMethod> createResolveAdaptedSuperMethods() {
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);

    List<ASTCDMethod> methodList = new ArrayList<>();
    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : symbolTableService.getAllCDTypes(cdDefinitionSymbol)) {
        if (type.isPresentAstNode() && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          methodList.add(createResolveAdaptedMethod(type.getAstNode(), cdDefinitionSymbol, foundSymbolsParameter, nameParameter,
              accessModifierParameter));
        }
      }
    }
    return methodList;
  }


  /**
   * creates all resolve methods
   * reuses the often used parameters, so that they only need to be created once
   */
  protected List<ASTCDMethod> createResolveMethods(List<? extends ASTCDType> symbolProds, String definitionName) {
    List<ASTCDMethod> resolveMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);

    for (ASTCDType symbolProd : symbolProds) {
      resolveMethods.addAll(createResolveMethods(symbolProd, nameParameter, foundSymbolsParameter, accessModifierParameter,
          symbolTableService.getCDSymbol(), definitionName));
    }

    return resolveMethods;
  }

  protected List<ASTCDMethod> createResolveMethods(ASTCDType symbolProd, ASTCDParameter nameParameter, ASTCDParameter foundSymbolsParameter,
                                                   ASTCDParameter accessModifierParameter, DiagramSymbol cdDefinitionSymbol, String definitionName) {
    List<ASTCDMethod> resolveMethods = new ArrayList<>();
    String className = symbolTableService.removeASTPrefix(symbolProd);
    String symbolFullTypeName = symbolTableService.getSymbolFullName(symbolProd, cdDefinitionSymbol);
    ASTMCType listSymbol = getMCTypeFacade().createListTypeOf(symbolFullTypeName);

    ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(
        getMCTypeFacade().createBasicGenericTypeOf(PREDICATE, symbolFullTypeName), PREDICATE_VAR);

    resolveMethods.add(createResolveManyMethod(className, symbolFullTypeName, listSymbol, foundSymbolsParameter,
        nameParameter, accessModifierParameter, predicateParameter));
    resolveMethods.add(createLoadModelsForMethod(className, nameParameter, definitionName));

    return resolveMethods;
  }

  protected List<ASTCDMethod> createSuperProdResolveMethods(String definitionName) {
    List<ASTCDMethod> resolveMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);

    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : symbolTableService.getAllCDTypes(cdDefinitionSymbol)) {
        if (type.isPresentAstNode() && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          resolveMethods.addAll(createResolveMethods(type.getAstNode(), nameParameter, foundSymbolsParameter,
              accessModifierParameter, cdDefinitionSymbol, definitionName));
        }
      }
    }
    return resolveMethods;
  }

  protected ASTCDMethod createResolveManyMethod(String className, String fullSymbolName, ASTMCType returnType,
                                                ASTCDParameter foundSymbolsParameter, ASTCDParameter nameParameter,
                                                ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    String methodName = String.format(RESOLVE_MANY, className);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(TEMPLATE_PATH + "ResolveMany4GlobalScope", className, fullSymbolName));
    return method;
  }

  protected ASTCDMethod createLoadFileForModelNameMethod() {
    ASTCDParameter modelNameParameter = cdParameterFacade.createParameter(String.class, "modelName");
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT.build(), "loadFileForModelName", modelNameParameter);
  }

  protected ASTCDMethod createLoadModelsForMethod(String className,
                                                  ASTCDParameter nameParameter, String definitionName) {
    String methodName = String.format(LOAD, className);
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT.build(), methodName, nameParameter);
  }

  protected ASTCDMethod createGetRealThisMethod(String realThis){
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT.build(), getMCTypeFacade().createQualifiedType(realThis), "getRealThis");
  }


  /**
   * enclosing scope methods
   */

  protected static final String ERROR_CODE = "0xA6100";

  protected List<ASTCDMethod> createEnclosingScopeMethods(String globalScopeName) {
    // create attribute just for method generation purposes
    ASTCDAttribute enclosingScopeAttribute = this.getCDAttributeFacade()
        .createAttribute(PROTECTED.build(),
            symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE_VAR);
    getDecorationHelper().addAttributeDefaultValues(enclosingScopeAttribute, glex);

    methodDecorator.disableTemplates();
    List<ASTCDMethod> enclosingScopeMethods = methodDecorator.decorate(enclosingScopeAttribute);
    methodDecorator.enableTemplates();
    for (ASTCDMethod enclosingScopeMethod : enclosingScopeMethods) {
      String generatedErrorCode = symbolTableService.getGeneratedErrorCode(globalScopeName + enclosingScopeAttribute.printType());
      // add return null if method has return type
      if (enclosingScopeMethod.getMCReturnType().isPresentMCType()) {
        this.replaceTemplate(EMPTY_BODY, enclosingScopeMethod, new StringHookPoint(
            "Log.error(\"" + ERROR_CODE + generatedErrorCode + " GlobalScope " + globalScopeName +
                " has no EnclosingScope, so you cannot call method" + enclosingScopeMethod.getName() + ".\");\n" +
                "    return null;"));
      } else {
        // no return if method is void type
        this.replaceTemplate(EMPTY_BODY, enclosingScopeMethod, new StringHookPoint(
            "Log.error(\"" + ERROR_CODE + generatedErrorCode + " GlobalScope " + globalScopeName +
                " has no EnclosingScope, so you cannot call method" + enclosingScopeMethod.getName() + ".\");"));
      }
    }
    return enclosingScopeMethods;
  }

  /**
   * getName Method
   */

  protected ASTCDMethod createGetNameMethod(String globalScopeName) {
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createStringType(), "getName");
    String generatedErrorCode = symbolTableService.getGeneratedErrorCode(globalScopeName);
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new StringHookPoint(
        "Log.error(\"0xA6101" + generatedErrorCode
            + " Global scopes do not have names.\");\n"
            + "    return null;"));
    return getNameMethod;
  }

  /**
   * Creates the isPresent method for global scopes. As these do not have names,
   * the method return false.
   *
   * @return false
   */
  protected ASTCDMethod createIsPresentNameMethod() {
    ASTCDMethod isPresentNameMethod = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), "isPresentName");
    this.replaceTemplate(EMPTY_BODY, isPresentNameMethod, new StringHookPoint("return false;"));
    return isPresentNameMethod;
  }


  protected ASTCDMethod creatCheckIfContinueAsSubScopeMethod() {
    ASTCDParameter modelNameParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "symbolName");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), "checkIfContinueAsSubScope", modelNameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return false;"));
    return method;
  }

  /**
   * DeSer methods
   */

  protected Collection<? extends ASTCDMethod> createDeSerMapMethods() {
    List<ASTCDMethod> deSerMapMethods = new ArrayList<>();
    ASTCDMethod mapAccessor = getCDMethodFacade().createMethod(PUBLIC_ABSTRACT.build(), getMCTypeFacade().createQualifiedType("Map<String," + I_SYMBOL_DE_SER + ">"), "getSymbolDeSers");
    deSerMapMethods.add(mapAccessor);

    // Create simple putDeSer(String key, IDeSer value)
    ASTCDParameter key = getCDParameterFacade().createParameter(String.class, "key");
    ASTCDParameter value = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(I_SYMBOL_DE_SER), "value");
    ASTCDMethod putMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "putSymbolDeSer", key, value);
    replaceTemplate(EMPTY_BODY, putMethod, new StringHookPoint("getSymbolDeSers()" + ".put(key, value);"));
    deSerMapMethods.add(putMethod);

    // Create simple value getDeSer(String key)
    key = getCDParameterFacade().createParameter(String.class, "key");
    ASTMCQualifiedType returnType = getMCTypeFacade().createQualifiedType(I_SYMBOL_DE_SER);
    ASTCDMethod getMethod = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "getSymbolDeSer", key);
    replaceTemplate(EMPTY_BODY, getMethod, new StringHookPoint("return " + "getSymbolDeSers()" + ".get(key);"));
    deSerMapMethods.add(getMethod);

    return deSerMapMethods;
  }

  protected List<ASTCDMethod> createPutDeSerMethods(List<? extends ASTCDType> symbolProds) {
    List<ASTCDMethod> deSerMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, KIND_VAR);

    for (ASTCDType symbolProd : symbolProds) {
      String className = symbolTableService.removeASTPrefix(symbolProd);
      String methodName = String.format(PUTSYMBOLDESER, className);
      String deSerName = symbolTableService.getSymbolDeSerFullName(symbolProd);

      ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), methodName, nameParameter);
      String hook = String.format("putSymbolDeSer(kind, new %s());", deSerName);
      this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(hook));

      deSerMethods.add(method);
    }

    for (DiagramSymbol cdSymbol: symbolTableService.getSuperCDsTransitive()) {
      symbolTableService.getSymbolDefiningProds((ASTCDDefinition) cdSymbol.getAstNode()).forEach(s -> {
        String className = symbolTableService.removeASTPrefix(s);
        String methodName = String.format(PUTSYMBOLDESER, className);
        String deSerName = symbolTableService.getSymbolDeSerFullName(s, cdSymbol);

        ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), methodName, nameParameter);
        String hook = String.format("putSymbolDeSer(kind, new %s());", deSerName);
        this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(hook));

        deSerMethods.add(method);
      });
    }

    return deSerMethods;
  }

  public boolean isGlobalScopeInterfaceTop() {
    return isGlobalScopeInterfaceTop;
  }

  public void setGlobalScopeInterfaceTop(boolean globalScopeInterfaceTop) {
    isGlobalScopeInterfaceTop = globalScopeInterfaceTop;
  }

}
