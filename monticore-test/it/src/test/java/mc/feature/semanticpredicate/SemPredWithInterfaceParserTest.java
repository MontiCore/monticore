/* (c) https://github.com/MontiCore/monticore */

package mc.feature.semanticpredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.semanticpredicate.sempredwithinterface._ast.ASTISequence;
import mc.feature.semanticpredicate.sempredwithinterface._parser.SemPredWithInterfaceParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class SemPredWithInterfaceParserTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParse() {
    String input = "foo foo";
    SemPredWithInterfaceParser p = new SemPredWithInterfaceParser();
    java.util.Optional<ASTISequence> ast = null;
    try {
       ast = p.parseISequence(new StringReader(input));
    } catch (IOException e) {
      Assertions.fail();
    }
    Assertions.assertTrue(ast.isPresent());
    ASTISequence seq = ast.get();
    Assertions.assertEquals(2, seq.getIList().size());
    
    Assertions.assertTrue(seq.getIList().get(0).isFirst());
    Assertions.assertFalse(seq.getIList().get(1).isFirst());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
