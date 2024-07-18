/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.helper;

import de.monticore.types.MCFullGenericTypesNodeIdentHelper;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypestest.MCFullGenericTypesTestMill;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCFullGenericTypesNodeIdentHelperTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCFullGenericTypesTestMill.reset();
    MCFullGenericTypesTestMill.init();
  }
  
  @Test
  public void testGetIdent() throws IOException {
    MCFullGenericTypesTestParser p = new MCFullGenericTypesTestParser();
    MCFullGenericTypesNodeIdentHelper identHelper = new MCFullGenericTypesNodeIdentHelper();
    Optional<ASTMCMultipleGenericType> astmcMultipleGenericType = p.parse_StringMCMultipleGenericType("a.b.D<C>.d.E<int>");

    Assertions.assertTrue(astmcMultipleGenericType.isPresent());

    Assertions.assertEquals("@a.b.D.d.E!MCMultipleGenericType", identHelper.getIdent(astmcMultipleGenericType.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
