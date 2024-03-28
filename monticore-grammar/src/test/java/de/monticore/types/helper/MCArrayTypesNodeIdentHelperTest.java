/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.helper;

import de.monticore.types.MCArrayTypesNodeIdentHelper;
import de.monticore.types.mcarraytypes.MCArrayTypesMill;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypestest._parser.MCArrayTypesTestParser;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCArrayTypesNodeIdentHelperTest {
  
  @Before
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCArrayTypesMill.reset();
    MCArrayTypesMill.init();
  }
  
  @Test
  public void testGetIdent() throws IOException {
    MCArrayTypesTestParser p = new MCArrayTypesTestParser();
    MCArrayTypesNodeIdentHelper identHelper = new MCArrayTypesNodeIdentHelper();

    Optional<ASTMCType> astmcArrayType = p.parse_StringMCType("A[]");

    assertTrue(astmcArrayType.isPresent());
    assertTrue(astmcArrayType.get() instanceof ASTMCArrayType);

    assertEquals("@A!MCArrayType", identHelper.getIdent((ASTMCArrayType)astmcArrayType.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
