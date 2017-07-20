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

package de.monticore.codegen.types;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Resources;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.AstDependentGeneratorTest;
import de.monticore.codegen.cd2java.types.TypeResolverGenerator;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
/**
 *  on 01.11.2016.
 */
public class TypeResolverGeneratorTest extends AstDependentGeneratorTest {

  @Override
  protected void dependencies(String... dependencies) {
    for (String dependency : dependencies) {
      if (!Files.exists(getPathToGeneratedCode(dependency))) {
        astTest.testCorrect(dependency, false);
        doGenerate(dependency);
      }
    }
  }

  @Test
  public void testAutomaton2() {
    final String grammarPath = "de/monticore/emf/Automaton2.mc4";
    dependencies("mc/grammars/lexicals/TestLexicals.mc4");
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Test
  public void testAutomaton() {
    final String grammarPath = "de/monticore/emf/Automaton.mc4";
    dependencies("mc/grammars/lexicals/TestLexicals.mc4");
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }


  @Test
  public void testFautomaton() {
    final String grammarPath = "de/monticore/fautomaton/action/Expression.mc4";
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Test
  public void testStateChart() {
    final String grammarPath = "de/monticore/statechart/Statechart.mc4";
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Test
  public void testSubgrammar() {
    final String grammarPath = "de/monticore/inherited/sub/Subgrammar.mc4";
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Test
  public void testSuperGrammar() {
    final String grammarPath = "de/monticore/inherited/Supergrammar.mc4";
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Test
  public void testLexicals() {
    final String grammarPath = "mc/grammars/lexicals/TestLexicals.mc4";
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  /**
   * is dependent on the lexicals grammar
   */
  @Test
  public void testLiterals() {
    final String grammarPath = "mc/grammars/literals/TestLiterals.mc4";
    dependencies("mc/grammars/lexicals/TestLexicals.mc4");
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  /**
   * is dependent on the literals grammar
   */
  @Test
  public void testTypes() {
    final String grammarPath = "mc/grammars/types/TestTypes.mc4";
    dependencies("mc/grammars/lexicals/TestLexicals.mc4", "mc/grammars/literals/TestLiterals.mc4");
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  /**
   * is dependent on the types grammar
   */
  @Test
  public void testJavaDSL() {
    final String grammarPath = "mc/grammars/TestJavaDSL.mc4";
    dependencies("mc/grammars/lexicals/TestLexicals.mc4", "mc/grammars/literals/TestLiterals.mc4", "mc/grammars/types/TestTypes.mc4");
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  /**
   * is dependent on the types grammar
   */
  @Test
  public void testCommon() {
    final String grammarPath = "mc/grammars/common/TestCommon.mc4";
    dependencies("mc/grammars/lexicals/TestLexicals.mc4", "mc/grammars/literals/TestLiterals.mc4", "mc/grammars/types/TestTypes.mc4");
    astTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }


  @Override
  protected void doGenerate(String model) {
    Log.info("Runs Type Resolver generator test for the model " + model, LOG);
    ClassLoader l = TypeResolverGenerator.class.getClassLoader();
    try {
      String script = Resources.asCharSource(
          l.getResource("de/monticore/groovy/monticoreOnlyTypeResolver.groovy"),
          Charset.forName("UTF-8")).read();

      Configuration configuration =
          ConfigurationPropertiesMapContributor.fromSplitMap(CLIArguments.forArguments(
              getCLIArguments("src/test/resources/" + model))
              .asMap());
      new MontiCoreScript().run(script, configuration);
    }
    catch (IOException e) {
      Log.error("0xA1018 TypeResolverGeneratorTest failed: ", e);
    }
  }


  @Override protected Path getPathToGeneratedCode(String model) {
    return Paths.get(OUTPUT_FOLDER, Names.getPathFromFilename(Names.getQualifier(model), "/").toLowerCase());
  }
}
