/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.top;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.io.paths.IterablePath;

import static de.monticore.cd.facade.CDModifier.*;

import static de.monticore.codegen.mc2cd.TransformationHelper.existsHandwrittenClass;
import static de.monticore.utils.Names.constructQualifiedName;

public class TopDecorator extends AbstractCreator<ASTCDCompilationUnit,ASTCDCompilationUnit> {

  /*
  Adds the suffix TOP to hand coded ASTs and makes generated TOP class abstract
  Attention! does not actually create a new CD object, because then the glex has the wrong objects referenced
   */

  private static final String TOP_SUFFIX = "TOP";

  private final IterablePath hwPath;

  public TopDecorator(IterablePath hwPath) {
    this.hwPath = hwPath;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit originalCD) {
    ASTCDCompilationUnit topCD = originalCD;
    topCD.getCDDefinition().getCDClassList().stream()
        .filter(cdClass -> existsHandwrittenClass(hwPath, constructQualifiedName(topCD.getPackageList(), cdClass.getName())))
        .forEach(this::applyTopMechanism);

    topCD.getCDDefinition().getCDInterfaceList().stream()
        .filter(cdInterface -> existsHandwrittenClass(hwPath, constructQualifiedName(topCD.getPackageList(), cdInterface.getName())))
        .forEach(this::applyTopMechanism);

    topCD.getCDDefinition().getCDEnumList().stream()
        .filter(cdEnum -> existsHandwrittenClass(hwPath, constructQualifiedName(topCD.getPackageList(), cdEnum.getName())))
        .forEach(this::applyTopMechanism);

    return topCD;
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
    if (type.isPresentModifier()) {
      makeAbstract(type.getModifier
              ());
    } else {
      type.setModifier(PACKAGE_PRIVATE_ABSTRACT.build());
    }
  }

  private void makeAbstract(ASTModifier modifier) {
    modifier.setAbstract(true);
  }
}
