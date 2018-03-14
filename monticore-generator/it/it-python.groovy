
/* (c) https://github.com/MontiCore/monticore */

package de.monticore

import de.monticore.codegen.parser.Languages

info("--------------------------------", LOG_ID)
info("MontiCore IT Facade (Python)", LOG_ID)
enableFailQuick(true)
globalScope = createGlobalScope(modelPath)

// Parse grammar
astGrammars = parseGrammars(grammars)

for (astGrammar in astGrammars) {

    astGrammar = createSymbolsFromAST(globalScope, astGrammar)

    // Generate parser
    generateParser(glex, astGrammar, globalScope, handcodedPath, out, false, Languages.PYTHON_3)
    generateParser(glex, astGrammar, globalScope, handcodedPath, out, false, Languages.JAVA)

}
