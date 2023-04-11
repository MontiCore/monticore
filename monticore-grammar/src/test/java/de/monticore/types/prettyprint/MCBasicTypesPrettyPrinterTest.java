/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest.MCBasicTypesTestMill;
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

  @Before
  public void init() {
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
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCQualifiedName(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(qualifiedName.deepEquals(ast.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMcImportStatement() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCImportStatement> ast = parser.parse_StringMCImportStatement("import de.monticore.types.*;");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCImportStatement importStatement = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCImportStatement(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(importStatement.deepEquals(ast.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMcPrimitiveType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCPrimitiveType> ast = parser.parse_StringMCPrimitiveType("long");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCPrimitiveType primitiveType = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCPrimitiveType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(primitiveType.deepEquals(ast.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCVoidType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCVoidType> ast = parser.parse_StringMCVoidType("void");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCVoidType voidType = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCVoidType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(voidType.deepEquals(ast.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCReturnTypeVoid() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCReturnType> ast = parser.parse_StringMCReturnType("void");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCReturnType voidType = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCReturnType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(voidType.deepEquals(ast.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCReturnType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCReturnType> ast = parser.parse_StringMCReturnType("boolean");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCReturnType voidType = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCReturnType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(voidType.deepEquals(ast.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCQualifiedType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCQualifiedType> ast = parser.parse_StringMCQualifiedType("a.b.c.d");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCQualifiedType qualifiedReferenceType = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCQualifiedType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(qualifiedReferenceType.deepEquals(ast.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testMCPackageDeclaration() throws IOException {
    MCBasicTypesTestParser parser = MCBasicTypesTestMill.parser();
    Optional<ASTMCPackageDeclaration> ast = parser.parse_StringMCPackageDeclaration("package a.b.c.d;");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCPackageDeclaration packageDeclaration = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCPackageDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(packageDeclaration.deepEquals(ast.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void primitivesTest(){
    Class foo = boolean.class;
    MCBasicTypesFullPrettyPrinter prettyprinter = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());

    String[] primitives = new String[]{"boolean", "byte", "char", "short", "int", "long",
        "float", "double"};
    try {
      for (String primitive : primitives) {
        prettyprinter.getPrinter().clearBuffer();
        MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
        // .parseType(primitive);

        Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCPrimitiveType(primitive);
        assertTrue(type.isPresent());
        assertEquals(primitive,prettyprinter.prettyprint(type.get()));
        assertTrue(type.get() instanceof ASTMCPrimitiveType);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void simpleReferenceTest(){
    MCBasicTypesFullPrettyPrinter prettyprinter = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String simpleReference = "de.monticore.types.prettyprint";
    try{
      MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
      Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCQualifiedType(simpleReference);
      assertTrue(type.isPresent());
      assertEquals(simpleReference,prettyprinter.prettyprint(type.get()));
      assertTrue(type.get() instanceof ASTMCQualifiedType);
    }catch(IOException e){
      e.printStackTrace();
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
