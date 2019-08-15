package de.monticore.codegen.cd2java._visitor.visitor_interface;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypesHelper;
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
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
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
    ASTCDCompilationUnit compilationUnit = input.deepClone();

    String scopeVisitorName = visitorService.getScopeVisitorSimpleTypeName();
    ASTMCQualifiedType scopeVisitorType = getCDTypeFacade().createQualifiedType(scopeVisitorName);

    String scopeTypeName = symbolTableService.getScopeTypeName();
    String scopeInterfaceTypeName = symbolTableService.getScopeInterfaceTypeName();
    String artifactScopeTypeName = symbolTableService.getArtifactScopeTypeName();

    List<ASTMCQualifiedType> superScopeVisitorTypes = visitorService.getSuperCDsTransitive()
        .stream()
        .map(visitorService::getScopeVisitorSimpleTypeName)
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
        .addAllCDMethods(createISymbolVisitorMethods(scopeVisitorName))
        .addAllCDMethods(createScopeVisitorMethods(getSuperSymbols()))
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
      replaceTemplate(EMPTY_BODY, astcdMethod, new StringHookPoint(scopeVisitorName + ".super.handle(symbol);"));
    }
  }

  protected List<ASTCDMethod> createScopeVisitorMethods(Set<String> symbolsNameList) {

    ASTMCType scopeType = symbolTableService.getScopeType();
    ASTMCQualifiedType artifactScopeType = symbolTableService.getArtifactScopeType();

    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.addAll(createVisitorMethods(symbolsNameList, scopeType));
    methodList.addAll(createVisitorMethods(symbolsNameList, artifactScopeType));
    return methodList;
  }

  protected List<ASTCDMethod> createVisitorMethods(Set<String> superSymbolList, ASTMCType scopeName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.add(visitorService.getVisitorMethod(VISIT, scopeName));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, scopeName));
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, scopeName);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint("_visitor.Handle", true));
    methodList.add(handleMethod);
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, scopeName);
    methodList.add(traverseMethod);
    this.replaceTemplate(EMPTY_BODY, traverseMethod,
        new TemplateHookPoint("_visitor.scope.Traverse", superSymbolList, MCTypesHelper.printType(scopeName)));
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
          superSymbolNames.add(cdSymbol.getPackageName() + "." + astcdDefinition.getName().toLowerCase() + "."
              + SYMBOL_TABLE_PACKGE + "." + astcdInterface.getName());
        }
      }
      for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
        if (astcdClass.isPresentModifier() && symbolTableService.hasSymbolStereotype(astcdClass.getModifier())) {
          superSymbolNames.add(cdSymbol.getPackageName() + "." + astcdDefinition.getName().toLowerCase() + "."
              + SYMBOL_TABLE_PACKGE + "." + astcdClass.getName());
        }
      }
    }
    return superSymbolNames;
  }

}
