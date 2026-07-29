/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.cardinality._prettyprint.CardinalityFullPrettyPrinter;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testcardinality.TestCardinalityMill;
import de.monticore.testcardinality._parser.TestCardinalityParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestCardinalityMill.class)
public class CardinalityPrettyPrinterTest {

  @Test
  public void testCardinality1() throws IOException {
    TestCardinalityParser parser = TestCardinalityMill.parser();
    Optional<ASTCardinality> result = parser.parseCardinality(new StringReader("[*]"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCardinality cardinality = result.get();
    
    CardinalityFullPrettyPrinter prettyPrinter = new CardinalityFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(cardinality);
    
    result = parser.parseCardinality(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cardinality.deepEquals(result.get()));
  }
  
  @Test
  public void testCardinality2() throws IOException {
    TestCardinalityParser parser = TestCardinalityMill.parser();
    Optional<ASTCardinality> result = parser.parseCardinality(new StringReader("[5..9]"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCardinality cardinality = result.get();
    
    CardinalityFullPrettyPrinter prettyPrinter = new CardinalityFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(cardinality);
    
    result = parser.parseCardinality(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cardinality.deepEquals(result.get()));
  }
  
  @Test
  public void testCardinality3() throws IOException {
    TestCardinalityParser parser = TestCardinalityMill.parser();
    Optional<ASTCardinality> result = parser.parseCardinality(new StringReader("[6..*]"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCardinality cardinality = result.get();
    
    CardinalityFullPrettyPrinter prettyPrinter = new CardinalityFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(cardinality);
    
    result = parser.parseCardinality(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cardinality.deepEquals(result.get()));
  }
}
