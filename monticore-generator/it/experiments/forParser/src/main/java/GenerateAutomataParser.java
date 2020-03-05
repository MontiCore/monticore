/* (c) https://github.com/MontiCore/monticore */

import de.monticore.codegen.parser.ParserGenerator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsLanguage;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsSymbolTableCreatorDelegator;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class GenerateAutomataParser {

  /**
   * Parse Automata.mc4 and create a Parser for the language
   */
  public static void main(String[] args) {
    Log.init();
    if (args.length != 2) {
      Log.error("Please specify one single path to the input model and one single path for the generated output");
      return;
    }

    String filename = args[0];

    try {
      // Create the AST
      ASTMCGrammar ast;
      ast = new Grammar_WithConceptsParser()
              .parseMCGrammar(filename).get();

      // Initialize symbol table
      // (using imported grammars from the model path)
      ModelPath modelPath = new ModelPath(Paths.get("target/monticore-grammar-grammars.jar"));
      Grammar_WithConceptsLanguage l = new Grammar_WithConceptsLanguage();
      Grammar_WithConceptsGlobalScope gs = new Grammar_WithConceptsGlobalScope(modelPath, l);
      new Grammar_WithConceptsSymbolTableCreatorDelegator(gs).createFromAST(ast);
      // Hand coded path
      IterablePath handcodedPath = IterablePath.empty();

      // Target directory
      File outputDir = new File(args[1]);

      // Generate the parser
      ParserGenerator.generateFullParser(new GlobalExtensionManagement(), ast, gs, handcodedPath, outputDir);
    }
    catch (IOException e) {
      // If something happens ... handling necessary
      e.printStackTrace();
    }
  }

}
