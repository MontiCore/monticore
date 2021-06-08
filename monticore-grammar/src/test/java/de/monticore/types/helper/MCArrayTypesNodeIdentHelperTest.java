/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.helper;

import de.monticore.types.MCArrayTypesNodeIdentHelper;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypestest._parser.MCArrayTypesTestParser;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCArrayTypesNodeIdentHelperTest {

  @Test
  public void testGetIdent() throws IOException {
    MCArrayTypesTestParser p = new MCArrayTypesTestParser();
    MCArrayTypesNodeIdentHelper identHelper = new MCArrayTypesNodeIdentHelper();

    Optional<ASTMCType> astmcArrayType = p.parse_StringMCType("A[]");

    assertTrue(astmcArrayType.isPresent());
    assertTrue(astmcArrayType.get() instanceof ASTMCArrayType);

    assertEquals("@A!MCArrayType", identHelper.getIdent((ASTMCArrayType)astmcArrayType.get()));
  }

}
