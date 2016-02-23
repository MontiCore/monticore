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

package de.monticore

// M1: configuration object "_configuration" prepared externally
debug("--------------------------------")
debug("MontiCore")
debug(" - eating your models since 2005")
debug("--------------------------------")
debug("Grammar argument    : " + _configuration.getGrammarsAsStrings())
debug("Grammar files       : " + grammars)
debug("Modelpath           : " + modelPath)
debug("Output dir          : " + out)
debug("Handcoded argument  : " + _configuration.getHandcodedPathAsStrings())
debug("Handcoded files     : " + handcodedPath)

// ############################################################
// M1: basic setup and initialization; enabling of reporting
initGlobals(_configuration)
// ############################################################

// ############################################################
// the first pass processes all input grammars up to transformation to CD and storage of the resulting CD to disk
while (grammarIterator.hasNext()) {
  input = grammarIterator.next()
  if (force || !isUpToDate(input)) {
    cleanUp(input)
    
    // M2: parse grammar
    astGrammar = parseGrammar(input)
    
    if (astGrammar.isPresent()) {
      astGrammar = astGrammar.get()
      
      startReportingFor(astGrammar, input)
      
      // M3: populate symbol table
      astGrammar = createSymbolsFromAST(symbolTable, astGrammar)
      
      // M4: execute context conditions
      runGrammarCoCos(astGrammar, symbolTable)
      
      // M7: transform grammar AST into Class Diagram AST
      astClassDiagram = transformAstGrammarToAstCd(glex, astGrammar, symbolTable, handcodedPath)
      
      astClassDiagramWithST = createSymbolsFromAST(symbolTable, astClassDiagram)
      
      // write Class Diagram AST to the CD-file (*.cd)
      storeInCdFile(astClassDiagramWithST, out)
      
      // M5 + M6: generate parser
      generateParser(astGrammar, symbolTable, handcodedPath, out)
      generateParserWrappers(astGrammar, symbolTable, handcodedPath, out)
      
      // store result of the first pass
      storeCDForGrammar(astGrammar, astClassDiagramWithST)
    }
  }
}
// end of first pass
// ############################################################

// ############################################################
// the second pass
// do the rest which requires already created CDs of possibly
// local super grammars etc.
for (astGrammar in getParsedGrammars()) {
  // make sure to use the right report manager again
  reportingFor(astGrammar)
  
  astClassDiagram = getCDOfParsedGrammar(astGrammar)
  
  // M8: decorate Class Diagram AST
  decorateEmfCd(glex, astClassDiagram, symbolTable, handcodedPath)
  
  // M?: generate symbol table
  generateSymbolTable(astGrammar, symbolTable, astClassDiagram, out, handcodedPath)
  
  // M9: generate AST classes
  generateEmfCompatible(glex, symbolTable, astClassDiagram, out, templatePath)
  
  info("Grammar " + astGrammar.getName() + " processed successfully!")
  
  // M10: flush reporting
  flushReporting(astGrammar)
}
