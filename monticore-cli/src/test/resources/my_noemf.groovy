/* (c) https://github.com/MontiCore/monticore */

package de.monticore

// M1: configuration object "_configuration" prepared externally
Log.debug("--------------------------------", LOG_ID)
Log.debug("MontiCore", LOG_ID)
Log.debug(" - eating your models since 2005", LOG_ID)
Log.debug("--------------------------------", LOG_ID)
Log.debug("Grammar argument    : " + _configuration.getGrammarsAsStrings(), LOG_ID)
Log.debug("Grammar files       : " + grammars, LOG_ID)
Log.debug("Modelpath           : " + modelPath, LOG_ID)
Log.debug("Output dir          : " + out, LOG_ID)
Log.debug("Report dir          : " + report, LOG_ID)
Log.debug("Handcoded argument  : " + _configuration.getHandcodedPathAsStrings(), LOG_ID)
Log.debug("Handcoded files     : " + handcodedPath, LOG_ID)

// ############################################################
// M1  basic setup and initialization:
// initialize incremental generation; enabling of reporting; create global scope
IncrementalChecker.initialize(out, report)
InputOutputFilesReporter.resetModelToArtifactMap()
mcScope = createMCGlobalScope(modelPath)
cdScope = createCD4AGlobalScope(modelPath)
Reporting.init(out.getAbsolutePath(), report.getAbsolutePath(), reportManagerFactory)
// ############################################################

// ############################################################
// the first pass processes all input grammars up to transformation to CD and storage of the resulting CD to disk
while (grammarIterator.hasNext()) {
  input = grammarIterator.next()
  if (force || !IncrementalChecker.isUpToDate(input, modelPath, templatePath, handcodedPath )) {
    IncrementalChecker.cleanUp(input)

    // M2: parse grammar
    astGrammar = parseGrammar(input)

    if (astGrammar.isPresent()) {
      astGrammar = astGrammar.get()

      // start reporting
      grammarName = Names.getQualifiedName(astGrammar.getPackageList(), astGrammar.getName())
      Reporting.on(grammarName)
      Reporting.reportModelStart(astGrammar, grammarName, "")

      Reporting.reportParseInputFile(input, grammarName)

      // M3: populate symbol table
      astGrammar = createSymbolsFromAST(mcScope, astGrammar)

      // M4: execute context conditions
      runGrammarCoCos(astGrammar, mcScope)

      // M5: transform grammar AST into Class Diagram AST
      astClassDiagramWithST = deriveCD(astGrammar, glex, cdScope, mcScope)

      // M6: generate parser and wrapper
      generateParser(glex, astGrammar, mcScope, handcodedPath, out)
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
  reportGrammarCd(astGrammar, cdScope, mcScope, report)

  astClassDiagram = getCDOfParsedGrammar(astGrammar)

  // M8: generate symbol table
  generateSymbolTable(glex, mcScope, astGrammar, cdScope, astClassDiagram, out, handcodedPath)

  // M9 Generate ast classes, visitor and context condition
  decoratedVisitorCD = decorateForVisitorPackage(glex, cdScope, astClassDiagram, modelPath, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedVisitorCD, out, handcodedPath)

  generateCocos(glex, cdScope, astClassDiagram, out)
  generateODs(glex, cdScope, mcScope, astClassDiagram, out)

  // M7: decorate Class Diagram AST
  decoratedASTClassDiagramm = decorateForASTPackage(glex, cdScope, astClassDiagram, modelPath, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedASTClassDiagramm, out, handcodedPath)

  Log.info("Grammar " + astGrammar.getName() + " processed successfully!", LOG_ID)

  // M10: flush reporting
  Reporting.reportModelEnd(astGrammar.getName(), "")
  Reporting.flush(astGrammar)
}
