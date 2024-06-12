/* (c) https://github.com/MontiCore/monticore */



import de.se_rwth.commons.logging.LogStub

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
// M1: Basic setup and initialization
// M1.1: Logging
Log.info("--------------------------------", LOG_ID)
Log.info("MontiCore", LOG_ID)
Log.info(" - eating your models since 2005", LOG_ID)
Log.info("--------------------------------", LOG_ID)
Log.debug("Grammar argument    : " + _configuration.getGrammarsAsStrings(), LOG_ID)
Log.debug("Grammar files       : " + grammars, LOG_ID)
Log.debug("Modelpath           : " + modelPath, LOG_ID)
Log.debug("Output dir          : " + out, LOG_ID)
Log.debug("Report dir          : " + report, LOG_ID)
Log.debug("Handcoded argument  : " + _configuration.getHandcodedPathAsStrings(), LOG_ID)
Log.debug("Handcoded files     : " + handcodedPath, LOG_ID)

// M1.2: Initialize reporting (output)
Reporting.init(out.getAbsolutePath(),
        report.getAbsolutePath(), reportManagerFactory)

// M 1.3: Initialize glex
glex = initGlex(_configuration)

// M1.4: Build Global Scope
mcScope = createMCGlobalScope(modelPath)
cdScope = createCD4AGlobalScope(modelPath)

// ############################################################
// Loop over the list of grammars provided as arguments (these grammars are
// usually independent or build on each other in the correct order).
// Dependency management is not in the scope of MontiCore itself.
// ############################################################
while (grammarIterator.hasNext()) {
  input = grammarIterator.next()

  // M2: Parse grammar
  astGrammar = parseGrammar(input)

  if (astGrammar.isPresent()) {
    astGrammar = astGrammar.get()

    // start reporting on that grammar
    grammarName = Names.getQualifiedName(astGrammar.getPackageList(), astGrammar.getName())
    Reporting.on(grammarName)
    Reporting.reportModelStart(astGrammar, grammarName, "")
    Reporting.reportParseInputFile(input, grammarName)

    // M3: Populate symbol table
    astGrammar = createSymbolsFromAST(mcScope, astGrammar)

    // M4: Execute context conditions
    runGrammarCoCos(astGrammar, mcScope)

    // M5: Transform grammar AST into a class diagram and report it
    cd = deriveCD(astGrammar, glex, cdScope)
    reportCD(cd, report)

    // M6: Generate parser and wrapper
    generateParser(glex, cd, astGrammar, mcScope, handcodedPath, templatePath, out)

    // M7: Decorate class diagrams
    decoratedCD = decorateCD(glex, cdScope, cd, handcodedPath)

    // M8 Generate ast classes, symbol table, visitor, and context conditions
    generateFromCD(glex, cd, decoratedCD, out, handcodedPath, templatePath)

    // M9: Write reports to files
    // M9.1: Inform about successful completion for grammar
    Log.info("Grammar " + astGrammar.getName() + " processed successfully!", LOG_ID)

    // M9.2: Flush reporting
    Reporting.reportModelEnd(astGrammar.getName(), "")
    Reporting.flush(astGrammar)
  }
}
