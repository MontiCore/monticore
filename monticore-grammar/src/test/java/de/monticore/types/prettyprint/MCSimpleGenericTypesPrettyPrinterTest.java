package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCSimpleGenericTypesPrettyPrinterTest {

  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    Log.getFindings().clear();
  }


  @Test
  public void testMCBasicTypeArgument() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCBasicGenericType> ast = parser.parse_StringMCBasicGenericType("java.util.List<Optional<Set<Integer>>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCBasicGenericType typeArgument = ast.get();
    MCSimpleGenericTypesPrettyPrinter printer = new MCSimpleGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCBasicGenericType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeArgument.deepEquals(ast.get()));
  }

  @Test
  public void testMCBasicTypeArgument2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCBasicGenericType> ast = parser.parse_StringMCBasicGenericType("some.randomObject<List<Map<Optional<Set<String>>>>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCBasicGenericType typeArgument = ast.get();
    MCSimpleGenericTypesPrettyPrinter printer = new MCSimpleGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCBasicGenericType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeArgument.deepEquals(ast.get()));
  }

  @Test
  public void testMCCustomTypeArgument() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCCustomTypeArgument> ast = parser.parse_StringMCCustomTypeArgument("some.randomObject<java.util.List<Map<Optional<Set<String>>>>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCCustomTypeArgument typeArgument = ast.get();
    MCSimpleGenericTypesPrettyPrinter printer = new MCSimpleGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCCustomTypeArgument(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeArgument.deepEquals(ast.get()));
  }
}
