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
import de.monticore.umlmodifier._prettyprint.UMLModifierFullPrettyPrinter;
import de.monticore.testumlmodifier._parser.TestUMLModifierParser;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

public class UMLModifierPrettyPrinterTest {
  
  @Before
  public void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void testModifierWord() throws IOException {
    TestUMLModifierParser parser = new TestUMLModifierParser();
    Optional<ASTModifier> result = parser.parseModifier(new StringReader("private"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTModifier modifier = result.get();
    
    UMLModifierFullPrettyPrinter prettyPrinter = new UMLModifierFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(modifier);
    
    result = parser.parseModifier(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(modifier.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testModifierSymbol() throws IOException {
    TestUMLModifierParser parser = new TestUMLModifierParser();
    Optional<ASTModifier> result = parser.parseModifier(new StringReader("-"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTModifier modifier = result.get();
    
    UMLModifierFullPrettyPrinter prettyPrinter = new UMLModifierFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(modifier);
    
    result = parser.parseModifier(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(modifier.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
