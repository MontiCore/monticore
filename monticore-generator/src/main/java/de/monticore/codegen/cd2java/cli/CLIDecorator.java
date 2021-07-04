/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.ast.CommentBuilder;
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
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC_STATIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

/**
 * creates the CLI class for a given Grammar
 */
public class CLIDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {
  public static final String TEMPLATE_PATH = "_cli.";
  protected final ParserService parserService;
  protected final SymbolTableService symbolTableService;


  public CLIDecorator(final GlobalExtensionManagement glex,
                      final ParserService parserservice,
                      final SymbolTableService symbolTableService) {
    super(glex);
    this.parserService = parserservice;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<ASTCDClass> cliClass = Optional.empty();
    boolean startProdPresent = symbolTableService.getStartProdASTFullName().isPresent();


    ASTCDDefinition cdDefinition = input.getCDDefinition();
    if (!cdDefinition.isPresentModifier() || !parserService.hasComponentStereotype(cdDefinition.getModifier())) {
      String cliClassName = parserService.getCliSimpleName();
      cliClass = Optional.of(CD4AnalysisMill.cDClassBuilder()
          .setModifier(PUBLIC.build())
          .setName(cliClassName)
          .addCDMember(createMainMethod(parserService.getCDSymbol()))
          .addCDMember(createParseMethod(parserService.getCDSymbol()))
          .addCDMember(createRunMethod(startProdPresent, parserService.getCDSymbol()))
          .addCDMember(createInitMethod())
          .addCDMember(createPrettyPrintMethod())
          .addCDMember(createPrintMethod(parserService.getCDSymbol()))
          .addCDMember(createPrintHelpMethod())
          .addCDMember(createPrintVersionMethod())
          .addCDMember(createReportMethod())
          .addCDMember(createRunDefaultCoCosMethod())
          .addCDMember(createStoreSymbolsMethod())
          .addCDMember(createInitOptionsMethod())
          .addCDMember(createAddStandardOptionsMethod())
          .addCDMember(createAddAdditionalOptionsMethod())
          .build());

      if (startProdPresent) {
        cliClass.get().addCDMember(createCreateSymbolTableMethod());
      }
    }
    return cliClass;
  }

