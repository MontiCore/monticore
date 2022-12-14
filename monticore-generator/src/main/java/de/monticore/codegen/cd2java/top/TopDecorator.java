/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.top;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.io.paths.MCPath;
import de.monticore.umlmodifier._ast.ASTModifier;

import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;
import static de.se_rwth.commons.Names.constructQualifiedName;
// TODO: Delete in version 7.5.0-SNAPSHOT (see cd4analysis)
@Deprecated
public class TopDecorator extends AbstractCreator<ASTCDCompilationUnit,ASTCDCompilationUnit> {

  /*
  Adds the suffix TOP to hand coded ASTs and makes generated TOP class abstract
  Attention! does not actually create a new CD object, because then the glex has the wrong objects referenced
   */

  public static final String TOP_SUFFIX = "TOP";

  protected final MCPath hwPath;

  public TopDecorator(MCPath hwPath) {
    this.hwPath = hwPath;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit originalCD) {
    for (ASTCDPackage p: originalCD.getCDDefinition().getCDPackagesList()) {
      p.getCDElementList().stream()
              .filter(e -> e instanceof ASTCDClass)
              .map(e -> (ASTCDClass) e)
              .filter(cdClass -> existsHandwrittenClass(hwPath, constructQualifiedName(p.getMCQualifiedName().getPartsList(), cdClass.getName())))
              .forEach(this::applyTopMechanism);

      p.getCDElementList().stream()
              .filter(e -> e instanceof ASTCDInterface)
              .map(e -> (ASTCDInterface) e)
              .filter(cdInterface -> existsHandwrittenClass(hwPath, constructQualifiedName(p.getMCQualifiedName().getPartsList(), cdInterface.getName())))
              .forEach(this::applyTopMechanism);

      p.getCDElementList().stream()
              .filter(e -> e instanceof ASTCDEnum)
              .map(e -> (ASTCDEnum) e)
              .filter(cdEnum -> existsHandwrittenClass(hwPath, constructQualifiedName(p.getMCQualifiedName().getPartsList(), cdEnum.getName())))
              .forEach(this::applyTopMechanism);
    }
    return originalCD;
  }

  protected void applyTopMechanism(ASTCDClass cdClass) {
    makeAbstract(cdClass);
    cdClass.setName(cdClass.getName() + TOP_SUFFIX);

    cdClass.getCDConstructorList().forEach(constructor ->
        constructor.setName(constructor.getName() + TOP_SUFFIX));
  }

  protected void applyTopMechanism(ASTCDInterface cdInterface) {
    cdInterface.setName(cdInterface.getName() + TOP_SUFFIX);
  }

  protected void applyTopMechanism(ASTCDEnum cdEnum) {
    cdEnum.setName(cdEnum.getName() + TOP_SUFFIX);
  }

  protected void makeAbstract(ASTCDType type) {
    makeAbstract(type.getModifier());
  }

  protected void makeAbstract(ASTModifier modifier) {
    modifier.setAbstract(true);
  }
}
