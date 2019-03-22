package de.monticore.codegen.cd2java.ast_interface;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ASTInterfaceDecorator extends AbstractDecorator<ASTCDInterface, ASTCDInterface> {

  private final MethodDecorator methodDecorator;

  private static final String AST_NODE = "de.monticore.ast.ASTNode";

  private static final String NODE = "Node";

  public ASTInterfaceDecorator(GlobalExtensionManagement glex, final MethodDecorator methodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
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



}
