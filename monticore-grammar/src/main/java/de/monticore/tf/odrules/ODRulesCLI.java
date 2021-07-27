/* (c) https://github.com/MontiCore/monticore */
/* generated by template de.monticore.tf.translation.MainClass*/

package de.monticore.tf.odrules;

import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import de.monticore.tf.script.DSTLCLIConfiguration;

import java.nio.file.Path;
import java.util.Iterator;
//TODO: Use the default CLI
public class ODRulesCLI {

  /**
   * Main method.
   *
   * @param args the CLI arguments
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      printHelp();
      return;
    }

    CLIArguments arguments = CLIArguments.forArguments(args);
    DSTLCLIConfiguration configuration = DSTLCLIConfiguration.fromArguments(arguments);

    if (arguments.asMap().containsKey(DSTLCLIConfiguration.Options.HELP.toString()) ||
        arguments.asMap().containsKey(DSTLCLIConfiguration.Options.HELP_SHORT.toString())) {
      printHelp();
      return;
    }

    // this needs to be called after the statement above; otherwise logback will
    // ignore custom configurations supplied via system property
    Slf4jLog.init();

    // before we launch MontiCore we check if there are any ".mtr" files in the input argument (source path)
    if (!configuration.getInternal().getModels("mtod").getEntries().isEmpty()) {
      Log.error("0xA1000 There are no \".mtod\" files to parse. Please check the \"grammars\" option.");
      return;
    }

    // execute the scripts (either default or custom)
    new ODRulesScript().run( configuration.getInternal());
  }

  protected void run(String[] args){}

  protected ODRulesCLI() {
  }

  protected static void printHelp() {
    System.out.println("ODRulesCLI Usage: java -jar StatechartCLI.jar -g <grammar> -m <model files> <options>");
    System.out.println();
    System.out.println("Options:");
    System.out.println("-o, -out <path>              Optional output directory for all generated code; defaults to target/generated-sources");
    System.out.println("-r, -reports <path>          Optional report directory for all generated repors; defaults to target/reports");
  }

}
