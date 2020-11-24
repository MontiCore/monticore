/* (c) https://github.com/MontiCore/monticore */

package de.monticore

/*
 * This configuration file contains the standard workflow of MontiCore:
 * It mainly processes a grammar (and imported sub-grammars) and produces
 * a variety of outputs, such as 
 * * a parser
 * * AST classes
 * * visitors
 * * symbol and scope management, etc.
 *
 * The workflow is organized in ten steps (M1 - M10).
 * For a detailed description see the MontiCore reference manual
 * at https:/www.monticore.de/
 */

// ############################################################
// M1  basic setup and initialization:
// Initialize logging; reporting; create global scope

// M1.1 Logging
TODO: initialize Logging here (1 Zeiler) der Art:
useLogbackConfiguration(argmap.get("-cl")); 

// M1.2 Initial information on debug level
Log.debug("--------------------------------", LOG_ID)
Log.debug("MontiCore", LOG_ID)
Log.debug(" - eating your models since 2005", LOG_ID)
Log.debug("--------------------------------", LOG_ID)
Log.debug("Grammar files       : " + grammars, LOG_ID)
Log.debug("Modelpath           : " + argmap.get("-mp"), LOG_ID)
Log.debug("Output dir          : " + argmap.get("-o"), LOG_ID)
Log.debug("Report dir          : " + argmap.get("-r"), LOG_ID)
Log.debug("Handcoded argument  : " + argmap.get("-hpxx"), LOG_ID)
Log.debug("Handcoded files     : " + ..., LOG_ID)

// M1.3 Build Global Scope 
mcScope = createMCGlobalScope(modelPath)	

// M1.4 Initialize Reporting (output)
Reporting.init(out.getAbsolutePath(), report.getAbsolutePath(), reportManagerFactory)


// ############################################################
// Loop over the list of grammars provided as arguments
// (these grammars are usually independent or build on each other in the
//  correct order.)
// Dependency management is not in the scope of MontiCore itself.

// ############################################################
// the first pass processes all input grammars
// transforms them to an integrated CD 
resultMap = [:]
while (grammarIterator.hasNext()) {
    input = grammarIterator.next()

    // M2.1: parse grammar
    astGrammar = parseGrammar(input)

    if (astGrammar.isPresent()) {
      astGrammar = astGrammar.get()

      // M2.2: start reporting on that grammar
      grammarName = Names.getQualifiedName(astGrammar.getPackageList(), astGrammar.getName())
      Reporting.on(grammarName)
      Reporting.reportModelStart(astGrammar, grammarName, "")
      Reporting.reportParseInputFile(input, grammarName)

      // M3: populate symbol table
      astGrammar = createSymbolsFromAST(mcScope, astGrammar)

      // M4: execute context conditions
      runGrammarCoCos(astGrammar, mcScope)

      // M5.1: transform grammar AST into Class Diagram AST
      astClassDiagramWithST = deriveCD(astGrammar, glex, mcScope)

      // M5.2: create symbol and scope class diagramm for the CD
      symbolClassDiagramm = deriveSymbolCD(astGrammar, mcScope)
      scopeClassDiagramm = deriveScopeCD(astGrammar, mcScope)

      // M5.3 report the basic class diagram for AST
      reportCD(astClassDiagramWithST, report)
      
      // M5.4 report the full AST incl. Symbols diagrams
      astClassDiagramWithST = addListSuffixToAttributeName(astClassDiagramWithST)
      reportCD(astClassDiagramWithST, decoratedASTClassDiagramm, decoratedSymbolTableCd, scopeClassDiagramm, report)

      // M6: generate parser and wrapper
      generateParser(glex, astClassDiagramWithST, astGrammar, mcScope, handcodedPath, out)

      resultMap.put(astGrammar, [astClassDiagramWithST, symbolClassDiagramm, scopeClassDiagramm])
    }
  }

  // M6.1: Say I am succesfully finished with pass 1
  Log.info("Grammar " + astGrammar.getName() + " processed in pass 1.", LOG_ID)
}
// end of first pass
// ############################################################

// ############################################################
// the second pass
// do the rest which requires already created CDs of possibly
// local super grammars etc.
for (entry in resultMap) {
  
  // get base grammar
  astGrammar = entry.key

  // adjust reporting to current grammar
  Reporting.on(astGrammar.getName())

  // get already created base class diagramms
  astClassDiagram = entry.value.get(0) 
  symbolClassDiagramm = entry.value.get(1) 
  scopeClassDiagramm = entry.value.get(2)


  // ############################################################
  // M7: Decorate class diagrams
  decoratedSymbolTableCd = decorateForSymbolTablePackage(glex, mcScope, astClassDiagram,
      symbolClassDiagramm, scopeClassDiagramm, handcodedPath)
  decoratedVisitorCD = decorateForVisitorPackage(glex, mcScope, astClassDiagram, handcodedPath)
  decoratedCoCoCD = decorateForCoCoPackage(glex, mcScope, astClassDiagram, handcodedPath)
  decoratedODCD = decorateForODPackage(glex, mcScope, astClassDiagram, handcodedPath)
  decoratedASTClassDiagramm = decorateForASTPackage(glex, mcScope, astClassDiagram, handcodedPath)
  decoratedMillCD = decorateMill(glex, mcScope, astClassDiagram, decoratedASTClassDiagramm,
      decoratedVisitorCD, decoratedSymbolTableCd, handcodedPath)


  // ############################################################
  // M8: Generate symbol management infrastructure, ast classes, visitor 
  // and context condition

  // M8.1: generate CD for the '_symboltable' package
  generateFromCD(glex, astClassDiagram, decoratedSymbolTableCd, out, handcodedPath)

  // M8.2: generate CD for the '_visitor' package
  generateFromCD(glex, astClassDiagram, decoratedVisitorCD, out, handcodedPath)

  // M8.3: generate CD for the '_coco' package
  generateFromCD(glex, astClassDiagram, decoratedCoCoCD, out, handcodedPath)

  // M8.4: generate CD for the '_od' package
  generateFromCD(glex, astClassDiagram, decoratedODCD, out, handcodedPath)

  // M8.5: generate CD for the '_ast' package
  generateFromCD(glex, astClassDiagram, decoratedASTClassDiagramm, out, handcodedPath)

  // M8.6: generate CD for the mills
  generateFromCD(glex, astClassDiagram, decoratedMillCD, out, handcodedPath)


  // ############################################################
  // M9: Write Reports to files

  // M9.1: Say I am succesfully finished
  Log.info("Grammar " + astGrammar.getName() + " processed successfully.", LOG_ID)

  // M9.2: flush reporting
  Reporting.reportModelEnd(astGrammar.getName(), "")
  Reporting.flush(astGrammar)

}

