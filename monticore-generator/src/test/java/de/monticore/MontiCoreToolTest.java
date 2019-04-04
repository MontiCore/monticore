package de.monticore;

import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MontiCoreToolTest {

  private static final String SCRIPT = "de/monticore/monticore_noemf_decorator.groovy";

  private static final Path MODEL_PATH = Paths.get("src", "test", "resources");

  private static final Path OUT_PATH = Paths.get("target", "generated-test-sources");

  @Test
  public void testAutomaton() {
    String[] args = { "-grammars",
        "src/test/resources/de/monticore/Automaton.mc4",
        "src/test/resources/mc/grammars/lexicals/TestLexicals.mc4",
        "-modelPath", MODEL_PATH.toAbsolutePath().toString(),
        "-out", OUT_PATH.toAbsolutePath().toString(), "-targetPath", "src/test/resources", "-force" };
    Log.getFindings().clear();
    testScript(args);
    Assert.assertTrue(Log.getFindings().isEmpty());
  }

  private void testScript(String[] args) {
    Configuration configuration =
        ConfigurationPropertiesMapContributor
            .fromSplitMap(CLIArguments.forArguments(args).asMap());
    MontiCoreConfiguration cfg = MontiCoreConfiguration.withConfiguration(configuration);
    new MontiCoreScript().run(SCRIPT, cfg);
  }
}
