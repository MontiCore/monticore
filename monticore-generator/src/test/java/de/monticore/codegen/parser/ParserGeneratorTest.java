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

package de.monticore.codegen.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Resources;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.AstDependentGeneratorTest;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * Test for the MontiCore generator. Generates parser and wrappers for parser
 * rules for example grammars and performs a compilation task for all generated
 * files.
 * 
 * @author Galina Volkova
 */
public class ParserGeneratorTest extends AstDependentGeneratorTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  public void testOD() {
    astTest.testOD();
    testCorrect("mc/grammars/TestOD.mc4");
  }
  
  public void testCommon() {
    astTest.testCommon();
    testCorrect("mc/grammars/common/TestCommon.mc4");
  }
  
  @Test
  public void testExpression() {
    astTest.testExpression();
    testCorrect("de/monticore/expression/Expression.mc4");
  }
  
  @Test
  public void testInterfaces() {
    astTest.testInterfaces();
    doGenerate("de/monticore/interfaces/Sup.mc4");
    doGenerate("de/monticore/interfaces/Sub.mc4");
  }
  
  @Test
  public void testInterfaceAttributes() {
    astTest.testInterfaceAttributes();;
    testCorrect("de/monticore/InterfaceAttributes.mc4");
  }
  
   @Test
  public void testStatechart() {
    astTest.testStatechart();
    testCorrect("de/monticore/statechart/Statechart.mc4");
  }
   
  @Test
  public void testCdAttributes() {
    astTest.testCdAttributes();
    testCorrect("de/monticore/CdAttributes.mc4");
  }
  
  public void testScopesExample() {
    astTest.testScopesExample();
    testCorrect("de/monticore/ScopesExample.mc4");
  }
  
  @Test
  public void testHelloWorld() {
    astTest.testHelloWorld();
    testCorrect("de/monticore/HelloWorld.mc4");
  }
  
  @Test
  public void testGrammarInDefaultPackage() {
    astTest.testGrammarInDefaultPackage();
    testCorrect("Automaton.mc4");
  }
  
  @Test
  public void testInherited() {
    astTest.testInherited();
    doGenerate("de/monticore/inherited/Supergrammar.mc4");
    doGenerate("de/monticore/inherited/sub/Subgrammar.mc4");
    Path path = Paths.get(OUTPUT_FOLDER, Names.getPathFromFilename("de/monticore/inherited/"));
    // assertTrue("There are compile errors in generated code for the models in grammars/inherited.",
    // compile(path));
  }
  
  @Test
 public void testAction() {
   astTest.testAction();
   testCorrect("de/monticore/Action.mc4");
 }
 
  /**
   * @see de.monticore.codegen.GeneratorTest#doGenerate(java.lang.String)
   */
  @Override
  protected void doGenerate(String model) {
    Log.info("Runs parser generator test for the model " + model, LOG);
    ClassLoader l = ParserGeneratorTest.class.getClassLoader();
    try {
      String script = Resources.asCharSource(
          l.getResource("de/monticore/groovy/monticoreOnlyParser.groovy"),
          Charset.forName("UTF-8")).read();
      
      Configuration configuration =
          ConfigurationPropertiesMapContributor.fromSplitMap(CLIArguments.forArguments(
              getCLIArguments("src/test/resources/" + model))
              .asMap());
      new MontiCoreScript().run(script, configuration);
    }
    catch (IOException e) {
      Log.error("0xA1019 ParserGeneratorTest failed: ", e);
    }
    
  }
  
  /**
   * @see de.monticore.codegen.GeneratorTest#getPathToGeneratedCode(java.lang.String)
   */
  @Override
  protected Path getPathToGeneratedCode(String grammar) {
    String garmmarPath = grammar.endsWith(GRAMMAR_EXTENSION)
        ? grammar.substring(0, grammar.indexOf(GRAMMAR_EXTENSION))
        : grammar;
    return Paths.get(OUTPUT_FOLDER, garmmarPath.toLowerCase());
  }
  
}
