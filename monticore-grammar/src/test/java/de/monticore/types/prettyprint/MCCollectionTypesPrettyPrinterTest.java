/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypestest.MCCollectionTypesTestMill;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCCollectionTypesPrettyPrinterTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCCollectionTypesTestMill.reset();
    MCCollectionTypesTestMill.init();
  }
  @Test
  public void testMCPrimitiveTypeArgument() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCPrimitiveTypeArgument> ast = parser.parse_StringMCPrimitiveTypeArgument("boolean");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCPrimitiveTypeArgument typeArgument = ast.get();
    MCCollectionTypesFullPrettyPrinter printer = new MCCollectionTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCPrimitiveTypeArgument(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(typeArgument.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCBasicTypeArgument() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCBasicTypeArgument> ast = parser.parse_StringMCBasicTypeArgument("a.b.c.d");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCBasicTypeArgument typeArgument = ast.get();
    MCCollectionTypesFullPrettyPrinter printer = new MCCollectionTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCBasicTypeArgument(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(typeArgument.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCListType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCListType> ast = parser.parse_StringMCListType("List<String>");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCListType listType = ast.get();
    MCCollectionTypesFullPrettyPrinter printer = new MCCollectionTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCListType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(listType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCOptionalType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCOptionalType> ast = parser.parse_StringMCOptionalType("Optional<de.monticore.ASTSomething>");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCOptionalType optionalType = ast.get();
    MCCollectionTypesFullPrettyPrinter printer = new MCCollectionTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCOptionalType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(optionalType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCMapType> ast = parser.parse_StringMCMapType("Map<Integer, String>");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCMapType mapType = ast.get();
    MCCollectionTypesFullPrettyPrinter printer = new MCCollectionTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCMapType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(mapType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCSetType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCSetType> ast = parser.parse_StringMCSetType("Set<de.monticore.ASTFoo>");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCSetType setType = ast.get();
    MCCollectionTypesFullPrettyPrinter printer = new MCCollectionTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCSetType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(setType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
