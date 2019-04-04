package de.monticore.types.printer;

import de.monticore.types.BasicTypesPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class BasicTypesPrinterTest {

  @Test
  public void testPrintType() throws IOException{
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCImportStatement> astmcImportStatement = parser.parse_StringMCImportStatement("import java.util.List;");
    Optional<ASTMCImportStatement> astmcImportStatement1 = parser.parse_StringMCImportStatement("import a.b.c.d.*;");
    Optional<ASTMCQualifiedName> astmcQualifiedName = parser.parse_StringMCQualifiedName("java.util.List");
    Optional<ASTMCReturnType> astmcReturnType = parser.parse_StringMCReturnType("String");
    Optional<ASTMCVoidType> astmcVoidType = parser.parse_StringMCVoidType("void");
    Optional<ASTMCPrimitiveType> astmcPrimitiveType = parser.parse_StringMCPrimitiveType("int");
    Optional<ASTMCQualifiedType> astmcQualifiedType = parser.parse_StringMCQualifiedType("java.util.List");

    assertFalse(parser.hasErrors());
    assertTrue(astmcImportStatement.isPresent());
    assertTrue(astmcImportStatement.isPresent());
    assertTrue(astmcImportStatement1.isPresent());
    assertTrue(astmcQualifiedName.isPresent());
    assertTrue(astmcReturnType.isPresent());
    assertTrue(astmcVoidType.isPresent());
    assertTrue(astmcPrimitiveType.isPresent());
    assertTrue(astmcQualifiedType.isPresent());

    assertEquals("String",BasicTypesPrinter.printReturnType(astmcReturnType.get()));
    assertEquals("void", BasicTypesPrinter.printVoidType(astmcVoidType.get()));
    assertEquals("int",BasicTypesPrinter.printType(astmcPrimitiveType.get()));
    assertEquals("java.util.List", BasicTypesPrinter.printType(astmcQualifiedType.get()));
  }
}
