/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
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
    Optional<ASTMCWildcardTypeArgument> astmcWildcardTypeArgument = parser.parse_StringMCWildcardTypeArgument("?");
    Optional<ASTMCWildcardTypeArgument> astmcWildcardTypeArgument1 = parser.parse_StringMCWildcardTypeArgument("? extends List");
    Optional<ASTMCWildcardTypeArgument> astmcWildcardTypeArgument2 = parser.parse_StringMCWildcardTypeArgument("? super Stream");
    Optional<ASTMCMultipleGenericType> astmcMultipleGenericType = parser.parse_StringMCMultipleGenericType("java.util.List<List<String>>.c.d<e,f,g,h>");
//    Optional<ASTMCTypeVariableDeclaration> astmcTypeVariableDeclaration = parser.parse_StringMCTypeVariableDeclaration("a extends b&c&d");
//    Optional<ASTMCTypeParameters> astmcTypeParameters = parser.parse_StringMCTypeParameters("<a extends b&c&d, e extends f&g>");
    Optional<ASTMCType> astmcArrayType = parser.parse_StringMCType("String[][][]");

    assertFalse(parser.hasErrors());
    assertTrue(astmcWildcardTypeArgument.isPresent());
    assertTrue(astmcWildcardTypeArgument1.isPresent());
    assertTrue(astmcWildcardTypeArgument2.isPresent());
    assertTrue(astmcMultipleGenericType.isPresent());
//    assertTrue(astmcTypeVariableDeclaration.isPresent());
//    assertTrue(astmcTypeParameters.isPresent());
    assertTrue(astmcArrayType.isPresent());
    assertTrue(astmcArrayType.get() instanceof ASTMCArrayType);

    MCFullGenericTypesPrettyPrinter printer = MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter();
    assertEquals("?", printer.prettyprint(astmcWildcardTypeArgument.get()));
    assertEquals("? extends List", printer.prettyprint(astmcWildcardTypeArgument1.get()));
    assertEquals("? super Stream", printer.prettyprint(astmcWildcardTypeArgument2.get()));
    assertEquals("java.util.List<List<String>>.c.d<e,f,g,h>", printer.prettyprint(astmcMultipleGenericType.get()));
//    assertEquals("<a extends b &c &d, e extends f &g>", FullGenericTypesPrinter.printType(astmcTypeParameters.get()));
//    assertEquals("a extends b &c &d", FullGenericTypesPrinter.printType(astmcTypeVariableDeclaration.get()));
    assertEquals("String[][][]", printer.prettyprint(astmcArrayType.get()));
  }
}
