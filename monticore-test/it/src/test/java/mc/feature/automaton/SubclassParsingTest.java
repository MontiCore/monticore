/* (c) https://github.com/MontiCore/monticore */

package mc.feature.automaton;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.automaton.automaton._parser.AutomatonParser;
import mc.feature.automaton.automaton._ast.ASTSubTransition;
import mc.feature.automaton.automaton._ast.ASTTransition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubclassParsingTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSubtypeParsing() throws IOException {
    
    AutomatonParser parser = new AutomatonParser();
    
    Optional<ASTTransition> ast = parser.parseTransition(new StringReader("sub a -x> b;"));
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTSubTransition.class, ast.get());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
