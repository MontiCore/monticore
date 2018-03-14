/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.testumlstereotype._parser.TestUMLStereotypeParser;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class UMLStereotypePrettyPrinterTest {
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
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
    
    UMLStereotypePrettyPrinter prettyPrinter = new UMLStereotypePrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(stereotype);
    
    result = parser.parseStereotype(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(stereotype.deepEquals(result.get()));
  }
  
  @Test
  public void testStereoValue() throws IOException {
    TestUMLStereotypeParser parser = new TestUMLStereotypeParser();
    Optional<ASTStereoValue> result = parser.parseStereoValue(new StringReader("s1=\"S1\""));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTStereoValue stereovalue = result.get();
    
    UMLStereotypePrettyPrinter prettyPrinter = new UMLStereotypePrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(stereovalue);
    result = parser.parseStereoValue(new StringReader(output));
    
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(stereovalue.deepEquals(result.get()));
  }
}
