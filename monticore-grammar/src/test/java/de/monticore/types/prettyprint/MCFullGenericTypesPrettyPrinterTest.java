/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCFullGenericTypesPrettyPrinterTest {

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
  public void testMCWildcardTypeArgumentExtends() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCWildcardTypeArgument> ast = parser.parse_StringMCWildcardTypeArgument("? extends java.util.List");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCWildcardTypeArgument wildcardType = ast.get();
    MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCWildcardTypeArgument(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(wildcardType.deepEquals(ast.get()));
  }

  @Test
  public void testMCWildcardTypeArgumentSuper() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCWildcardTypeArgument> ast = parser.parse_StringMCWildcardTypeArgument("? super de.monticore.ASTNode");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCWildcardTypeArgument wildcardType = ast.get();
    MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCWildcardTypeArgument(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(wildcardType.deepEquals(ast.get()));
  }

  @Test
  public void testMCMultipleGenericType() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCMultipleGenericType> ast = parser.parse_StringMCMultipleGenericType("java.util.List<Integer>.some.util.Set<String>.Opt<List<String>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCMultipleGenericType complexReferenceType = ast.get();
    MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCMultipleGenericType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(complexReferenceType.deepEquals(ast.get()));
  }

  @Test
  public void testMCArrayType() throws IOException {
    //have to use ASTMCType because of left recursion in ASTMCArrayType there is no parse Method
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCType> ast = parser.parse_StringMCType("String[][]");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCType type = ast.get();
    MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(type.deepEquals(ast.get()));
  }
}
