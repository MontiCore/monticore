/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.data;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
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
    changedInput.addAllCDMembers(dataMethods);

    //add abstract methods for attributes of the interface
    List<ASTCDMethod> attributeMethods = originalInput.getCDAttributeList().stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<ASTCDMethod> methodListWithoutDuplicates = service.getMethodListWithoutDuplicates(originalInput.getCDMethodList(), attributeMethods);
    methodListWithoutDuplicates.forEach(m -> m.getModifier().setAbstract(true));
    changedInput.addAllCDMembers(methodListWithoutDuplicates);

    changedInput.getCDAttributeList().clear();
    // make other methods abstract (for referenced symbol methods)
    changedInput.getCDMethodList().forEach(x->x.getModifier().setAbstract(true));
    // only then add the normal methods (e.g. for astrule methods with implementation)
    changedInput.addAllCDMembers(originalInput.getCDMethodList());

    // delete all private or protected methods
    List<ASTCDMethod> l = changedInput.getCDMethodList().stream()
            .filter(m -> !m.getModifier().isProtected() && !m.getModifier().isPrivate()).collect(Collectors.toList());
    changedInput.setCDMethodList(l);

    if (!changedInput.isPresentCDExtendUsage()) {
      changedInput.setCDExtendUsage(CD4AnalysisMill.cDExtendUsageBuilder().build());
    }
    changedInput.getCDExtendUsage().setSuperclassList(originalInput.getInterfaceList());
    return changedInput;
  }
}
