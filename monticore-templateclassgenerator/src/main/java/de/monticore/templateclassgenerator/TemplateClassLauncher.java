/* (c) https://github.com/MontiCore/monticore */
package de.monticore.templateclassgenerator;

import com.google.common.io.Resources;
import de.se_rwth.commons.cli.CLIArguments;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Main class for launching TCG with jar.
 */
public class TemplateClassLauncher {

  public static void main(String[] args) {
    if (args.length % 2 == 1 || args.length > 4) {
      System.out
          .println(
              "TemplateClassGenerator CLI Usage: java -jar monticore-templateclassgenerator.jar <templatepath> <out>");
      return;
    }

    CLIArguments arguments = CLIArguments.forArguments(args);
    TemplateClassGeneratorConfiguration config = TemplateClassGeneratorConfiguration
        .fromArguments(arguments);
    TemplateClassGeneratorScript script = new TemplateClassGeneratorScript();
    
    try {
      ClassLoader l = TemplateClassLauncher.class.getClassLoader();
      String scriptPath = Resources.asCharSource(
          l.getResource("de/monticore/templateclassgenerator/templateclassgenerator.groovy"),
          Charset.forName("UTF-8")).read();
      script.run(scriptPath, config);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
}
