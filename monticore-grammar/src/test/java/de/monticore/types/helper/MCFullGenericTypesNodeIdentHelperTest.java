// (c) https://github.com/MontiCore/monticore

package de.monticore.types.helper;

import de.monticore.types.MCFullGenericTypesNodeIdentHelper;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCFullGenericTypesNodeIdentHelperTest {

  @Test
  public void testGetIdent() throws IOException {
    MCFullGenericTypesTestParser p = new MCFullGenericTypesTestParser();
    MCFullGenericTypesNodeIdentHelper identHelper = new MCFullGenericTypesNodeIdentHelper();
    Optional<ASTMCMultipleGenericType> astmcMultipleGenericType = p.parse_StringMCMultipleGenericType("a.b.D<C>.d.E<int>");

    assertTrue(astmcMultipleGenericType.isPresent());

    assertEquals("@a.b.D.d.E!MCMultipleGenericType", identHelper.getIdent(astmcMultipleGenericType.get()));
  }

}
