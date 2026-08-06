/* (c) https://github.com/MontiCore/monticore */

package mc.feature.listrule;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.listrule.listrule.ListRuleMill;
import mc.feature.listrule.listrule._parser.ListRuleParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(ListRuleMill.class)
public class ListRuleTest {

  @Test
  public void testParent1() throws IOException {
    ListRuleParser p = ListRuleMill.parser();
    p.parse_String("P1 a, P1 b");
    
    assertFalse(p.hasErrors());

    // Empty lists are NOT allowed
    p.parse_String("");
    
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("mismatched input '<EOF>' expecting 'P1'");
  }

  @Test
  public void testParent2() throws IOException {
    ListRuleParser p = ListRuleMill.parser();
    p.parse_StringParent2("Parent2 P2 a, P2 b Parent2");
    
    assertFalse(p.hasErrors());
  }

  @Test
  public void testParent3() throws IOException {
    ListRuleParser p = ListRuleMill.parser();
    p.parse_StringParent3("P3 a, P3 b");
    
    assertFalse(p.hasErrors());
  }

  @Test
  public void testParent4() throws IOException {
    ListRuleParser p = ListRuleMill.parser();
    p.parse_StringParent4("P4 a, P4 b");
    
    assertFalse(p.hasErrors());

    // Empty lists are allowed
    p.parse_StringParent4("");
    
    assertFalse(p.hasErrors());
  }

  @Test
  public void testParent6() throws IOException {
    ListRuleParser p = ListRuleMill.parser();
    p.parse_StringParent6("a, P");
    
    assertFalse(p.hasErrors());
  }}
