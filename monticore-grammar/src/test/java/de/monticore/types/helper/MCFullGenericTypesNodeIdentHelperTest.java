/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCFullGenericTypesNodeIdentHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCTypeParameters;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardType;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCFullGenericTypesNodeIdentHelperTest {
  @Test
  public void testGetIdent() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCTypeParameters> astmcTypeParameters = parser.parse_StringMCTypeParameters("<a extends b&c&d, e extends f&g>");
    Optional<ASTMCTypeParameters> astmcTypeParameters1 = parser.parse_StringMCTypeParameters("<a extends b&c&d>");

    assertFalse(parser.hasErrors());
    assertTrue(astmcTypeParameters.isPresent());
    assertTrue(astmcTypeParameters1.isPresent());

    MCFullGenericTypesNodeIdentHelper helper = new MCFullGenericTypesNodeIdentHelper();
    assertEquals("@a..!MCTypeParameters", helper.getIdent(astmcTypeParameters.get()));
    assertEquals("@a!MCTypeParameters", helper.getIdent(astmcTypeParameters1.get()));
  }
}
