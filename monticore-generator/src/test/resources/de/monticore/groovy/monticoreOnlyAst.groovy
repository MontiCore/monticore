/* (c) https://github.com/MontiCore/monticore */

package de.monticore.groovy

debug("--------------------------------", LOG_ID)
debug("MontiCore", LOG_ID)
debug(" - eating your models since 2005", LOG_ID)
debug("--------------------------------", LOG_ID)
debug("Input files    : " + _configuration.getGrammarsAsStrings(), LOG_ID)
debug("Modelpath      : " + _configuration.getModelPathAsStrings(), LOG_ID)
debug("Output dir     : " + out, LOG_ID)
debug("Handcoded path : " + _configuration.getHandcodedPathAsStrings(), LOG_ID)

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
    
    getCDOfParsedGrammar(astGrammar)

    // M7: decorate Class Diagram AST

    generateVisitors(glex, globalScope, astClassDiagramWithST, out, handcodedPath)
    generateCocos(glex, globalScope, astClassDiagramWithST, out)
    generateODs(glex, globalScope, astClassDiagramWithST, out)

    decoratedASTClassDiagramm = decorateForASTPackage(glex, astClassDiagramWithST, modelPath, handcodedPath)
    generateFromCD(glex, decoratedASTClassDiagramm, out, handcodedPath)

  }
}
