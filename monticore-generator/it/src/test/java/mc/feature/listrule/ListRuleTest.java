/* (c) https://github.com/MontiCore/monticore */

package mc.feature.listrule;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.listrule.listrule._parser.ListRuleParser;

public class ListRuleTest extends GeneratorIntegrationsTest {

  @Test
  public void testParent1() throws IOException {
    StringReader s = new StringReader(
        "P1 a, P1 b");
    ListRuleParser p = new ListRuleParser();
    p.parseParent(s);

    assertEquals(false, p.hasErrors());

    // Empty lists are NOT allowed
    s = new StringReader("");
    p.parse(s);

    assertEquals(true, p.hasErrors());
  }

  @Test
  public void testParent2() throws IOException {
    StringReader s = new StringReader(
        "Parent2 P2 a, P2 b Parent2");
    ListRuleParser p = new ListRuleParser();
    p.parseParent2(s);

    assertEquals(false, p.hasErrors());
  }

  @Test
  public void testParent3() throws IOException {
    StringReader s = new StringReader(
        "P3 a, P3 b");
    ListRuleParser p = new ListRuleParser();
    p.parseParent3(s);

    assertEquals(false, p.hasErrors());
  }

  @Test
  public void testParent4() throws IOException {
    StringReader s = new StringReader(
        "P4 a, P4 b");
    ListRuleParser p = new ListRuleParser();
    p.parseParent4(s);

    assertEquals(false, p.hasErrors());

    // Empty lists are allowed
    s = new StringReader("");
    p.parseParent4(s);

    assertEquals(false, p.hasErrors());
  }


  @Test
  public void testParent6() throws IOException {
    StringReader s = new StringReader(
        "a, P1");
    ListRuleParser p = new ListRuleParser();
    p.parseParent6(s);

    assertEquals(false, p.hasErrors());
  }}
