/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.MC2CDTransformation;
import de.monticore.codegen.parser.ParserGenerator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
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

    Log.ensureInitalization();

    if (args.length != 2) {
      Log.error("0xEE630 Please specify one single path to the input model and one single path for the generated output");
      return;
    }


    try {
      // Create the AST
      String filename = args[0];
      ASTMCGrammar ast = new Grammar_WithConceptsParser()
              .parseMCGrammar(filename).get();

      ASTCDCompilationUnit astCD = new MC2CDTransformation(new GlobalExtensionManagement()).apply(ast);

      // Initialize symbol table
      // (using imported grammars from the model path)
      ModelPath modelPath = new ModelPath(Paths.get(
          "target/monticore-grammar-grammars.jar"));
      IGrammar_WithConceptsGlobalScope gs = Grammar_WithConceptsMill
          .grammar_WithConceptsGlobalScopeBuilder()
          .setModelPath(modelPath)
          .build();
      Grammar_WithConceptsMill
          .grammar_WithConceptsSymbolTableCreatorDelegatorBuilder()
          .setGlobalScope(gs)
          .build()
          .createFromAST(ast);
      // Hand coded path
      IterablePath handcodedPath = IterablePath.empty();

      // Target directory
      File outputDir = new File(args[1]);

      // Generate the parser
      GlobalExtensionManagement glex =  new GlobalExtensionManagement();
      ParserGenerator.generateFullParser(
          glex, astCD, ast, gs, handcodedPath, outputDir);
    }
    catch (IOException e) {
      // If something happens ... handling necessary
      e.printStackTrace();
    }
  }

}
