/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC_STATIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class CliDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {
  protected final ParserService parserService;
  public static final String TEMPLATE_PATH = "_cli.";

  public CliDecorator(final GlobalExtensionManagement glex,
                      final ParserService parserservice){
    super(glex);
    this.parserService = parserservice;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<ASTCDClass>  cliClass = Optional.empty();

    ASTCDDefinition cdDefinition = input.getCDDefinition();
    if (!cdDefinition.isPresentModifier() || !parserService.hasComponentStereotype(cdDefinition.getModifier())) {
      String cliClassName = parserService.getCliSimpleName();
      String millFullName = parserService.getMillFullName();
      cliClass = Optional.of(CD4AnalysisMill.cDClassBuilder()
          .setModifier(PUBLIC.build())
          .setName(cliClassName)
          .addCDMember(createMainMethod(parserService.getCDSymbol()))
          .build());
    }

    return cliClass;
  }

  protected ASTCDMethod createMainMethod(DiagramSymbol cdSymbol) {
    String grammarname = cdSymbol.getName();
    String millFullName = parserService.getMillFullName();
    //Optional<String> startprod = symbolTableService.getStartProdASTFullName();
    //ASTMCType returnType = getMCTypeFacade().createQualifiedType(startprod.get());
    ASTMCType checkerType = getMCTypeFacade().createArrayType("String", 1);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "args");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), "main", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Main", grammarname,  millFullName));
    return addCheckerMethod;
  }
}
