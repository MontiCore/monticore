/* (c) https://github.com/MontiCore/monticore */

package mc.feature.semanticpredicate;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.semanticpredicate.sempredwithinterface.SemPredWithInterfaceMill;
import mc.feature.semanticpredicate.sempredwithinterface._ast.ASTISequence;
import mc.feature.semanticpredicate.sempredwithinterface._parser.SemPredWithInterfaceParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(SemPredWithInterfaceMill.class)
public class SemPredWithInterfaceParserTest {
  
  @Test
  public void testParse() {
    String input = "foo foo";
    SemPredWithInterfaceParser p = SemPredWithInterfaceMill.parser();
    java.util.Optional<ASTISequence> ast = java.util.Optional.empty();
    try {
       ast = p.parse_StringISequence(input);
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
