/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._parser;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton.tr.automatontr._ast.*;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class AutomatonTransformationRuleParserTest  {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testValidAutomatonRule() throws  IOException {
    String inputFile = "src/test/resources/ValidPattern.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    ASTAutomatonTFRule o = ast.get();
    assertNotNull(o);
    assertEquals(1, o.getTFRule().getITFPartList().size());
    ASTAutomaton_Pat a = (ASTAutomaton_Pat) o.getTFRule().getITFPartList().get(0);
    assertEquals(2, a.getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testForwardToInitialRule() throws IOException {
    String inputFile = "src/test/resources/ForwardToInitial.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    ASTAutomatonTFRule o = ast.get();
    assertNotNull(o);

    assertEquals(3, o.getTFRule().getITFPartList().size());
    ASTITFState s1 = (ASTITFState) o.getTFRule().getITFPartList().get(0);
    assertNotNull(s1);
    ASTState_Pat s2 = (ASTState_Pat) o.getTFRule().getITFPartList().get(1);
    assertNotNull(s2);
    assertEquals(1, s2.getStateList().size());

    assertEquals(3, o.getTFRule().getITFPartList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test @Ignore
  public void testIsIdentifierFix() throws IOException {
    String inputFile = "src/test/resources/IsIdentifierFix.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    ASTAutomatonTFRule o = ast.get();
    assertNotNull(o);

    assertEquals(3, o.getTFRule().getITFPartList().size());
    ASTState_Pat s1 = (ASTState_Pat) o.getTFRule().getITFPartList().get(0);
    assertNotNull(s1);
    ASTState_Pat s2 = (ASTState_Pat) o.getTFRule().getITFPartList().get(1);
    assertNotNull(s2);
    ASTState_Pat s3 = (ASTState_Pat) o.getTFRule().getITFPartList().get(2);
    assertNotNull(s3);

    assertFalse(s1.getName().isIdentifierFix());
    assertTrue(s2.getName().isIdentifierFix());
    assertTrue(s3.getName().isIdentifierFix());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSchemaVar() throws  IOException {
    String inputFile = "src/test/resources/SchemaVar.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    ASTAutomatonTFRule o = ast.get();
    assertNotNull(o);

    assertEquals(1, o.getTFRule().getITFPartList().size());
    ASTAutomaton_Pat a = (ASTAutomaton_Pat) o.getTFRule().getITFPartList().get(0);
    assertNotNull(a);

    assertEquals(1, a.getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRenameTransitionSource() throws IOException {
    String inputFile = "src/test/resources/RenameTransitionSource.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    ASTAutomatonTFRule o = ast.get();
    assertNotNull(o);

    assertEquals(1, o.getTFRule().getITFPartList().size());
    ASTTransition_Pat t = (ASTTransition_Pat) o.getTFRule().getITFPartList().get(0);
    assertNotNull(t);
    assertEquals("$foo", t.getFrom().getIdentifier());
    assertEquals("$bar", t.getFrom().getNewIdentifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNameAsType() throws  IOException {
    String inputFile = "src/test/resources/NameAsType.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    ASTAutomatonTFRule o = ast.get();
    assertNotNull(o);

    assertEquals(2, o.getTFRule().getITFPartList().size());

    ASTState_Pat a = (ASTState_Pat) o.getTFRule().getITFPartList().get(0);
    assertNotNull(a);
    assertTrue(a.isPresentName());
    assertEquals("$a", a.getName().getIdentifier());

    ASTState_Pat b = (ASTState_Pat) o.getTFRule().getITFPartList().get(1);
    assertNotNull(b);
    assertEquals("$b", b.getName().getIdentifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
