/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generator.typesafety.codegen;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import de.montiarc.generator.TemplateClassGeneratorConfiguration;
import de.montiarc.generator.TemplateClassGeneratorScript;
import de.monticore.java.javadsl._ast.ASTCompilationUnit;
import de.monticore.java.javadsl._ast.ASTTypeDeclaration;
import de.monticore.java.javadsl._parser.JavaDSLParser;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class GroovyScriptTest {
  /**
   * Parent folder for the generated code
   */
  protected static final String OUTPUT_FOLDER = "target/generated-sources/monticore/codetocompile";
  
  protected static final String MODEL_PATH = "src/test/resources/";
  
  
  
  /**
   * Base generator arguments
   */
  private List<String> generatorArguments = Lists
      .newArrayList(
          getConfigProperty(TemplateClassGeneratorConfiguration.Options.MODELPATH.toString()), MODEL_PATH,
          getConfigProperty(TemplateClassGeneratorConfiguration.Options.OUT.toString()), OUTPUT_FOLDER);
  
  protected static final String LOG = "GeneratorTest";
  

  
  @Test
  public void testMultipleModels(){
    doGenerate();
  }
  
  @Test
  public void test() {
    JavaDSLParser p = new JavaDSLParser();
    try {
     Optional<ASTCompilationUnit> s=  p.parse_String("java.util.List l");
     if(s.isPresent()){
       ASTCompilationUnit c = s.get();
       List<ASTTypeDeclaration> types = c.getTypeDeclarations();
       System.out.println(types);
     }
      
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    
  }


  private void doGenerate() {
    Log.info("Runs AST generator test" , LOG);
    ClassLoader l = GroovyScriptTest.class.getClassLoader();
    try {
      String script = Resources.asCharSource(
          l.getResource("de/montiarc/generator/montiarc.groovy"),
          Charset.forName("UTF-8")).read();
      
      Configuration configuration =
          ConfigurationPropertiesMapContributor.fromSplitMap(CLIArguments.forArguments(
              getCLIArguments())
              .asMap());
      new TemplateClassGeneratorScript().run(script, configuration);
    }
    catch (IOException e) {
      Log.error("0xA1018 AstGeneratorTest failed: ", e);
    }
  }
  
  public static String getConfigProperty(String property) {
    return new StringBuilder("-").append(property).toString();
  }
  
  private String[] getCLIArguments() {
    List<String> args = Lists.newArrayList(this.generatorArguments);
    return args.toArray(new String[0]);
  }
}
