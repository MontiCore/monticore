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

import static com.google.common.io.Resources.asCharSource;
import static de.se_rwth.commons.Names.getPathFromFilename;
import static de.se_rwth.commons.Names.getQualifier;
import static de.se_rwth.commons.cli.CLIArguments.forArguments;
import static de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor.fromSplitMap;
import static de.se_rwth.commons.logging.Log.*;
import static de.se_rwth.commons.logging.LogStub.init;
import static java.nio.charset.Charset.forName;
import static java.nio.file.Paths.get;

public class SymbolTableGeneratorTest extends AstDependentGeneratorTest {

  private ParserGeneratorTest parserTest = new ParserGeneratorTest();

  @BeforeClass
  public static void setup() {
    init();
    enableFailQuick(false);
  }

  @Test
  public void testGrammarWithSymbolTableInfo() {
    final String grammarPath = "de/monticore/AutomatonST.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath, false);
  }

  @Override
  protected void doGenerate(String model) {
    info("Runs symbol table generator test for the model " + model, LOG);
    ClassLoader l = SymbolTableGeneratorTest.class.getClassLoader();
    try {
      String script = asCharSource(
              l.getResource("de/monticore/groovy/monticoreOnlyST.groovy"),
              forName("UTF-8")).read();

      Configuration configuration =
              fromSplitMap(forArguments(
                      getCLIArguments("src/test/resources/" + model))
                      .asMap());
      new MontiCoreScript().run(script, configuration);
    } catch (IOException e) {
      error("0xA1016 SymbolTableGeneratorTest failed: ", e);
    }
  }

  @Override
  protected Path getPathToGeneratedCode(String grammar) {
    return get(OUTPUT_FOLDER, getPathFromFilename(getQualifier(grammar), "/").toLowerCase());
  }

}
