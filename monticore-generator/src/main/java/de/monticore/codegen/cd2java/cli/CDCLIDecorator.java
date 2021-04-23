/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.*;

public class CDCLIDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  public static final String TEMPLATE_PATH = "_cli.";

  protected final ParserService parserService;

  protected final RunnerDecorator runnerDecorator;
  protected final CliDecorator cliDecorator;

  public CDCLIDecorator(final GlobalExtensionManagement glex,
                        final RunnerDecorator runnerDecorator,
                        final CliDecorator cliDecorator,
                        final ParserService parserService) {
    super(glex);
    this.parserService = parserService;
    this.runnerDecorator = runnerDecorator;
    this.cliDecorator = cliDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit mainCD) {

    List<String> topLevelPackage = new ArrayList<>(mainCD.getPackageList());

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(mainCD.getCDDefinition().getName())
        .build();
    ASTCDDefinition cdDefinition = mainCD.getCDDefinition();
    if (!cdDefinition.isPresentModifier() || !parserService.hasComponentStereotype(cdDefinition.getModifier())) {
      Optional<ASTCDClass> runnerClass = runnerDecorator.decorate(mainCD);
      runnerClass.ifPresent(astCD::addCDElement);
      Optional<ASTCDClass> cliClass = cliDecorator.decorate(mainCD);
      cliClass.ifPresent(astCD::addCDElement);
    }

    for (ASTCDClass cdClass : astCD.getCDClassesList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(topLevelPackage));
      if (cdClass.isPresentModifier()) {
        this.replaceTemplate(ANNOTATIONS, cdClass, createAnnotationsHookPoint(cdClass.getModifier()));
      }
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(topLevelPackage)
        .setCDDefinition(astCD)
        .build();
  }
}


