/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor.visitor_interface;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;

public class ASTVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  private static final String AST_PACKAGE = "._ast.";

  private final VisitorInterfaceDecorator visitorInterfaceDecorator;

  private final VisitorService visitorService;

  public ASTVisitorDecorator(final GlobalExtensionManagement glex, final VisitorInterfaceDecorator visitorInterfaceDecorator, final VisitorService visitorService) {
    super(glex);
    this.visitorInterfaceDecorator = visitorInterfaceDecorator;
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

    ASTCDInterface astcdInterface = visitorInterfaceDecorator.decorate(compilationUnit);
    astcdInterface.addCDMethod(addVisitASTNodeMethods());
    astcdInterface.addCDMethod(addEndVisitASTNodeMethods());
    return astcdInterface;
  }

  protected ASTCDMethod addVisitASTNodeMethods() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(AST_INTERFACE);
    return visitorService.getVisitorMethod(VisitorConstants.VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitASTNodeMethods() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(AST_INTERFACE);
    return visitorService.getVisitorMethod(VisitorConstants.END_VISIT, astNodeType);
  }
}
