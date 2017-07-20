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

package de.monticore.cli;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.MontiCoreConfiguration;
import de.se_rwth.commons.logging.Log;

/**
 * A collection of exemplary use cases for the CLI arguments. These unit tests
 * do not really test something but are written to try out certain argument
 * combinations and hence designed to not fail.
 *
 * @author (last commit) $Author$
 * @since 4.0.0
 */
public class MontiCoreCLITest {
  
  /**
   * Pretty default arguments.
   */
  static String[] simpleArgs = {
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java" };
  
  /**
   * Arguments activating the detailed developer logging.
   */
  static String[] devLogArgs = {
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java",
      "-" + MontiCoreCLIConfiguration.Options.DEV };
  
  /**
   * Arguments specifying a custom log configuration file to use.
   */
  static String[] customLogArgs = {
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java",
      "-" + MontiCoreCLIConfiguration.Options.CUSTOMLOG, "src/test/resources/test.logging.xml" };
  
  /**
   * Arguments for using a custom Groovy script.
   */
  static String[] customScriptArgs = {
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java",
      "-" + MontiCoreCLIConfiguration.Options.SCRIPT, "src/test/resources/my_noemf.groovy",
      "-" + MontiCoreConfiguration.Options.FORCE };
  
  /**
   * Arguments for using a custom Groovy script.
   */
  static String[] customEmfScriptArgs = {
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/de/monticore/AutomatonEmf.mc4",
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java",
      "-" + MontiCoreCLIConfiguration.Options.SCRIPT, "src/test/resources/my_emf.groovy",
      "-" + MontiCoreConfiguration.Options.FORCE };
  
  /**
   * These arguments specify inputs where there are no ".mc4" files. This will
   * be reported to the user instead of silently doing nothing.
   */
  static String[] argsWithNoGrammars = {
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/monticore",
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java" };
  
  @BeforeClass
  public static void deactivateFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMontiCoreCLI() {
    
    MontiCoreCLI.main(simpleArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testMontiCoreDevLogCLI() {
    
    MontiCoreCLI.main(devLogArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testMontiCoreCustomLogCLI() {
    
    MontiCoreCLI.main(customLogArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testMontiCoreCustomScriptCLI() {
    MontiCoreCLI.main(customScriptArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testMontiCoreCustomEmfScriptCLI() {
    MontiCoreCLI.main(customEmfScriptArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testArgsWithNoGrammars() {
    MontiCoreCLI.main(argsWithNoGrammars);
    
    assertTrue(!false);
  }
  
}
