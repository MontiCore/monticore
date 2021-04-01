/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import com.google.common.base.Joiner;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CDGenerator;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java._parser.ParserCDDecorator;
import de.monticore.codegen.cd2java._parser.ParserClassDecorator;
import de.monticore.codegen.cd2java._parser.ParserForSuperDecorator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java.top.TopDecorator;
import de.monticore.codegen.parser.antlr.AntlrTool;
import de.monticore.codegen.parser.antlr.Grammar2Antlr;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ParserGenerator {

  public static final String PARSER_PACKAGE = "_parser";

  public static final String LOG = "ParserGenerator";


  /**
   * Code generation from grammar ast to an antlr compatible file format and wrapper parser
   *
   * @param glex
   * @param symbolTable - symbol table already derived from grammar AST
   * @param astGrammar - grammar AST
   * @param targetDir - target file
   */
  public static void generateFullParser(
      GlobalExtensionManagement glex,
      ASTCDCompilationUnit astClassDiagram,
      ASTMCGrammar astGrammar,
      IGrammar_WithConceptsGlobalScope symbolTable,
      IterablePath handcodedPath,
      IterablePath templatePath,
      File targetDir)
  {
    generateParser(glex, astGrammar, symbolTable, handcodedPath, templatePath, targetDir);
    generateParserWrapper(glex, astClassDiagram, handcodedPath, templatePath, targetDir);
  }


  /**
   * Code generation from grammar ast to an antlr compatible file format
   *
   * @param glex
   * @param symbolTable - symbol table already derived from grammar AST
   * @param astGrammar - grammar AST
   * @param targetDir - target file
   */
  public static void generateParser(
      GlobalExtensionManagement glex,
      ASTMCGrammar astGrammar,
      IGrammar_WithConceptsGlobalScope symbolTable,
      IterablePath handcodedPath,
      IterablePath templatePath,
      File targetDir)
  {
    generateParser(glex, astGrammar, symbolTable, handcodedPath, templatePath, targetDir, true, Languages.JAVA);
  }


  /**
   * Code generation from grammar ast to an antlr compatible file format
   *
   * @param glex
   * @param symbolTable - symbol table already derived from grammar AST
   * @param astGrammar - grammar AST
   * @param targetDir - target file
   * @param embeddedJavaCode - embed Java Code
   */
  public static void generateParser(
      GlobalExtensionManagement glex,
      ASTMCGrammar astGrammar,
      IGrammar_WithConceptsGlobalScope symbolTable,
      IterablePath handcodedPath,
      IterablePath templatePath,
      File targetDir,
      boolean embeddedJavaCode,
      Languages lang) {
    if (astGrammar.isComponent()) {
      Log.info("No parser generation for the grammar " + astGrammar.getName(), LOG);
      return;
    }
    Log.debug("Start parser generation for the grammar " + astGrammar.getName(), LOG);
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setHandcodedPath(handcodedPath);
    setup.setAdditionalTemplatePaths(templatePath.getPaths().stream().map(p -> new File(p.toUri())).collect(Collectors.toList()));
    setup.setOutputDirectory(targetDir);

    String qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
        ? astGrammar.getName()
        : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
        astGrammar.getName());
    MCGrammarSymbol grammarSymbol = symbolTable.<MCGrammarSymbol> resolveMCGrammar(
        qualifiedGrammarName).orElse(null);
    Log.errorIfNull(grammarSymbol, "0xA4034 Grammar " + qualifiedGrammarName
        + " can't be resolved in the scope " + symbolTable);

    MCGrammarInfo grammarInfo = new MCGrammarInfo(grammarSymbol);

    ParserGeneratorHelper genHelper = new ParserGeneratorHelper(astGrammar, grammarInfo, embeddedJavaCode, lang);
    glex.setGlobalValue("parserHelper", genHelper);
    glex.setGlobalValue("nameHelper", new Names());
    setup.setGlex(glex);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getParserPackage()),
        astGrammar.getName() + "Antlr.g4");
    Grammar2Antlr grammar2Antlr = new Grammar2Antlr(genHelper, grammarInfo, true);
    Grammar_WithConceptsTraverser traverser = Grammar_WithConceptsMill.traverser();
    traverser.add4Grammar(grammar2Antlr);
    traverser.setGrammarHandler(grammar2Antlr);
    new GeneratorEngine(setup).generate("parser.Parser", filePath, astGrammar, grammar2Antlr);

    // construct parser, lexer, ... (antlr),
    String gFile = Paths.get(targetDir.getAbsolutePath(), filePath.toString()).toString();
    String outputLang = "-Dlanguage=" + lang.getLanguage();
    Log.debug("Start Antlr generation for the antlr file " + gFile, LOG);
    AntlrTool antlrTool = new AntlrTool(new String[] { outputLang }, grammarSymbol,
        Paths.get(targetDir.getAbsolutePath(),
            Names.getPathFromPackage(genHelper.getParserPackage())));
    antlrTool.createParser(gFile);
    Log.debug("End parser generation for the grammar " + astGrammar.getName(), LOG);
  }

  /**
   * Code generation from grammar ast to an antlr compatible file format
   * @param glex
   *
   * @param targetDir - target dir
   */
  public static void generateParserWrapper(
      GlobalExtensionManagement glex,
      ASTCDCompilationUnit astClassDiagram,
      IterablePath handcodedPath,
      IterablePath templatePath,
      File targetDir) {
    final ParserService parserService = new ParserService(astClassDiagram);
    final ParserClassDecorator parserClassDecorator = new ParserClassDecorator(glex, parserService);
    final ParserForSuperDecorator parserForSuperDecorator = new ParserForSuperDecorator(glex, parserService);
    final ParserCDDecorator parserCDDecorator = new ParserCDDecorator(glex, parserClassDecorator, parserForSuperDecorator, parserService);

    ASTCDCompilationUnit decoratedCD = parserCDDecorator.decorate(astClassDiagram);

    TopDecorator topDecorator = new TopDecorator(handcodedPath);
    decoratedCD = topDecorator.decorate(decoratedCD);

    glex.setGlobalValue("service", new AbstractService(astClassDiagram));
    glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    final String diagramName = decoratedCD.getCDDefinition().getName();
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir);
    setup.setHandcodedPath(handcodedPath);
    setup.setAdditionalTemplatePaths(templatePath.getPaths().stream().map(p -> new File(p.toUri())).collect(Collectors.toList()));
    setup.setModelName(diagramName);
    setup.setGlex(glex);
    CDGenerator generator = new CDGenerator(setup);
    generator.generate(decoratedCD);
  }
  private ParserGenerator() {
    // noninstantiable
  }
}
