package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcgenerictypes._ast.ASTMCComplexType;
import de.monticore.types.mcgenerictypes._ast.ASTMCTypeParameters;
import de.monticore.types.mcgenerictypes._ast.ASTMCTypeVariableDeclaration;
import de.monticore.types.mcgenerictypes._ast.ASTMCWildcardType;
import de.monticore.types.mcgenerictypestest._parser.MCGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCGenericTypesPrettyPrinterTest {

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
  public void testMCWildcardTypeExtends() throws IOException {
    MCGenericTypesTestParser parser = new MCGenericTypesTestParser();
    Optional<ASTMCWildcardType> ast = parser.parse_StringMCWildcardType("? extends java.util.List");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCWildcardType wildcardType = ast.get();
    MCGenericTypesPrettyPrinter printer = new MCGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCWildcardType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(wildcardType.deepEquals(ast.get()));
  }

  @Test
  public void testMCWildcardTypeSuper() throws IOException {
    MCGenericTypesTestParser parser = new MCGenericTypesTestParser();
    Optional<ASTMCWildcardType> ast = parser.parse_StringMCWildcardType("? super de.monticore.ASTNode");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCWildcardType wildcardType = ast.get();
    MCGenericTypesPrettyPrinter printer = new MCGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCWildcardType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(wildcardType.deepEquals(ast.get()));
  }

  @Test
  public void testMCComplexType() throws IOException {
    MCGenericTypesTestParser parser = new MCGenericTypesTestParser();
    Optional<ASTMCComplexType> ast = parser.parse_StringMCComplexType("java.util.List<Integer>.some.util.Set<String>.Opt<List<String>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCComplexType complexReferenceType = ast.get();
    MCGenericTypesPrettyPrinter printer = new MCGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCComplexType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(complexReferenceType.deepEquals(ast.get()));
  }

  @Test
  public void testMCArrayType() throws IOException {
    //have to use ASTMCType because of left recursion in ASTMCArrayType there is no parse Method
    MCGenericTypesTestParser parser = new MCGenericTypesTestParser();
    Optional<ASTMCType> ast = parser.parse_StringMCType("String[][]");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCType type = ast.get();
    MCGenericTypesPrettyPrinter printer = new MCGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(type.deepEquals(ast.get()));
  }

  @Test
  public void testMCTypeParameters() throws IOException {
    MCGenericTypesTestParser parser = new MCGenericTypesTestParser();
    Optional<ASTMCTypeParameters> ast = parser.parse_StringMCTypeParameters("<A, B extends List<A>.A<Name>, C extends Set<F>.Opt<H> &  Map<G>.List<T>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCTypeParameters typeParameters = ast.get();
    MCGenericTypesPrettyPrinter printer = new MCGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCTypeParameters(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeParameters.deepEquals(ast.get()));
  }

  @Test
  public void testMCTypeVariableDeclaration() throws IOException {
    MCGenericTypesTestParser parser = new MCGenericTypesTestParser();
    Optional<ASTMCTypeVariableDeclaration> ast = parser.parse_StringMCTypeVariableDeclaration(" C extends Set<F>.Opt<H> &  Map<G>.List<T> & Foo<Bar>.A<B>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCTypeVariableDeclaration typeVariableDeclaration = ast.get();
    MCGenericTypesPrettyPrinter printer = new MCGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCTypeVariableDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeVariableDeclaration.deepEquals(ast.get()));
  }
}
