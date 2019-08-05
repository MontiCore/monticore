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

// Parse grammar
astGrammars = parseGrammars(grammars)

for (astGrammar in astGrammars) {

  astGrammar = createSymbolsFromAST(globalScope, astGrammar)

  // Generate parser
  generateParser(glex, astGrammar, globalScope, handcodedPath, out)

}
