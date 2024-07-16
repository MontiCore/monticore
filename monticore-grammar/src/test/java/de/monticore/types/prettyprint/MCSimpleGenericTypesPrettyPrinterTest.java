/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import de.monticore.types.mcsimplegenerictypestest.MCSimpleGenericTypesTestMill;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCSimpleGenericTypesPrettyPrinterTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCSimpleGenericTypesTestMill.reset();
    MCSimpleGenericTypesTestMill.init();
  }



  @Test
  public void testMCBasicTypeArgument() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCBasicGenericType> ast = parser.parse_StringMCBasicGenericType("java.util.List<Optional<Set<Integer>>>");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCBasicGenericType typeArgument = ast.get();
    MCSimpleGenericTypesFullPrettyPrinter printer = new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCBasicGenericType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(typeArgument.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCBasicTypeArgument2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCBasicGenericType> ast = parser.parse_StringMCBasicGenericType("some.randomObject<List<Map<Optional<Set<String>>>>>");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCBasicGenericType typeArgument = ast.get();
    MCSimpleGenericTypesFullPrettyPrinter printer = new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCBasicGenericType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(typeArgument.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCCustomTypeArgument() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCCustomTypeArgument> ast = parser.parse_StringMCCustomTypeArgument("some.randomObject<java.util.List<Map<Optional<Set<String>>>>>");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCCustomTypeArgument typeArgument = ast.get();
    MCSimpleGenericTypesFullPrettyPrinter printer = new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCCustomTypeArgument(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(typeArgument.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMultipleMCCustomTypeArgument() throws IOException {
    String type = "java.util.List<socnet.Person<Konto>,List<boolean>>";
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCBasicGenericType> ast = parser.parse_StringMCBasicGenericType(type);
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    Optional<ASTMCBasicGenericType> astBefore = ast;
    MCSimpleGenericTypesFullPrettyPrinter printer = new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());

    Assertions.assertEquals(type, output);

    ast = parser.parse_StringMCBasicGenericType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(astBefore.get().deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