  /**
   * creates static main method to execute the CLI
   *
   * @param cdSymbol class diagram of the current language
   * @return the decorated main method
   */
  protected ASTCDMethod createMainMethod(DiagramSymbol cdSymbol) {
    String grammarname = cdSymbol.getName();
    ASTMCType stringArrayType = getMCTypeFacade().createArrayType("String", 1);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(stringArrayType, "args");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), "main", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "Main", grammarname));
    return method;
  }

  /**
   * creates Run method to execute the CLI
   *
   * @param startProdPresent true if the language has a StartProd, else false.
   * @return the decorated Run method
   */
  protected ASTCDMethod createRunMethod(boolean startProdPresent, DiagramSymbol cdsymbol) {
    String generatedError = symbolTableService.getGeneratedErrorCode(cdsymbol.getName() + "run");
    String cliName = symbolTableService.getCliSimpleName();
    ASTMCType stringArrayType = getMCTypeFacade().createArrayType("String", 1);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(stringArrayType, "args");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "run", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "Run", startProdPresent, cliName, generatedError));
    return method;
  }

  /**
   * creates the Parse method for parsing the AST of a given language
   *
   * @param cdSymbol class diagram of the current language
   * @return the decorated Parse method
   */
  protected ASTCDMethod createParseMethod(DiagramSymbol cdSymbol) {
    String generatedError1 = symbolTableService.getGeneratedErrorCode(cdSymbol.getName() + "parse");
    String generatedError2 = symbolTableService.getGeneratedErrorCode(cdSymbol.getName() + "parse2");
    Optional<String> startprod = parserService.getStartProdASTFullName();
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(startprod.get());
    String millFullName = parserService.getMillFullName();
    String parserFullname = parserService.getParserClassFullName();
    ASTMCType stringType = getMCTypeFacade().createStringType();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(stringType, "model");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "Parser", startprod.get(), millFullName, parserFullname, generatedError1, generatedError2));
    return method;
  }

  /**
   * creates method for creating a SymbolTable for a given AST
   *
   * @return the decorated CreateSymbolTable method
   */
  protected ASTCDMethod createCreateSymbolTableMethod() {
    String artifactScope = symbolTableService.getArtifactScopeInterfaceFullName();
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(artifactScope);
    Optional<String> startProd = parserService.getStartProdASTFullName();
    String millFullName = parserService.getMillFullName();
    String scopesgenitordelegator = symbolTableService.getScopesGenitorDelegatorFullName();
    ASTMCType startProdType = getMCTypeFacade().createQualifiedType(startProd.get());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(startProdType, "node");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "createSymbolTable", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "SymbolTable", millFullName, scopesgenitordelegator, artifactScope));
    return method;
  }

  /**
   * creates a method where reports can be stored
   *
   * @return the decorated report method
   */
  protected ASTCDMethod createReportMethod() {
    DiagramSymbol cdsymbol = symbolTableService.getCDSymbol();
    String generatedError = symbolTableService.getGeneratedErrorCode(cdsymbol.getName() + "report");
    Optional<String> startProd = parserService.getStartProdASTFullName();
    ASTMCType startProdType = getMCTypeFacade().createQualifiedType(startProd.get());
    ASTMCType stringType = getMCTypeFacade().createStringType();
    ASTCDParameter pathParameter = getCDParameterFacade().createParameter(stringType, "path");
    ASTCDParameter astParameter = getCDParameterFacade().createParameter(startProdType, "ast");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "report", astParameter, pathParameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("Log.warn(\"0xA1058" + generatedError + " Reports are not implemented yet.\");"));
    return method;
  }

  /**
   * creates a method to execute all default context conditions
   *
   * @return the decorated RunDefaultCoCos method
   */
  protected ASTCDMethod createRunDefaultCoCosMethod() {
    DiagramSymbol cdsymbol = symbolTableService.getCDSymbol();
    String generatedError = symbolTableService.getGeneratedErrorCode(cdsymbol.getName() + "runDefaultCoCos");
    Optional<String> startProd = parserService.getStartProdASTFullName();
    ASTMCType startProdType = getMCTypeFacade().createQualifiedType(startProd.get());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(startProdType, "ast");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "runDefaultCoCos", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("Log.warn(\"0xA1053" + generatedError + " CoCo's are not implemented yet.\");"));
    return method;
  }

  /**
   * creates a method to store the symbolTable in a file
   *
   * @return the decorated StoreSymbols method
   */
  protected ASTCDMethod createStoreSymbolsMethod() {
    String symbols2Json = symbolTableService.getSymbols2JsonFullName();
    String artifactScope = symbolTableService.getArtifactScopeInterfaceFullName();
    ASTMCType artifactScopeType = getMCTypeFacade().createQualifiedType(artifactScope);
    ASTMCType stringType = getMCTypeFacade().createStringType();
    ASTCDParameter scopeParameter = getCDParameterFacade().createParameter(artifactScopeType, "scope");
    ASTCDParameter pathParameter = getCDParameterFacade().createParameter(stringType, "path");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "storeSymbols", scopeParameter, pathParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "StoreSymbols", symbols2Json));
    return method;
  }

  /**
   * creates a method to PrettyPrint a given AST
   *
   * @return the decorated PrettyPrint method
   */
  protected ASTCDMethod createPrettyPrintMethod() {
    DiagramSymbol cdsymbol = symbolTableService.getCDSymbol();
    String generatedError = symbolTableService.getGeneratedErrorCode(cdsymbol.getName() + "prettyPrint");
    Optional<String> startProd = parserService.getStartProdASTFullName();
    ASTMCType startProdType = getMCTypeFacade().createQualifiedType(startProd.get());
    ASTMCType stringType = getMCTypeFacade().createStringType();
    ASTCDParameter fileParameter = getCDParameterFacade().createParameter(stringType, "file");
    ASTCDParameter astParameter = getCDParameterFacade().createParameter(startProdType, "ast");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "prettyPrint", astParameter, fileParameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("Log.warn(\"0xA1052" + generatedError + " PrettyPrinting is not implemented yet.\");"));
    return method;
  }

  /**
   * creates a method to print the help dialog for the CLI
   *
   * @return the decorated PrintHelp method
   */
  protected ASTCDMethod createPrintHelpMethod() {
    String cliName = symbolTableService.getCliSimpleName();
    ASTMCType optionsType = getMCTypeFacade().createQualifiedType("org.apache.commons.cli.Options");
    ASTCDParameter parameter = getCDParameterFacade().createParameter(optionsType, "options");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "printHelp", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "PrintHelp", cliName));
    return method;
  }

  /**
   * creates a method to print the version information to the CLI
   *
   * @return the decorated printVersion method
   */
  protected ASTCDMethod createPrintVersionMethod() {
    String name = symbolTableService.getCDSymbol().getName();

    String metadataFile = name + "Metadata.properties";
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "printVersion");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "PrintVersion", metadataFile));
    return method;
  }

  /**
   * creates a method to initialize the CLI parameters
   *
   * @return the decorated InitOptions method
   */
  protected ASTCDMethod createInitOptionsMethod() {
    ASTMCType returnType = getMCTypeFacade().createQualifiedType("org.apache.commons.cli.Options");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "initOptions");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "InitOptions"));
    return method;
  }

  /**
   * creates a method to initialize the standard CLI parameters
   *
   * @return the decorated InitOptions method
   */
  protected ASTCDMethod createAddStandardOptionsMethod() {
    ASTMCType returnType = getMCTypeFacade().createQualifiedType("org.apache.commons.cli.Options");
    ASTCDParameter parameter = getCDParameterFacade().createParameter(returnType, "options");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "addStandardOptions", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "AddStandardOptions"));
    return method;
  }


  /**
   * creates a method to initialize additional, user-specific CLI parameters
   *
   * @return the decorated InitOptions method
   */
  protected ASTCDMethod createAddAdditionalOptionsMethod() {
    ASTMCType returnType = getMCTypeFacade().createQualifiedType("org.apache.commons.cli.Options");
    ASTCDParameter parameter = getCDParameterFacade().createParameter(returnType, "options");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "addAdditionalOptions", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "AddAdditionalOptions"));
    return method;
  }
  /**
   * creates a method to initialize the logging and Mill
   *
   * @return the decorated InitOptions method
   */
  protected ASTCDMethod createInitMethod() {
    String millFullName = symbolTableService.getMillFullName();
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "init");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "Init", millFullName));
    return method;
  }

  /**
   * creates a method to print a String to a file
   *
   * @return the decorated Print method
   */
  protected ASTCDMethod createPrintMethod(DiagramSymbol cdsymbol) {
    String generatedError = symbolTableService.getGeneratedErrorCode(cdsymbol.getName() + "print");
    ASTMCType stringType = getMCTypeFacade().createStringType();
    ASTCDParameter pathParameter = getCDParameterFacade().createParameter(stringType, "path");
    ASTCDParameter contentParameter = getCDParameterFacade().createParameter(stringType, "content");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "print", contentParameter, pathParameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "Print", generatedError));
    return method;
  }

}
