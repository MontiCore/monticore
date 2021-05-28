/* (c) https://github.com/MontiCore/monticore */

package de.monticore

import de.monticore.IncrementalChecker

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
symbolCdScope = createCD4AGlobalScope(modelPath)
scopeCdScope = createCD4AGlobalScope(modelPath)
Reporting.init(out.getAbsolutePath(), report.getAbsolutePath(), reportManagerFactory)
// ############################################################

// ############################################################
// the first pass processes all input grammars
// transforms them to a CD and stores the resulting CD to disk
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
      //     and create symbol and scope class diagramm
      astClassDiagramWithST = deriveASTCD(astGrammar, glex, cdScope)
      deriveSymbolCD(astGrammar, symbolCdScope)
      deriveScopeCD(astGrammar, scopeCdScope)

      // M6: generate parser and wrapper
      generateParser(glex, astClassDiagramWithST, astGrammar, mcScope, handcodedPath, templatePath, out)
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

  // get already created base class diagramms
  astClassDiagram = getCDOfParsedGrammar(astGrammar)
  symbolClassDiagramm = getSymbolCDOfParsedGrammar(astGrammar)
  scopeClassDiagramm = getScopeCDOfParsedGrammar(astGrammar)

  astClassDiagram = addListSuffixToAttributeName(astClassDiagram)

  // report the basic AST diagram
  reportCD(astClassDiagram, report)

  // M9 Generate ast classes, visitor and context condition
  // decorate and generate CD for the '_symboltable' package
  decoratedSymbolTableCd = decorateForSymbolTablePackage(glex, cdScope, astClassDiagram,
           symbolClassDiagramm, scopeClassDiagramm, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedSymbolTableCd, out, handcodedPath, templatePath)

  // decorate and generate Traverser CD for the '_visitor' package
  decoratedTraverserCD = decorateTraverserForVisitorPackage(glex, cdScope, astClassDiagram, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedTraverserCD, out, handcodedPath, templatePath)

  // decorate and generate CD for the '_coco' package
  decoratedCoCoCD = decorateForCoCoPackage(glex, cdScope, astClassDiagram, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedCoCoCD, out, handcodedPath, templatePath)

  // decorate and generate CD for the '_od' package
  decoratedODCD = decorateForODPackage(glex, cdScope, astClassDiagram, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedODCD, out, handcodedPath, templatePath)

  // decorate and generate CD for the '_ast' package
  decoratedASTClassDiagramm = decorateForASTPackage(glex, cdScope, astClassDiagram, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedASTClassDiagramm, out, handcodedPath, templatePath)

  // decorate and generate CD for the mills
  decoratedMillCD = decorateMill(glex, cdScope, astClassDiagram, decoratedASTClassDiagramm,
                                 decoratedSymbolTableCd, decoratedTraverserCD, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedMillCD, out, handcodedPath, templatePath)

  // decorate and generate CD for the CLIs
  decoratedCLICD = decorateCLI(glex, cdScope, astClassDiagram, decoratedASTClassDiagramm,
          decoratedSymbolTableCd, decoratedTraverserCD, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedCLICD, out, handcodedPath)

  //decorate and generate CD for the '_auxiliary' package
  decoratedAuxiliaryCD = decorateAuxiliary(glex, cdScope, astClassDiagram, decoratedASTClassDiagramm, handcodedPath)
  generateFromCD(glex, astClassDiagram, decoratedAuxiliaryCD, out, handcodedPath, templatePath)

  // report the full AST incl. Symbols diagrams
  reportCD(astClassDiagram, decoratedASTClassDiagramm, decoratedSymbolTableCd, scopeClassDiagramm, report)

  Log.info("Grammar " + astGrammar.getName() + " processed successfully!", LOG_ID)

  // M10: flush reporting
  Reporting.reportModelEnd(astGrammar.getName(), "")
  Reporting.flush(astGrammar)
}
