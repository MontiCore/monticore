/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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

info("--------------------------------", LOG_ID)
info(" Custom Script", LOG_ID)
info("--------------------------------", LOG_ID)
debug("Grammar argument    : " + _configuration.getGrammarsAsStrings(), LOG_ID)
info("Grammar files       : " + grammars, LOG_ID)
info("Modelpath           : " + modelPath, LOG_ID)
debug("Output dir          : " + out, LOG_ID)
debug("Handcoded argument  : " + _configuration.getHandcodedPathAsStrings(), LOG_ID)
info("Handcoded files     : " + handcodedPath, LOG_ID)

// ############################################################
// M0 Incremental Generation
IncrementalChecker.initialize(out)
resetModelToArtifactMap()
globalScope = createGlobalScope(modelPath)

// M1: basic setup and initialization; enabling of reporting
Reporting.init(out.getAbsolutePath(), reportManagerFactory)
// ############################################################

// ############################################################
// the first pass processes all input grammars up to transformation to CD and storage of the resulting CD to disk
while (grammarIterator.hasNext()) {
  input = grammarIterator.next()
  if (force || !IncrementalChecker.isUpToDate(input, out, modelPath, templatePath, handcodedPath )) {
    IncrementalChecker.cleanUp(input)

    // M2: parse grammar
    astGrammar = parseGrammar(input)

    if (astGrammar.isPresent()) {
      astGrammar = astGrammar.get()

      // start reporting
      grammarName = Names.getQualifiedName(astGrammar.getPackageList(), astGrammar.getName())
      Reporting.on(grammarName)
      Reporting.reportParseInputFile(input, grammarName)

      // M3: populate symbol table
      astGrammar = createSymbolsFromAST(globalScope, astGrammar)

      // M4: execute context conditions
      runGrammarCoCos(astGrammar, globalScope)

      // M7: transform grammar AST into Class Diagram AST
      astClassDiagram = transformAstGrammarToAstCd(glex, astGrammar, globalScope, handcodedPath)

      astClassDiagramWithST = createSymbolsFromAST(globalScope, astClassDiagram)

      // write Class Diagram AST to the CD-file (*.cd)
      storeInCdFile(astClassDiagramWithST, out)

      // M5 + M6: generate parser
      generateParser(glex, astGrammar, globalScope, handcodedPath, out)

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
  Reporting.on(Names.getQualifiedName(astGrammar.getPackageList(), astGrammar.getName()))
  reportGrammarCd(astGrammar, globalScope, out)

  astClassDiagram = getCDOfParsedGrammar(astGrammar)

  // M8: decorate Class Diagram AST
  decorateEmfCd(glex, astClassDiagram, globalScope, handcodedPath)

  // M?: generate symbol table
  generateSymbolTable(astGrammar, globalScope, astClassDiagram, out, handcodedPath)

  // M9: generate AST classes
  generateEmfCompatible(glex, globalScope, astClassDiagram, out, templatePath)

  info("Grammar " + astGrammar.getName() + " processed successfully!", LOG_ID)

  // M10: flush reporting
  flush(astGrammar)
}
