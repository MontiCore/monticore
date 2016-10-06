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

package de.monticore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstAdditionalMethods;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * Test for the {@link MontiCoreScript} class.
 *
 * @author Galina Volkova, Andreas Horst
 */
public class MontiCoreScriptTest {
  
  private ASTMCGrammar grammar;
  
  private ASTCDCompilationUnit cdCompilationUnit;
  
  private GlobalExtensionManagement glex;
  
  private static Set<String> additionalMethods = Sets.newLinkedHashSet();
  
  private static Path modelPathPath = Paths.get("src/test/resources");
  
  private static File outputPath = new File("target/generated-test-sources");
  
  private static ModelPath modelPath = new ModelPath(modelPathPath, outputPath.toPath());
  
  private static IterablePath targetPath = IterablePath
      .from(new File("src/test/resources"), "java");
  
  private static IterablePath templatePath = IterablePath
      .from(new File("src/test/resources"), "ftl");
  
  static String[] simpleArgs = { "-grammars",
      "src/test/resources/de/monticore/statechart/Statechart.mc4",
      "src/test/resources/mc/grammars/lexicals/TestLexicals.mc4",
      "-modelPath", modelPathPath.toAbsolutePath().toString(),
      "-out", outputPath.getAbsolutePath(), "-targetPath", "src/test/resources", "-force" };
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
    for (AstAdditionalMethods additionalMethod : AstAdditionalMethods.class.getEnumConstants()) {
      additionalMethods.add(additionalMethod.name());
    }
  }
  
  @Before
  public void init() {
    glex = new GlobalExtensionManagement();
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/statechart/Statechart.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    grammar = ast.get();
  }
  
  /** {@link MontiCoreScript#parseGrammar(java.nio.file.Path)} */
  @Test
  public void testParseGrammar() {
    assertNotNull(grammar);
    assertEquals("Statechart", grammar.getName());
  }
  
  /** {@link MontiCoreScript#generateParser(ASTMCGrammar, String)} */
  @Test
  public void testGenerateParser() {
    assertNotNull(grammar);
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = mc.initSymbolTable(modelPath);
    mc.generateParser(grammar, symbolTable, IterablePath.empty(), new File("target/generated-sources/monticore/testcode"));
  }
  
  /** {@link MontiCoreScript#transformAstGrammarToAstCd(mc.grammar._ast.ASTCDCompilationUnit)} */
  @Test
  public void testTransformAstGrammarToAstCd() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = mc.initNewSymbolTable(modelPath);
    mc.createNewSymbolsFromAST(symbolTable, grammar);
    cdCompilationUnit = mc.transformAstGrammarToAstCd(
        new GlobalExtensionManagement(), grammar, symbolTable, targetPath);
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
  }
  
  /** {@link MontiCoreScript#decorateCd(GlobalExtensionManagement, ASTCDCompilationUnit)} */
  @Test
  public void testDecorateCd() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = mc.initNewSymbolTable(modelPath);
    mc.createNewSymbolsFromAST(symbolTable, grammar);
    cdCompilationUnit = mc.transformAstGrammarToAstCd(new GlobalExtensionManagement(),
        grammar, symbolTable, targetPath);
    assertNotNull(cdCompilationUnit);
    GeneratorHelper genHelper = new GeneratorHelper(cdCompilationUnit, symbolTable);
    assertEquals("de.monticore.statechart.statechart._ast", GeneratorHelper.getPackageName(
        genHelper.getPackageName(), GeneratorHelper.AST_PACKAGE_SUFFIX));
    assertNotNull(cdCompilationUnit.getCDDefinition());
    ASTCDDefinition cdDefinition = cdCompilationUnit.getCDDefinition();
    assertEquals(8, cdDefinition.getCDClasses().size());
    assertEquals(5, cdDefinition.getCDInterfaces().size());
    
    mc.decorateCd(glex, cdCompilationUnit, symbolTable, targetPath);
    // Added Builder classes to the each not list class
    assertEquals(18, cdDefinition.getCDClasses().size());
    
    // Check if there are all additional methods defined in the given CD class
    List<String> methods = Lists.newArrayList();
    for (ASTCDClass cdClass : cdDefinition.getCDClasses()) {
      // All methods of CD class
      for (ASTCDMethod method : cdClass.getCDMethods()) {
        methods.add(method.getName());
      }
      String withOrder = "WithOrder";
      for (String additionalMethod : additionalMethods) {
        if (additionalMethod.endsWith(withOrder)) {
          assertTrue(methods.contains(additionalMethod.substring(0,
              additionalMethod.indexOf(withOrder))));
        }
        else {
          assertTrue(methods.contains(additionalMethod));
        }
      }
    }
    
  }
  
  /** {@link MontiCoreScript#generate(GlobalExtensionManagement, ASTCDCompilationUnit, java.util.Map)} */
  @Test
  public void testGenerate() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = mc.initNewSymbolTable(modelPath);
    mc.createNewSymbolsFromAST(symbolTable, grammar);
    cdCompilationUnit = mc.transformAstGrammarToAstCd(new GlobalExtensionManagement(),
        grammar, symbolTable, targetPath);
    mc.storeInCdFile(cdCompilationUnit, outputPath);
    mc.generate(glex, symbolTable, cdCompilationUnit, outputPath, templatePath);
  }
  
  /** {@link MontiCoreScript#run(MontiCoreConfiguration)} */
  @Test
  public void testDefaultScript() {    
    Configuration configuration =
        ConfigurationPropertiesMapContributor
            .fromSplitMap(CLIArguments.forArguments(simpleArgs).asMap());
    MontiCoreConfiguration cfg = MontiCoreConfiguration.withConfiguration(configuration);
    new MontiCoreScript().run(cfg);
    
    assertTrue(!false);
  }
  
}
