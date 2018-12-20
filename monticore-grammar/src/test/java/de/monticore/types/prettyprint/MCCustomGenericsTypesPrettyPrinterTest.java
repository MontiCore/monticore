package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCBasicGenericsReferenceType;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mccustomgenerictypestest._parser.MCCustomGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCCustomGenericsTypesPrettyPrinterTest {

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
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCBasicGenericsReferenceType> ast = parser.parse_StringMCBasicGenericsReferenceType("java.util.List<Optional<Set<Integer>>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCBasicGenericsReferenceType typeArgument = ast.get();
    MCCustomGenericsTypesPrettyPrinter printer = new MCCustomGenericsTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCBasicGenericsReferenceType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeArgument.deepEquals(ast.get()));
  }

  @Test
  public void testMCBasicTypeArgument2() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCBasicGenericsReferenceType> ast = parser.parse_StringMCBasicGenericsReferenceType("some.randomObject<List<Map<Optional<Set<String>>>>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCBasicGenericsReferenceType typeArgument = ast.get();
    MCCustomGenericsTypesPrettyPrinter printer = new MCCustomGenericsTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCBasicGenericsReferenceType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeArgument.deepEquals(ast.get()));
  }

  @Test
  public void testMCCustomTypeArgument() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCCustomTypeArgument> ast = parser.parse_StringMCCustomTypeArgument("some.randomObject<java.util.List<Map<Optional<Set<String>>>>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCCustomTypeArgument typeArgument = ast.get();
    MCCustomGenericsTypesPrettyPrinter printer = new MCCustomGenericsTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCCustomTypeArgument(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeArgument.deepEquals(ast.get()));
  }
}
