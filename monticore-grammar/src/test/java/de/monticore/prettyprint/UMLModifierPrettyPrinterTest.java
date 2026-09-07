/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testumlmodifier.TestUMLModifierMill;
import de.monticore.testumlmodifier._parser.TestUMLModifierParser;
import de.monticore.umlmodifier._ast.ASTModifier;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestUMLModifierMill.class)
public class UMLModifierPrettyPrinterTest {

  @Test
  public void testModifierWord() throws IOException {
    TestUMLModifierParser parser = TestUMLModifierMill.parser();
    Optional<ASTModifier> result = parser.parse_StringModifier("private");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTModifier modifier = result.get();
    
    String output = TestUMLModifierMill.prettyPrint(modifier, false);
    
    result = parser.parse_StringModifier(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(modifier.deepEquals(result.get()));
  }
  
  @Test
  public void testModifierSymbol() throws IOException {
    TestUMLModifierParser parser = TestUMLModifierMill.parser();
    Optional<ASTModifier> result = parser.parse_StringModifier("-");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTModifier modifier = result.get();
    
    String output = TestUMLModifierMill.prettyPrint(modifier, false);
    
    result = parser.parse_StringModifier(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(modifier.deepEquals(result.get()));
  }
}
