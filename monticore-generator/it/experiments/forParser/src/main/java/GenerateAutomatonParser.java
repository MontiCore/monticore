import java.io.IOException;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._symboltable.GrammarSymbolTableCreatorDelegator;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsLanguage;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsSymbolTableCreatorDelegator;
import de.se_rwth.commons.logging.Log;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.parser.ParserGenerator;

import java.io.File;
import java.nio.file.Paths;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;


import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;

public class GenerateAutomatonParser {
  
  /**
   * Parse Automaton.mc4 and create a Parser for the language
   */
  public static void main(String[] args) {
    Log.init();

    try {
      // Create the AST
      String filename = "Automaton.mc4";
      ASTMCGrammar ast;
      ast = new Grammar_WithConceptsParser()
                      .parseMCGrammar(filename).get();
	  
      // Initialize symbol table
      // (using imported grammars from the model path)
      ModelPath modelPath = new ModelPath(Paths.get("monticore-cli.jar"));
      Grammar_WithConceptsLanguage l = new Grammar_WithConceptsLanguage();
      Grammar_WithConceptsGlobalScope gs = new Grammar_WithConceptsGlobalScope(modelPath, l);
      new Grammar_WithConceptsSymbolTableCreatorDelegator(gs);
      // Hand coded path
      IterablePath handcodedPath = IterablePath.empty();
      
      // Target directory
      File outputDir = new File("gen");
      
      // Generate the parser
      ParserGenerator.generateFullParser(new GlobalExtensionManagement(),ast, gs, handcodedPath, outputDir);
    }
    catch (IOException e) {
      // If something happens ... handling necessary
      e.printStackTrace();
    }
  }
  
}
