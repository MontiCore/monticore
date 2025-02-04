/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypestest.MCFullGenericTypesTestMill;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class FullGenericTypesPrinterTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCFullGenericTypesTestMill.reset();
    MCFullGenericTypesTestMill.init();
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

    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astmcWildcardTypeArgument.isPresent());
    Assertions.assertTrue(astmcWildcardTypeArgument1.isPresent());
    Assertions.assertTrue(astmcWildcardTypeArgument2.isPresent());
    Assertions.assertTrue(astmcMultipleGenericType.isPresent());
//    assertTrue(astmcTypeVariableDeclaration.isPresent());
//    assertTrue(astmcTypeParameters.isPresent());

    Assertions.assertEquals("?", MCFullGenericTypesMill.prettyPrint(astmcWildcardTypeArgument.get(), true));
    Assertions.assertEquals("? extends List", MCFullGenericTypesMill.prettyPrint(astmcWildcardTypeArgument1.get(), true));
    Assertions.assertEquals("? super Stream", MCFullGenericTypesMill.prettyPrint(astmcWildcardTypeArgument2.get(), true));
    Assertions.assertEquals("java.util.List<List<String>>.c.d<e,f,g,h>", MCFullGenericTypesMill.prettyPrint(astmcMultipleGenericType.get(), true));
//    assertEquals("<a extends b &c &d, e extends f &g>", FullGenericTypesPrinter.printType(astmcTypeParameters.get()));
//    assertEquals("a extends b &c &d", FullGenericTypesPrinter.printType(astmcTypeVariableDeclaration.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
