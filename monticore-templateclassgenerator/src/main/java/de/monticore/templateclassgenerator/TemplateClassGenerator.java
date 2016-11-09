/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.templateclassgenerator;

import java.io.IOException;
import java.nio.charset.Charset;
import com.google.common.io.Resources;
import de.se_rwth.commons.cli.CLIArguments;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class TemplateClassGenerator {
  
  public static void main(String[] args) {
    if (args.length != 4) {
      System.out
          .println("TemplateClassGenerator CLI Usage: java -jar monticore-templateclassgenerator.jar <templatepath> <out>");
      return;
    }
    
    CLIArguments arguments = CLIArguments.forArguments(args);
    TemplateClassGeneratorConfiguration config = TemplateClassGeneratorConfiguration.fromArguments(arguments); 
    TemplateClassGeneratorScript script = new TemplateClassGeneratorScript();
    
    try {
      ClassLoader l = TemplateClassGenerator.class.getClassLoader();
      String scriptPath = Resources.asCharSource(l.getResource("de/monticore/templateclassgenerator/templateclassgenerator.groovy"), Charset.forName("UTF-8")).read();
      script.run(scriptPath, config);

    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    
    
  }
  
}
