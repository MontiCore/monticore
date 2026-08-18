/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testoverridemcbasics.TestOverrideMCBasicsMill;
import de.monticore.testoverridemcbasics._ast.ASTFoo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TestOverrideMCBasicsMill.class)
public class OverrideMCBasicsTest {
  
  @Test
  public void testName() throws IOException {
    Optional<ASTFoo> ast = TestOverrideMCBasicsMill.parser().parse_StringFoo("Meier");
    assertTrue(ast.isPresent());

    List<String> shouldParseName = List.of("Müller", "Vπ", "a", "b", "c"
        , "d", "e", "f", "g", "h");
    for (String s : shouldParseName) {
      ast = TestOverrideMCBasicsMill.parser().parse_StringFoo(s);
      assertTrue(ast.isPresent(), "Could not parse string '" + s + "'");
      assertEquals(s, ast.get().getName());
    }
  }

  @Test
  public void testUnicodeMethods() {
    assertFalse(Character.isUnicodeIdentifierStart('1'));
    assertTrue(Character.isUnicodeIdentifierPart('1'));

    assertFalse(Character.isUnicodeIdentifierStart('.'));
    assertFalse(Character.isUnicodeIdentifierPart('.'));

    List<Character> notAllowed = List.of('.', '(', ')', '+', ',', '/', ' ');
    for (Character s : notAllowed) {
      assertFalse(Character.isUnicodeIdentifierPart(s), "Character <"+s+"> is not allowed");
      assertFalse(Character.isUnicodeIdentifierStart(s), "Character <"+s+"> is not allowed");
    }
  }

}
