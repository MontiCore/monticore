package de.monticore.codegen.cd2java.ast_interface;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;

import static de.monticore.codegen.cd2java.ast_new.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java.ast_new.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;

public class ASTInterfaceDecorator extends AbstractDecorator<ASTCDInterface, ASTCDInterface> {

  private final ASTService astService;

  private final VisitorService visitorService;


  public ASTInterfaceDecorator(GlobalExtensionManagement glex, ASTService astService, VisitorService visitorService) {
    super(glex);
    this.astService = astService;
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDInterface input) {
    input.addCDMethod(getAcceptMethod(visitorService.getVisitorType()));
    input.addInterface(getCDTypeFacade().createReferenceTypeByDefinition(AST_INTERFACE));
    input.addInterface(astService.getASTBaseInterface());
    input.clearCDAttributes();
    return input;
  }

  protected ASTCDMethod getAcceptMethod(ASTType visitorType) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(visitorType, "visitor");
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, getCDTypeFacade().createVoidType(), ACCEPT_METHOD, parameter);
  }


}
