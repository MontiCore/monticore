package de.monticore.codegen.cd2java.data;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;
import java.util.stream.Collectors;

public class InterfaceDecorator extends AbstractDecorator<ASTCDInterface, ASTCDInterface> {

  private final DataDecoratorUtil dataDecoratorUtil;

  private final MethodDecorator methodDecorator;

  private final AbstractService<?> service;

  public InterfaceDecorator(final GlobalExtensionManagement glex, final DataDecoratorUtil dataDecoratorUtil,
                            final MethodDecorator methodDecorator, final AbstractService abstractService) {
    super(glex);
    this.dataDecoratorUtil = dataDecoratorUtil;
    this.methodDecorator = methodDecorator;
    this.service = abstractService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDInterface cdInterface) {
    //add abstract methods like deepClone, deepEquals etc.
    List<ASTCDMethod> dataMethods = dataDecoratorUtil.decorate(cdInterface);
    dataMethods.forEach(m -> m.getModifier().setAbstract(true));
    cdInterface.addAllCDMethods(dataMethods);

    //add abstract methods for attributes of the interface
    List<ASTCDMethod> attributeMethods = cdInterface.getCDAttributeList().stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<ASTCDMethod> methodListWithoutDuplicates = service.getMethodListWithoutDuplicates(cdInterface.getCDMethodList(), attributeMethods);
    methodListWithoutDuplicates.forEach(m -> m.getModifier().setAbstract(true));
    cdInterface.addAllCDMethods(methodListWithoutDuplicates);

    cdInterface.getCDAttributeList().clear();
    return cdInterface;
  }
}
