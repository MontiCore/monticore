/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
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
import de.se_rwth.commons.StringTransformations;

import java.util.*;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SYMBOL;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * creates a SymbolVisitor class from a grammar
 */
public class SymbolVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  protected boolean isTop;

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

    ASTMCQualifiedType symbolVisitorType = getMCTypeFacade().createQualifiedType(visitorService.getSymbolVisitorSimpleName());
    Set<String> symbolNames = getSymbolNames(compilationUnit.getCDDefinition());

    return CD4CodeMill.cDInterfaceBuilder()
        .setName(visitorService.getSymbolVisitorSimpleName())
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
    ASTMCType astNodeType = getMCTypeFacade().createQualifiedType(I_SYMBOL);
    return visitorService.getVisitorMethod(VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitISymbolMethod() {
    ASTMCType astNodeType = getMCTypeFacade().createQualifiedType(I_SYMBOL);
    return visitorService.getVisitorMethod(END_VISIT, astNodeType);
  }

  protected ASTCDMethod addHandleISymbolMethod() {
    ASTMCType astNodeType = getMCTypeFacade().createQualifiedType(I_SYMBOL);
    return visitorService.getVisitorMethod(HANDLE, astNodeType);
  }

  protected Set<String> getSymbolNames(ASTCDDefinition astcdDefinition) {
    // only add defining symbol Names not referencing like e.g. symbol (MCType)
    Set<String> symbolNames = new HashSet<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
      if (astcdClass.isPresentModifier() && symbolTableService.hasSymbolStereotype(astcdClass.getModifier())) {
        Optional<String> definingSymbolTypeName = symbolTableService.getDefiningSymbolFullName(astcdClass);
        definingSymbolTypeName.ifPresent(symbolNames::add);
      }
    }

    for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfaceList()) {
      if (astcdInterface.isPresentModifier() && symbolTableService.hasSymbolStereotype(astcdInterface.getModifier())) {
        Optional<String> definingSymbolTypeName = symbolTableService.getDefiningSymbolFullName(astcdInterface);
        definingSymbolTypeName.ifPresent(symbolNames::add);
      }
    }
    return symbolNames;
  }

  protected List<ASTCDMethod> getVisitorMethods(Set<String> symbolNames) {
    List<ASTCDMethod> visitorMethodList = new ArrayList<>();
    for (String symbolName : symbolNames) {
      ASTMCQualifiedType symbolType = getMCTypeFacade().createQualifiedType(symbolName);
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
    String hookPoint;
    if (!isTop()) {
      hookPoint = "return this;";
    } else {
      hookPoint = "return (" + visitorService.getSymbolVisitorSimpleName() + ")this;";
    }
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint(hookPoint));
    return getRealThisMethod;
  }

  protected ASTCDMethod addSetRealThisMethods(ASTMCType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, REAL_THIS);
    ASTCDMethod setRealThis = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    String generatedErrorCode = visitorService.getGeneratedErrorCode(visitorType.printType(
        new MCSimpleGenericTypesPrettyPrinter(new IndentPrinter())) + SET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, setRealThis, new StringHookPoint(
        "    throw new UnsupportedOperationException(\"0xA7015" + generatedErrorCode + " The setter for realThis is " +
            "not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return setRealThis;
  }

  public boolean isTop() {
    return isTop;
  }

  public void setTop(boolean top) {
    isTop = top;
  }
}
