/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
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

    MCBasicTypesPrettyPrinter printer = MCBasicTypesMill.mcBasicTypesPrettyPrinter();
    assertEquals("String", printer.prettyprint(astmcReturnType.get()));
    assertEquals("void", printer.prettyprint(astmcVoidType.get()));
    assertEquals("int", printer.prettyprint(astmcPrimitiveType.get()));
    assertEquals("java.util.List", printer.prettyprint(astmcQualifiedType.get()));
  }
}
