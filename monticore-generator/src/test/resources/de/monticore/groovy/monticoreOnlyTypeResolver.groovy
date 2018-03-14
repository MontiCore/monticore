/* (c) https://github.com/MontiCore/monticore */

package de.monticore.groovy

import de.monticore.codegen.cd2java.types.TypeResolverGenerator

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

        // Generate Visitor And Type Resolver
        TypeResolverGenerator.generate(glex, globalScope, astClassDiagramWithST, out)
    }
}
