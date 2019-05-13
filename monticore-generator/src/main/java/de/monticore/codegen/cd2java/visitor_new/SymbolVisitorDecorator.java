package de.monticore.codegen.cd2java.visitor_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.symboltable.SymbolTableConstants.SYMBOL_FULL_NAME;
import static de.monticore.codegen.cd2java.symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKGE;
import static de.monticore.codegen.cd2java.visitor_new.VisitorConstants.*;

public class SymbolVisitorDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDInterface> {

  private final VisitorDecorator visitorDecorator;

  private final VisitorService visitorService;

  public SymbolVisitorDecorator(final GlobalExtensionManagement glex, final VisitorDecorator visitorDecorator, final VisitorService visitorService) {
    super(glex);
    this.visitorDecorator = visitorDecorator;
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    ASTCDCompilationUnit compilationUnit = input.deepClone();

    //set classname to correct Name with path
    String astPath = compilationUnit.getCDDefinition().getName().toLowerCase() + "." + SYMBOL_TABLE_PACKGE + ".";
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassList()) {
      astcdClass.setName(astPath + astcdClass.getName());
    }

    for (ASTCDInterface astcdInterface : compilationUnit.getCDDefinition().getCDInterfaceList()) {
      astcdInterface.setName(astPath + astcdInterface.getName());
    }

    for (ASTCDEnum astcdEnum : compilationUnit.getCDDefinition().getCDEnumList()) {
      astcdEnum.setName(astPath + astcdEnum.getName());
    }
    visitorDecorator.disableTemplates();
    ASTCDInterface astcdInterface = visitorDecorator.decorate(compilationUnit);

    astcdInterface.getCDMethodList().stream().filter(m -> TRAVERSE.equals(m.getName())).forEach(m ->
        this.replaceTemplate("visitor_new.Traverse", m, new TemplateHookPoint(EMPTY_BODY, astcdInterface)));
    astcdInterface.getCDMethodList().stream().filter(m -> HANDLE.equals(m.getName())).forEach(m ->
        this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("visitor_new.Handle", true)));

    astcdInterface.addCDMethod(addVisitASTNodeMethods());
    astcdInterface.addCDMethod(addEndVisitASTNodeMethods());
    return astcdInterface;
  }

  protected ASTCDMethod addVisitASTNodeMethods() {
    ASTType astNodeType = getCDTypeFacade().createTypeByDefinition(SYMBOL_FULL_NAME);
    return visitorService.getVisitorMethod(VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitASTNodeMethods() {
    ASTType astNodeType = getCDTypeFacade().createTypeByDefinition(SYMBOL_FULL_NAME);
    return visitorService.getVisitorMethod(END_VISIT, astNodeType);
  }

}
