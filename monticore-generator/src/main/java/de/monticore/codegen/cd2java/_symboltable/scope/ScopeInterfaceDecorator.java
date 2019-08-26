package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.data.ListSuffixDecorator.LIST_SUFFIX_S;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;

public class ScopeInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;

  public ScopeInterfaceDecorator(final GlobalExtensionManagement glex,
                                 final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String scopeInterfaceName = INTERFACE_PREFIX + input.getCDDefinition().getName() + SCOPE_SUFFIX;

    List<ASTCDClass> symbolClasses = symbolTableService.getSymbolClasses(input.getCDDefinition().getCDClassList());
    List<ASTCDInterface> symbolInterfaces = symbolTableService.getSymbolInterfaces(input.getCDDefinition().getCDInterfaceList());

    List<CDDefinitionSymbol> superCDsTransitive = symbolTableService.getSuperCDsTransitive();
    List<ASTMCQualifiedType> superScopeInterfaces = superCDsTransitive
        .stream()
        .map(symbolTableService::getScopeInterfaceType)
        .collect(Collectors.toList());


    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(scopeInterfaceName)
        .setModifier(PUBLIC.build())
        .addAllInterfaces(superScopeInterfaces)
        .addAllCDMethods(createAlreadyResolvedMethods(symbolClasses))
        .addAllCDMethods(createAlreadyResolvedMethods(symbolInterfaces))
        .addAllCDMethods(createResolveMethods(symbolClasses))
        .addAllCDMethods(createResolveMethods(symbolInterfaces))
        .build();
  }

  protected List<ASTCDMethod> createAlreadyResolvedMethods(List<? extends ASTCDType> symbolProds) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolProd : symbolProds) {
      ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createBooleanType()).build();
      String alreadyResolvedName = StringTransformations.capitalize(symbolTableService.getSymbolSimpleTypeName(symbolProd)) + LIST_SUFFIX_S + ALREADY_RESOLVED;
      methodList.add(getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, returnType, "is" + alreadyResolvedName));
      ASTCDParameter parameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "symbolAlreadyResolved");
      methodList.add(getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, "set" + alreadyResolvedName, parameter));
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
      String symbolFullTypeName = symbolTableService.getSymbolFullTypeName(symbolProd);
      ASTMCOptionalType optSymbol = getCDTypeFacade().createOptionalTypeOf(symbolFullTypeName);
      ASTMCReturnType optReturnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(optSymbol).build();
      ASTMCType listSymbol = getCDTypeFacade().createCollectionTypeOf(symbolFullTypeName);
      ASTMCReturnType listReturnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(listSymbol).build();
      ASTMCType setSymbol = getCDTypeFacade().createSetTypeOf(symbolFullTypeName);
      ASTMCReturnType setReturnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(setSymbol).build();

      ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(getCDTypeFacade()
          .createTypeByDefinition(PREDICATE + "<" + symbolFullTypeName + ">"), "predicate");

      String resolveMethodName = String.format(RESOLVE, className);
      resolveMethods.add(createResolveNameMethod(resolveMethodName, optReturnType, nameParameter));
      resolveMethods.add(createResolveNameModifierMethod(resolveMethodName, optReturnType, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveNameModifierPredicateMethod(resolveMethodName, optReturnType, nameParameter,
          accessModifierParameter, predicateParameter));
      resolveMethods.add(createResolveFoundSymbolsNameModifierMethod(resolveMethodName, optReturnType, foundSymbolsParameter,
          nameParameter, accessModifierParameter));

      String resolveDownMethodName = String.format(RESOLVE_DOWN, className);
      resolveMethods.add(createResolveDownNameMethod(resolveDownMethodName, optReturnType, nameParameter));
      resolveMethods.add(createResolveDownNameModifierMethod(resolveDownMethodName, optReturnType, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveDownNameModifierPredicateMethod(resolveDownMethodName, optReturnType, nameParameter,
          accessModifierParameter, predicateParameter));

      String resolveDownManyMethodName = String.format(RESOLVE_DOWN_MANY, className);
      resolveMethods.add(createResolveDownManyNameMethod(resolveDownManyMethodName, listReturnType, nameParameter));
      resolveMethods.add(createResolveDownManyNameModifierMethod(resolveDownManyMethodName, listReturnType, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveDownManyNameModifierPredicateMethod(resolveDownManyMethodName, listReturnType, nameParameter,
          accessModifierParameter, predicateParameter));
      resolveMethods.add(createResolveDownManyFoundSymbolsNameModifierMethod(resolveDownManyMethodName, className, symbolFullTypeName,
          listReturnType, foundSymbolsParameter, nameParameter, accessModifierParameter));

      String resolveLocallyMethodName = String.format(RESOLVE_LOCALLY, className);
      resolveMethods.add(createResolveLocallyNameMethod(resolveLocallyMethodName, optReturnType, nameParameter));

      String resolveLocallyManyMethodName = String.format(RESOLVE_LOCALLY_MANY, className);
      resolveMethods.add(createResolveLocallyManyMethod(resolveLocallyManyMethodName, className, symbolFullTypeName,
          setReturnType, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));

      String resolveImportedMethodName = String.format(RESOLVE_IMPORTED, className);
      resolveMethods.add(createResolveImportedNameMethod(resolveImportedMethodName, optReturnType, nameParameter));

      String resolveManyMethodName = String.format(RESOLVE_MANY, className);
      resolveMethods.add(createResolveManyNameMethod(resolveManyMethodName, listReturnType, nameParameter));
      resolveMethods.add(createResolveManyNameModifierMethod(resolveManyMethodName, listReturnType, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveManyNameModifierPredicateMethod(resolveManyMethodName, listReturnType, nameParameter,
          accessModifierParameter, predicateParameter));
      resolveMethods.add(createResolveManyNamePredicateMethod(resolveManyMethodName, listReturnType, nameParameter, predicateParameter));
      resolveMethods.add(createResolveManyFoundSymbolsNameModifierMethod(resolveManyMethodName, listReturnType, foundSymbolsParameter,
          nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveManyFoundSymbolsNameModifierPredicateMethod(resolveManyMethodName, className, symbolFullTypeName,
          listReturnType, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));

      String resolveAdaptedLocallyManyMethodName = String.format(RESOLVE_ADAPTED_LOCALLY_MANY, className);
      resolveMethods.add(createResolveAdaptedLocallyManyMethod(resolveAdaptedLocallyManyMethodName,
          listReturnType, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));


      String filterMethodName = String.format(FILTER, className);
      resolveMethods.add(createFilterMethod(filterMethodName, symbolFullTypeName, listReturnType, nameParameter));

      String continueWithEnclosingScopeMethodName = String.format(CONTINUE_WITH_ENCLOSING_SCOPE, className);
      resolveMethods.add(createContinueWithEnclosingScopeMethod(continueWithEnclosingScopeMethodName, className,
          listReturnType, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));

      String continueAsSubScopeMethodName = String.format(CONTINUE_AS_SUB_SCOPE, className);
      resolveMethods.add(createContinueAsSubScopeMethod(continueAsSubScopeMethodName, className,
          listReturnType, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));


      String getSymbolsMethodName = "get" + className + "Symbols";
      resolveMethods.add(createGetSymbolsMethod(getSymbolsMethodName, symbolFullTypeName));

      String getLocalSymbolsMethodName = "getLocal" + className + "Symbols";
      resolveMethods.add(createGetLocalSymbolsMethod(getLocalSymbolsMethodName, className, listReturnType));

      ASTCDParameter symbolParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(symbolFullTypeName), "symbol");
      resolveMethods.add(createAddMethod(symbolParameter));
      resolveMethods.add(createRemoveMethod(symbolParameter));
    }

    return resolveMethods;
  }

  protected ASTCDMethod createResolveNameMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));

    return method;
  }

  protected ASTCDMethod createResolveNameModifierMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
                                                        ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));

    return method;
  }

  protected ASTCDMethod createResolveNameModifierPredicateMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
                                                                 ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));
    return method;
  }

  protected ASTCDMethod createResolveFoundSymbolsNameModifierMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter foundSymbolsParameter,
                                                                    ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));
    return method;
  }

  protected ASTCDMethod createResolveDownNameMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.ResolveDelegate", methodName,
        new ArrayList<>(method.getCDParameterList())));

    return method;
  }

  protected ASTCDMethod createResolveDownNameModifierMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
                                                            ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));

    return method;
  }

  protected ASTCDMethod createResolveDownNameModifierPredicateMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
                                                                     ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));
    return method;
  }

  protected ASTCDMethod createResolveDownManyNameMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return this." + methodName + "(false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, x -> true);"));

    return method;
  }

  protected ASTCDMethod createResolveDownManyNameModifierMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
                                                                ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return this." + methodName + "(false, name, modifier, x -> true);"));
    return method;
  }

  protected ASTCDMethod createResolveDownManyNameModifierPredicateMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
                                                                         ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return this." + methodName + "(false, name, modifier, predicate);"));
    return method;
  }

  protected ASTCDMethod createResolveDownManyFoundSymbolsNameModifierMethod(String methodName, String className, String fullSymbolName,
                                                                            ASTMCReturnType returnType, ASTCDParameter foundSymbolsParameter,
                                                                            ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.ResolveDownMany",
        className, fullSymbolName, symbolTableService.getScopeInterfaceTypeName()));
    return method;
  }

  protected ASTCDMethod createResolveLocallyNameMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint(" return getResolvedOrThrowException(\n" +
            "   this." + methodName + "Many(false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, x -> true));"
        ));
    return method;
  }

  protected ASTCDMethod createResolveImportedNameMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this.resolveAutomatonLocally(name);"));
    return method;
  }

  protected ASTCDMethod createResolveManyNameMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + methodName + "(name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION);"));

    return method;
  }

  protected ASTCDMethod createResolveManyNameModifierMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
                                                            ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + methodName + "(name, modifier, x -> true);"));

    return method;
  }

  protected ASTCDMethod createResolveManyNameModifierPredicateMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
                                                                     ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + methodName + "(false, name, modifier, predicate);"));
    return method;
  }


  protected ASTCDMethod createResolveManyNamePredicateMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
                                                             ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + methodName + "(false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, predicate);"));
    return method;
  }


  protected ASTCDMethod createResolveManyFoundSymbolsNameModifierMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter foundSymbolsParameter,
                                                                        ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + methodName + "(foundSymbols, name, modifier, x -> true);"));
    return method;
  }

  protected ASTCDMethod createResolveManyFoundSymbolsNameModifierPredicateMethod(String methodName, String className, String fullSymbolName,
                                                                                 ASTMCReturnType returnType, ASTCDParameter foundSymbolsParameter,
                                                                                 ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                                                 ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_symboltable.scope.i.ResolveMany", className, fullSymbolName));
    return method;
  }

  protected ASTCDMethod createResolveAdaptedLocallyManyMethod(String methodName, ASTMCReturnType returnType, ASTCDParameter foundSymbolsParameter,
                                                              ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                              ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return new java.util.ArrayList<>();"));
    return method;
  }

  protected ASTCDMethod createResolveLocallyManyMethod(String methodName, String className, String fullSymbolName,
                                                       ASTMCReturnType returnType, ASTCDParameter foundSymbolsParameter,
                                                       ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                       ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_symboltable.scope.i.ResolveManyLocally", className, fullSymbolName));
    return method;
  }

  protected ASTCDMethod createFilterMethod(String methodName, String fullSymbolName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTMCType symbolsMap = getCDTypeFacade().createTypeByDefinition(String.format(SYMBOLS_MULTI_MAP, fullSymbolName));
    ASTCDParameter symbolsParameter = getCDParameterFacade().createParameter(symbolsMap, "symbols");

    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        nameParameter, symbolsParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.Filter", fullSymbolName));
    return method;
  }

  protected ASTCDMethod createContinueWithEnclosingScopeMethod(String methodName, String className,
                                                               ASTMCReturnType returnType, ASTCDParameter foundSymbolsParameter,
                                                               ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                               ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_symboltable.scope.i.ContinueWithEnclosingScope", className));
    return method;
  }

  protected ASTCDMethod createContinueAsSubScopeMethod(String methodName, String className,
                                                       ASTMCReturnType returnType, ASTCDParameter foundSymbolsParameter,
                                                       ASTCDParameter nameParameter, ASTCDParameter accessModifierParameter,
                                                       ASTCDParameter predicateParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_symboltable.scope.i.ContinueAsSubScope", className));
    return method;
  }

  protected ASTCDMethod createGetSymbolsMethod(String methodName, String fullSymbolName) {
    ASTMCType symbolsMap = getCDTypeFacade().createTypeByDefinition(String.format(SYMBOLS_MULTI_MAP, fullSymbolName));
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(symbolsMap).build();

    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, returnType, methodName);
  }

  protected ASTCDMethod createGetLocalSymbolsMethod(String methodName, String className, ASTMCReturnType returnType) {
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
}
