/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractCreator;
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
  protected final SymbolTableService symbolTableService;
  public static final String TEMPLATE_PATH = "_cli.";

  public CliDecorator(final GlobalExtensionManagement glex,
                      final SymbolTableService symbolTableService){
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<ASTCDClass>  cliClass = Optional.empty();

    ASTCDDefinition cdDefinition = input.getCDDefinition();
    if (!cdDefinition.isPresentModifier() || !symbolTableService.hasComponentStereotype(cdDefinition.getModifier())) {
      String cliClassName = symbolTableService.getCliSimpleName();
      String millFullName = symbolTableService.getMillFullName();
      cliClass = Optional.of(CD4AnalysisMill.cDClassBuilder()
          .setModifier(PUBLIC.build())
          .setName(cliClassName)
          .addCDMember(createMainMethod(symbolTableService.getCDSymbol()))
          .build());
    }

    return cliClass;
  }

  protected ASTCDMethod createMainMethod(DiagramSymbol cdSymbol) {
    String grammarname = cdSymbol.getName();
    String millFullName = symbolTableService.getMillFullName();
    Optional<String> startprod = symbolTableService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createArrayType("String", 1);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "args");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), "main", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Main", grammarname, startprod.get(), millFullName));
    return addCheckerMethod;
  }
}
