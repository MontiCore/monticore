package de.monticore.codegen.cd2java.ast_interface;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ASTInterfaceDecorator extends AbstractDecorator<ASTCDInterface, ASTCDInterface> {

  private static final String AST_NODE = "de.monticore.ast.ASTNode";

  private static final String NODE = "Node";

  private static final String ACCEPT = "accept";


  public ASTInterfaceDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public ASTCDInterface decorate(ASTCDInterface input) {


    return CD4AnalysisMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(input.getName())
        .addAllInterfaces(input.getInterfaceList().stream()
            .map(ASTReferenceType::deepClone)
            .collect(Collectors.toList()))
        .addInterface(getCDTypeFactory().createReferenceTypeByDefinition(AST_NODE))
        .build()
        ;
  }

  protected ASTCDMethod getAcceptMethod(ASTType visitorType) {
    ASTCDParameter parameter = getCDParameterFactory().createParameter(visitorType, "visitor");
    return getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createVoidType(), ACCEPT, parameter);
  }


}
