package de.monticore.codegen.types;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import de.monticore.MontiCoreConfiguration;
import de.monticore.MontiCoreScript;
import de.monticore.codegen.AstDependentGeneratorTest;
import de.monticore.codegen.cd2java.types.TypeResolverGenerator;
import de.monticore.codegen.parser.ParserGeneratorTest;
import de.monticore.codegen.symboltable.SymbolTableGeneratorTest;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertTrue;
/**
 * Created by Odgrlb on 01.11.2016.
 */
public class TypeResolverGeneratorTest extends AstDependentGeneratorTest {

  private ParserGeneratorTest parserTest = new ParserGeneratorTest();
  private SymbolTableGeneratorTest symbolTest = new SymbolTableGeneratorTest();

  @Test
  public void testAutomaton2() {
    final String grammarPath = "de/monticore/emf/Automaton2.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Test
  public void testAutomaton() {
    final String grammarPath = "de/monticore/emf/Automaton.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }


  @Test
  public void testFautomaton() {
    final String grammarPath = "de/monticore/fautomaton/action/Expression.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Test
  public void testStateChart() {
    final String grammarPath = "de/monticore/statechart/Statechart.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Test
  public void testSubgrammar() {
    final String grammarPath = "de/monticore/inherited/sub/Subgrammar.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  @Test
  public void testSuperGrammar() {
    final String grammarPath = "de/monticore/inherited/Supergrammar.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  /**
   * must run before literals grammar
   */
  @Test
  public void testLexicals() {
    final String grammarPath = "de/monticore/lexicals/Lexicals.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  /**
   * must run before types grammar
   * is dependent on the lexicals grammar
   */
  @Test
  public void testLiterals() {
    final String grammarPath = "de/monticore/literals/Literals.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  /**
   * must run before javadsl and common grammar
   * is dependent on the literals grammar
   */
  @Test
  public void testTypes() {
    final String grammarPath = "de/monticore/types/Types.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  /**
   * is dependent on the types grammar
   */
  @Test
  public void testJavaDSL() {
    final String grammarPath = "de/monticore/java/JavaDSL.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
    testCorrect(grammarPath);
  }

  /**
   * is dependent on the types grammar
   */
  @Test
  public void testCommon() {
    final String grammarPath = "de/monticore/common/Common.mc4";
    astTest.testCorrect(grammarPath, false);
    parserTest.testCorrect(grammarPath, false);
    symbolTest.testCorrect(grammarPath, false);
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

  private String[] getCLIArguments(String grammar) {
    List<String> args = Lists.newArrayList(getGeneratorArguments());
    args.add(getConfigProperty(MontiCoreConfiguration.Options.GRAMMARS.toString()));
    args.add(grammar);
    return args.toArray(new String[0]);
  }

  @Override protected Path getPathToGeneratedCode(String model) {
    return Paths.get(OUTPUT_FOLDER, Names.getPathFromFilename(Names.getQualifier(model), "/").toLowerCase());
  }
}
