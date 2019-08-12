/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCBasicTypesPrettyPrinterTest {

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
  public void testMCQualifiedName() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCQualifiedName> ast = parser.parse_StringMCQualifiedName("Name1.Name2.Name3");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCQualifiedName qualifiedName = ast.get();
    MCBasicTypesPrettyPrinter printer = new MCBasicTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCQualifiedName(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(qualifiedName.deepEquals(ast.get()));
  }

  @Test
  public void testMcImportStatement() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCImportStatement> ast = parser.parse_StringMCImportStatement("import de.monticore.types.*;");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCImportStatement importStatement = ast.get();
    MCBasicTypesPrettyPrinter printer = new MCBasicTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCImportStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(importStatement.deepEquals(ast.get()));
  }

  @Test
  public void testMcPrimitiveType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCPrimitiveType> ast = parser.parse_StringMCPrimitiveType("long");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCPrimitiveType primitiveType = ast.get();
    MCBasicTypesPrettyPrinter printer = new MCBasicTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCPrimitiveType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(primitiveType.deepEquals(ast.get()));
  }

  @Test
  public void testMCVoidType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCVoidType> ast = parser.parse_StringMCVoidType("void");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCVoidType voidType = ast.get();
    MCBasicTypesPrettyPrinter printer = new MCBasicTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCVoidType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(voidType.deepEquals(ast.get()));
  }

  @Test
  public void testMCReturnTypeVoid() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCReturnType> ast = parser.parse_StringMCReturnType("void");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCReturnType voidType = ast.get();
    MCBasicTypesPrettyPrinter printer = new MCBasicTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCReturnType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(voidType.deepEquals(ast.get()));
  }

  @Test
  public void testMCReturnType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCReturnType> ast = parser.parse_StringMCReturnType("boolean");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCReturnType voidType = ast.get();
    MCBasicTypesPrettyPrinter printer = new MCBasicTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCReturnType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(voidType.deepEquals(ast.get()));
  }

  @Test
  public void testMCQualifiedType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCQualifiedType> ast = parser.parse_StringMCQualifiedType("a.b.c.d");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCQualifiedType qualifiedReferenceType = ast.get();
    MCBasicTypesPrettyPrinter printer = new MCBasicTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCQualifiedType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(qualifiedReferenceType.deepEquals(ast.get()));
  }

  @Test
  public void primitivesTest(){
    Class foo = boolean.class;
    MCBasicTypesPrettyPrinter prettyprinter = new MCBasicTypesPrettyPrinter(new IndentPrinter());

    String[] primitives = new String[]{"boolean", "byte", "char", "short", "int", "long",
        "float", "double"};
    try {
      for (String primitive : primitives) {
        prettyprinter.getPrinter().clearBuffer();
        MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
        // .parseType(primitive);

        Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCPrimitiveType(primitive);
        type.get().accept(prettyprinter);
        assertTrue(type.isPresent());
        assertEquals(primitive,prettyprinter.getPrinter().getContent());
        assertTrue(type.get() instanceof ASTMCPrimitiveType);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void simpleReferenceTest(){
    MCBasicTypesPrettyPrinter prettyprinter = new MCBasicTypesPrettyPrinter(new IndentPrinter());
    String simpleReference = "de.monticore.types.prettyprint";
    try{
      MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
      Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCQualifiedType(simpleReference);
      type.get().accept(prettyprinter);
      assertTrue(type.isPresent());
      assertEquals(simpleReference,prettyprinter.getPrinter().getContent());
      assertTrue(type.get() instanceof ASTMCQualifiedType);
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
