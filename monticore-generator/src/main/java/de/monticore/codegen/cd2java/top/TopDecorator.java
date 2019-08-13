/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.top;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.io.paths.IterablePath;

import static de.monticore.codegen.cd2java.factories.CDModifier.PACKAGE_PRIVATE_ABSTRACT;
import static de.monticore.codegen.mc2cd.TransformationHelper.existsHandwrittenClass;
import static de.monticore.utils.Names.constructQualifiedName;

public class TopDecorator extends AbstractTransformer<ASTCDCompilationUnit> {

  private static final String TOP_SUFFIX = "TOP";

  private final IterablePath hwPath;

  public TopDecorator(IterablePath hwPath) {
    this.hwPath = hwPath;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit originalCD, ASTCDCompilationUnit changedCD) {
    changedCD.getCDDefinition().getCDClassList().stream()
        .filter(cdClass -> existsHandwrittenClass(hwPath, constructQualifiedName(changedCD.getPackageList(), cdClass.getName())))
        .forEach(this::applyTopMechanism);

    changedCD.getCDDefinition().getCDInterfaceList().stream()
        .filter(cdInterface -> existsHandwrittenClass(hwPath, constructQualifiedName(changedCD.getPackageList(), cdInterface.getName())))
        .forEach(this::applyTopMechanism);

    changedCD.getCDDefinition().getCDEnumList().stream()
        .filter(cdEnum -> existsHandwrittenClass(hwPath, constructQualifiedName(changedCD.getPackageList(), cdEnum.getName())))
        .forEach(this::applyTopMechanism);

    return changedCD;
  }

  private void applyTopMechanism(ASTCDClass cdClass) {
    makeAbstract(cdClass);
    cdClass.setName(cdClass.getName() + TOP_SUFFIX);

    cdClass.getCDConstructorList().forEach(constructor ->
        constructor.setName(constructor.getName() + TOP_SUFFIX));
  }

  private void applyTopMechanism(ASTCDInterface cdInterface) {
    cdInterface.setName(cdInterface.getName() + TOP_SUFFIX);
  }

  private void applyTopMechanism(ASTCDEnum cdEnum) {
    cdEnum.setName(cdEnum.getName() + TOP_SUFFIX);
  }

  private void makeAbstract(ASTCDType type) {
    if (type.getModifierOpt().isPresent()) {
      makeAbstract(type.getModifierOpt().get());
    } else {
      type.setModifier(PACKAGE_PRIVATE_ABSTRACT.build());
    }
  }

  private void makeAbstract(ASTModifier modifier) {
    modifier.setAbstract(true);
  }
}
