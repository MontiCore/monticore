/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

/**
 * created mill class for a grammar
 */
public class RunnerDecorator extends AbstractCreator<List<ASTCDCompilationUnit>, Optional<ASTCDClass>> {

  public static final String TEMPLATE_PATH = "_cli.";
  protected final AbstractService abstractService;


  public RunnerDecorator(final GlobalExtensionManagement glex,
                         final AbstractService abstractService) {
    super(glex);
    this.abstractService = abstractService;

  }


  public Optional<ASTCDClass> decorate(final List<ASTCDCompilationUnit> cds) {
    Optional<ASTCDClass>  cliClass = Optional.empty();

    ASTCDDefinition cdDefinition = cds.get(0).getCDDefinition();
    if (!cdDefinition.isPresentModifier() || !abstractService.hasComponentStereotype(cdDefinition.getModifier())) {
      String cliClassName = abstractService.getCliSimpleName();
      cliClass = Optional.of(CD4AnalysisMill.cDClassBuilder()
          .setModifier(PUBLIC.build())
          .setName(cliClassName)
          .addCDMethod(createMainMethod(abstractService.getCDSymbol()))
          .addCDMethod(createCreateSymbolTableMethod(abstractService.getCDSymbol()))
          .addCDMethod(createParseMethod(abstractService.getCDSymbol()))
          .build());
    }

    return cliClass;

  }

  protected ASTCDMethod createMainMethod(CDDefinitionSymbol cdSymbol) {
    String grammarname = cdSymbol.getName();
    Optional<String> startprod = abstractService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createArrayType("String", 1);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "args");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, "main", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Main", grammarname, startprod.get()));
    return addCheckerMethod;
  }

  protected ASTCDMethod createParseMethod(CDDefinitionSymbol cdSymbol) {
    String grammarname = cdSymbol.getName();
    Optional<String> startprod = abstractService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createStringType();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "model");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, "parse", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Parser", grammarname, startprod.get()));
    return addCheckerMethod;
  }

  protected ASTCDMethod createCreateSymbolTableMethod(CDDefinitionSymbol cdDefinitionSymbol) {
    String grammarname = cdDefinitionSymbol.getName();
    Optional<String> str = abstractService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createQualifiedType(str.get());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "ast");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, "createSymbolTable", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "SymbolTable", grammarname));
    return addCheckerMethod;
  }

  protected ASTCDAttribute createAttribute() {
    ASTMCType type = getMCTypeFacade().createStringType();
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PRIVATE, type, "mill");
    return attribute;
  }

  protected ASTCDAttribute createMillASTCliAttributeII() {
    ASTMCType type = getMCTypeFacade().createIntType();
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PRIVATE, type, "in");
    return attribute;
  }

}
