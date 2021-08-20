/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule.translation;

public class AutomatonRule2ODVisitorTest {

  /*
   * This method also serves as a test for the endVisit method
   */
//  @Test
//  public void testVisit01() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/ForwardToInitial.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check lhs states
//    ASTODObject lhs_state_1 = genRule.getLhs().getODObject("state_1");
//    assertNotNull(lhs_state_1);
//    assertNotNull(lhs_state_1.getType());
//    assertTrue(lhs_state_1.getType() instanceof ASTSimpleReferenceType);
//    String lhs_state_1_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_state_1.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", lhs_state_1_type);
//    ASTODObject lhs_state_2 = genRule.getLhs().getODObject("state_2");
//    assertNotNull(lhs_state_2);
//    ASTODObject lhs_state_3 = genRule.getLhs().getODObject("state_3");
//    assertNotNull(lhs_state_3);
//    assertEquals(1, lhs_state_3.getODAttributes().size());
//    ASTODAttribute initialAttribute = lhs_state_3.getODAttributes().get(0);
//    assertEquals("initial", initialAttribute.getName());
//    assertTrue(initialAttribute.getValue() instanceof ASTBooleanLiteral);
//    assertEquals(true, ((ASTBooleanLiteral) initialAttribute.getValue()).getValue());
//    ASTODObject lhs_transition_1 = genRule.getLhs().getODObject("transition_1");
//    assertNotNull(lhs_transition_1);
//
//    // check both attributes of lhs transition
//    assertEquals(2, lhs_transition_1.getODAttributes().size());
//    for (ASTODAttribute attr : lhs_transition_1.getODAttributes()) {
//      if ("from".equals(attr.getName())) {
//        String fromQName = NameHelper.dotSeparatedStringFromList(((ASTQualifiedName) attr.getValue()).getParts());
//        assertEquals("state_1.name", fromQName);
//        assertTrue(attr.getType() instanceof ASTSimpleReferenceType);
//        String fromTypeQName = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) attr.getType()).getName());
//        assertEquals("String", fromTypeQName);
//      } else if ("to".equals(attr.getName())) {
//        String toQName = NameHelper.dotSeparatedStringFromList(((ASTQualifiedName) attr.getValue()).getParts());
//        assertEquals("state_2.name", toQName);
//        assertTrue(attr.getType() instanceof ASTSimpleReferenceType);
//        String toTypeQName = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) attr.getType()).getName());
//        assertEquals("String", toTypeQName);
//      } else {
//        fail("Unexpected attribute " + attr.getName());
//      }
//    }
//
//    // check lhs links
//    assertEquals(1, genRule.getLhs().getODLinks().size());
//    ASTODLink lhs_link_1 = genRule.getLhs().getODLinks().get(0);
//    assertTrue(lhs_link_1.isComposition());
//    assertEquals("state_2", lhs_link_1.getLeftReferenceNamesString());
//    assertEquals("state_3", lhs_link_1.getRightReferenceNamesString());
//    assertEquals("state", lhs_link_1.getRightRole());
//
//    // check rhs
//    assertEquals(4, genRule.getRhs().getODObjects().size());
//
//    // check rhs states
//    ASTODObject rhs_state_1 = genRule.getRhs().getODObject("state_1");
//    assertNotNull(rhs_state_1);
//    ASTODObject rhs_state_2 = genRule.getRhs().getODObject("state_2");
//    assertNotNull(rhs_state_2);
//    ASTODObject rhs_state_3 = genRule.getRhs().getODObject("state_3");
//    assertNotNull(rhs_state_3);
//    ASTODObject rhs_transition_1 = genRule.getRhs().getODObject("transition_1");
//    assertNotNull(rhs_transition_1);
//
//    // check both attributes of rhs transition
//    assertEquals(1, rhs_transition_1.getODAttributes().size());
//    ASTODAttribute attr = rhs_transition_1.getODAttributes().get(0);
//    assertEquals("to", attr.getName());
//    String toQName = NameHelper.dotSeparatedStringFromList(((ASTQualifiedName) attr.getValue()).getParts());
//    assertEquals("state_3.name", toQName);
//
//    // check rhs links
//    assertEquals(1, genRule.getRhs().getODLinks().size());
//    ASTODLink rhs_link_1 = genRule.getRhs().getODLinks().get(0);
//    assertTrue(rhs_link_1.isComposition());
//    assertEquals("state_2", rhs_link_1.getLeftReferenceNamesString());
//    assertEquals("state_3", rhs_link_1.getRightReferenceNamesString());
//    assertEquals("state", rhs_link_1.getRightRole());
//
//  }
//
//  /*
//   * This method also serves as a test for the endVisit method
//   */
//  @Test
//  public void testVisit02() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/FixObject.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check lhs states
//    ASTODObject lhs_state_1 = genRule.getLhs().getODObject("state_1");
//    assertNotNull(lhs_state_1);
//    assertNotNull(lhs_state_1.getType());
//    assertTrue(lhs_state_1.getType() instanceof ASTSimpleReferenceType);
//    String lhs_state_1_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_state_1.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", lhs_state_1_type);
//    ASTODObject lhs_state_2 = genRule.getLhs().getODObject("state_2");
//    assertNull(lhs_state_2);
//
//    // check rhs states
//    ASTODObject rhs_state_1 = genRule.getRhs().getODObject("state_1");
//    assertNull(rhs_state_1);
//    ASTODObject rhs_state_2 = genRule.getRhs().getODObject("state_2");
//    assertNotNull(rhs_state_2);
//    assertNotNull(rhs_state_2.getType());
//    assertTrue(rhs_state_2.getType() instanceof ASTSimpleReferenceType);
//    String rhs_state_2_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) rhs_state_2.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", rhs_state_2_type);
//
//  }
//
//  @Test
//  public void testVisit03() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/ListMatch.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check lhs states
//    ASTODObject lhs_state_1 = genRule.getLhs().getODObject("state_1");
//    assertNotNull(lhs_state_1);
//    assertNotNull(lhs_state_1.getType());
//    assertTrue(lhs_state_1.getType() instanceof ASTSimpleReferenceType);
//    String lhs_state_1_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_state_1.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", lhs_state_1_type);
//    assertTrue(lhs_state_1.hasStereotype(ODRuleStereotypes.LIST));
//    ASTODObject lhs_state_2 = genRule.getLhs().getODObject("state_2");
//    assertNull(lhs_state_2);
//
//    // check rhs states
//    ASTODObject rhs_state_1 = genRule.getRhs().getODObject("state_1");
//    assertNull(rhs_state_1);
//  }
//
//  @Test
//  public void testVisit04() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/IsIdentifierFix.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check states on lhs
//    ASTODObject lhs_state_1 = genRule.getLhs().getODObject("state_1");
//    assertNotNull(lhs_state_1);
//
//    assertEquals(0, lhs_state_1.getODAttributes().size());
//
//    ASTODObject lhs_state_2 = genRule.getLhs().getODObject("state_2");
//    assertNotNull(lhs_state_2);
//
//    assertEquals(1, lhs_state_2.getODAttributes().size());
//    assertEquals("name", lhs_state_2.getODAttributes().get(0).getName());
//
//    assertTrue(lhs_state_2.getODAttributes().get(0).getValue() instanceof ASTStringLiteral);
//    ASTStringLiteral state_2_lhs_name_value = (ASTStringLiteral) lhs_state_2.getODAttributes().get(0).getValue();
//    assertEquals("\"fixState\"", state_2_lhs_name_value.getSource());
//
//    ASTODObject lhs_state_3 = genRule.getLhs().getODObject("state_3");
//    assertNotNull(lhs_state_3);
//
//    assertEquals(1, lhs_state_3.getODAttributes().size());
//    assertEquals("name", lhs_state_3.getODAttributes().get(0).getName());
//
//    assertTrue(lhs_state_3.getODAttributes().get(0).getValue() instanceof ASTStringLiteral);
//    ASTStringLiteral state_3_lhs_name_value = (ASTStringLiteral) lhs_state_3.getODAttributes().get(0).getValue();
//    assertEquals("\"$fixState\"", state_3_lhs_name_value.getSource());
//  }
//
//  @Test
//  public void testVisit05() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/SetNameToStringLiteral.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check state on lhs
//    ASTODObject lhs_state_1 = genRule.getLhs().getODObject("state_1");
//    assertNotNull(lhs_state_1);
//
//    assertEquals(0, lhs_state_1.getODAttributes().size());
//
//    // check state on rhs
//    ASTODObject rhs_state_1 = genRule.getRhs().getODObject("state_1");
//    assertNotNull(rhs_state_1);
//
//    assertEquals(1, rhs_state_1.getODAttributes().size());
//    ASTODAttribute nameAttr = rhs_state_1.getODAttributes().get(0);
//    assertEquals("name", nameAttr.getName());
//    assertTrue(nameAttr.getValue() instanceof ASTStringLiteral);
//    ASTStringLiteral attr_value = (ASTStringLiteral) nameAttr.getValue();
//    assertEquals("\"fixName\"", attr_value.getSource());
//
//  }
//
//  @Test
//  public void testVisit06() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/SchemaVar.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check lhs states
//    ASTODObject lhs_state_1 = genRule.getLhs().getODObject("$BAR");
//    assertNotNull(lhs_state_1);
//    assertNotNull(lhs_state_1.getType());
//    assertTrue(lhs_state_1.getType() instanceof ASTSimpleReferenceType);
//    String lhs_state_1_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_state_1.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", lhs_state_1_type);
//    ASTODObject lhs_state_2 = genRule.getLhs().getODObject("state_2");
//    assertNull(lhs_state_2);
//
//    // check rhs states
//    ASTODObject rhs_state_1 = genRule.getRhs().getODObject("$BAR");
//    assertNotNull(rhs_state_1);
//    assertNotNull(rhs_state_1.getType());
//    assertTrue(rhs_state_1.getType() instanceof ASTSimpleReferenceType);
//    String rhs_state_1_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) rhs_state_1.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", rhs_state_1_type);
//    ASTODObject rhs_state_2 = genRule.getRhs().getODObject("state_2");
//    assertNull(rhs_state_2);
//
//  }
//
//  @Test
//  public void testVisit07() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/ObjectName.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check lhs transitions
//    ASTODObject lhs_transition_1 = genRule.getLhs().getODObject("transition_1");
//    assertNotNull(lhs_transition_1);
//    assertNotNull(lhs_transition_1.getType());
//    assertTrue(lhs_transition_1.getType() instanceof ASTSimpleReferenceType);
//    String lhs_transisiton_1_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_transition_1.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTTransition", lhs_transisiton_1_type);
//    ASTODObject lhs_transition_t = genRule.getLhs().getODObject("$T");
//    assertNotNull(lhs_transition_t);
//    assertNotNull(lhs_transition_t.getType());
//    assertTrue(lhs_transition_t.getType() instanceof ASTSimpleReferenceType);
//    String lhs_transition_t_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_transition_t.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTTransition", lhs_transition_t_type);
//
//    // check rhs transitions
//  }
//
//  @Test
//  public void testVisit08() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/CreateFirstState.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check lhs elements
//    assertNotNull(genRule);
//    assertNotNull(genRule.getLhs());
//    ASTODObject lhs_state_neg = genRule.getLhs().getODObject("state_1");
//    assertNotNull(lhs_state_neg.getType());
//    assertTrue(lhs_state_neg.getType() instanceof ASTSimpleReferenceType);
//    String lhs_state_neg_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_state_neg.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", lhs_state_neg_type);
//    assertTrue(lhs_state_neg.hasStereotype(de.monticore.tf.odrules.ODRuleStereotypes.NOT));
//
//    // check rhs state
//    ASTODObject rhs_state_2 = genRule.getRhs().getODObject("state_2");
//    assertNotNull(rhs_state_2);
//    assertNotNull(rhs_state_2.getType());
//    assertTrue(rhs_state_2.getType() instanceof ASTSimpleReferenceType);
//    String rhs_state_1_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) rhs_state_2.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", rhs_state_1_type);
//    ASTODAttribute rhs_state_2_name = rhs_state_2.getODAttributes().get(0);
//    assertEquals("name", rhs_state_2_name.getName());
//    assertTrue(rhs_state_2_name.getValue() instanceof ASTStringLiteral);
//    ASTStringLiteral rhs_state_2_name_value = (ASTStringLiteral) rhs_state_2_name.getValue();
//    assertNotNull(rhs_state_2_name_value);
//    assertEquals("sub", rhs_state_2_name_value.getValue());
//  }
//
//  @Test
//  public void testVisit09() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/AnonymousIdentVar.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check lhs elements
//    assertNotNull(genRule);
//    assertNotNull(genRule.getLhs());
//    ASTODObject lhs_state_1 = genRule.getLhs().getODObject("state_1");
//    assertNotNull(lhs_state_1.getType());
//    assertTrue(lhs_state_1.getType() instanceof ASTSimpleReferenceType);
//    String lhs_state_neg_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_state_1.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", lhs_state_neg_type);
//
//    ASTODObject lhs_transition_1 = genRule.getLhs().getODObject("transition_1");
//    assertNotNull(lhs_transition_1.getType());
//    assertTrue(lhs_transition_1.getType() instanceof ASTSimpleReferenceType);
//    String lhs_transition_1_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_transition_1.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTTransition", lhs_transition_1_type);
//
//    // state must not have a name attribute
//    assertEquals(0, lhs_state_1.getODAttributes().size());
//
//    // transition must not have any attributes, too
//    assertEquals(0, lhs_transition_1.getODAttributes().size());
//  }
//
//  @Test
//  public void testVisit10() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/SetInitialToFalseInList.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check lhs elements
//    assertNotNull(genRule);
//    assertNotNull(genRule.getLhs());
//    ASTODObject lhs_state_1 = genRule.getLhs().getODObject("state_1");
//    assertNotNull(lhs_state_1.getType());
//    assertTrue(lhs_state_1.getType() instanceof ASTSimpleReferenceType);
//    String lhs_state_neg_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_state_1.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", lhs_state_neg_type);
//
//    // state must not have the intial attribute and a list stereotpye
//    assertEquals(0, lhs_state_1.getODAttributes().size());
//    assertTrue(lhs_state_1.hasStereotype(ODRuleStereotypes.LIST));
//
//    ASTODObject rhs_state_1 = genRule.getRhs().getODObject("state_1");
//    assertNotNull(rhs_state_1.getType());
//    assertTrue(rhs_state_1.getType() instanceof ASTSimpleReferenceType);
//    String rhs_state_neg_type = NameHelper.dotSeparatedStringFromList(((ASTSimpleReferenceType) lhs_state_1.getType()).getName());
//    assertEquals("mc.testcases.automaton._ast.ASTState", rhs_state_neg_type);
//
//    // state must have the intial attribute and a list stereotpye
//    assertEquals(1, rhs_state_1.getODAttributes().size());
//    assertTrue(rhs_state_1.hasStereotype(ODRuleStereotypes.LIST));
//
//  }
//
//  @Test
//  public void testVisit11() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/EmtpyRuleWithConstraint.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check constraint
//    assertNotNull(genRule.getConstraint());
//    assertTrue(genRule.getConstraint().deepEquals(ast.getConstraint()));
//
//  }
//
//  @Test
//  public void testVisit12() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/CopyTransition.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check result
//    ASTODDefinition lhs = genRule.getLhs();
//    assertEquals(1, lhs.getODObjects().size());
//    ASTODObject lhs_t_exist = lhs.getODObject("$T_EXIST");
//    assertNotNull(lhs_t_exist);
//    assertTrue(lhs_t_exist.getODAttributes().isEmpty());
//
//    ASTODDefinition rhs = genRule.getRhs();
//    assertEquals(2, rhs.getODObjects().size());
//    ASTODObject rhs_t_exist = rhs.getODObject("$T_EXIST");
//    assertNotNull(rhs_t_exist);
//    ASTODObject rhs_t_create = rhs.getODObject("$T_CREATE");
//    assertNotNull(rhs_t_create);
//    assertEquals(3, rhs_t_create.getODAttributes().size());
//
//  }
//
//  @Test
//  public void testVisit13() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
//    final String inputFile = "src/test/resources/Folding.mtr";
//    AutomatonRule2ODTool tool = new AutomatonRule2ODTool(new String[] { inputFile, "-analysis", "ALL", "parseStatePattern", "-synthesis", "ALL", "translate" });
//
//    DSLRoot<?> root = tool.getRootfactory().create(inputFile);
//    tool.getExecuter().parseStatePattern(root);
//    tool.run();
//
//    TFRule ast = (TFRule) root.getAst();
//
//    AutomatonRuleCollectVariablesVisitor cv = new AutomatonRuleCollectVariablesVisitor();
//    Visitor.run(cv, ast);
//    AutomatonRule2ODVisitor testee = new AutomatonRule2ODVisitor(cv.getCollectedVariables());
//    Visitor.run(testee, ast);
//
//    ASTODRule genRule = testee.getOD();
//
//    // check result
//    assertEquals(1, genRule.getFoldingSet().size());
//    de.monticore.tf.odrules._ast.ASTFoldingSet f = genRule.getFoldingSet().get(0);
//    assertEquals(2, f.getObjectNames().size());
//    assertEquals("$FOO", f.getObjectNames().get(0));
//    assertEquals("$BAR", f.getObjectNames().get(1));
//  }

}
