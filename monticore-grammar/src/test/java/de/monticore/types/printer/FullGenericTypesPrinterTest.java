/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.FullGenericTypesPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes._ast.*;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class FullGenericTypesPrinterTest {

  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testPrintType() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCWildcardType> astmcWildcardType = parser.parse_StringMCWildcardType("?");
    Optional<ASTMCWildcardType> astmcWildcardType1 = parser.parse_StringMCWildcardType("? extends List");
    Optional<ASTMCWildcardType> astmcWildcardType2 = parser.parse_StringMCWildcardType("? super Stream");
    Optional<ASTMCMultipleGenericType> astmcMultipleGenericType = parser.parse_StringMCMultipleGenericType("java.util.List<List<String>>.c.d<e,f,g,h>");
    Optional<ASTMCTypeVariableDeclaration> astmcTypeVariableDeclaration = parser.parse_StringMCTypeVariableDeclaration("a extends b&c&d");
    Optional<ASTMCTypeParameters> astmcTypeParameters = parser.parse_StringMCTypeParameters("<a extends b&c&d, e extends f&g>");
    Optional<ASTMCType> astmcArrayType = parser.parse_StringMCType("String[][][]");

    assertFalse(parser.hasErrors());
    assertTrue(astmcWildcardType.isPresent());
    assertTrue(astmcWildcardType1.isPresent());
    assertTrue(astmcWildcardType2.isPresent());
    assertTrue(astmcMultipleGenericType.isPresent());
    assertTrue(astmcTypeVariableDeclaration.isPresent());
    assertTrue(astmcTypeParameters.isPresent());
    assertTrue(astmcArrayType.isPresent());
    assertTrue(astmcArrayType.get() instanceof ASTMCArrayType);

    assertEquals("?", FullGenericTypesPrinter.printType(astmcWildcardType.get()));
    assertEquals("? extends List", FullGenericTypesPrinter.printType(astmcWildcardType1.get()));
    assertEquals("? super Stream", FullGenericTypesPrinter.printType(astmcWildcardType2.get()));
    assertEquals("java.util.List<List<String>>.c.d<e,f,g,h>", FullGenericTypesPrinter.printType(astmcMultipleGenericType.get()));
    assertEquals("<a extends b &c &d, e extends f &g>", FullGenericTypesPrinter.printType(astmcTypeParameters.get()));
    assertEquals("a extends b &c &d", FullGenericTypesPrinter.printType(astmcTypeVariableDeclaration.get()));
    assertEquals("String[][][]", FullGenericTypesPrinter.printType(astmcArrayType.get()));
  }
}
