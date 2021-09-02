/* (c) https://github.com/MontiCore/monticore */

import de.monticore.codegen.parser.ParserGenerator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class GenerateAutomataParser {

  /**
   * Parse Automata.mc4 and create a Parser for the language
   */
  public static void main(String[] args) {

    Log.ensureInitalization();

    if (args.length != 2) {
      Log.error("0xEE630 Please specify one single path to the input model and one single path for the generated output");
      return;
    }


    try {
      // Create the AST
      String filename = args[0];
      ASTMCGrammar ast = Grammar_WithConceptsMill.parser()
              .parse(filename).get();
      
      // Initialize symbol table
      // (using imported grammars from the model path)
      MCPath modelPath = new MCPath(Paths.get(
          "target/monticore-grammar-grammars.jar"));
      IGrammar_WithConceptsGlobalScope gs = Grammar_WithConceptsMill
          .globalScope();
      gs.setSymbolPath(modelPath);
      
      Grammar_WithConceptsMill.scopesGenitorDelegator()
          .createFromAST(ast);
      
      // Hand coded path
      MCPath handcodedPath = new MCPath();

      // Template path
      MCPath templatePath = new MCPath();

      // Target directory
      File outputDir = new File(args[1]);

      // Generate the parser
      GlobalExtensionManagement glex = new GlobalExtensionManagement();
      ParserGenerator.generateParser(
          glex, ast, gs, handcodedPath, templatePath, outputDir);
    }
    catch (IOException e) {
      // If something happens ... handling necessary
      e.printStackTrace();
    }
  }

}
