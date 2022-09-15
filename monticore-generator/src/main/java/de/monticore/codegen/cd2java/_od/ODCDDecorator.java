/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._od;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import static de.monticore.codegen.cd2java._od.ODConstants.OD_PACKAGE;

public class ODCDDecorator extends AbstractDecorator {

  protected final ODDecorator odDecorator;

  public ODCDDecorator(GlobalExtensionManagement glex,
                       ODDecorator odDecorator) {
    super(glex);
    this.odDecorator = odDecorator;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedUnit) {
    ASTCDPackage odPackage = getPackage(input, decoratedUnit, OD_PACKAGE);
    odPackage.addCDElement(odDecorator.decorate(input));
  }
}
