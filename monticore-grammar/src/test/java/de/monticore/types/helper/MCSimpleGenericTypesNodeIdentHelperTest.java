/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCSimpleGenericTypesNodeIdentHelper;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class MCSimpleGenericTypesNodeIdentHelperTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCSimpleGenericTypesMill.reset();
    MCSimpleGenericTypesMill.init();
  }
  
  @Test
  public void testGetIdent() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCBasicGenericType> astmcType = parser.parse_StringMCBasicGenericType("List<List<String>>");
    Optional<ASTMCBasicGenericType> astmcType1 = parser.parse_StringMCBasicGenericType("java.util.List<Integer>");
    Optional<ASTMCBasicGenericType> astmcType2 = parser.parse_StringMCBasicGenericType("Optional<java.util.List<Double>>");
    Optional<ASTMCBasicGenericType> astmcType3 = parser.parse_StringMCBasicGenericType("java.util.Optional<a.b.C>");
    Optional<ASTMCBasicGenericType> astmcType4 = parser.parse_StringMCBasicGenericType("a.b.c.D<d.e.f.G>");

    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astmcType.isPresent());
    Assertions.assertTrue(astmcType1.isPresent());
    Assertions.assertTrue(astmcType2.isPresent());
    Assertions.assertTrue(astmcType3.isPresent());

    MCSimpleGenericTypesNodeIdentHelper helper = new MCSimpleGenericTypesNodeIdentHelper();
    Assertions.assertEquals("@List!MCBasicGenericType", helper.getIdent(astmcType.get()));
    Assertions.assertEquals("@java.util.List!MCBasicGenericType", helper.getIdent(astmcType1.get()));
    Assertions.assertEquals("@Optional!MCBasicGenericType", helper.getIdent(astmcType2.get()));
    Assertions.assertEquals("@java.util.Optional!MCBasicGenericType", helper.getIdent(astmcType3.get()));
    Assertions.assertEquals("@a.b.c.D!MCBasicGenericType", helper.getIdent(astmcType4.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
