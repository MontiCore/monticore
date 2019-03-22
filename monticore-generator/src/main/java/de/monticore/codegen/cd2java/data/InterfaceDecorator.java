package de.monticore.codegen.cd2java.data;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;

import java.util.List;
import java.util.stream.Collectors;

public class InterfaceDecorator extends AbstractDecorator<ASTCDInterface, ASTCDInterface> {

  private final DataDecoratorUtil dataDecoratorUtil;

  private final MethodDecorator methodDecorator;

  public InterfaceDecorator(final GlobalExtensionManagement glex, final DataDecoratorUtil dataDecoratorUtil,
                            final MethodDecorator methodDecorator) {
    super(glex);
    this.dataDecoratorUtil = dataDecoratorUtil;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDInterface decorate(ASTCDInterface cdInterface) {
    cdInterface.addAllCDMethods(dataDecoratorUtil.decorate(cdInterface));
    cdInterface.addAllCDMethods(cdInterface.getCDAttributeList().stream()
          .map(methodDecorator::decorate)
          .flatMap(List::stream)
          .collect(Collectors.toList()));
    cdInterface.getCDMethodList().forEach(m -> m.getModifier().setAbstract(true));
    cdInterface.getCDAttributeList().clear();
    return cdInterface;
  }

}
