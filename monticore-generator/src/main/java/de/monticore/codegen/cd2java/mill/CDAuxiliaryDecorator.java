/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;

import static de.monticore.codegen.cd2java.mill.MillConstants.AUXILIARY_PACKAGE;

public class CDAuxiliaryDecorator extends AbstractDecorator {

  protected final MillForSuperDecorator millForSuperDecorator;

  public CDAuxiliaryDecorator(final GlobalExtensionManagement glex,
                              final MillForSuperDecorator millForSuperDecorator){
    super(glex);
    this.millForSuperDecorator = millForSuperDecorator;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    List<ASTCDClass> millForSuperClasses = millForSuperDecorator.decorate(input);
    ASTCDPackage cdPackage = getPackage(input, decoratedCD, AUXILIARY_PACKAGE);
    cdPackage.addAllCDElements(millForSuperClasses);
  }
}
