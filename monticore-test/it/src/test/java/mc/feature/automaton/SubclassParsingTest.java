/* (c) https://github.com/MontiCore/monticore */

package mc.feature.automaton;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.automaton.automaton._parser.AutomatonParser;
import mc.feature.automaton.automaton._ast.ASTSubTransition;
import mc.feature.automaton.automaton._ast.ASTTransition;
import org.junit.jupiter.api.Test;

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
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(ast.get() instanceof ASTSubTransition);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
