/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest.MCBasicTypesTestMill;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCBasicTypesPrettyPrinterTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCBasicTypesTestMill.reset();
    MCBasicTypesTestMill.init();
  }



  @Test
  public void testMCQualifiedName() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCQualifiedName> ast = parser.parse_StringMCQualifiedName("Name1.Name2.Name3");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCQualifiedName qualifiedName = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCQualifiedName(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(qualifiedName.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMcImportStatement() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCImportStatement> ast = parser.parse_StringMCImportStatement("import de.monticore.types.*;");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCImportStatement importStatement = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCImportStatement(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(importStatement.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMcPrimitiveType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCPrimitiveType> ast = parser.parse_StringMCPrimitiveType("long");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCPrimitiveType primitiveType = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCPrimitiveType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(primitiveType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCVoidType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCVoidType> ast = parser.parse_StringMCVoidType("void");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCVoidType voidType = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCVoidType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(voidType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCReturnTypeVoid() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCReturnType> ast = parser.parse_StringMCReturnType("void");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCReturnType voidType = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCReturnType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(voidType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCReturnType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCReturnType> ast = parser.parse_StringMCReturnType("boolean");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCReturnType voidType = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCReturnType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(voidType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCQualifiedType() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCQualifiedType> ast = parser.parse_StringMCQualifiedType("a.b.c.d");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCQualifiedType qualifiedReferenceType = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCQualifiedType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(qualifiedReferenceType.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testMCPackageDeclaration() throws IOException {
    MCBasicTypesTestParser parser = MCBasicTypesTestMill.parser();
    Optional<ASTMCPackageDeclaration> ast = parser.parse_StringMCPackageDeclaration("package a.b.c.d;");
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    ASTMCPackageDeclaration packageDeclaration = ast.get();
    MCBasicTypesFullPrettyPrinter printer = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCPackageDeclaration(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(packageDeclaration.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
        Assertions.assertTrue(type.isPresent());
        Assertions.assertEquals(primitive, prettyprinter.prettyprint(type.get()));
        Assertions.assertTrue(type.get() instanceof ASTMCPrimitiveType);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void simpleReferenceTest(){
    MCBasicTypesFullPrettyPrinter prettyprinter = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    String simpleReference = "de.monticore.types.prettyprint";
    try{
      MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
      Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCQualifiedType(simpleReference);
      Assertions.assertTrue(type.isPresent());
      Assertions.assertEquals(simpleReference, prettyprinter.prettyprint(type.get()));
      Assertions.assertTrue(type.get() instanceof ASTMCQualifiedType);
    }catch(IOException e){
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
