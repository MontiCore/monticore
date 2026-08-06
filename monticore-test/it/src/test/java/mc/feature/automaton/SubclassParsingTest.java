/* (c) https://github.com/MontiCore/monticore */

package mc.feature.automaton;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.automaton.automaton.AutomatonMill;
import mc.feature.automaton.automaton._ast.ASTSubTransition;
import mc.feature.automaton.automaton._ast.ASTTransition;
import mc.feature.automaton.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class SubclassParsingTest {

  @Test
  public void testSubtypeParsing() throws IOException {
    
    AutomatonParser parser = AutomatonMill.parser();
    
    Optional<ASTTransition> ast = parser.parse_StringTransition("sub a -x> b;");
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTSubTransition.class, ast.get());
  }
}
