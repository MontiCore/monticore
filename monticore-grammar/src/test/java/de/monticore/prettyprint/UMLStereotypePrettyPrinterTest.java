/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.monticore.testumlstereotype.TestUMLStereotypeMill;
import org.junit.jupiter.api.BeforeEach;
import de.monticore.umlstereotype._prettyprint.UMLStereotypeFullPrettyPrinter;
import de.monticore.testumlstereotype._parser.TestUMLStereotypeParser;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UMLStereotypePrettyPrinterTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestUMLStereotypeMill.reset();
    TestUMLStereotypeMill.init();
  }
  
  @BeforeEach
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void testStereotype() throws IOException {
    TestUMLStereotypeParser parser = new TestUMLStereotypeParser();
    Optional<ASTStereotype> result = parser.parseStereotype(new StringReader("<<s1=\"S1\">>"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTStereotype stereotype = result.get();
    
    UMLStereotypeFullPrettyPrinter prettyPrinter = new UMLStereotypeFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(stereotype);
    
    result = parser.parseStereotype(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(stereotype.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testStereoValue() throws IOException {
    TestUMLStereotypeParser parser = new TestUMLStereotypeParser();
    Optional<ASTStereoValue> result = parser.parseStereoValue(new StringReader("s1=\"S1\""));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTStereoValue stereovalue = result.get();
    
    UMLStereotypeFullPrettyPrinter prettyPrinter = new UMLStereotypeFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(stereovalue);
    result = parser.parseStereoValue(new StringReader(output));
    
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(stereovalue.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
