/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.tf.odrulegeneration.ODRuleGenerationMill;
import de.monticore.tf.odrulegeneration._ast.ASTObjectCondition;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.odrules._visitor.ODRulesTraverser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateConditionsVisitorTest {

  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    ODRulesMill.reset();
    ODRulesMill.init();
    ODRuleGenerationMill.reset();
    ODRuleGenerationMill.init();
  }

  @AfterEach
  public void after() {
    ODRulesMill.reset();
    ODRuleGenerationMill.reset();
  }

  protected List<String> generateConditions(String lhs) throws IOException {
    Optional<ASTODRule> rule = ODRulesMill.parser()
        .parse_String("grammar a.A;\npattern objectdiagram lhs {\n" + lhs + "\n}");
    assertTrue(rule.isPresent(), Log.getFindings().toString());

    GenerateConditionsVisitor visitor = new GenerateConditionsVisitor();
    ODRulesTraverser traverser = ODRulesMill.inheritanceTraverser();
    traverser.add4ODRules(visitor);
    rule.get().getLhs().accept(traverser);
    assertTrue(Log.getFindings().isEmpty(), Log.getFindings().toString());

    List<String> conditions = visitor.getObjectConditions().stream()
        .map(ASTObjectCondition::getConditionString)
        .toList();
    conditions.forEach(c -> assertNotNull(c, "condition without condition string"));
    return conditions;
  }

  @Test
  public void testPresentCheckOfOptionalValue() throws IOException {
    // only the "get" prefix of the getter is replaced (not the one within "target")
    List<String> conditions = generateConditions(
        "a_1 : a._ast.ASTA { String name [1] = b_1.target [0..1]; }\n"
            + "b_1 : a._ast.ASTB { }");
    assertTrue(conditions.contains(
        "b_1_cand.isPresentTarget() && (!cand.getName().equals(b_1_cand.getTarget()))"), conditions.toString());
  }

  @Test
  public void testStringValueWithDot() throws IOException {
    List<String> conditions = generateConditions(
        "a_1 : a._ast.ASTA { String name [1] = \"a.b\"; }");
    assertEquals(List.of("(!cand.getName().equals(\"a.b\"))"), conditions);
  }

  @Test
  public void testStringListValueWithDot() throws IOException {
    List<String> conditions = generateConditions(
        "a_1 : a._ast.ASTA { java.util.List<String> parts [*] = {\"a.b\"}; }");
    assertEquals(List.of("(cand.getPartsList() == null || cand.getPartsList().size() != 1"
        + "|| ! cand.getPartsList().get(0).equals(\"a.b\") )"), conditions);
  }

  @Test
  public void testListValueOfDerivedAttributeName() throws IOException {
    // handwritten rules may use the attribute name of the class diagram (State* -> getStateList)
    List<String> conditions = generateConditions(
        "a_1 : a._ast.ASTA { a._ast.ASTState states [*] = {\"x\"}; }");
    assertEquals(List.of("(cand.getStateList() == null || cand.getStateList().size() != 1"
        + "|| ! cand.getStateList().get(0).equals(\"x\") )"), conditions);
  }

  @Test
  public void testCapitalizedObjectName() throws IOException {
    // the candidates are named like the objects
    List<String> conditions = generateConditions(
        "Automaton_1 : a._ast.ASTAutomaton { }\n"
            + "state_1 : a._ast.ASTState { }\n"
            + "composition Automaton_1 -- (states) [*] state_1;");
    assertTrue(conditions.stream().anyMatch(c -> c.contains("Automaton_1_cand.getStateList()")),
        conditions.toString());
    assertFalse(conditions.stream().anyMatch(c -> c.contains("automaton_1_cand")), conditions.toString());
  }

  @Test
  public void testLinkWithoutName() throws IOException {
    // conditions can only be created for named links
    List<String> conditions = generateConditions(
        "a_1 : a._ast.ASTA { }\n"
            + "b_1 : a._ast.ASTB { }\n"
            + "link a_1 -- [1] b_1;");
    assertEquals(List.of(), conditions);
  }

}
