/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generator.typesafety.codegen;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.montiarc.generator.TemplateClassGeneratorScript;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class GeneratorTest {
  
private static Path outputDirectory = Paths.get("target/generated-test-sources/gen");
  
  private static Path modelPath = Paths.get("src/test/resources/");
  
  
  @Test
  public void testTypesafetyScript(){
    new TemplateClassGeneratorScript().generate("targetName", modelPath, new File("src/test/resources/templates/component/Component.ftl"), outputDirectory.toFile());
  }
  
}
