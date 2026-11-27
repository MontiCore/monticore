/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule.translation;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._ast.ASTODDefinition;
import de.monticore.tf.odrules._ast.ASTODLink;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.odrules.util.Util;
import de.monticore.tf.rule2od.Variable2AttributeMap;
import de.monticore.tf.ruletranslation.Rule2ODState;
import de.monticore.tf.runtime.matching.ModelTraversal;
import de.monticore.tf.runtime.matching.ModelTraversalFactory;
import mc.testcases.automaton.tr.automatontr.AutomatonTRMill;
import mc.testcases.automaton.tr.automatontr._ast.ASTAutomatonTFRule;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import mc.testcases.automaton.tr.automatontr._visitor.AutomatonTRTraverser;
import mc.testcases.automaton.tr.translation.AutomatonRule2OD;
import mc.testcases.automaton.tr.translation.AutomatonRuleCollectVariables;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the translation of replacements within optional structures into ODRules, where a
 * replacement may lack a side: {@code opt [[ [[ x :- ]] ]]} deletes the optional element x, which
 * thus only exists on the LHS, and {@code opt [[ [[ :- x ]] ]]} creates it, so it only exists on
 * the RHS. No composition must be created for a side on which the element does not exist.
 */
@TestWithMCLanguage(AutomatonTRMill.class)
public class ReplacementWithinOptionalRule2ODTest {

  /**
   * Parses the rule and translates it into an ODRule, incl. its symbol table (needed to look up
   * its objects)
   */
  protected ASTODRule createODRule(String rule) throws IOException {
    AutomatonTRParser parser = AutomatonTRMill.parser();
    ASTAutomatonTFRule ast = parser.parse_StringAutomatonTFRule(rule).orElseThrow();
    assertFalse(parser.hasErrors());

    @SuppressWarnings({"unchecked", "rawtypes"})
    ModelTraversal<AutomatonTRTraverser> traversal = ModelTraversalFactory.getInstance()
        .create((Supplier) AutomatonTRMill::inheritanceTraverser);
    ast.accept(traversal.getTraverser());

    ODRulesMill.reset();
    ODRulesMill.init();
    Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), traversal.getParents());
    AutomatonRuleCollectVariables variables = new AutomatonRuleCollectVariables(state);
    ast.accept(variables.getTraverser());
    AutomatonRule2OD rule2OD = new AutomatonRule2OD(state);
    ast.accept(rule2OD.getTraverser());
    ASTODRule od = rule2OD.getOD();
    ODRulesMill.scopesGenitorDelegator().createFromAST(od);
    return od;
  }

  /** The compositions of transitions in the definition, e.g. "automaton_1 -transition-> transition_1" */
  protected static List<String> links(ASTODDefinition definition) {
    return definition.getODLinkList().stream()
        .filter(link -> "transition".equals(link.getRightRole()))
        .map(ReplacementWithinOptionalRule2ODTest::print)
        .toList();
  }

  protected static String print(ASTODLink link) {
    return link.getLeftReferenceName(0).getQName() + " -" + link.getRightRole() + "-> "
        + link.getRightReferenceName(0).getQName();
  }

  /**
   * Deleting an optional element by a replacement within the optional structure results in the
   * same ODRule as the replacement around the optional structure: the element and its
   * composition are only on the LHS, the (empty) optional structure is on both sides
   */
  @Test
  public void testDeletionWithinOptional() throws IOException {
    ASTODRule within = createODRule("automaton $_ { state $S; opt [[ [[ $S - $In > $_; :- ]] ]] }");
    ASTODRule around = createODRule("automaton $_ { state $S; [[ opt [[ $S - $In > $_; ]] :- ]] }");

    assertTrue(around.deepEquals(within), "same ODRule as for the replacement around the optional");
    assertEquals(List.of("automaton_1 -transition-> transition_1"), links(within.getLhs()));
    assertEquals(List.of(), links(within.getRhs()));
    assertNotNull(Util.getODObject(within.getLhs(), "transition_1"));
    assertNull(Util.getODObject(within.getRhs(), "transition_1"));
    assertNotNull(Util.getODObject(within.getRhs(), "optional_1"));
  }

  /**
   * Creating an element by a replacement within an optional structure: the element and its
   * composition are only on the RHS, the (empty) optional structure is on both sides
   */
  @Test
  public void testCreationWithinOptional() throws IOException {
    ASTODRule od = createODRule("automaton $_ { state $S; opt [[ [[ :- $S - reset > $S; ]] ]] }");

    assertEquals(List.of(), links(od.getLhs()));
    assertEquals(List.of("automaton_1 -transition-> transition_1"), links(od.getRhs()));
    assertNull(Util.getODObject(od.getLhs(), "transition_1"));
    assertNotNull(Util.getODObject(od.getRhs(), "transition_1"));
    assertNotNull(Util.getODObject(od.getLhs(), "optional_1"));
  }

  /**
   * A replacement with both sides within an optional structure is translated as before: the
   * replaced element on the LHS, the replacing one on the RHS
   */
  @Test
  public void testReplacementWithinOptional() throws IOException {
    ASTODRule od = createODRule(
        "automaton $_ { state $S; opt [[ [[ $S - $In > $T; :- $T - $In > $S; ]] ]] }");

    assertEquals(List.of("automaton_1 -transition-> transition_1"), links(od.getLhs()));
    assertEquals(List.of("automaton_1 -transition-> transition_2"), links(od.getRhs()));
  }

  /** An optional element without replacement is on both sides */
  @Test
  public void testOptionalWithoutReplacement() throws IOException {
    ASTODRule od = createODRule("automaton $_ { state $S; opt [[ $S - $In > $_; ]] }");

    assertEquals(List.of("automaton_1 -transition-> transition_1"), links(od.getLhs()));
    assertEquals(List.of("automaton_1 -transition-> transition_1"), links(od.getRhs()));
  }
}
