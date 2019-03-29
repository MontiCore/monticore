package de.monticore.codegen.cd2java.ast_interface;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.ast_new.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java.ast_new.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
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
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(input.getName())
        .addCDMethod(getAcceptMethod(visitorService.getVisitorType()))
        .addAllInterfaces(input.getInterfaceList().stream()
            .map(ASTReferenceType::deepClone)
            .collect(Collectors.toList()))
        .addInterface(getCDTypeFactory().createReferenceTypeByDefinition(AST_INTERFACE))
        .addInterface(astService.getASTBaseInterface())
        .build()
        ;
  }

  protected ASTCDMethod getAcceptMethod(ASTType visitorType) {
    ASTCDParameter parameter = getCDParameterFactory().createParameter(visitorType, "visitor");
    return getCDMethodFactory().createMethod(PUBLIC_ABSTRACT, getCDTypeFactory().createVoidType(), ACCEPT_METHOD, parameter);
  }


}
