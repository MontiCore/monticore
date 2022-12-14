/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.helper;

import de.monticore.types.MCFullGenericTypesNodeIdentHelper;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCFullGenericTypesNodeIdentHelperTest {
  
  @Before
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testGetIdent() throws IOException {
    MCFullGenericTypesTestParser p = new MCFullGenericTypesTestParser();
    MCFullGenericTypesNodeIdentHelper identHelper = new MCFullGenericTypesNodeIdentHelper();
    Optional<ASTMCMultipleGenericType> astmcMultipleGenericType = p.parse_StringMCMultipleGenericType("a.b.D<C>.d.E<int>");

    assertTrue(astmcMultipleGenericType.isPresent());

    assertEquals("@a.b.D.d.E!MCMultipleGenericType", identHelper.getIdent(astmcMultipleGenericType.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
