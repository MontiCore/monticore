package de.monticore.codegen.cd2java._visitor;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SCOPE_FULL_NAME;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_FULL_NAME;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ScopeVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  private final VisitorService visitorService;

  private final SymbolTableService symbolTableService;

  public ScopeVisitorDecorator(final GlobalExtensionManagement glex,
                               final VisitorService visitorService,
                               final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {

    String scopeVisitorName = visitorService.getScopeVisitorSimpleTypeName();
    String symbolVisitorName = visitorService.getSymbolVisitorSimpleTypeName();

    ASTMCQualifiedType scopeVisitorType = getCDTypeFacade().createQualifiedType(scopeVisitorName);

    List<ASTMCQualifiedType> superScopeVisitorTypes = visitorService.getSuperCDsDirect()
        .stream()
        .map(visitorService::getScopeVisitorFullTypeName)
        .map(getCDTypeFacade()::createQualifiedType)
        .collect(Collectors.toList());

    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(scopeVisitorName)
        .setModifier(PUBLIC.build())
        .addInterface(getCDTypeFacade().createQualifiedType(visitorService.getSymbolVisitorSimpleTypeName()))
        .addAllInterfaces(superScopeVisitorTypes)
        .addCDMethod(addGetRealThisMethods(scopeVisitorType))
        .addCDMethod(addSetRealThisMethods(scopeVisitorType))
        .addAllCDMethods(createIScopeVisitorMethods())
        .addAllCDMethods(createISymbolVisitorMethods(symbolVisitorName))
        .addAllCDMethods(createScopeVisitorMethods(getSuperSymbols(), input.getCDDefinition()))
        .build();
  }

  protected ASTCDMethod addGetRealThisMethods(ASTMCType visitorType) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(visitorType).build();
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, returnType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("return this;"));
    return getRealThisMethod;
  }

  protected ASTCDMethod addSetRealThisMethods(ASTMCType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, "realThis");
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint(
        "    throw new UnsupportedOperationException(\"0xA7011x709 The setter for realThis is " +
            "not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return getRealThisMethod;
  }

  protected List<ASTCDMethod> createIScopeVisitorMethods() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTMCQualifiedType iScopeType = getCDTypeFacade().createQualifiedType(SCOPE_FULL_NAME);
    methodList.add(visitorService.getVisitorMethod(VISIT, iScopeType));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, iScopeType));
    return methodList;
  }

  protected List<ASTCDMethod> createISymbolVisitorMethods(String scopeVisitorName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTMCQualifiedType iScopeType = getCDTypeFacade().createQualifiedType(SYMBOL_FULL_NAME);
    methodList.add(visitorService.getVisitorMethod(VISIT, iScopeType));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, iScopeType));
    methodList.add(visitorService.getVisitorMethod(HANDLE, iScopeType));
    addTemplatesforISymbol(methodList, scopeVisitorName);
    return methodList;
  }

  protected void addTemplatesforISymbol(List<ASTCDMethod> methodList, String scopeVisitorName) {
    for (ASTCDMethod astcdMethod : methodList) {
      replaceTemplate(EMPTY_BODY, astcdMethod, new StringHookPoint(scopeVisitorName + ".super.handle(node);"));
    }
  }

  protected List<ASTCDMethod> createScopeVisitorMethods(Set<String> symbolsNameList, ASTCDDefinition astcdDefinition) {

    ASTMCType scopeType = symbolTableService.getScopeType();
    ASTMCQualifiedType artifactScopeType = symbolTableService.getArtifactScopeType();

    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.addAll(createVisitorMethods(symbolsNameList, scopeType));
    if(hasProd(astcdDefinition)){
      methodList.addAll(createVisitorMethods(symbolsNameList, artifactScopeType));
    }
    return methodList;
  }

  protected List<ASTCDMethod> createVisitorMethods(Set<String> superSymbolList, ASTMCType scopeName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.add(visitorService.getVisitorMethod(VISIT, scopeName));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, scopeName));
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, scopeName);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLE_TEMPLATE, true));
    methodList.add(handleMethod);
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, scopeName);
    methodList.add(traverseMethod);
    this.replaceTemplate(EMPTY_BODY, traverseMethod,
        new TemplateHookPoint(TRAVERSE_SCOPE_TEMPLATE, superSymbolList, symbolTableService.getScopeInterfaceTypeName()));
    return methodList;
  }

  protected Set<String> getSuperSymbols() {
    // get super CDs
    List<CDDefinitionSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();
    Set<String> superSymbolNames = new HashSet<>();
    for (CDDefinitionSymbol cdSymbol : superCDsTransitive) {
      // get AST for symbol
      ASTCDDefinition astcdDefinition = cdSymbol.getAstNode().get();
      // add all symbol definitions to list
      for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfaceList()) {
        if (astcdInterface.isPresentModifier() && symbolTableService.hasSymbolStereotype(astcdInterface.getModifier())) {
          superSymbolNames.add(symbolTableService.getSymbolFullTypeName(astcdInterface, cdSymbol));
        }
      }
      for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
        if (astcdClass.isPresentModifier() && symbolTableService.hasSymbolStereotype(astcdClass.getModifier())) {
          superSymbolNames.add(symbolTableService.getSymbolFullTypeName(astcdClass,cdSymbol));
        }
      }
    }
    return superSymbolNames;
  }

  protected boolean hasProd(ASTCDDefinition astcdDefinition) {
    // is true if it has any class productions or any interface productions that are not the language interface
    return !astcdDefinition.isEmptyCDClasss() ||
          (!astcdDefinition.isEmptyCDInterfaces() &&
               !(astcdDefinition.sizeCDInterfaces() == 1
               && astcdDefinition.getCDInterface(0).getName().equals(visitorService.getSimleLanguageInterfaceName())));
  }

}
