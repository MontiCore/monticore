/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor.visitor_interface;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;

public class ASTVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  private final VisitorInterfaceDecorator visitorInterfaceDecorator;

  private final VisitorService visitorService;

  public ASTVisitorDecorator(final GlobalExtensionManagement glex,
                             final VisitorInterfaceDecorator visitorInterfaceDecorator,
                             final VisitorService visitorService) {
    super(glex);
    this.visitorInterfaceDecorator = visitorInterfaceDecorator;
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit ast) {
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithPackage(ast);

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
