/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
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

public class MCFullGenericTypesTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCFullGenericTypesTestMill.reset();
    MCFullGenericTypesTestMill.init();
  }
  
  @Test
  public void testPrintTypeWithoutTypeArguments() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCMultipleGenericType> multipleGenericType = parser.parse_StringMCMultipleGenericType("a.B<C>.D.E<F>.G");
    Optional<ASTMCGenericType> genericType = parser.parse_StringMCGenericType("a.B<C>.D.E<F>.G");
    Assertions.assertTrue(genericType.isPresent());
    Assertions.assertTrue(multipleGenericType.isPresent());
    Assertions.assertEquals("a.B.D.E.G", multipleGenericType.get().printWithoutTypeArguments());
    Assertions.assertEquals("a.B.D.E.G", genericType.get().printWithoutTypeArguments());
    Assertions.assertFalse(parser.hasErrors());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
