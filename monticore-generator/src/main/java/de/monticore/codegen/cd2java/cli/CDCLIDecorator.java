/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import com.google.common.collect.Lists;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;

import java.util.List;
import java.util.Optional;

import static de.monticore.cd.codegen.CD2JavaTemplates.*;
import static de.monticore.codegen.cd2java.CoreTemplates.createAnnotationsHookPoint;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;

/**
 * Creates the Class diagram for all CLI-Specific classes
 */

public class CDCLIDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

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

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit mainCD) {
    List<String> astPackage = Lists.newArrayList();
    mainCD.getMCPackageDeclaration().getMCQualifiedName().getPartsList().forEach(p -> astPackage.add(p.toLowerCase()));
    astPackage.add(mainCD.getCDDefinition().getName().toLowerCase());
    ASTMCPackageDeclaration packageDecl = CD4CodeMill.mCPackageDeclarationBuilder()
            .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(astPackage).build()).build();

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(mainCD.getCDDefinition().getName())
        .setModifier(CD4AnalysisMill.modifierBuilder().build())
        .build();
    astCD.setDefaultPackageName(packageDecl.getMCQualifiedName().toString());
    ASTCDDefinition cdDefinition = mainCD.getCDDefinition();
    if (!parserService.hasComponentStereotype(cdDefinition.getModifier())) {
      Optional<ASTCDClass> cliClass = cliDecorator.decorate(mainCD);
      cliClass.ifPresent(astCD::addCDElement);
    }

    for (ASTCDClass cdClass : astCD.getCDClassesList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(astPackage));
      this.replaceTemplate(ANNOTATIONS, cdClass, createAnnotationsHookPoint(cdClass.getModifier()));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setMCPackageDeclaration(packageDecl)
        .setCDDefinition(astCD)
        .build();
  }
}


