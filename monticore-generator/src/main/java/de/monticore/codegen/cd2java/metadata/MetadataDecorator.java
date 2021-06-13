package de.monticore.codegen.cd2java.metadata;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.time.LocalDate;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class MetadataDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {
  protected final ParserService parserService;
  public static final String TEMPLATE_PATH = "_metadata.";

  public MetadataDecorator(final GlobalExtensionManagement glex,
                           final ParserService parserservice) {
    super(glex);
    this.parserService = parserservice;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<ASTCDClass> metadataClass = Optional.empty();

    ASTCDDefinition cdDefinition = input.getCDDefinition();
    if (!cdDefinition.isPresentModifier() || !parserService.hasComponentStereotype(cdDefinition.getModifier())) {
      String metadataClassName = parserService.getMetadataSimpleName();

      metadataClass = Optional.of(CD4AnalysisMill.cDClassBuilder()
          .setModifier(PUBLIC.build())
          .setName(metadataClassName)
          .addCDMember(createVersionMethod(parserService.getCDSymbol()))
          .build()
      );
    }

    return metadataClass;
  }

  protected ASTCDMethod createVersionMethod(DiagramSymbol cdSymbol) {
    String toolName = cdSymbol.getName() + "Tool";
    String toolVersion = "PLACEHOLDER";
    String buildDate = LocalDate.now().toString();
    String monticoreVersion = "PLACEHOLDER";

    ASTMCType returnType = getMCTypeFacade().createStringType();
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "version");
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "Version",
            toolName,
            toolVersion,
            buildDate,
            monticoreVersion
        )
    );
    return addCheckerMethod;
  }
}
