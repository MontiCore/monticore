/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule.translation;

import org.junit.Before;

public class AutomatonRule2ODToolTest {

  @Before
  public void setUp() {

  }

//  @Test
//  public void testRun() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/ForwardToInitial.mtr";
//    AutomatonRule2ODTool testee = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = testee.getRootfactory().create(inputFile);
//    testee.getExecuter().parseStatePattern(root);
//    testee.run();
//
//    assertFalse(root.hasFatalError());
//    assertFalse(root.getParser().hasErrors());
//    assertNotNull(root.getAst());
//  }
//
//  @Test
//  public void testMainPlugin_1() {
//    final String inputFile = "src/test/resources/ForwardToInitial.mtr";
//    final String outputFile = "gen/ForwardToInitial.mtod";
//    // remove outputFile if it exists
//    new File(outputFile).delete();
//
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] {});
//    tool.mainPlugin(inputFile);
//
//    // check whether the output file has been created
//    assertTrue(new File(outputFile).exists());
//
//    // parseStatePattern generated file
//    ODRuleTool odRuleTool_actual = new ODRuleTool(new String[] { outputFile });
//    ODRuleRoot actualRoot = (ODRuleRoot) odRuleTool_actual.mainPlugin(outputFile);
//
//    assertNotNull(actualRoot.getAst());
//    ASTODDefinition lhs = actualRoot.getAst().getLhs();
//    assertNotNull(lhs);
//    ASTODDefinition rhs = actualRoot.getAst().getRhs();
//    assertNotNull(rhs);
//
//    assertEquals(1, lhs.getODObject("state_3").getODAttributes().size());
//    ASTODAttribute lhs_initial = lhs.getODObject("state_3").getODAttributes().get(0);
//    assertNotNull(lhs_initial);
//    assertEquals("true", lhs_initial.printValue());
//
//    assertEquals(1, rhs.getODObject("state_3").getODAttributes().size());
//    ASTODAttribute rhs_initial = rhs.getODObject("state_3").getODAttributes().get(0);
//    assertNotNull(rhs_initial);
//    assertEquals("false", rhs_initial.printValue());
//  }
//
//  @Test
//  public void testMainPlugin_2() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/CreateSubstate.mtr";
//    final String outputFile = "gen/CreateSubstate.mtod";
//    final String expectedOutputFile = "src/test/resources/mtod/CreateSubstate.mtod";
//
//    // remove outputFile if it exists
//    new File(outputFile).delete();
//
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile });
//    tool.mainPlugin(inputFile);
//
//    // check whether the output file has been created
//    assertTrue(new File(outputFile).exists());
//
//    // parseStatePattern reference file
//    ODRuleTool odRuleTool_expected = new ODRuleTool(new String[] { expectedOutputFile });
//    ODRuleRoot expectedRoot = (ODRuleRoot) odRuleTool_expected.mainPlugin(expectedOutputFile);
//
//    // parseStatePattern generated file
//    ODRuleTool odRuleTool_actual = new ODRuleTool(new String[] { outputFile });
//    ODRuleRoot actualRoot = (ODRuleRoot) odRuleTool_actual.mainPlugin(outputFile);
//
//    // compare results
//    String difference = new ODRuleComparison().getDifference(expectedRoot.getAst(), actualRoot.getAst());
//    assertNull(difference, difference);
//
//  }
}
