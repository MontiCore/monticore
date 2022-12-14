/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.google.common.collect.Lists;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cdbasis._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdinterfaceandenum._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

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
    ASTCDInterface clazz = CD4AnalysisMill.cDInterfaceBuilder()
            .setName(artifactScopeInterfaceSimpleName)
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder()
                    .addAllSuperclass(getSuperArtifactScopeInterfaces())
                    .addSuperclass(symbolTableService.getScopeInterfaceType()).build())
            .addAllCDMembers(createImportsAttributeMethods())
            .addCDMember(createGetTopLevelSymbolMethod(symbolProds))
            .addCDMember(createCheckIfContinueAsSubScopeMethod())
            .addCDMember(createGetRemainingNameForResolveDownMethod())
            .addCDMember(createGetFullNameMethod())
            .addAllCDMembers(createContinueWithEnclosingScopeMethods(symbolProds, symbolTableService.getCDSymbol()))
            .addAllCDMembers(createSuperContinueWithEnclosingScopeMethods())
            .build();
    CD4C.getInstance().addImport(clazz, "de.monticore.symboltable.*");
    return clazz;
  }

  protected List<ASTMCObjectType> getSuperArtifactScopeInterfaces(){
    return getSuperArtifactScopeInterfaces(symbolTableService.getCDSymbol());
  }

  protected List<ASTMCObjectType> getSuperArtifactScopeInterfaces(DiagramSymbol symbol){
    List<ASTMCObjectType> result = new ArrayList<>();
    for (DiagramSymbol superGrammar : symbolTableService.getSuperCDsDirect(symbol)) {
      if (!superGrammar.isPresentAstNode()) {
        Log.error("0xA4324 Unable to load AST of '" + superGrammar.getFullName()
            + "' that is supergrammar of '" + symbolTableService.getCDName() + "'.");
        continue;
      }
      result.add(symbolTableService.getArtifactScopeInterfaceType(superGrammar));
    }
    if (result.isEmpty()) {
      result.add(getMCTypeFacade().createQualifiedType(I_ARTIFACT_SCOPE_TYPE));
    }
    return result;
  }

  protected List<ASTCDMethod> createImportsAttributeMethods() {
    ASTMCListType type = getMCTypeFacade().createListTypeOf(IMPORT_STATEMENT);
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(PROTECTED.build(), type, "imports");
    List<ASTCDMethod> methods = methodDecorator.decorate(attr).stream()
        .filter(m -> !(m.getName().equals("getImportsList") || m.getName().equals("setImportsList")))
        .collect(Collectors.toList());

    ASTCDMethod getMethod = getCDMethodFacade()
        .createMethod(PUBLIC_ABSTRACT.build(), type, "getImportsList");
    methods.add(getMethod);

    ASTCDMethod setMethod = getCDMethodFacade().createMethod(PUBLIC_ABSTRACT.build(), "setImportsList",
        getCDParameterFacade().createParameter(type, "imports"));
    methods.add(setMethod);

    return methods;
  }

  protected ASTCDMethod createGetTopLevelSymbolMethod(List<ASTCDType> symbolProds) {
    ArrayList<ASTCDType> symbolProdsWithSuper = new ArrayList<>(symbolProds);
    symbolProdsWithSuper.addAll(getSuperSymbols());
    List<String> symbolNames = symbolProdsWithSuper
        .stream()
        .map(ASTCDType::getName)
        .map(symbolTableService::removeASTPrefix)
        .collect(Collectors.toList());
    ASTCDMethod getTopLevelSymbol = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createOptionalTypeOf(I_SYMBOL), "getTopLevelSymbol");
    this.replaceTemplate(EMPTY_BODY, getTopLevelSymbol, new TemplateHookPoint(TEMPLATE_PATH + "GetTopLevelSymbol", symbolNames));
    return getTopLevelSymbol;
  }

  protected ASTCDMethod createCheckIfContinueAsSubScopeMethod() {
    ASTCDParameter symbolNameParam = getCDParameterFacade().createParameter(String.class, "symbolName");
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), "checkIfContinueAsSubScope", symbolNameParam);
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new TemplateHookPoint(TEMPLATE_PATH + "CheckIfContinueAsSubScope"));
    return getNameMethod;
  }

  protected ASTCDMethod createGetRemainingNameForResolveDownMethod() {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(String.class, "symbolName");
    ASTCDMethod getRemainingNameForResolveDown = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createListTypeOf("String"), "getRemainingNameForResolveDown", parameter);
    this.replaceTemplate(EMPTY_BODY, getRemainingNameForResolveDown, new TemplateHookPoint(TEMPLATE_PATH + "GetRemainingNameForResolveDown"));
    return getRemainingNameForResolveDown;
  }

  protected List<ASTCDMethod> createContinueWithEnclosingScopeMethods(List<ASTCDType> symbolProds, DiagramSymbol definitionSymbol) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTCDParameter parameterFoundSymbols = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);
    ASTCDParameter parameterName = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), NAME_VAR);
    ASTCDParameter parameterModifier = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);

    for (ASTCDType type : symbolProds) {
      Optional<String> definingSymbolFullName = symbolTableService.getDefiningSymbolFullName(type, definitionSymbol);
      String className = symbolTableService.removeASTPrefix(type);

      if (definingSymbolFullName.isPresent()) {
        ASTCDParameter parameterPredicate = getCDParameterFacade().createParameter(getMCTypeFacade().createBasicGenericTypeOf(
            PREDICATE, definingSymbolFullName.get()), PREDICATE_VAR);
        String methodName = String.format(CONTINUE_WITH_ENCLOSING_SCOPE, className);

        ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createListTypeOf(definingSymbolFullName.get()),
            methodName, parameterFoundSymbols, parameterName, parameterModifier, parameterPredicate);
        this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
            TEMPLATE_PATH + "ContinueWithEnclosingScope4ArtifactScope", definingSymbolFullName.get(), className));
        methodList.add(method);
      }
    }
    return methodList;
  }

  protected ASTCDMethod createGetFullNameMethod(){
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createStringType(), "getFullName");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "GetFullName"));
    return method;
  }

  protected List<ASTCDMethod> createSuperContinueWithEnclosingScopeMethods() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      // only filter for types which define a symbol
      List<ASTCDType> symbolProds = symbolTableService.getAllCDTypes(cdDefinitionSymbol)
          .stream()
          .filter(t -> t.isPresentAstNode())
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
    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : symbolTableService.getAllCDTypes(cdDefinitionSymbol)) {
        if (symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          symbolAttributes.add(type.getAstNode());
        }
      }
    }
    return symbolAttributes;
  }
}
