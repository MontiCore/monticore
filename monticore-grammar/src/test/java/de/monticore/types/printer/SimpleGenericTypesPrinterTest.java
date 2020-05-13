/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class SimpleGenericTypesPrinterTest {
  @Test
  public void testPrintType() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCCustomTypeArgument> astmcCustomTypeArgument = parser.parse_StringMCCustomTypeArgument("List<String>");
    Optional<ASTMCBasicGenericType> astmcBasicGenericType = parser.parse_StringMCBasicGenericType("java.util.List<List<String>>");

    assertFalse(parser.hasErrors());
    assertTrue(astmcBasicGenericType.isPresent());
    assertTrue(astmcCustomTypeArgument.isPresent());

    MCSimpleGenericTypesPrettyPrinter printer = MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter();
    assertEquals("List<String>", printer.prettyprint(astmcCustomTypeArgument.get()));
    assertEquals("java.util.List<List<String>>", printer.prettyprint(astmcBasicGenericType.get()));
  }
}
