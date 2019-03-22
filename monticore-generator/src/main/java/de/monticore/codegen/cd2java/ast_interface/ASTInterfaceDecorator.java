package de.monticore.codegen.cd2java.ast_interface;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ASTInterfaceDecorator extends AbstractDecorator<ASTCDInterface, ASTCDInterface> {

  private final MethodDecorator methodDecorator;

  public ASTInterfaceDecorator(GlobalExtensionManagement glex, final MethodDecorator methodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDInterface decorate(ASTCDInterface input) {


    return CD4AnalysisMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(input.getName())
        .addAllCDMethods(getAttributeMethods(input.getCDAttributeList()))
        .build()
        ;
  }

  protected List<ASTCDMethod> getAttributeMethods(List<ASTCDAttribute> astcdAttributes) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : astcdAttributes) {
      methodList.addAll(methodDecorator.decorate(astcdAttribute));
    }
    methodList.forEach(m -> m.getModifier().setAbstract(true));
    return methodList;
  }

}
