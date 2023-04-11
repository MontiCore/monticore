/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class FullGenericTypesPrinterTest {

  @Before
  public void init() {
    LogStub.init();
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

    assertFalse(parser.hasErrors());
    assertTrue(astmcWildcardTypeArgument.isPresent());
    assertTrue(astmcWildcardTypeArgument1.isPresent());
    assertTrue(astmcWildcardTypeArgument2.isPresent());
    assertTrue(astmcMultipleGenericType.isPresent());
//    assertTrue(astmcTypeVariableDeclaration.isPresent());
//    assertTrue(astmcTypeParameters.isPresent());

    assertEquals("?", MCFullGenericTypesMill.prettyPrint(astmcWildcardTypeArgument.get(), true));
    assertEquals("? extends List", MCFullGenericTypesMill.prettyPrint(astmcWildcardTypeArgument1.get(), true));
    assertEquals("? super Stream", MCFullGenericTypesMill.prettyPrint(astmcWildcardTypeArgument2.get(), true));
    assertEquals("java.util.List<List<String>>.c.d<e,f,g,h>", MCFullGenericTypesMill.prettyPrint(astmcMultipleGenericType.get(), true));
//    assertEquals("<a extends b &c &d, e extends f &g>", FullGenericTypesPrinter.printType(astmcTypeParameters.get()));
//    assertEquals("a extends b &c &d", FullGenericTypesPrinter.printType(astmcTypeVariableDeclaration.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
