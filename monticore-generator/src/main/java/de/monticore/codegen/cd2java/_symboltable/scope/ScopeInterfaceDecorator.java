package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;
import static de.monticore.codegen.cd2java.data.ListSuffixDecorator.LIST_SUFFIX_S;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;

public class ScopeInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  public ScopeInterfaceDecorator(final GlobalExtensionManagement glex,
                                 final SymbolTableService symbolTableService,
                                 final VisitorService visitorService) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String scopeInterfaceName = INTERFACE_PREFIX + input.getCDDefinition().getName() + SCOPE_SUFFIX;

    List<ASTCDType> symbolClasses = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    List<CDDefinitionSymbol> superCDsTransitive = symbolTableService.getSuperCDsTransitive();
    List<ASTMCQualifiedType> superScopeInterfaces = superCDsTransitive
        .stream()
        .map(symbolTableService::getScopeInterfaceType)
        .collect(Collectors.toList());
    if (superScopeInterfaces.isEmpty()) {
      superScopeInterfaces.add(getCDTypeFacade().createQualifiedType(SCOPE_INTERFACE_FULL_NAME));
    }


    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(scopeInterfaceName)
        .setModifier(PUBLIC.build())
        .addAllInterfaces(superScopeInterfaces)
        .addAllCDMethods(createAlreadyResolvedMethods(symbolClasses))
        .addAllCDMethods(createResolveMethods(symbolClasses))
        .addAllCDMethods(createSubScopesMethods(scopeInterfaceName))
        .addAllCDMethods(createEnclosingScopeMethods(scopeInterfaceName))
        .addCDMethod(createAcceptMethod())
        .build();
  }

  protected List<ASTCDMethod> createAlreadyResolvedMethods(List<? extends ASTCDType> symbolProds) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolProd : symbolProds) {
      Optional<String> definingSymbolTypeName = symbolTableService.getDefiningSymbolSimpleName(symbolProd);
      if (definingSymbolTypeName.isPresent()) {
        String alreadyResolvedName = StringTransformations.capitalize(Names.getSimpleName(definingSymbolTypeName.get())) + LIST_SUFFIX_S + ALREADY_RESOLVED;
        methodList.add(getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, getCDTypeFacade().createBooleanType(), "is" + alreadyResolvedName));
        ASTCDParameter parameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "symbolAlreadyResolved");
        methodList.add(getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, "set" + alreadyResolvedName, parameter));
      }
    }
    return methodList;
  }


  protected List<ASTCDMethod> createResolveMethods(List<? extends ASTCDType> symbolProds) {
    List<ASTCDMethod> resolveMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, "name");
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(ACCESS_MODIFIER), "modifier");
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "foundSymbols");

    for (ASTCDType symbolProd : symbolProds) {
      String className = symbolTableService.removeASTPrefix(symbolProd);
      String symbolFullTypeName = symbolTableService.getSymbolFullName(symbolProd);
      ASTMCOptionalType optSymbol = getCDTypeFacade().createOptionalTypeOf(symbolFullTypeName);
      ASTMCType listSymbol = getCDTypeFacade().createCollectionTypeOf(symbolFullTypeName);
      ASTMCType setSymbol = getCDTypeFacade().createSetTypeOf(symbolFullTypeName);

      ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(getCDTypeFacade()
          .createTypeByDefinition(PREDICATE + "<" + symbolFullTypeName + ">"), "predicate");

      String resolveMethodName = String.format(RESOLVE, className);
      resolveMethods.add(createResolveNameMethod(resolveMethodName, optSymbol, nameParameter));
      resolveMethods.add(createResolveNameModifierMethod(resolveMethodName, optSymbol, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveNameModifierPredicateMethod(resolveMethodName, optSymbol, nameParameter,
          accessModifierParameter, predicateParameter));
      resolveMethods.add(createResolveFoundSymbolsNameModifierMethod(resolveMethodName, optSymbol, foundSymbolsParameter,
          nameParameter, accessModifierParameter));

      String resolveDownMethodName = String.format(RESOLVE_DOWN, className);
      resolveMethods.add(createResolveDownNameMethod(resolveDownMethodName, optSymbol, nameParameter));
      resolveMethods.add(createResolveDownNameModifierMethod(resolveDownMethodName, optSymbol, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveDownNameModifierPredicateMethod(resolveDownMethodName, optSymbol, nameParameter,
          accessModifierParameter, predicateParameter));

      String resolveDownManyMethodName = String.format(RESOLVE_DOWN_MANY, className);
      resolveMethods.add(createResolveDownManyNameMethod(resolveDownManyMethodName, listSymbol, nameParameter));
      resolveMethods.add(createResolveDownManyNameModifierMethod(resolveDownManyMethodName, listSymbol, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveDownManyNameModifierPredicateMethod(resolveDownManyMethodName, listSymbol, nameParameter,
          accessModifierParameter, predicateParameter));
      resolveMethods.add(createResolveDownManyFoundSymbolsNameModifierPredicateMethod(resolveDownManyMethodName, className, symbolFullTypeName,
          listSymbol, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));

      String resolveLocallyMethodName = String.format(RESOLVE_LOCALLY, className);
      resolveMethods.add(createResolveLocallyNameMethod(resolveLocallyMethodName, optSymbol, nameParameter));

      String resolveLocallyManyMethodName = String.format(RESOLVE_LOCALLY_MANY, className);
      resolveMethods.add(createResolveLocallyManyMethod(resolveLocallyManyMethodName, className, symbolFullTypeName,
          setSymbol, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));

      String resolveImportedMethodName = String.format(RESOLVE_IMPORTED, className);
      resolveMethods.add(createResolveImportedNameMethod(resolveImportedMethodName, className, optSymbol, nameParameter));

      String resolveManyMethodName = String.format(RESOLVE_MANY, className);
      resolveMethods.add(createResolveManyNameMethod(resolveManyMethodName, listSymbol, nameParameter));
      resolveMethods.add(createResolveManyNameModifierMethod(resolveManyMethodName, listSymbol, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveManyNameModifierPredicateMethod(resolveManyMethodName, listSymbol, nameParameter,
          accessModifierParameter, predicateParameter));
      resolveMethods.add(createResolveManyNamePredicateMethod(resolveManyMethodName, listSymbol, nameParameter, predicateParameter));
      resolveMethods.add(createResolveManyFoundSymbolsNameModifierMethod(resolveManyMethodName, listSymbol, foundSymbolsParameter,
          nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveManyFoundSymbolsNameModifierPredicateMethod(resolveManyMethodName, className, symbolFullTypeName,
          listSymbol, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));

      String resolveAdaptedLocallyManyMethodName = String.format(RESOLVE_ADAPTED_LOCALLY_MANY, className);
      resolveMethods.add(createResolveAdaptedLocallyManyMethod(resolveAdaptedLocallyManyMethodName,
          listSymbol, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));


      String filterMethodName = String.format(FILTER, className);
      resolveMethods.add(createFilterMethod(filterMethodName, symbolFullTypeName, optSymbol, nameParameter));

      String continueWithEnclosingScopeMethodName = String.format(CONTINUE_WITH_ENCLOSING_SCOPE, className);
      resolveMethods.add(createContinueWithEnclosingScopeMethod(continueWithEnclosingScopeMethodName, className,
          listSymbol, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));

      String continueAsSubScopeMethodName = String.format(CONTINUE_AS_SUB_SCOPE, className);
      resolveMethods.add(createContinueAsSubScopeMethod(continueAsSubScopeMethodName, className,
          listSymbol, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));


      String getSymbolsMethodName = "get" + className + "Symbols";
      resolveMethods.add(createGetSymbolsMethod(getSymbolsMethodName, symbolFullTypeName));

      String getLocalSymbolsMethodName = "getLocal" + className + "Symbols";
      resolveMethods.add(createGetLocalSymbolsMethod(getLocalSymbolsMethodName, className, listSymbol));

      ASTCDParameter symbolParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(symbolFullTypeName), "symbol");
      resolveMethods.add(createAddMethod(symbolParameter));
      resolveMethods.add(createRemoveMethod(symbolParameter));
    }

    return resolveMethods;
  }

  protected ASTCDMethod createResolveNameMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.iscope.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));

    return method;
  }

  protected ASTCDMethod createResolveNameModifierMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter,
                                                        ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.iscope.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));

    return method;
  }

  protected ASTCDMethod createResolveNameModifierPredicateMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter,
                                                                 ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.iscope.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));
    return method;
  }

  protected ASTCDMethod createResolveFoundSymbolsNameModifierMethod(String methodName, ASTMCType returnType, ASTCDParameter foundSymbolsParameter,
                                                                    ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.iscope.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));
    return method;
  }

  protected ASTCDMethod createResolveDownNameMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.iscope.ResolveDelegate", methodName,
        new ArrayList<>(method.getCDParameterList())));

    return method;
  }

  protected ASTCDMethod createResolveDownNameModifierMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter,
                                                            ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.iscope.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));

    return method;
  }

  protected ASTCDMethod createResolveDownNameModifierPredicateMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter,
                                                                     ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.iscope.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));
    return method;
  }

  protected ASTCDMethod createResolveDownManyNameMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return this." + methodName + "(false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, x -> true);"));

    return method;
  }

  protected ASTCDMethod createResolveDownManyNameModifierMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter,
                                                                ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return this." + methodName + "(false, name, modifier, x -> true);"));
    return method;
  }

  protected ASTCDMethod createResolveDownManyNameModifierPredicateMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter,
                                                                         ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return this." + methodName + "(false, name, modifier, predicate);"));
    return method;
  }

  protected ASTCDMethod createResolveDownManyFoundSymbolsNameModifierPredicateMethod(String methodName, String className, String fullSymbolName,
                                                                                     ASTMCType returnType, ASTCDParameter foundSymbolsParameter,
                                                                                     ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                                                     ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.iscope.ResolveDownMany",
        className, fullSymbolName, symbolTableService.getScopeInterfaceFullName()));
    return method;
  }

  protected ASTCDMethod createResolveLocallyNameMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint(" return getResolvedOrThrowException(\n" +
            "   this." + methodName + "Many(false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, x -> true));"
        ));
    return method;
  }

  protected ASTCDMethod createResolveImportedNameMethod(String methodName, String className, ASTMCType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this.resolve" + className + "Locally(name);"));
    return method;
  }

  protected ASTCDMethod createResolveManyNameMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + methodName + "(name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION);"));

    return method;
  }

  protected ASTCDMethod createResolveManyNameModifierMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter,
                                                            ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + methodName + "(name, modifier, x -> true);"));

    return method;
  }

  protected ASTCDMethod createResolveManyNameModifierPredicateMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter,
                                                                     ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + methodName + "(false, name, modifier, predicate);"));
    return method;
  }


  protected ASTCDMethod createResolveManyNamePredicateMethod(String methodName, ASTMCType returnType, ASTCDParameter nameParameter,
                                                             ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + methodName + "(false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, predicate);"));
    return method;
  }


  protected ASTCDMethod createResolveManyFoundSymbolsNameModifierMethod(String methodName, ASTMCType returnType, ASTCDParameter foundSymbolsParameter,
                                                                        ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + methodName + "(foundSymbols, name, modifier, x -> true);"));
    return method;
  }

  protected ASTCDMethod createResolveManyFoundSymbolsNameModifierPredicateMethod(String methodName, String className, String fullSymbolName,
                                                                                 ASTMCType returnType, ASTCDParameter foundSymbolsParameter,
                                                                                 ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                                                 ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_symboltable.scope.iscope.ResolveMany", className, fullSymbolName));
    return method;
  }

  protected ASTCDMethod createResolveAdaptedLocallyManyMethod(String methodName, ASTMCType returnType, ASTCDParameter foundSymbolsParameter,
                                                              ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                              ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return new java.util.ArrayList<>();"));
    return method;
  }

  protected ASTCDMethod createResolveLocallyManyMethod(String methodName, String className, String fullSymbolName,
                                                       ASTMCType returnType, ASTCDParameter foundSymbolsParameter,
                                                       ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                       ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_symboltable.scope.iscope.ResolveManyLocally", className, fullSymbolName));
    return method;
  }

  protected ASTCDMethod createFilterMethod(String methodName, String fullSymbolName, ASTMCType returnType, ASTCDParameter nameParameter) {
    ASTMCType symbolsMap = getCDTypeFacade().createTypeByDefinition(String.format(SYMBOLS_MULTI_MAP, fullSymbolName));
    ASTCDParameter symbolsParameter = getCDParameterFacade().createParameter(symbolsMap, "symbols");

    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, symbolsParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.iscope.Filter", fullSymbolName));
    return method;
  }

  protected ASTCDMethod createContinueWithEnclosingScopeMethod(String methodName, String className,
                                                               ASTMCType returnType, ASTCDParameter foundSymbolsParameter,
                                                               ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                               ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_symboltable.scope.iscope.ContinueWithEnclosingScope", className));
    return method;
  }

  protected ASTCDMethod createContinueAsSubScopeMethod(String methodName, String className,
                                                       ASTMCType returnType, ASTCDParameter foundSymbolsParameter,
                                                       ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                       ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_symboltable.scope.iscope.ContinueAsSubScope", className));
    return method;
  }

  protected ASTCDMethod createGetSymbolsMethod(String methodName, String fullSymbolName) {
    ASTMCType symbolsMap = getCDTypeFacade().createTypeByDefinition(String.format(SYMBOLS_MULTI_MAP, fullSymbolName));

    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, symbolsMap, methodName);
  }

  protected ASTCDMethod createGetLocalSymbolsMethod(String methodName, String className, ASTMCType returnType) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return get" + className + "Symbols().values();"));
    return method;
  }

  protected ASTCDMethod createAddMethod(ASTCDParameter symbolParameter) {
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, "add", symbolParameter);
  }

  protected ASTCDMethod createRemoveMethod(ASTCDParameter symbolParameter) {
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, "remove", symbolParameter);
  }

  protected List<ASTCDMethod> createSubScopesMethods(String scopeInterface) {
    ASTMCType collectionType = getCDTypeFacade().createCollectionTypeOf("? extends " + scopeInterface);
    ASTCDMethod getSubScopes = getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, collectionType, "getSubScopes");

    ASTCDParameter subScopeParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), "subScope");
    ASTCDMethod addSubScope = getCDMethodFacade().createMethod(PUBLIC, "addSubScope", subScopeParameter);
    this.replaceTemplate(EMPTY_BODY, addSubScope,
        new StringHookPoint("Log.error(\"0xA7013x558 The method \\\"addSubScope\\\" of interface \\\"IAutomataScope\\\" is not implemented.\");"));

    ASTCDMethod removeSubScope = getCDMethodFacade().createMethod(PUBLIC, "removeSubScope", subScopeParameter);
    this.replaceTemplate(EMPTY_BODY, removeSubScope,
        new StringHookPoint("Log.error(\"0xA7013x558 The method \\\"removeSubScope\\\" of interface \\\"IAutomataScope\\\" is not implemented.\");"));

    return new ArrayList<>(Arrays.asList(getSubScopes, addSubScope, removeSubScope));
  }

  protected List<ASTCDMethod> createEnclosingScopeMethods(String scopeInterface) {
    ASTMCType optType = getCDTypeFacade().createOptionalTypeOf("? extends " + scopeInterface);
    ASTCDMethod getEnclosingScope = getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, optType, "getEnclosingScope");

    ASTCDParameter enclosingScopeParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), "enclosingScope");
    ASTCDMethod setEnclosingScope = getCDMethodFacade().createMethod(PUBLIC, "setEnclosingScope", enclosingScopeParameter);
    this.replaceTemplate(EMPTY_BODY, setEnclosingScope,
        new StringHookPoint("Log.error(\"0xA7012x558 The method \\\"setEnclosingScope\\\" of interface \\\"IAutomataScope\\\" is not implemented.\");"));

    return new ArrayList<>(Arrays.asList(getEnclosingScope, setEnclosingScope));
  }

  protected ASTCDMethod createAcceptMethod() {
    String ownScopeVisitor = visitorService.getScopeVisitorFullName();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(ownScopeVisitor), VISITOR_PREFIX);
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, ACCEPT_METHOD, parameter);
  }

}
