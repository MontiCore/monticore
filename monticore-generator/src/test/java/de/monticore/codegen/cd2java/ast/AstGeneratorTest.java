/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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

package de.monticore.codegen.cd2java.ast;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import de.monticore.MontiCoreConfiguration;
import de.monticore.MontiCoreScript;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.GeneratorTest;
import de.monticore.codegen.parser.ParserGeneratorTest;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * Test for the MontiCore generator. Generates ast files for the example
 * grammars and performs a compilation task for all generated files.
 * 
 * @author Galina Volkova, Sebastian Oberhoff
 */
public class AstGeneratorTest extends GeneratorTest {
  
  /**
   * Shows if generated ast code has to be compiled
   */
  private boolean doCompile = true;
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testInterfaceAttributes() {
    testCorrectWithDependencies("de/monticore/InterfaceAttributes.mc4",
        "mc/grammars/lexicals/TestLexicals.mc4");
  }
  
  @Test
  public void testStatechart() {
    testCorrectWithDependencies("de/monticore/statechart/Statechart.mc4",
        "mc/grammars/lexicals/TestLexicals.mc4");
  }
  
  @Test
  public void testCdAttributes() {
    testCorrect("de/monticore/CdAttributes.mc4");
  }
  
  @Test
  public void testAstAttributes() {
    testCorrect("de/monticore/CdAttributes.mc4");
  }
  
  public void testScopesExample() {
    testCorrectWithDependencies("de/monticore/ScopesExample.mc4",
        "mc/grammars/lexicals/TestLexicals.mc4");
  }
  
  @Test
  public void testHelloWorld() {
    testCorrect("de/monticore/HelloWorld.mc4");
  }
  
  @Test
  public void testEnum() {
    testCorrectWithDependencies("mc/robot/RobotDSL.mc4",
        "mc/grammars/lexicals/TestLexicals.mc4");
  }
  
  @Test
  public void testGrammarInDefaultPackage() {
    testCorrectWithDependencies("Automaton.mc4", "mc/grammars/lexicals/TestLexicals.mc4");
  }
  
  @Test
  public void testInherited() {
    doGenerate("de/monticore/inherited/Supergrammar.mc4");
    doGenerate("de/monticore/inherited/sub/Subgrammar.mc4");
    Path path = Paths.get(OUTPUT_FOLDER, Names.getPathFromFilename("de/monticore/inherited/"));
   // assertTrue("There are compile errors in generated code for the models in grammars/inherited.",
    // compile(path));
  }

  @Test
  public void testInherited2() {
    doGenerate("de/monticore/fautomaton/action/Expression.mc4");
    doGenerate("de/monticore/fautomaton/automaton/FlatAutomaton.mc4");
    doGenerate("de/monticore/fautomaton/automatonwithaction/ActionAutomaton.mc4");
    Path path = Paths.get(OUTPUT_FOLDER, Names.getPathFromFilename("de/monticore/mc/fautomaton/"));
    assertTrue("There are compile errors in generated code for the models in grammars/inherited.",
     compile(path));
  }

  @Test
  public void testAction() {
    testCorrect("de/monticore/Action.mc4");
  }
  
  @Test
  public void testVisitors() {
    testCorrect("de/monticore/visitors/A.mc4", false);
    testCorrect("de/monticore/visitors/B.mc4", false);
    testCorrect("de/monticore/visitors/C.mc4", false);
  }
  
  @Override
  public void testCorrect(String model) {
    if (isDoCompile()) {
      testCorrect(model, getPathToGeneratedCode(model));
    }
    else if (checkAstGeneratedFiles(model)) {
      testCorrect(model, false);
    }
  }
  
  /**
   * @see de.monticore.codegen.GeneratorTest#doGenerate(java.lang.String)
   */
  @Override
  protected void doGenerate(String model) {
    Log.info("Runs AST generator test for the model " + model, LOG);
    ClassLoader l = ParserGeneratorTest.class.getClassLoader();
    try {
      String script = Resources.asCharSource(
          l.getResource("de/monticore/groovy/monticoreOnlyAst_emf.groovy"),
          Charset.forName("UTF-8")).read();
      
      Configuration configuration =
          ConfigurationPropertiesMapContributor.fromSplitMap(CLIArguments.forArguments(
              getCLIArguments("src/test/resources/" + model))
              .asMap());
      new MontiCoreScript().run(script, configuration);
    }
    catch (IOException e) {
      Log.error("0xA1018 AstGeneratorTest failed: ", e);
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
    return Paths.get(OUTPUT_FOLDER, garmmarPath.toLowerCase(), GeneratorHelper.AST_PACKAGE_SUFFIX);
  }
  
  /**
   * If there is no generated code for dependencies then generate and compile.
   * If ast code is tested then generate and compile ast code, if code dependent
   * on ast is tested, check if ast code exists. If not, generate. 
   * Then generate code that has to be tested (e.g. parser- or symboltable-code)
   * 
   * @param model
   * @param dependencies
   */
  protected void testCorrectWithDependencies(String model, String... dependencies) {
    dependencies(dependencies);
    if (isDoCompile()) {
      testCorrect(model, getPathToGeneratedCode(model));
    }
    else if (checkAstGeneratedFiles(model)) {
      testCorrect(model, false);
    }
  }
  
  /**
   * Checks if ast code already was generated TODO: Write me!
   * 
   * @param grammar
   * @return
   */
  private boolean checkAstGeneratedFiles(String grammar) {
    return !Files.exists(getPathToGeneratedCode(grammar));
  }
  
  private String[] getCLIArguments(String grammar) {
    List<String> args = Lists.newArrayList(getGeneratorArguments());
    args.add(getConfigProperty(MontiCoreConfiguration.Options.GRAMMARS.toString()));
    args.add(grammar);
    return args.toArray(new String[0]);
  }
  
  /**
   * @return doCompile
   */
  public boolean isDoCompile() {
    return this.doCompile;
  }
  
  /**
   * @param doCompile the doCompile to set
   */
  public void setDoCompile(boolean doCompile) {
    this.doCompile = doCompile;
  }
  
}
