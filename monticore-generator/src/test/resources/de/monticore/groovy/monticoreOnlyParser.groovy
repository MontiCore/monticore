/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

package de.monticore.groovy

debug("--------------------------------")
debug("MontiCore")
debug(" - eating your models since 2005")
debug("--------------------------------")
debug("Input files    : " + _configuration.getGrammarsAsStrings())
debug("Modelpath      : " + _configuration.getModelPathAsStrings())
debug("Output dir     : " + out)
debug("Handcoded path : " + _configuration.getHandcodedPathAsStrings())

// Parse grammar
astGrammars = parseGrammars(grammars)
glex = new GlobalExtensionManagement()
symbolTable = initSymbolTable(modelPath)

for (astGrammar in astGrammars) {  
  
  astGrammar = createSymbolsFromAST(symbolTable, astGrammar)
  
  // Generate parser
  generateParser(glex, astGrammar, symbolTable, handcodedPath, out)
  
  // Generate wrappers for parser rules
  generateParserWrappers(glex, astGrammar, symbolTable, handcodedPath, out)
  
}
