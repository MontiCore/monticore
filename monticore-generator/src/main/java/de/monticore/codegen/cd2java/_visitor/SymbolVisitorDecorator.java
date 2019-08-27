/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.*;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_FULL_NAME;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class SymbolVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  public SymbolVisitorDecorator(final GlobalExtensionManagement glex,
                                final VisitorService visitorService,
                                final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    ASTCDCompilationUnit compilationUnit = input.deepClone();

    ASTMCQualifiedType symbolVisitorType = getCDTypeFacade().createQualifiedType(visitorService.getSymbolVisitorSimpleTypeName());
    Set<String> symbolNames = getSymbolNames(compilationUnit.getCDDefinition());

    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(visitorService.getSymbolVisitorSimpleTypeName())
        .setModifier(PUBLIC.build())
        .addCDMethod(addEndVisitISymbolMethod())
        .addCDMethod(addVisitISymbolMethod())
        .addCDMethod(addHandleISymbolMethod())
        .addAllCDMethods(getVisitorMethods(symbolNames))
        .addCDMethod(addGetRealThisMethods(symbolVisitorType))
        .addCDMethod(addSetRealThisMethods(symbolVisitorType))
        .build();
  }

  protected ASTCDMethod addVisitISymbolMethod() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(SYMBOL_FULL_NAME);
    return visitorService.getVisitorMethod(VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitISymbolMethod() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(SYMBOL_FULL_NAME);
    return visitorService.getVisitorMethod(END_VISIT, astNodeType);
  }

  protected ASTCDMethod addHandleISymbolMethod() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(SYMBOL_FULL_NAME);
    return visitorService.getVisitorMethod(HANDLE, astNodeType);
  }

  protected Set<String> getSymbolNames(ASTCDDefinition astcdDefinition) {
    // only add defining symbol Names not referencing like e.g. symbol (MCType)
    Set<String> symbolNames = new HashSet<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
      if (astcdClass.isPresentModifier() && symbolTableService.hasSymbolStereotype(astcdClass.getModifier())) {
        Optional<String> definingSymbolTypeName = symbolTableService.getDefiningSymbolTypeName(astcdClass);
        definingSymbolTypeName.ifPresent(symbolNames::add);
      }
    }

    for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfaceList()) {
      if (astcdInterface.isPresentModifier() && symbolTableService.hasSymbolStereotype(astcdInterface.getModifier())) {
        Optional<String> definingSymbolTypeName = symbolTableService.getDefiningSymbolTypeName(astcdInterface);
        definingSymbolTypeName.ifPresent(symbolNames::add);
      }
    }
    return symbolNames;
  }

  protected List<ASTCDMethod> getVisitorMethods(Set<String> symbolNames) {
    List<ASTCDMethod> visitorMethodList = new ArrayList<>();
    for (String symbolName : symbolNames) {
      ASTMCQualifiedType symbolType = getCDTypeFacade().createQualifiedType(symbolName);
      visitorMethodList.add(visitorService.getVisitorMethod(VISIT, symbolType));
      visitorMethodList.add(visitorService.getVisitorMethod(END_VISIT, symbolType));
      //add template for handle method
      ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, symbolType);
      this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLE_TEMPLATE, true));
      visitorMethodList.add(handleMethod);
      visitorMethodList.add(visitorService.getVisitorMethod(TRAVERSE, symbolType));
    }
    return visitorMethodList;
  }

  protected ASTCDMethod addGetRealThisMethods(ASTMCType visitorType) {
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, visitorType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("return this;"));
    return getRealThisMethod;
  }

  protected ASTCDMethod addSetRealThisMethods(ASTMCType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, "realThis");
    ASTCDMethod setRealThis = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, setRealThis, new StringHookPoint(
        "    throw new UnsupportedOperationException(\"0xA7011x709 The setter for realThis is " +
            "not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return setRealThis;
  }

}
