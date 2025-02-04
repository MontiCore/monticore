/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;
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

public class BasicTypesPrinterTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCBasicTypesTestMill.reset();
    MCBasicTypesTestMill.init();
  }
  
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

    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astmcImportStatement.isPresent());
    Assertions.assertTrue(astmcImportStatement.isPresent());
    Assertions.assertTrue(astmcImportStatement1.isPresent());
    Assertions.assertTrue(astmcQualifiedName.isPresent());
    Assertions.assertTrue(astmcReturnType.isPresent());
    Assertions.assertTrue(astmcVoidType.isPresent());
    Assertions.assertTrue(astmcPrimitiveType.isPresent());
    Assertions.assertTrue(astmcQualifiedType.isPresent());

    Assertions.assertEquals("String", MCBasicTypesMill.prettyPrint(astmcReturnType.get(), true));
    Assertions.assertEquals("void", MCBasicTypesMill.prettyPrint(astmcVoidType.get(), true));
    Assertions.assertEquals("int", MCBasicTypesMill.prettyPrint(astmcPrimitiveType.get(), true));
    Assertions.assertEquals("java.util.List", MCBasicTypesMill.prettyPrint(astmcQualifiedType.get(), true));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
