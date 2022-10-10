/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.Optional;

/**
 * Creates the Class diagram for all CLI-Specific classes
 */

public class CDCLIDecorator extends AbstractDecorator {

  public static final String TEMPLATE_PATH = "_cli.";

  protected final ParserService parserService;

  protected final CLIDecorator cliDecorator;

  public CDCLIDecorator(final GlobalExtensionManagement glex,
                        final CLIDecorator cliDecorator,
                        final ParserService parserService) {
    super(glex);
    this.parserService = parserService;
    this.cliDecorator = cliDecorator;
  }

  public void decorate(final ASTCDCompilationUnit mainCD, ASTCDCompilationUnit decoratedCD) {
    if (!parserService.hasComponentStereotype(mainCD.getCDDefinition().getModifier())) {
      ASTCDPackage visitorPackage = getPackage(mainCD, decoratedCD, DEFAULT_PACKAGE);
      Optional<ASTCDClass> cliClass = cliDecorator.decorate(mainCD);
      cliClass.ifPresent(visitorPackage::addCDElement);
    }
  }
}


