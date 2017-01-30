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

package de.monticore.codegen.symboltable;

import de.monticore.ModelingLanguage;
import de.monticore.MontiCoreScript;
import de.monticore.codegen.AstDependentGeneratorTest;
import de.monticore.codegen.parser.ParserGeneratorTest;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.languages.grammar.MontiCoreGrammarLanguage;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class SymbolTableGeneratorTest extends AstDependentGeneratorTest {

  private ParserGeneratorTest parserTest = new ParserGeneratorTest();

  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }

  @Ignore("TODO")
  @Test
  public void testGrammarWithSymbolTableInfo() {
    final String grammarPath = "de/monticore/symboltable/GrammarWithSymbolTableInfo.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Ignore("TODO")
  @Test
  public void testGrammarWithSymbolTableInfo2() {
    final String grammarPath = "de/monticore/symboltable/GrammarWithSymbolTableInfo2.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Ignore("TODO")
  @Test
  public void testGrammarWithoutClassRules() {
    final String grammarPath = "de/monticore/symboltable/GrammarWithoutClassRules.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Ignore("TODO")
  @Test
  public void testGrammarWithoutUsageOfName() {
    final String grammarPath = "de/monticore/symboltable/GrammarWithoutUsageOfName.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Override
  protected void doGenerate(String model) {
    Log.info("Runs Symbol Table generator test for the model " + model, LOG);

    final File modelPath = new File("src/test/resources");

    final File modelFile = Paths.get(modelPath.toString(), model).toFile();

    final MontiCoreScript montiCore = new MontiCoreScript();

    final Optional<ASTMCGrammar> ast = montiCore
        .parseGrammar(Paths.get(modelFile.getAbsolutePath()));
    assertTrue(ast.isPresent());
    final ASTMCGrammar grammar = ast.get();

    final File outputPath = new File(OUTPUT_FOLDER);

    final ModelingLanguage grammarLanguage = new MontiCoreGrammarLanguage();

    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addTopScopeResolvers(grammarLanguage.getResolvingFilters());

    GlobalScope globalScope = new GlobalScope(new ModelPath(Paths.get(modelPath.getAbsolutePath())),
        grammarLanguage, resolvingConfiguration);


//TODO    decoreate and create cdInfos
    // CdInfos cdInfo = new CdInfos(topAst, symbolTable)
//    SymbolTableGenerator.generate(grammar, globalScope, cdInfo, outputPath, IterablePath.from(outputPath, "java"));
  }

  @Override
  protected Path getPathToGeneratedCode(String grammar) {
    return Paths.get(OUTPUT_FOLDER, Names.getPathFromFilename(Names.getQualifier(grammar), "/").toLowerCase());
  }

}
