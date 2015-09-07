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

package de.monticore.codegen.parser.antlr;

import java.nio.file.Path;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarRootAST;

import com.google.common.base.Preconditions;

import de.se_rwth.commons.logging.Log;

/**
 * 
 * ANTLR parser generator
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class AntlrTool extends Tool {
  
  public AntlrTool(String[] args, Path outputDir) {
    super(args);
    Preconditions.checkArgument(outputDir.toFile().exists(),
        "The potput directory for AntlrTool " +
            outputDir + " doesn't exist.");
    this.outputDirectory = outputDir.toString();
    this.haveOutputDir = true;
    handleArgs();
  }
  
  @Override
  public void error(ANTLRMessage message) {
    Log.error(message.toString());
  }
  
  @Override
  public void warning(ANTLRMessage message) {
    Log.warn(message.toString());
  }
  
  /**
   * Parses the given ANTLR grammar and generates parser
   * @param inputFile - ANTLR grammar 
   */
  public void createParser(String inputFile) { 
    Grammar grammar = parseAntlrFile(inputFile);
    generateParser(grammar);
  }
  
  /**
   * Creates a grammar object associated with the ANTLR grammar AST. 
   * @param inputFile - ANTLR grammar 
   * @return a grammar object associated with the ANTLR grammar AST
   */
  public Grammar parseAntlrFile(String inputFile) { 
    GrammarRootAST ast = parseGrammar(inputFile);
    Grammar grammar = createGrammar(ast);
    grammar.fileName = inputFile;
    return grammar;
  }
  
  /**
   * Generates ANTLR Parser
   * @param antlrGrammar
   */
  public void generateParser(Grammar antlrGrammar) { 
    process(antlrGrammar, true);
  }

}
