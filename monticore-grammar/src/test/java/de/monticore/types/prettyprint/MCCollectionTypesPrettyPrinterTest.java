/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCCollectionTypesPrettyPrinterTest {

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
  public void testMCPrimitiveTypeArgument() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCPrimitiveTypeArgument> ast = parser.parse_StringMCPrimitiveTypeArgument("boolean");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCPrimitiveTypeArgument typeArgument = ast.get();
    MCCollectionTypesPrettyPrinter printer = new MCCollectionTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCPrimitiveTypeArgument(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeArgument.deepEquals(ast.get()));
  }

  @Test
  public void testMCBasicTypeArgument() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCBasicTypeArgument> ast = parser.parse_StringMCBasicTypeArgument("a.b.c.d");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCBasicTypeArgument typeArgument = ast.get();
    MCCollectionTypesPrettyPrinter printer = new MCCollectionTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCBasicTypeArgument(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeArgument.deepEquals(ast.get()));
  }

  @Test
  public void testMCListType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCListType> ast = parser.parse_StringMCListType("List<String>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCListType listType = ast.get();
    MCCollectionTypesPrettyPrinter printer = new MCCollectionTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCListType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(listType.deepEquals(ast.get()));
  }

  @Test
  public void testMCOptionalType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCOptionalType> ast = parser.parse_StringMCOptionalType("Optional<de.monticore.ASTSomething>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCOptionalType optionalType = ast.get();
    MCCollectionTypesPrettyPrinter printer = new MCCollectionTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCOptionalType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(optionalType.deepEquals(ast.get()));
  }

  @Test
  public void testMCMapType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCMapType> ast = parser.parse_StringMCMapType("Map<Integer, String>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCMapType mapType = ast.get();
    MCCollectionTypesPrettyPrinter printer = new MCCollectionTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCMapType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(mapType.deepEquals(ast.get()));
  }

  @Test
  public void testMCSetType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCSetType> ast = parser.parse_StringMCSetType("Set<de.monticore.ASTFoo>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCSetType setType = ast.get();
    MCCollectionTypesPrettyPrinter printer = new MCCollectionTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCSetType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(setType.deepEquals(ast.get()));
  }
}
