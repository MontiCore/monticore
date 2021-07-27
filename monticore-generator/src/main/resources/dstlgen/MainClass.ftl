<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "fileExtension", "scriptname", "package")}
package ${package};

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import de.monticore.tf.script.DSTLCLIConfiguration;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import ${package}.script.${scriptname};

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ${className} {

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

    // before we launch MontiCore we check if there are any ".${fileExtension}" files in the input argument (source path)
    Iterator<Path> inputPaths = configuration.getInternal().getModels("${fileExtension}").getResolvedPaths();
    if (!inputPaths.hasNext()) {
      Log.error("0xA1000 There are no \".${fileExtension}\" files to parse. Please check the \"grammars\" option.");
      return;
    }


    // execute the script (which has been written as java instead of groovy)
    new ${scriptname}().run(configuration.getInternal());
  }

  private ${className}() {
  }

  protected static void printHelp() {
    System.out.println("${className} Usage: java -jar ${className}.jar -m <model files> <options>");
    System.out.println();
    System.out.println("Options:");
    System.out.println("-m, -models <path>           Required models directory for mtr files to be compiled to trafos");
    System.out.println("-o, -out <path>              Optional output directory for all generated code; defaults to target/generated-sources");
    System.out.println("-r, -reports <path>          Optional report directory for all generated repors; defaults to target/reports");
  }

}
