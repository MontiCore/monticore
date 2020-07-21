/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class CollectionTypesPrinterTest {
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

    MCCollectionTypesPrettyPrinter printer = MCCollectionTypesMill.mcCollectionTypesPrettyPrinter();
    assertEquals("java.util.List", printer.prettyprint(astmcBasicTypeArgument.get()));
    assertEquals("int", printer.prettyprint(astmcPrimitiveTypeArgument.get()));
    assertEquals("List<java.lang.String>",printer.prettyprint(astmcListType.get())); // funktioniert nicht
    assertEquals("Set<int>",printer.prettyprint(astmcSetType.get()));
    assertEquals("Optional<Character>", printer.prettyprint(astmcOptionalType.get()));
    assertEquals("Map<String,String>",printer.prettyprint(astmcMapType.get()));
  }
}
