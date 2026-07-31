/* (c) https://github.com/MontiCore/monticore */

package mc.feature.listrule;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.listrule.listrule._parser.ListRuleParser;

import static org.junit.jupiter.api.Assertions.*;

public class ListRuleTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testParent1() throws IOException {
    StringReader s = new StringReader(
        "P1 a, P1 b");
    ListRuleParser p = new ListRuleParser();
    p.parseParent(s);
    
    assertFalse(p.hasErrors());

    // Empty lists are NOT allowed
    s = new StringReader("");
    p.parse(s);
    
    assertTrue(p.hasErrors());
  }

  @Test
  public void testParent2() throws IOException {
    StringReader s = new StringReader(
        "Parent2 P2 a, P2 b Parent2");
    ListRuleParser p = new ListRuleParser();
    p.parseParent2(s);
    
    assertFalse(p.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testParent3() throws IOException {
    StringReader s = new StringReader(
        "P3 a, P3 b");
    ListRuleParser p = new ListRuleParser();
    p.parseParent3(s);
    
    assertFalse(p.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testParent4() throws IOException {
    StringReader s = new StringReader(
        "P4 a, P4 b");
    ListRuleParser p = new ListRuleParser();
    p.parseParent4(s);
    
    assertFalse(p.hasErrors());

    // Empty lists are allowed
    s = new StringReader("");
    p.parseParent4(s);
    
    assertFalse(p.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testParent6() throws IOException {
    StringReader s = new StringReader(
        "a, P");
    ListRuleParser p = new ListRuleParser();
    p.parseParent6(s);
    
    assertFalse(p.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }}
