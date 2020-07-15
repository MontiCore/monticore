/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;

/**
 * creates an artifactScope interface from a grammar
 */
public class ArtifactScopeInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  protected final VisitorService visitorService;

  protected static final String TEMPLATE_PATH = "_symboltable.iartifactscope.";

  public ArtifactScopeInterfaceDecorator(final GlobalExtensionManagement glex,
                                final SymbolTableService symbolTableService,
                                final VisitorService visitorService,
                                final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String artifactScopeInterfaceSimpleName = symbolTableService.getArtifactScopeInterfaceSimpleName();
    List<ASTCDType> symbolProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(artifactScopeInterfaceSimpleName)
        .setModifier(PUBLIC.build())
        .addInterface(symbolTableService.getScopeInterfaceType())
        .addInterface(getMCTypeFacade().createQualifiedType(I_ARTIFACT_SCOPE_TYPE))
        .addAllCDMethods(createPackageNameAttributeMethods())
        .addAllCDMethods(createImportsAttributeMethods())
        .addCDMethod(createGetTopLevelSymbolMethod(symbolProds))
        .addCDMethod(createCheckIfContinueAsSubScopeMethod())
        .addCDMethod(createGetRemainingNameForResolveDownMethod())
        .addAllCDMethods(createContinueWithEnclosingScopeMethods(symbolProds, symbolTableService.getCDSymbol()))
        .addAllCDMethods(createSuperContinueWithEnclosingScopeMethods())
        .build();
  }

  protected List<ASTCDMethod> createPackageNameAttributeMethods() {
    ASTCDMethod getMethod = getCDMethodFacade()
        .createMethod(PUBLIC_ABSTRACT, getMCTypeFacade().createStringType(), "getPackageName");
    ASTCDMethod setMethod = getCDMethodFacade()
        .createMethod(PUBLIC_ABSTRACT,  "setPackageName",
            getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "packageName"));
    return Lists.newArrayList(getMethod, setMethod);
  }

  protected List<ASTCDMethod> createImportsAttributeMethods() {
    ASTMCListType type = getMCTypeFacade().createListTypeOf(IMPORT_STATEMENT);
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(PRIVATE, type, "imports");
    List<ASTCDMethod> methods = methodDecorator.decorate(attr).stream()
        .filter(m -> !(m.getName().equals("getImportList") || m.getName().equals("setImportList")))
        .collect(Collectors.toList());

    ASTCDMethod getMethod = getCDMethodFacade()
        .createMethod(PUBLIC_ABSTRACT, type, "getImportList");
    methods.add(getMethod);

    ASTCDMethod setMethod = getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, "setImportList",
        getCDParameterFacade().createParameter(type, "imports"));
    methods.add(setMethod);

    return methods;
  }

  protected ASTCDAttribute createImportsAttribute() {
    ASTModifier modifier = PRIVATE.build();
    symbolTableService.addDeprecatedStereotype(modifier, Optional.empty());
    return getCDAttributeFacade()
        .createAttribute(modifier, getMCTypeFacade().createListTypeOf(IMPORT_STATEMENT), "imports");
  }

  protected ASTCDMethod createGetTopLevelSymbolMethod(List<ASTCDType> symbolProds) {
    ArrayList<ASTCDType> symbolProdsWithSuper = new ArrayList<>(symbolProds);
    symbolProdsWithSuper.addAll(getSuperSymbols());
    List<String> symbolNames = symbolProdsWithSuper
        .stream()
        .map(ASTCDType::getName)
        .map(symbolTableService::removeASTPrefix)
        .collect(Collectors.toList());
    ASTCDMethod getTopLevelSymbol = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createOptionalTypeOf(I_SYMBOL), "getTopLevelSymbol");
    this.replaceTemplate(EMPTY_BODY, getTopLevelSymbol, new TemplateHookPoint(TEMPLATE_PATH + "GetTopLevelSymbol", symbolNames));
    return getTopLevelSymbol;
  }

  protected ASTCDMethod createCheckIfContinueAsSubScopeMethod() {
    ASTCDParameter symbolNameParam = getCDParameterFacade().createParameter(String.class, "symbolName");
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createBooleanType(), "checkIfContinueAsSubScope", symbolNameParam);
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new TemplateHookPoint(TEMPLATE_PATH + "CheckIfContinueAsSubScope"));
    return getNameMethod;
  }

  protected ASTCDMethod createGetRemainingNameForResolveDownMethod() {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(String.class, "symbolName");
    ASTCDMethod getRemainingNameForResolveDown = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "getRemainingNameForResolveDown", parameter);
    this.replaceTemplate(EMPTY_BODY, getRemainingNameForResolveDown, new TemplateHookPoint(TEMPLATE_PATH + "GetRemainingNameForResolveDown"));
    return getRemainingNameForResolveDown;
  }

  protected List<ASTCDMethod> createContinueWithEnclosingScopeMethods(List<ASTCDType> symbolProds, CDDefinitionSymbol definitionSymbol) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTCDParameter parameterFoundSymbols = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);
    ASTCDParameter parameterName = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), NAME_VAR);
    ASTCDParameter parameterModifier = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    String globalScope = symbolTableService.getGlobalScopeFullName();

    for (ASTCDType type : symbolProds) {
      Optional<String> definingSymbolFullName = symbolTableService.getDefiningSymbolFullName(type, definitionSymbol);
      String className = symbolTableService.removeASTPrefix(type);

      if (definingSymbolFullName.isPresent()) {
        ASTCDParameter parameterPredicate = getCDParameterFacade().createParameter(getMCTypeFacade().createBasicGenericTypeOf(
            PREDICATE, definingSymbolFullName.get()), PREDICATE_VAR);
        String methodName = String.format(CONTINUE_WITH_ENCLOSING_SCOPE, className);

        ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createListTypeOf(definingSymbolFullName.get()),
            methodName, parameterFoundSymbols, parameterName, parameterModifier, parameterPredicate);
        this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
            TEMPLATE_PATH + "ContinueWithEnclosingScope4ArtifactScope", definingSymbolFullName.get(), className, globalScope));
        methodList.add(method);
      }
    }
    return methodList;
  }

  protected List<ASTCDMethod> createSuperContinueWithEnclosingScopeMethods() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      // only filter for types which define a symbol
      List<ASTCDType> symbolProds = cdDefinitionSymbol.getTypes()
          .stream()
          .filter(t -> t.isPresentAstNode())
          .filter(t -> t.getAstNode().isPresentModifier())
          .filter(t -> symbolTableService.hasSymbolStereotype(t.getAstNode().getModifier()))
          .filter(CDTypeSymbol::isPresentAstNode)
          .map(CDTypeSymbol::getAstNode)
          .collect(Collectors.toList());
      methodList.addAll(createContinueWithEnclosingScopeMethods(symbolProds, cdDefinitionSymbol));
    }
    return methodList;
  }

  public List<ASTCDType> getSuperSymbols() {
    List<ASTCDType> symbolAttributes = new ArrayList<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.isPresentAstNode() && type.getAstNode().isPresentModifier()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          symbolAttributes.add(type.getAstNode());
        }
      }
    }
    return symbolAttributes;
  }
}
