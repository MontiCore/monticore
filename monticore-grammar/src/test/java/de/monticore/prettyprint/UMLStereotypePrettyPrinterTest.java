/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testumlstereotype.TestUMLStereotypeMill;
import de.monticore.testumlstereotype._parser.TestUMLStereotypeParser;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestUMLStereotypeMill.class)
public class UMLStereotypePrettyPrinterTest {

  @Test
  public void testStereotype() throws IOException {
    TestUMLStereotypeParser parser = TestUMLStereotypeMill.parser();
    Optional<ASTStereotype> result = parser.parse_StringStereotype("<<s1=\"S1\">>");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTStereotype stereotype = result.get();
    
    String output = TestUMLStereotypeMill.prettyPrint(stereotype, false);
    
    result = parser.parse_StringStereotype(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(stereotype.deepEquals(result.get()));
  }
  
  @Test
  public void testStereoValue() throws IOException {
    TestUMLStereotypeParser parser = TestUMLStereotypeMill.parser();
    Optional<ASTStereoValue> result = parser.parse_StringStereoValue("s1=\"S1\"");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTStereoValue stereovalue = result.get();
    
    String output = TestUMLStereotypeMill.prettyPrint(stereovalue, false);
    
    result = parser.parse_StringStereoValue(output);
    
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(stereovalue.deepEquals(result.get()));
  }
}
