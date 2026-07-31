/* (c) https://github.com/MontiCore/monticore */

package mc.feature.semanticpredicate;

import java.io.IOException;
import java.io.StringReader;

import mc.feature.semanticpredicate.sempredwithinterface.SemPredWithInterfaceMill;

import mc.GeneratorIntegrationsTest;
import mc.feature.semanticpredicate.sempredwithinterface._ast.ASTISequence;
import mc.feature.semanticpredicate.sempredwithinterface._parser.SemPredWithInterfaceParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SemPredWithInterfaceParserTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testParse() {
    String input = "foo foo";
    SemPredWithInterfaceParser p = SemPredWithInterfaceMill.parser();
    java.util.Optional<ASTISequence> ast = java.util.Optional.empty();
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
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
