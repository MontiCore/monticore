/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesFullPrettyPrinter;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class CollectionTypesPrinterTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
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

    assertEquals("java.util.List", MCCollectionTypesMill.prettyPrint(astmcBasicTypeArgument.get(), true));
    assertEquals("int", MCCollectionTypesMill.prettyPrint(astmcPrimitiveTypeArgument.get(), true));
    assertEquals("List<java.lang.String>", MCCollectionTypesMill.prettyPrint(astmcListType.get(), true));
    assertEquals("Set<int>", MCCollectionTypesMill.prettyPrint(astmcSetType.get(), true));
    assertEquals("Optional<Character>", MCCollectionTypesMill.prettyPrint(astmcOptionalType.get(), true));
    assertEquals("Map<String,String>", MCCollectionTypesMill.prettyPrint(astmcMapType.get(), true));

    assertTrue(Log.getFindings().isEmpty());
  }
}
