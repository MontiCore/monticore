/* (c) https://github.com/MontiCore/monticore */

package de.monticore.groovy

debug("--------------------------------", LOG_ID)
debug("MontiCore", LOG_ID)
debug(" - eating your models since 2005", LOG_ID)
debug("--------------------------------", LOG_ID)
debug("Grammar argument    : " + _configuration.getGrammarsAsStrings(), LOG_ID)
debug("Grammar files       : " + grammars, LOG_ID)
debug("Modelpath           : " + modelPath, LOG_ID)
debug("Output dir          : " + out, LOG_ID)
debug("Handcoded argument  : " + _configuration.getHandcodedPathAsStrings(), LOG_ID)
debug("Handcoded files     : " + handcodedPath, LOG_ID)

globalScope = createGlobalScope(modelPath)

while (grammarIterator.hasNext()) {
  // Parse grammar
  astGrammar = parseGrammar(grammarIterator.next())

  if (astGrammar.isPresent()) {
    astGrammar = astGrammar.get();

    astGrammar = createSymbolsFromAST(globalScope, astGrammar)

    // Transform AST-Grammar -> AST-CD
    astClassDiagramWithST = deriveCD(astGrammar, glex, globalScope)

    // Writes Class Diagram AST to the CD-file (*.cd)
    storeInCdFile(astClassDiagramWithST, out)

    // Decorate AST-CD
    decorateEmfCd(glex, astClassDiagramWithST, globalScope, handcodedPath)

    // Generate AST files
    generateEmfCompatible(glex, globalScope, astClassDiagramWithST, out, templatePath)
  }
}
