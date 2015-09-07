/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.codegen.parser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.base.Joiner;

import de.monticore.codegen.parser.antlr.AntlrTool;
import de.monticore.codegen.parser.antlr.Grammar2Antlr;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class ParserGenerator {
  
  public final static String PARSER_PACKAGE = "_parser";
  
  public static final String LOG = "ParserGenerator";
  
  /**
   * Code generation from grammar ast to an antlr compatible file format
   * 
   * @param astGrammar - grammar AST
   * @param targetFile - target file
   */
  public static void generateParser(ASTMCGrammar astGrammar, Scope symbolTable, File targetFile) {
    if (astGrammar.isComponent()) {
      Log.info("No parser generation for the grammar " + astGrammar.getName(), LOG);
      return;
    }
    Log.debug("Start parser generation for the grammar " + astGrammar.getName(), LOG);
    final GeneratorSetup setup = new GeneratorSetup(targetFile);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    
    String qualifiedGrammarName = astGrammar.getPackage().isEmpty() ? astGrammar.getName() :
      Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackage()),
          astGrammar.getName());
    MCGrammarSymbol grammarSymbol = symbolTable.<MCGrammarSymbol> resolve(
        qualifiedGrammarName, MCGrammarSymbol.KIND).orElse(null);
    Log.errorIfNull(grammarSymbol, "0xA4034 Grammar " + qualifiedGrammarName
        + " can't be resolved in the scope " + symbolTable);
    
    ParserGeneratorHelper genHelper = new ParserGeneratorHelper(astGrammar, grammarSymbol);
    glex.setGlobalValue("parserHelper", genHelper);
    glex.setGlobalValue("nameHelper", new Names());
    setup.setGlex(glex);
    
    // TODO: grammarInfo as parameter for this method?
    MCGrammarInfo grammarInfo = new MCGrammarInfo(genHelper.getGrammarSymbol());
    
    final GeneratorEngine generator = new GeneratorEngine(setup);
    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getParserPackage()),  
        astGrammar.getName() + ".g4");
    generator.generate("parser.Parser", filePath, astGrammar, new Grammar2Antlr(genHelper, grammarInfo));
    
    // construct parser, lexer, ... (antlr), 
    String gFile = Paths.get(targetFile.getAbsolutePath(), filePath.toString()).toString();
    Log.debug("Start Antlr generation for the antlr file " + gFile, LOG);
    AntlrTool antlrTool = new AntlrTool(new String[]{}, 
        Paths.get(targetFile.getAbsolutePath(),Names.getPathFromPackage(genHelper.getParserPackage())));
    
    antlrTool.createParser(gFile);
    Log.debug("End parser generation for the grammar " + astGrammar.getName(), LOG);
  }
  
  /**
   * Code generation from grammar ast to an antlr compatible file format
   * 
   * @param astGrammar - grammar AST
   * @param targetFile - target file
   */
  public static void generateParserWrappers(ASTMCGrammar astGrammar, Scope symbolTable, File targetFile) {
    if (astGrammar.isComponent()) {
      return;
    }
    final GeneratorSetup setup = new GeneratorSetup(targetFile);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    
    String qualifiedGrammarName = astGrammar.getPackage().isEmpty() ? astGrammar.getName() :
      Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackage()),
          astGrammar.getName());
    MCGrammarSymbol grammarSymbol = symbolTable.<MCGrammarSymbol> resolve(
        qualifiedGrammarName, MCGrammarSymbol.KIND).orElse(null);
        Log.errorIfNull(grammarSymbol, "0xA4035 Grammar " + qualifiedGrammarName
        + " can't be resolved in the scope " + symbolTable);
    ParserGeneratorHelper genHelper = new ParserGeneratorHelper(astGrammar, grammarSymbol);
    glex.setGlobalValue("parserHelper", genHelper);
    glex.setGlobalValue("nameHelper", new Names());
    setup.setGlex(glex);
    
    final GeneratorEngine generator = new GeneratorEngine(setup);
    // Retrieve data
    for (MCRuleSymbol rule : genHelper.getGrammarSymbol().getRulesWithInherited().values()) {
      final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getParserPackage()), rule.getName() + "MCParser.java");
      if (rule.getAstNode().isPresent() && genHelper.generateParserForRule(rule)) {
        generator.generate("parser.MCConcreteParser", filePath, rule.getAstNode().get(), rule);
      }
    }
    
    // Generate Factory for wrapper methods
    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getParserPackage()), astGrammar.getName() + "ParserFactory.java");
    generator.generate("parser.ParserFactory", filePath, astGrammar, astGrammar, genHelper.getGrammarSymbol().getRulesWithInherited().values());
    
  }
  
  private ParserGenerator() {
    // noninstantiable
  }
}
