/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

/**
 * created mill class for a grammar
 */
public class RunnerDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  public static final String TEMPLATE_PATH = "_cli.";
  protected final ParserService parserService;
  protected final SymbolTableService symbolTableService;


  public RunnerDecorator(final GlobalExtensionManagement glex,
                         final ParserService parserService,
                         final SymbolTableService symbolTableService) {
    super(glex);
    this.parserService = parserService;
    this.symbolTableService = symbolTableService;

  }


  public Optional<ASTCDClass> decorate(final ASTCDCompilationUnit cd) {
    Optional<ASTCDClass>  cliClass = Optional.empty();
    boolean startProdPresent = symbolTableService.getStartProdASTFullName().isPresent();
    ASTCDDefinition cdDefinition = cd.getCDDefinition();
    if (!cdDefinition.isPresentModifier() || !parserService.hasComponentStereotype(cdDefinition.getModifier())) {
      String runnerClassName = parserService.getRunnerSimpleName();
      String millFullName = parserService.getMillFullName();
      cliClass = Optional.of(CD4AnalysisMill.cDClassBuilder()
          .setModifier(PUBLIC.build())
          .setName(runnerClassName)
          .addCDMember(createParseMethod(parserService.getCDSymbol()))
          .addCDMember(createRunMethod(parserService.getCDSymbol(),startProdPresent))
          .addCDMember(createPrettyPrintMethod(parserService.getCDSymbol()))
          .addCDMember(createPrintMethod(parserService.getCDSymbol()))
          .addCDMember(createPrintHelpMethod(parserService.getCDSymbol()))
          .addCDMember(createReportMethod(parserService.getCDSymbol()))
          .addCDMember(createRunDefaultCoCosMethod(parserService.getCDSymbol()))
          .addCDMember(createStoreSymbolsMethod(parserService.getCDSymbol()))
          .addCDMember(createInitOptionsMethod(parserService.getCDSymbol()))
          .build());
      if (startProdPresent){
        cliClass.get().addCDMember(createCreateSymbolTableMethod(parserService.getCDSymbol()));

      }
    }

    return cliClass;

  }


  protected ASTCDMethod createRunMethod(DiagramSymbol cdSymbol, boolean startProdPresesnt) {
    String grammarname = cdSymbol.getName();
    String millFullName = parserService.getMillFullName();
    Optional<String> startprod = parserService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createArrayType("String", 1);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "args");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "run", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Run", startProdPresesnt));
    return addCheckerMethod;
  }

  protected ASTCDMethod createParseMethod(DiagramSymbol cdSymbol) {
    String grammarname = cdSymbol.getName();
    Optional<String> startprod = parserService.getStartProdASTFullName();
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(startprod.get());
    String millFullName = parserService.getMillFullName();
    String parserFullname = parserService.getParserClassFullName();
    ASTMCType checkerType = getMCTypeFacade().createStringType();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "model");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), returnType , "parse", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Parser", grammarname, startprod.get(),millFullName , parserFullname));
    return addCheckerMethod;
  }

  protected ASTCDMethod createCreateSymbolTableMethod(DiagramSymbol cdDefinitionSymbol) {
    String grammarname = cdDefinitionSymbol.getName();
    String artifactScope = symbolTableService.getArtifactScopeInterfaceFullName();
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(artifactScope);
    Optional<String> str = parserService.getStartProdASTFullName();
    String millFullName = parserService.getMillFullName();
    String scopesgenitordelegator = symbolTableService.getScopesGenitorDelegatorFullName();
    ASTMCType checkerType = getMCTypeFacade().createQualifiedType(str.get());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "node");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(),returnType, "createSymbolTable", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "SymbolTable", grammarname, millFullName, scopesgenitordelegator, artifactScope));
    return addCheckerMethod;
  }

  protected ASTCDMethod createReportMethod(DiagramSymbol cdDefinitionSymbol) {
    Optional<String> startProd = parserService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createQualifiedType(startProd.get());
    ASTMCType checkerType2 = getMCTypeFacade().createStringType();
    ASTCDParameter parameter2 = getCDParameterFacade().createParameter(checkerType2, "path");
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "ast");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "report", parameter,parameter2);
    return addCheckerMethod;
  }

  protected ASTCDMethod createRunDefaultCoCosMethod(DiagramSymbol cdDefinitionSymbol) {
    Optional<String> startProd = parserService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createQualifiedType(startProd.get());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "ast");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "runDefaultCoCos", parameter);
    return addCheckerMethod;
  }

  protected ASTCDMethod createStoreSymbolsMethod(DiagramSymbol cdDefinitionSymbol) {
    String symbols2Json = symbolTableService.getSymbols2JsonFullName();
    String artifactScope = symbolTableService.getArtifactScopeInterfaceFullName();
    ASTMCType checkerType = getMCTypeFacade().createQualifiedType(artifactScope);
    ASTMCType checkerType2 = getMCTypeFacade().createStringType();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "scope");
    ASTCDParameter parameter2 = getCDParameterFacade().createParameter(checkerType2, "path");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "storeSymbols", parameter,parameter2);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "StoreSymbols", symbols2Json));
    return addCheckerMethod;
  }

  protected ASTCDMethod createPrettyPrintMethod(DiagramSymbol cdDefinitionSymbol) {
    String grammarname = cdDefinitionSymbol.getName();
    Optional<String> str = parserService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createQualifiedType(str.get());
    ASTMCType checkerType2 = getMCTypeFacade().createStringType();
    ASTCDParameter parameter2 = getCDParameterFacade().createParameter(checkerType2, "file");
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "ast");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "prettyPrint", parameter,parameter2);
    return addCheckerMethod;
  }

  protected ASTCDMethod createPrintHelpMethod(DiagramSymbol cdDefinitionSymbol) {
    String runnername = symbolTableService.getRunnerSimpleName();
    ASTMCType checkerType = getMCTypeFacade().createQualifiedType("org.apache.commons.cli.Options");
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "options");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "printHelp", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "PrintHelp", runnername));
    return addCheckerMethod;
  }
  protected ASTCDMethod createInitOptionsMethod(DiagramSymbol cdDefinitionSymbol) {
    ASTMCType returnType = getMCTypeFacade().createQualifiedType("org.apache.commons.cli.Options");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(),returnType ,"initOptions");
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "InitOptions"));
    return addCheckerMethod;
  }

  protected ASTCDMethod createPrintMethod(DiagramSymbol cdDefinitionSymbol) {
    String grammarname = cdDefinitionSymbol.getName();
    ASTMCType checkerType = getMCTypeFacade().createStringType();
    ASTMCType checkerType2 = getMCTypeFacade().createStringType();
    ASTCDParameter parameter2 = getCDParameterFacade().createParameter(checkerType2, "path");
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "content");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "print", parameter,parameter2);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Print"));
    return addCheckerMethod;
  }

}
