/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypestest.MCSimpleGenericTypesTestMill;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class SimpleGenericTypesPrinterTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCSimpleGenericTypesTestMill.reset();
    MCSimpleGenericTypesTestMill.init();
  }
  
  @Test
  public void testPrintType() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCCustomTypeArgument> astmcCustomTypeArgument = parser.parse_StringMCCustomTypeArgument("List<String>");
    Optional<ASTMCBasicGenericType> astmcBasicGenericType = parser.parse_StringMCBasicGenericType("java.util.List<List<String>>");

    assertFalse(parser.hasErrors());
    assertTrue(astmcBasicGenericType.isPresent());
    assertTrue(astmcCustomTypeArgument.isPresent());

    assertEquals("List<String>", MCSimpleGenericTypesMill.prettyPrint(astmcCustomTypeArgument.get(), false));
    assertEquals("java.util.List<List<String>>", MCSimpleGenericTypesMill.prettyPrint(astmcBasicGenericType.get(), false));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
