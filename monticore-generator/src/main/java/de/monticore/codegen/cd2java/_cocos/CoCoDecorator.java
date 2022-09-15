/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;

import static de.monticore.codegen.cd2java._cocos.CoCoConstants.COCO_PACKAGE;

/**
 * combines the CoCo checker class and all CoCo interfaces to create the _coco package
 */
public class CoCoDecorator extends AbstractDecorator {

  protected final CoCoCheckerDecorator cocoCheckerDecorator;

  protected final CoCoInterfaceDecorator cocoInterfaceDecorator;

  protected final MCPath handCodedPath;

  public CoCoDecorator(final GlobalExtensionManagement glex,
                       final MCPath handCodedPath,
                       final CoCoCheckerDecorator cocoCheckerDecorator,
                       final CoCoInterfaceDecorator cocoInterfaceDecorator) {
    super(glex);
    this.cocoCheckerDecorator = cocoCheckerDecorator;
    this.cocoInterfaceDecorator = cocoInterfaceDecorator;
    this.handCodedPath = handCodedPath;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage cocoPackage = getPackage(input, decoratedCD, COCO_PACKAGE);
    cocoPackage.addCDElement(cocoCheckerDecorator.decorate(input));
    cocoPackage.addAllCDElements(cocoInterfaceDecorator.decorate(input.getCDDefinition()));
  }
}
