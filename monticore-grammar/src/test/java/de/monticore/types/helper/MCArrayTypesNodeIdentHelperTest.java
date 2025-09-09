/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.helper;

import de.monticore.types.MCArrayTypesNodeIdentHelper;
import de.monticore.types.mcarraytypes.MCArrayTypesMill;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypestest._parser.MCArrayTypesTestParser;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCArrayTypesNodeIdentHelperTest {
  
  @BeforeEach
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

    Assertions.assertTrue(astmcArrayType.isPresent());
    Assertions.assertTrue(astmcArrayType.get() instanceof ASTMCArrayType);

    Assertions.assertEquals("@A!MCArrayType", identHelper.getIdent((ASTMCArrayType)astmcArrayType.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
