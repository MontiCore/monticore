/* (c) Monticore license: https://github.com/MontiCore/monticore */
import java.io.IOException;

import de.se_rwth.commons.logging.Log;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.parser.ParserGenerator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MontiCoreGrammarLanguage;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;

import de.monticore.grammar.symboltable.MontiCoreGrammarSymbolTableCreator;

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
      MCGrammarSymbolTableHelper.initializeSymbolTable(ast, modelPath);
      
      // Hand coded path
      IterablePath handcodedPath = IterablePath.empty();
      
      // Target directory
      File outputDir = new File("gen");
      
      // Generate the parser
      ParserGenerator.generateFullParser(ast, handcodedPath, outputDir);
    }
    catch (IOException e) {
      // If something happens ... handling necessary
      e.printStackTrace();
    }
  }
  
}
