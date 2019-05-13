package de.monticore.codegen.cd2java.visitor_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import static de.monticore.codegen.cd2java.ast_new.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java.visitor_new.VisitorConstants.END_VISIT;
import static de.monticore.codegen.cd2java.visitor_new.VisitorConstants.VISIT;

public class ASTVisitorDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDInterface> {

  private static final String AST_PACKAGE = "._ast.";

  private final VisitorDecorator visitorDecorator;

  private final VisitorService visitorService;

  public ASTVisitorDecorator(final GlobalExtensionManagement glex, final VisitorDecorator visitorDecorator, final VisitorService visitorService) {
    super(glex);
    this.visitorDecorator = visitorDecorator;
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit ast) {
    ASTCDCompilationUnit compilationUnit = ast.deepClone();
    //set classname to correct Name with path
    String astPath = compilationUnit.getCDDefinition().getName().toLowerCase() + AST_PACKAGE;
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassList()) {
      astcdClass.setName(astPath + astcdClass.getName());
    }

    for (ASTCDInterface astcdInterface : compilationUnit.getCDDefinition().getCDInterfaceList()) {
      astcdInterface.setName(astPath + astcdInterface.getName());
    }

    for (ASTCDEnum astcdEnum : compilationUnit.getCDDefinition().getCDEnumList()) {
      astcdEnum.setName(astPath + astcdEnum.getName());
    }

    ASTCDInterface astcdInterface = visitorDecorator.decorate(compilationUnit);
    astcdInterface.addCDMethod(addVisitASTNodeMethods());
    astcdInterface.addCDMethod(addEndVisitASTNodeMethods());
    return astcdInterface;
  }

  protected ASTCDMethod addVisitASTNodeMethods() {
    ASTType astNodeType = getCDTypeFacade().createTypeByDefinition(AST_INTERFACE);
    return visitorService.getVisitorMethod(VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitASTNodeMethods() {
    ASTType astNodeType = getCDTypeFacade().createTypeByDefinition(AST_INTERFACE);
    return visitorService.getVisitorMethod(END_VISIT, astNodeType);
  }
}
