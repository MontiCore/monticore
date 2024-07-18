/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.monticore.testumlmodifier.TestUMLModifierMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.monticore.umlmodifier._prettyprint.UMLModifierFullPrettyPrinter;
import de.monticore.testumlmodifier._parser.TestUMLModifierParser;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

public class UMLModifierPrettyPrinterTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestUMLModifierMill.reset();
    TestUMLModifierMill.init();
  }

  @Test
  public void testModifierWord() throws IOException {
    TestUMLModifierParser parser = new TestUMLModifierParser();
    Optional<ASTModifier> result = parser.parseModifier(new StringReader("private"));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTModifier modifier = result.get();
    
    UMLModifierFullPrettyPrinter prettyPrinter = new UMLModifierFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(modifier);
    
    result = parser.parseModifier(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(modifier.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testModifierSymbol() throws IOException {
    TestUMLModifierParser parser = new TestUMLModifierParser();
    Optional<ASTModifier> result = parser.parseModifier(new StringReader("-"));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTModifier modifier = result.get();
    
    UMLModifierFullPrettyPrinter prettyPrinter = new UMLModifierFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(modifier);
    
    result = parser.parseModifier(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(modifier.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
