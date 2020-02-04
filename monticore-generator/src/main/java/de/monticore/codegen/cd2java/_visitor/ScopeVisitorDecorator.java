/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SCOPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SYMBOL;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * creates a ScopeVisitor class from a grammar
 */
public class ScopeVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  public ScopeVisitorDecorator(final GlobalExtensionManagement glex,
                               final VisitorService visitorService,
                               final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String scopeVisitorName = visitorService.getScopeVisitorSimpleName();
    String symbolVisitorName = visitorService.getSymbolVisitorSimpleName();

    ASTMCQualifiedType scopeVisitorType = getMCTypeFacade().createQualifiedType(scopeVisitorName);

    List<ASTMCQualifiedType> superScopeVisitorTypes = visitorService.getSuperCDsDirect()
        .stream()
        .map(visitorService::getScopeVisitorFullName)
        .map(getMCTypeFacade()::createQualifiedType)
        .collect(Collectors.toList());

    return CD4CodeMill.cDInterfaceBuilder()
        .setName(scopeVisitorName)
        .setModifier(PUBLIC.build())
        .addInterface(getMCTypeFacade().createQualifiedType(visitorService.getSymbolVisitorSimpleName()))
        .addAllInterfaces(superScopeVisitorTypes)
        .addCDMethod(addGetRealThisMethods(scopeVisitorType))
        .addCDMethod(addSetRealThisMethods(scopeVisitorType))
        .addAllCDMethods(createIScopeVisitorMethods())
        .addAllCDMethods(createISymbolVisitorMethods(symbolVisitorName))
        .addAllCDMethods(createScopeVisitorMethods(getSuperSymbols(), input.getCDDefinition()))
        .build();
  }

  protected ASTCDMethod addGetRealThisMethods(ASTMCType visitorType) {
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, visitorType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("return this;"));
    return getRealThisMethod;
  }

  protected ASTCDMethod addSetRealThisMethods(ASTMCType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, REAL_THIS);
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    String generatedErrorCode = visitorService.getGeneratedErrorCode(visitorType.printType(
        new MCSimpleGenericTypesPrettyPrinter(new IndentPrinter())) + SET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint(
        "    throw new UnsupportedOperationException(\"0xA7012" + generatedErrorCode + " The setter for realThis is " +
            "not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return getRealThisMethod;
  }

  protected List<ASTCDMethod> createIScopeVisitorMethods() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTMCQualifiedType iScopeType = getMCTypeFacade().createQualifiedType(I_SCOPE);
    methodList.add(visitorService.getVisitorMethod(VISIT, iScopeType));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, iScopeType));
    return methodList;
  }

  protected List<ASTCDMethod> createISymbolVisitorMethods(String scopeVisitorName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTMCQualifiedType iScopeType = getMCTypeFacade().createQualifiedType(I_SYMBOL);
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
    ASTMCQualifiedType artifactScopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getArtifactScopeFullName());

    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.addAll(createVisitorMethods(symbolsNameList, scopeType));
    if (symbolTableService.hasProd(astcdDefinition)) {
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
        new TemplateHookPoint(TRAVERSE_SCOPE_TEMPLATE, superSymbolList, symbolTableService.getScopeInterfaceFullName()));
    return methodList;
  }

  protected Set<String> getSuperSymbols() {
    // get super CDs
    List<CDDefinitionSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();
    Set<String> superSymbolNames = new HashSet<>();
    for (CDDefinitionSymbol cdSymbol : superCDsTransitive) {
      // get AST for symbol
      ASTCDDefinition astcdDefinition = cdSymbol.getAstNode();
      // add all symbol definitions to list
      for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfaceList()) {
        if (astcdInterface.isPresentModifier() && symbolTableService.hasSymbolStereotype(astcdInterface.getModifier())) {
          superSymbolNames.add(symbolTableService.getSymbolFullName(astcdInterface, cdSymbol));
        }
      }
      for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
        if (astcdClass.isPresentModifier() && symbolTableService.hasSymbolStereotype(astcdClass.getModifier())) {
          superSymbolNames.add(symbolTableService.getSymbolFullName(astcdClass, cdSymbol));
        }
      }
    }
    return superSymbolNames;
  }


}
