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

package de.monticore.groovy

debug("--------------------------------", LOG_ID)
debug("MontiCore", LOG_ID)
debug(" - eating your models since 2005", LOG_ID)
debug("--------------------------------", LOG_ID)
debug("Input files    : " + _configuration.getGrammarsAsStrings(), LOG_ID)
debug("Modelpath      : " + _configuration.getModelPathAsStrings(), LOG_ID)
debug("Output dir     : " + out, LOG_ID)
debug("Handcoded path : " + _configuration.getHandcodedPathAsStrings(), LOG_ID)

grammarIterator = grammars.getResolvedPaths()
// Create object for managing hook points, features and global variables
glex = new GlobalExtensionManagement()
symbolTable = initSymbolTable(modelPath)

while (grammarIterator.hasNext()) {
    // Parse grammar
    astGrammar = parseGrammar(grammarIterator.next())

    if (astGrammar.isPresent()) {
        astGrammar = astGrammar.get();

        astGrammar = createSymbolsFromAST(symbolTable, astGrammar)

        // Transform AST-Grammar -> AST-CD
        astClassDiagram = transformAstGrammarToAstCd(glex, astGrammar, symbolTable, handcodedPath)

        astClassDiagramWithST = createSymbolsFromAST(symbolTable, astClassDiagram)

        // Generate symbol table
        generateSymbolTable(astGrammar, symbolTable, astClassDiagram, out, handcodedPath)
    }
}
