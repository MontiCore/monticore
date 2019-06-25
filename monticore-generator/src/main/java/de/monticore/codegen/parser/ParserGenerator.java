/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import com.google.common.base.Joiner;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.parser.antlr.AntlrTool;
import de.monticore.codegen.parser.antlr.Grammar2Antlr;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ParserGenerator {

  public static final String PARSER_PACKAGE = "_parser";

  public static final String PARSER_WRAPPER = "Parser";

  public static final String LOG = "ParserGenerator";


  /**
   * Code generation from grammar ast to an antlr compatible file format and wrapper parser
   *
   * @param glex
   * @param symbolTable - symbol table already derived from grammar AST
   * @param astGrammar - grammar AST
   * @param targetDir - target file
   */
  public static void generateFullParser(GlobalExtensionManagement glex,
                                        ASTMCGrammar astGrammar,
                                        Grammar_WithConceptsGlobalScope symbolTable,
                                        IterablePath handcodedPath,
                                        File targetDir) {
    generateParser(glex, astGrammar, symbolTable, handcodedPath, targetDir);
    generateParserWrapper(glex, astGrammar, symbolTable, handcodedPath, targetDir);
  }


  /**
   * Code generation from grammar ast to an antlr compatible file format
   *
   * @param glex
   * @param symbolTable - symbol table already derived from grammar AST
   * @param astGrammar - grammar AST
   * @param targetDir - target file
   */
  public static void generateParser(GlobalExtensionManagement glex,
                                    ASTMCGrammar astGrammar,
                                    Grammar_WithConceptsGlobalScope symbolTable,
                                    IterablePath handcodedPath,
                                    File targetDir) {
    generateParser(glex, astGrammar, symbolTable, handcodedPath, targetDir, true, Languages.JAVA);
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
  public static void generateParser(GlobalExtensionManagement glex,
                                    ASTMCGrammar astGrammar,
                                    Grammar_WithConceptsGlobalScope symbolTable,
                                    IterablePath handcodedPath,
                                    File targetDir,
                                    boolean embeddedJavaCode,
                                    Languages lang) {
    if (astGrammar.isComponent()) {
      Log.info("No parser generation for the grammar " + astGrammar.getName(), LOG);
      return;
    }
    Log.debug("Start parser generation for the grammar " + astGrammar.getName(), LOG);
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir);

    String qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
            ? astGrammar.getName()
            : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
            astGrammar.getName());
    MCGrammarSymbol grammarSymbol = symbolTable.<MCGrammarSymbol> resolveMCGrammar(
            qualifiedGrammarName).orElse(null);
    Log.errorIfNull(grammarSymbol, "0xA4034 Grammar " + qualifiedGrammarName
            + " can't be resolved in the scope " + symbolTable);

    // TODO: grammarInfo as parameter for this method?
    MCGrammarInfo grammarInfo = new MCGrammarInfo(grammarSymbol);

    ParserGeneratorHelper genHelper = new ParserGeneratorHelper(astGrammar, grammarInfo, embeddedJavaCode, lang);
    glex.setGlobalValue("parserHelper", genHelper);
    glex.setGlobalValue("nameHelper", new Names());
    setup.setGlex(glex);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getParserPackage()),
            astGrammar.getName() + "Antlr.g4");
    new GeneratorEngine(setup).generate("parser.Parser", filePath, astGrammar,
            new Grammar2Antlr(genHelper,
                    grammarInfo, embeddedJavaCode));

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
   * @param astGrammar - grammar AST
   * @param targetDir - target dir
   */
  public static void generateParserWrapper(GlobalExtensionManagement glex, ASTMCGrammar astGrammar,
                                           Grammar_WithConceptsGlobalScope symbolTable,
                                           IterablePath handcodedPath, File targetDir) {
    if (astGrammar.isComponent()) {
      return;
    }
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir);

    String qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
            ? astGrammar.getName()
            : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
            astGrammar.getName());
    MCGrammarSymbol grammarSymbol = symbolTable.<MCGrammarSymbol> resolveMCGrammar(
            qualifiedGrammarName).orElse(null);
    Log.errorIfNull(grammarSymbol, "0xA4035 Grammar " + qualifiedGrammarName
            + " can't be resolved in the scope " + symbolTable);
    glex.setGlobalValue("nameHelper", new Names());
    setup.setGlex(glex);

    final GeneratorEngine generator = new GeneratorEngine(setup);
    // Generate wrapper
    String parserWrapperSuffix = TransformationHelper.existsHandwrittenClass(handcodedPath,
            GeneratorHelper.getDotPackageName(
                    GeneratorHelper.getPackageName(astGrammar, PARSER_PACKAGE)) + astGrammar.getName()
                    + PARSER_WRAPPER) ? GeneratorSetup.GENERATED_CLASS_SUFFIX : "";
    final Path path = Paths.get(
            Names.getPathFromPackage(GeneratorHelper.getPackageName(astGrammar, PARSER_PACKAGE)),
            astGrammar.getName() + "Parser" + parserWrapperSuffix + ".java");
    generator.generate("parser.MCParser", path, astGrammar, astGrammar,
            parserWrapperSuffix, grammarSymbol.getProdsWithInherited().values());
  }
  private ParserGenerator() {
    // noninstantiable
  }
}
