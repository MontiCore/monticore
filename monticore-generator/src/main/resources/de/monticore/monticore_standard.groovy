/* (c) https://github.com/MontiCore/monticore */

package de.monticore

/*
 * This configuration file 
 * contains the standard workflow of MontiCore:
 * it mainly processes a grammar (and imported sub-grammars)
 * and produces a variety of outputs, such as 
 * * a parser
 * * AST classes
 * * vistors
 * * symbol and scope management, etc.
 *
 * The workflow is organized in ten steps (M1 - M10)
 * For a detailed description see the MontiCore reference manual
 * at https:/www.monticore.de/
 */

// ############################################################
// M1  basic setup and initialization:
// initialize logging; reporting; create global scope

// M1.1 Logging
TODO: initialize Logging here (1 Zeiler) der Art:
useLogbackConfiguration(argmap.get("-cl"));

// M1.2 Initial Hello on Debug level
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


// M1.3 initialize incremental generation support
IncrementalChecker.initialize(out, report)    -- Noch notwendig? Falscher Name der Klasse?
InputOutputFilesReporter.resetModelToArtifactMap()

// M1.4 Build Global Scope
mcScope = createMCGlobalScope(modelPath)		// Es kann nur einen GlobalScope geben?
// TODELETE:cdScope = createCD4AGlobalScope(modelPath)		// Wieso hier soviele??
// TODELETE:symbolCdScope = createCD4AGlobalScope(modelPath)
// TODELETE:scopeCdScope = createCD4AGlobalScope(modelPath)

// M1.5 Initialize Reporting (output)
Reporting.init(out.getAbsolutePath(), report.getAbsolutePath(), reportManagerFactory)


// ############################################################
// Loop over the list of grammars provided as arguments
// (these grammars are usually independent or buil on each other in the
//  correct order.)
// Dependency management is not in the scope of MontiCore itself.

// ############################################################
// the first pass processes all input grammars
// transforms them to an integrated CD 
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
      astClassDiagramWithST = deriveCD(astGrammar, glex, cdScope)

      // M5.2: create symbol and scope class diagramm for the CD
      symbolClassDiagramm = deriveSymbolCD(astGrammar, symbolCdScope)
      scopeClassDiagramm = deriveScopeCD(astGrammar, scopeCdScope)
      // TODO: eigentlich sollte das nur ein ASTCD sein  das Symbols und Scopes beinhaltet?

      // M5.3 report the class diagram
      reportCD(astClassDiagram, symbolClassDiagramm, scopeClassDiagramm, report)

      // M6: generate parser and wrapper
      generateParser(glex, astGrammar, mcScope, handcodedPath, out)

      // 
      TODO: Store astGrammar, und zugehoeriges astClassDiagramWithST
      in einer hier(!) verwalteten Liste resultList für den zweiten Pass
    }
  }

  // M5.1: Say I am succesfully finished with pass 1
  Log.info("Grammar " + astGrammar.getName() + " processed in pass 1.", LOG_ID)
}
// end of first pass
// ############################################################


// ############################################################
// the second pass
// do the rest which requires already created CDs of possibly
// local super grammars etc.
for (astGrammar in resultList) {
  // make sure to use the right report manager again
  // TODO: Wie wärs einen ganz neuen Reportmanager aufzumachen, wenn die alten eh verloren sind ...
  Reporting.on(Names.getQualifiedName(astGrammar.getPackageList(), astGrammar.getName()))

  // get already created base class diagramms
  // TODO: hm, vereinfachen, da ja hier Verfügbar.
  // Und wieso sind das DREI CDs. Das müsste eines sein. Ein einziges!
  // und jetzt kommts: Eines für die Sammlung aller Grammatiken
  // (wenn man sie schon gemeinsam bearbeitet, kann das ja auch alles integriert sein)
  // Ich wäre aber auch mit einer Integration in ein CD pro grammatik zufrieden
  astClassDiagram = getCDOfParsedGrammar(astGrammar)
  symbolClassDiagramm = getSymbolCDOfParsedGrammar(astGrammar)
  scopeClassDiagramm = getScopeCDOfParsedGrammar(astGrammar)

  // unklar??? Evtl. schon bei M5.3 durchführen???
  astClassDiagram = addListSuffixToAttributeName(astClassDiagram)

  // M7 fehlt irgendwie

  // Ok, M8 scheint integriert worden zu sein: kann also entfallen

  // Fehlt hier: glex erzeugen

  // ############################################################
  // M9 Generate ast classes, visitor and context condition
  // M9.1: decorate and generate CD for the '_symboltable' package

  // new M9.1: Decorate und generate auseinanderziehen:
  // Zuerst alles(!) dekorieren, ggf. hier nur einen einzigen Aufruf starten

  // new M9.2: dann kommt der (optionale) Aufruf des alr Argument angegebenen templates 
  // der kann ja alle Hookpoints setzen

  // new M9.3-xxx: die einzelnen generierungen anstoßen

  Macht das so Sinn?

  decoratedSymbolTableCd = decorateForSymbolTablePackage(glex, cdScope, astClassDiagram,
           symbolClassDiagramm, scopeClassDiagramm, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedSymbolTableCd, out, handcodedPath)

  // M9.2: decorate and generate CD for the '_visitor' package
  decoratedVisitorCD = decorateForVisitorPackage(glex, cdScope, astClassDiagram, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedVisitorCD, out, handcodedPath)

  // M9.3: decorate and generate CD for the '_coco' package
  decoratedCoCoCD = decorateForCoCoPackage(glex, cdScope, astClassDiagram, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedCoCoCD, out, handcodedPath)

  // M9.4: decorate and generate CD for the '_od' package
  decoratedODCD = decorateForODPackage(glex, cdScope, astClassDiagram, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedODCD, out, handcodedPath)

  // M9.5: decorate and generate CD for the '_ast' package
  decoratedASTClassDiagramm = decorateForASTPackage(glex, cdScope, astClassDiagram, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedASTClassDiagramm, out, handcodedPath)

  // M9.6: decorate and generate CD for the mills
  decoratedMillCD = decorateMill(glex, cdScope, astClassDiagram, decoratedASTClassDiagramm,
            decoratedVisitorCD, decoratedSymbolTableCd, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedMillCD, out, handcodedPath)




  // ############################################################
  // M10 Write Reports to files

  // M10.1: Say I am succesfully finished
  Log.info("Grammar " + astGrammar.getName() + " processed successfully.", LOG_ID)

  // M10.2: flush reporting
  Reporting.reportModelEnd(astGrammar.getName(), "")
  Reporting.flush(astGrammar)

}

