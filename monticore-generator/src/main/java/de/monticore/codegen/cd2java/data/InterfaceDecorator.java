/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.data;

import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;
import java.util.stream.Collectors;

public class InterfaceDecorator extends AbstractTransformer<ASTCDInterface> {

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
  public ASTCDInterface decorate(final ASTCDInterface originalInput, ASTCDInterface changedInput) {
    //add abstract methods like deepClone, deepEquals etc.
    List<ASTCDMethod> dataMethods = dataDecoratorUtil.decorate(changedInput);
    dataMethods.forEach(m -> m.getModifier().setAbstract(true));
    changedInput.addAllCDMethods(dataMethods);

    //add abstract methods for attributes of the interface
    List<ASTCDMethod> attributeMethods = originalInput.getCDAttributeList().stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<ASTCDMethod> methodListWithoutDuplicates = service.getMethodListWithoutDuplicates(originalInput.getCDMethodList(), attributeMethods);
    methodListWithoutDuplicates.forEach(m -> m.getModifier().setAbstract(true));
    changedInput.addAllCDMethods(methodListWithoutDuplicates);

    changedInput.getCDAttributeList().clear();
    // make other methods abstract (for referenced symbol methods)
    changedInput.getCDMethodList().forEach(x->x.getModifier().setAbstract(true));
    // only then add the normal methods (e.g. for astrule methods with implementation)
    changedInput.addAllCDMethods(originalInput.getCDMethodList());

    changedInput.addAllInterfaces(originalInput.getInterfaceList());
    return changedInput;
  }
}
