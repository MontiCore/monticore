// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class CDPrettyPrinterDecorator extends AbstractDecorator {


  public CDPrettyPrinterDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD, ASTCDCompilationUnit prettyPrintCD) {
    ASTCDPackage cdPackage = getPackage(input, decoratedCD, PrettyPrinterConstants.PRETTYPRINT_PACKAGE);
    cdPackage.addAllCDElements(prettyPrintCD.getCDDefinition().getCDPackagesList().get(0).getCDElementList());
  }
}
