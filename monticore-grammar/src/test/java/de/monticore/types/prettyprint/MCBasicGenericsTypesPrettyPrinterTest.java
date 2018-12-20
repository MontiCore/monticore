package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasicgenericstypes._ast.*;
import de.monticore.types.mcbasicgenericstypestest._parser.MCBasicGenericsTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCBasicGenericsTypesPrettyPrinterTest {

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
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCBasicTypeArgument> ast = parser.parse_StringMCBasicTypeArgument("a.b.c.d");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCBasicTypeArgument typeArgument = ast.get();
    MCBasicGenericsTypesPrettyPrinter printer = new MCBasicGenericsTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCBasicTypeArgument(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeArgument.deepEquals(ast.get()));
  }

  @Test
  public void testMCListType() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCListType> ast = parser.parse_StringMCListType("List<String>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCListType listType = ast.get();
    MCBasicGenericsTypesPrettyPrinter printer = new MCBasicGenericsTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCListType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(listType.deepEquals(ast.get()));
  }

  @Test
  public void testMCOptionalType() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCOptionalType> ast = parser.parse_StringMCOptionalType("Optional<de.monticore.ASTSomething>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCOptionalType optionalType = ast.get();
    MCBasicGenericsTypesPrettyPrinter printer = new MCBasicGenericsTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCOptionalType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(optionalType.deepEquals(ast.get()));
  }

  @Test
  public void testMCMapType() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCMapType> ast = parser.parse_StringMCMapType("Map<Integer, String>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCMapType mapType = ast.get();
    MCBasicGenericsTypesPrettyPrinter printer = new MCBasicGenericsTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCMapType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(mapType.deepEquals(ast.get()));
  }

  @Test
  public void testMCSetType() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCSetType> ast = parser.parse_StringMCSetType("Set<de.monticore.ASTFoo>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCSetType setType = ast.get();
    MCBasicGenericsTypesPrettyPrinter printer = new MCBasicGenericsTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCSetType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(setType.deepEquals(ast.get()));
  }
}
