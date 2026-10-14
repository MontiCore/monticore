/* (c) https://github.com/MontiCore/monticore */

package mc.feature.keyrule;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.keyrule.nokeywordalternatives.NoKeywordAlternativesMill;
import mc.feature.keyrule.nokeywordalternatives._ast.ASTB;
import mc.feature.keyrule.nokeywordalternatives._ast.ASTI;
import mc.feature.keyrule.nokeywordalternatives._parser.NoKeywordAlternativesParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(NoKeywordAlternativesMill.class)
public class NoKeywordAlternativesTest {

  @Test
  public void testConcreteProduction() throws IOException {
    NoKeywordAlternativesParser parser = NoKeywordAlternativesMill.parser();
    Optional<ASTB> ast = parser.parse_StringB("a c;");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
  }

  @Test
  public void testInterfaceProduction() throws IOException {
    NoKeywordAlternativesParser parser = NoKeywordAlternativesMill.parser();
    // Both alternatives consume Name Name SEMI; prediction selects A before checking "c".
    Optional<ASTI> ast = parser.parse_StringI("a c;");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTB.class, ast.get());
  }

  @Test
  public void testStartProduction() throws IOException {
    NoKeywordAlternativesParser parser = NoKeywordAlternativesMill.parser();
    Optional<ASTI> ast = parser.parse_String("a c;");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTB.class, ast.get());
  }
}
