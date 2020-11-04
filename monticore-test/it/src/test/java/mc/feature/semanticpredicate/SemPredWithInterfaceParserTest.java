/* (c) https://github.com/MontiCore/monticore */

package mc.feature.semanticpredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.semanticpredicate.sempredwithinterface._ast.ASTISequence;
import mc.feature.semanticpredicate.sempredwithinterface._parser.SemPredWithInterfaceParser;

public class SemPredWithInterfaceParserTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testParse() {
    String input = "foo foo";
    SemPredWithInterfaceParser p = new SemPredWithInterfaceParser();
    java.util.Optional<ASTISequence> ast = null;
    try {
       ast = p.parseISequence(new StringReader(input));
    } catch (IOException e) {
      fail();
    }
    assertTrue(ast.isPresent());
    ASTISequence seq = ast.get();
    assertEquals(2, seq.getIList().size());
    
    assertTrue(seq.getIList().get(0).isFirst());
    assertFalse(seq.getIList().get(1).isFirst());
  }
  
}
