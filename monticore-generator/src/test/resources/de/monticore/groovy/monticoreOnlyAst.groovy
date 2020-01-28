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

cd4AScope = createCD4AGlobalScope(modelPath)
mcScope = createMCGlobalScope(modelPath)

while (grammarIterator.hasNext()) {
  // Parse grammar
  astGrammar = parseGrammar(grammarIterator.next())

  if (astGrammar.isPresent()) {
    astGrammar = astGrammar.get()

    astGrammar = createSymbolsFromAST(mcScope, astGrammar)

    // Transform AST-Grammar -> AST-CD
    astClassDiagramWithST = deriveCD(astGrammar, glex, cd4AScope)

    // Writes Class Diagram AST to the CD-file (*.cd)
    storeInCdFile(astClassDiagramWithST, out)

    getCDOfParsedGrammar(astGrammar)

    // M7: decorate Class Diagram AST

    decoratedVisitorCD = decorateForVisitorPackage(glex, cdScope, astClassDiagram, handcodedPath)
    generateFromCD(glex, astClassDiagram, decoratedVisitorCD, out, handcodedPath)

    decoratedCoCoCD = decorateForCoCoPackage(glex, cdScope, astClassDiagram, handcodedPath)
    generateFromCD(glex, astClassDiagram, decoratedCoCoCD, out, handcodedPath)

    // decorate and generate CD for the '_od' package
    decoratedODCD = decorateForODPackage(glex, cdScope, astClassDiagram, handcodedPath)
    generateFromCD(glex, astClassDiagram, decoratedODCD, out, handcodedPath)

    decoratedASTClassDiagramm = decorateForASTPackage(glex, cd4AScope, astClassDiagramWithST, handcodedPath)
    generateFromCD(glex, astClassDiagramWithST, decoratedASTClassDiagramm, out, handcodedPath)

  }
}
