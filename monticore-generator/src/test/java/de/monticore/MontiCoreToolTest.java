/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.io.Resources;
import de.monticore.codegen.parser.ParserGeneratorTest;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

@Ignore
public class MontiCoreToolTest {

  private static final String SCRIPT = "de/monticore/monticore_noemf_decorator.groovy";

  private static final Path MODEL_PATH = Paths.get("src", "test", "resources");

  private static final Path OUT_PATH = Paths.get("target", "generated-test-sources", "codetocompile");

  @Test
  public void testAutomaton() {
    String[] args = { "-grammars",
        "src/test/resources/Automaton.mc4",
        "src/test/resources/mc/grammars/lexicals/TestLexicals.mc4",
        "-modelPath", MODEL_PATH.toAbsolutePath().toString(),
        "-out", OUT_PATH.toAbsolutePath().toString(), "-targetPath", "src/test/resources", "-force" };
    Log.getFindings().clear();
    testScript(args);
    Assert.assertTrue(Log.getFindings().isEmpty());
  }

  private void testScript(String[] args) {

    ClassLoader l = ParserGeneratorTest.class.getClassLoader();
    try {
      String script = Resources.asCharSource(
          l.getResource(SCRIPT),
          Charset.forName("UTF-8")).read();

      Configuration configuration = ConfigurationPropertiesMapContributor
          .fromSplitMap(CLIArguments.forArguments(args).asMap());
      new MontiCoreScript().run(script, configuration);
    }
    catch (IOException e) {
      Log.error("0xA1017 AstGeneratorTest failed: ", e);
    }
  }


}
