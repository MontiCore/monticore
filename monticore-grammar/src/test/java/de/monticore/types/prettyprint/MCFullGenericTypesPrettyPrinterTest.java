/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypestest.MCFullGenericTypesTestMill;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.monticore.types.mcfullgenerictypes._prettyprint.MCFullGenericTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCFullGenericTypesPrettyPrinterTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCFullGenericTypesTestMill.reset();
    MCFullGenericTypesTestMill.init();
  }

  @Test
  public void testMCWildcardTypeArgumentExtends() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCWildcardTypeArgument> ast = parser.parse_StringMCWildcardTypeArgument("? extends java.util.List");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCWildcardTypeArgument wildcardType = ast.get();
    MCFullGenericTypesFullPrettyPrinter printer = new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCWildcardTypeArgument(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(wildcardType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCWildcardTypeArgumentSuper() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCWildcardTypeArgument> ast = parser.parse_StringMCWildcardTypeArgument("? super de.monticore.ASTNode");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCWildcardTypeArgument wildcardType = ast.get();
    MCFullGenericTypesFullPrettyPrinter printer = new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCWildcardTypeArgument(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(wildcardType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMultipleGenericType() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCMultipleGenericType> ast = parser.parse_StringMCMultipleGenericType("java.util.List<Integer>.some.util.Set<String>.Opt<List<String>>");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCMultipleGenericType complexReferenceType = ast.get();
    MCFullGenericTypesFullPrettyPrinter printer = new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCMultipleGenericType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(complexReferenceType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
