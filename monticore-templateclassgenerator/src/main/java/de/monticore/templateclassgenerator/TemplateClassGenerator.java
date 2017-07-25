/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */
package de.monticore.templateclassgenerator;

import java.io.IOException;
import java.nio.charset.Charset;
import com.google.common.io.Resources;
import de.se_rwth.commons.cli.CLIArguments;

/**
 * Main class for launching TCG with jar.
 *
 * @author Jerome Pfeiffer
 */
public class TemplateClassGenerator {
  
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
      ClassLoader l = TemplateClassGenerator.class.getClassLoader();
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
