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

// M1.3: Initialize glex
glex = initGlex(_configuration)

// groovy script hook point
hook(gh1, glex, grammars)

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
    decoratedCD = decorateEmfCD(glex, cdScope, cd, handcodedPath)
    if (genTag) {
      // Also decorate infrastructure for domain-specific tagging IFF this task is run on a tagging grammar
      decoratedCD = decorateTagCD(glex, cdScope, cd, handcodedPath, decoratedCD, astGrammar)
    }

    // groovy script hook point
    hook(gh2, glex, astGrammar, decoratedCD, cd)

    // generator template configuration with -ct hook point
    configureGenerator(glex, decoratedCD, templatePath)

    // M8 Generate ast classes, symbol table, visitor, and context conditions
    generateEmfFromCD(glex, cd, decoratedCD, out, handcodedPath, templatePath)

    if (genDST) {
      // Generate infrastructure for domain-specific transformation IFF this task is run on a TR grammar
      generateDSTInfrastructure(astGrammar, out, modelPathHC)
    } else {
      // Generate a DSTL (ending in TR.mc4)
      generateDSTLanguage(astGrammar, out, modelPathHC)
    }

    if (!genTag) {
      // Generate the tagging grammars (ending in TagSchema.mc4 and TagDefinition.mc4)
      generateTaggingLanguages(astGrammar, out, modelPathHC)
    }

    // M9: Write reports to files
    // M9.1: Inform about successful completion for grammar
    Log.info("Grammar " + astGrammar.getName() + " processed successfully!", LOG_ID)

    // M10: Flush reporting
    Reporting.reportModelEnd(astGrammar.getName(), "")
    Reporting.flush(astGrammar)
  }
}

if (toolName.isPresent()) {
  generateLaunchScripts(glex, out, toolName.get())
}