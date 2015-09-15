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

package de.monticore.codegen.cd2java.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.AstDependentGeneratorTest;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CoCoGeneratorTest extends AstDependentGeneratorTest {
  
  private ASTMCGrammar grammar;
  
  private ASTCDCompilationUnit cdCompilationUnit;
  
  private GlobalExtensionManagement glex;
  
  private ModelPath modelPath = new ModelPath(Paths.get("src/test/resources"));
  
  @BeforeClass
  public static void setup() {
    // Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Ignore("not ready yet")
  @Test
  public void test() {
    String grammarToTest = "src/test/resources/Automaton.mc4";
    
    astTest.testCorrect(grammarToTest);
    
    glex = new GlobalExtensionManagement();
    Path model = Paths.get(new File(
        grammarToTest).getAbsolutePath());
    
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = mc.initSymbolTable(modelPath);
    IterablePath targetPath = IterablePath.from(new File("src/test/resource"), "java");
    Optional<ASTMCGrammar> ast = mc.parseGrammar(model);
    assertTrue(ast.isPresent());
    grammar = ast.get();
    File targetFile = new File(OUTPUT_FOLDER);
    cdCompilationUnit = mc.transformAstGrammarToAstCd(new GlobalExtensionManagement(),
        grammar, symbolTable, targetPath);
    assertEquals("CD4Analysis", cdCompilationUnit.getCDDefinition().getName());
    
    CoCoGenerator.generate(glex, symbolTable, cdCompilationUnit, targetFile);
  }
  
  /**
   * @see de.monticore.codegen.GeneratorTest#doGenerate(java.lang.String)
   */
  @Override
  protected void doGenerate(String model) {
    Log.info("Runs VisitorGenerator test for the model " + model, LOG);
    
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = mc.initSymbolTable(modelPath);
    glex = new GlobalExtensionManagement();
    Optional<ASTMCGrammar> ast = mc.parseGrammar(Paths.get(new File(model).getAbsolutePath()));
    assertTrue(ast.isPresent());
    grammar = ast.get();
    
    IterablePath targetPath = IterablePath.from(new File("target"), "java");
    File targetFile = new File(OUTPUT_FOLDER);
    cdCompilationUnit = mc.transformAstGrammarToAstCd(new GlobalExtensionManagement(),
        grammar, symbolTable, targetPath);
    
    CoCoGenerator.generate(glex, symbolTable, cdCompilationUnit, targetFile);
    // TODO needs asts and visitors to be generated first
    // CLIArguments cliArguments =
    // CLIArguments.forArguments(getCLIArguments(model));
    // MontiCoreScript.run(MontiCoreConfiguration.withProperties(cliArguments.asMap()));
  }
  
  /**
   * @see de.monticore.codegen.GeneratorTest#getPathToGeneratedCode(java.lang.String)
   */
  @Override
  protected Path getPathToGeneratedCode(String grammar) {
    return Paths.get(OUTPUT_FOLDER,
        Names.getPathFromFilename(Names.getQualifier(grammar), "/").toLowerCase());
  }
  
  // private String[] getCLIArguments(String grammar) {
  // List<String> args = Lists.newArrayList(getGeneratorArguments());
  // args.add(getConfigProperty(MontiCoreConfiguration.GRAMMAR_PROPERTY));
  // args.add(grammar);
  // return args.toArray(new String[0]);
  // }
  
}
