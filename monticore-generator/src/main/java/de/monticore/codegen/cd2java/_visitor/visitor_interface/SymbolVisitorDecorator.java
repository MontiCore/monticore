/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor.visitor_interface;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_FULL_NAME;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;

public class SymbolVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  private final VisitorInterfaceDecorator visitorInterfaceDecorator;

  private final VisitorService visitorService;

  private final SymbolTableService symbolTableService;

  public SymbolVisitorDecorator(final GlobalExtensionManagement glex,
                                final VisitorInterfaceDecorator visitorInterfaceDecorator,
                                final VisitorService visitorService,
                                final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorInterfaceDecorator = visitorInterfaceDecorator;
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    ASTCDCompilationUnit compilationUnit = input.deepClone();

    // only use symbol Classes
    List<ASTCDClass> symbolClasses = compilationUnit.getCDDefinition().getCDClassList()
        .stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(c -> visitorService.hasSymbolStereotype(c.getModifier()))
        .collect(Collectors.toList());
    compilationUnit.getCDDefinition().setCDClassList(symbolClasses);


    List<ASTCDInterface> symbolInterfaces = compilationUnit.getCDDefinition().getCDInterfaceList()
        .stream()
        .filter(ASTCDInterface::isPresentModifier)
        .filter(c -> visitorService.hasSymbolStereotype(c.getModifier()))
        .collect(Collectors.toList());
    compilationUnit.getCDDefinition().setCDInterfaceList(symbolInterfaces);

    //enums cannot be a symbol
    compilationUnit.getCDDefinition().clearCDEnums();

    //set classname to correct Name with path
    compilationUnit.getCDDefinition().getCDClassList().forEach(c -> c.setName(symbolTableService.getSymbolTypeName(c)));
    compilationUnit.getCDDefinition().getCDInterfaceList().forEach(c -> c.setName(symbolTableService.getSymbolTypeName(c)));

    // to not generate implementation of traverse method
    visitorInterfaceDecorator.disableTemplates();
    ASTCDInterface astcdInterface = visitorInterfaceDecorator.decorate(compilationUnit);
    astcdInterface.setName(visitorService.getSymbolVisitorSimpleTypeName());

    // set handle template
    astcdInterface.getCDMethodList().stream().filter(m -> HANDLE.equals(m.getName())).forEach(m ->
        this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_visitor.Handle", true)));
    // set get and set realThis templates
    astcdInterface.getCDMethodList().stream().filter(m -> GET_REAL_THIS.equals(m.getName())).forEach(m ->
        this.replaceTemplate(EMPTY_BODY, m, new StringHookPoint("return this;")));
    astcdInterface.getCDMethodList().stream().filter(m -> SET_REAL_THIS.equals(m.getName())).forEach(m ->
        this.replaceTemplate(EMPTY_BODY, m, new StringHookPoint(
            "    throw new UnsupportedOperationException(\"0xA7011x709 The setter for realThis is " +
                "not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n")));

    astcdInterface.addCDMethod(addVisitASTNodeMethods());
    astcdInterface.addCDMethod(addEndVisitASTNodeMethods());
    return astcdInterface;
  }

  protected ASTCDMethod addVisitASTNodeMethods() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(SYMBOL_FULL_NAME);
    return visitorService.getVisitorMethod(VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitASTNodeMethods() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(SYMBOL_FULL_NAME);
    return visitorService.getVisitorMethod(END_VISIT, astNodeType);
  }

}
