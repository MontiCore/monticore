/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Resources;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.AstDependentGeneratorTest;
import de.monticore.codegen.parser.ParserGeneratorTest;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class SymbolTableGeneratorTest extends AstDependentGeneratorTest {

  private ParserGeneratorTest parserTest = new ParserGeneratorTest();

  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testGrammarWithSymbolTableInfo() {
    final String grammarPath = "de/monticore/symboltable/GrammarWithSymbolTableInfo.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Override
  protected void doGenerate(String model) {
    Log.info("Runs symbol table generator test for the model " + model, LOG);
    ClassLoader l = SymbolTableGeneratorTest.class.getClassLoader();
    try {
      String script = Resources.asCharSource(
          l.getResource("de/monticore/groovy/monticoreOnlyST.groovy"),
          Charset.forName("UTF-8")).read();

      Configuration configuration =
          ConfigurationPropertiesMapContributor.fromSplitMap(CLIArguments.forArguments(
              getCLIArguments("src/test/resources/" + model))
              .asMap());
      new MontiCoreScript().run(script, configuration);
    }
    catch (IOException e) {
      Log.error("0xA1016 SymbolTableGeneratorTest failed: ", e);
    }
  }

  @Override
  protected Path getPathToGeneratedCode(String grammar) {
    return Paths.get(OUTPUT_FOLDER, Names.getPathFromFilename(Names.getQualifier(grammar), "/").toLowerCase());
  }

}
