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
      resolveMethods.add(createResolveNameMethods(resolveMethodName, optReturnType, nameParameter));
      resolveMethods.add(createResolveNameModifierMethods(resolveMethodName, optReturnType, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveNameModifierPredicateMethod(resolveMethodName, optReturnType, nameParameter,
          accessModifierParameter, predicateParameter));
      resolveMethods.add(createResolveFoundSymbolsNameModifierMethod(resolveMethodName, optReturnType, foundSymbolsParameter,
          nameParameter, accessModifierParameter));

      String resolveDownMethodName = String.format(RESOLVE_DOWN, className);
      resolveMethods.add(createResolveDownNameMethods(resolveDownMethodName, optReturnType, nameParameter));
      resolveMethods.add(createResolveDownNameModifierMethods(resolveDownMethodName, optReturnType, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveDownNameModifierPredicateMethod(resolveDownMethodName, optReturnType, nameParameter,
          accessModifierParameter, predicateParameter));

      String resolveDownManyMethodName = String.format(RESOLVE_DOWN_MANY, className);
      resolveMethods.add(createResolveDownManyNameMethods(resolveDownManyMethodName, listReturnType, nameParameter));
      resolveMethods.add(createResolveDownManyNameModifierMethods(resolveDownManyMethodName, listReturnType, nameParameter, accessModifierParameter));
      resolveMethods.add(createResolveDownManyNameModifierPredicateMethod(resolveDownManyMethodName, listReturnType, nameParameter,
          accessModifierParameter, predicateParameter));
      resolveMethods.add(createResolveDownManyFoundSymbolsNameModifierMethod(resolveDownManyMethodName, className, symbolFullTypeName,
          listReturnType, foundSymbolsParameter, nameParameter, accessModifierParameter));

      String resolveLocallyMethodName = String.format(RESOLVE_LOCALLY, className);
      resolveMethods.add(createResolveLocallyNameMethods(resolveLocallyMethodName, optReturnType, nameParameter));

      String resolveLocallyManyMethodName = String.format(RESOLVE_LOCALLY_MANY, className);
      resolveMethods.add(createResolveLocallyManyMethod(resolveLocallyManyMethodName, className, symbolFullTypeName,
          setReturnType, foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter));

      String resolveImportedMethodName = String.format(RESOLVE_IMPORTED, className);
      resolveMethods.add(createResolveImportedNameMethods(resolveImportedMethodName, optReturnType, nameParameter));

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
    }

    return resolveMethods;
  }

  protected ASTCDMethod createResolveNameMethods(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.ResolveDelegate",
        methodName, new ArrayList<>(method.getCDParameterList())));

    return method;
  }

  protected ASTCDMethod createResolveNameModifierMethods(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
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

  protected ASTCDMethod createResolveDownNameMethods(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.scope.i.ResolveDelegate", methodName,
        new ArrayList<>(method.getCDParameterList())));

    return method;
  }

  protected ASTCDMethod createResolveDownNameModifierMethods(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
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

  protected ASTCDMethod createResolveDownManyNameMethods(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return this." + methodName + "(false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, x -> true);"));

    return method;
  }

  protected ASTCDMethod createResolveDownManyNameModifierMethods(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter,
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

  protected ASTCDMethod createResolveLocallyNameMethods(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint(" return getResolvedOrThrowException(\n" +
            "   this." + methodName + "Many(false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, x -> true));"
        ));
    return method;
  }

  protected ASTCDMethod createResolveImportedNameMethods(String methodName, ASTMCReturnType returnType, ASTCDParameter nameParameter) {
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
}
