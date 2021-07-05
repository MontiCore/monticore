/* (c) https://github.com/MontiCore/monticore */

package de.monticore

import de.monticore.codegen.parser.Languages

info("--------------------------------", LOG_ID)
info("MontiCore IT Facade (Python)", LOG_ID)
enableFailQuick(true)
globalScope = createMCGlobalScope(modelPath)

// Parse grammar
astGrammars = parseGrammars(grammars)

Reporting.off()
glex = initGlex(_configuration)

for (astGrammar in astGrammars) {
    input = grammarIterator.next()
    astGrammar = createSymbolsFromAST(globalScope, astGrammar)

    // Generate parser
    generateParser(glex, astGrammar, globalScope, handcodedPath, templatePath, out, false, Languages.PYTHON_3)
}