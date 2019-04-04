package de.monticore.types.printer;

import de.monticore.types.BasicGenericsTypesPrinter;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class BasicGenericsTypesPrinterTest {
  @Test
  public void testPrintType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCBasicTypeArgument> astmcBasicTypeArgument = parser.parse_StringMCBasicTypeArgument("java.util.List");
    Optional<ASTMCPrimitiveTypeArgument> astmcPrimitiveTypeArgument = parser.parse_StringMCPrimitiveTypeArgument("int");
    Optional<ASTMCListType> astmcListType = parser.parse_StringMCListType("List<java.lang.String>");
    Optional<ASTMCSetType> astmcSetType = parser.parse_StringMCSetType("Set<int>");
    Optional<ASTMCOptionalType> astmcOptionalType = parser.parse_StringMCOptionalType("Optional<Character>");
    Optional<ASTMCMapType> astmcMapType = parser.parse_StringMCMapType("Map<String,String>");

    assertFalse(parser.hasErrors());
    assertTrue(astmcBasicTypeArgument.isPresent());
    assertTrue(astmcPrimitiveTypeArgument.isPresent());
    assertTrue(astmcListType.isPresent());
    assertTrue(astmcSetType.isPresent());
    assertTrue(astmcOptionalType.isPresent());
    assertTrue(astmcMapType.isPresent());

    assertEquals("java.util.List", BasicGenericsTypesPrinter.printType(astmcBasicTypeArgument.get()));
    assertEquals("int", BasicGenericsTypesPrinter.printType(astmcPrimitiveTypeArgument.get()));
    assertEquals("List<java.lang.String>",BasicGenericsTypesPrinter.printType(astmcListType.get())); // funktioniert nicht
    assertEquals("Set<int>",BasicGenericsTypesPrinter.printType(astmcSetType.get()));
    assertEquals("Optional<Character>", BasicGenericsTypesPrinter.printType(astmcOptionalType.get()));
    assertEquals("Map<String,String>",BasicGenericsTypesPrinter.printType(astmcMapType.get()));
  }
}
