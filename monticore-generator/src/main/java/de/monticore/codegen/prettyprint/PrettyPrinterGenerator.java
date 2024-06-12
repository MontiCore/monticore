// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint;

import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._prettyprint.Grammar_WithConceptsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;


public class PrettyPrinterGenerator {

  public static final String LOG = "PrettyPrinterGenerator";

  public static ASTCDCompilationUnit generatePrettyPrinter(
          GlobalExtensionManagement glex,
          ASTMCGrammar astGrammar
  ) {

    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    glex.setGlobalValue("grammarPrinter", new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter(), false));


    Log.debug("Start PrettyPrinter generation for the grammar " + astGrammar.getName(), LOG);


    // instantiate converter
    Grammar2PPConverter converter = new Grammar2PPConverter();

    return converter.doConvert(astGrammar, glex);
  }
}
