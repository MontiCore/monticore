/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCSimpleGenericTypesNodeIdentHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MCSimpleGenericTypesNodeIdentHelperTest {
  @Test
  public void testGetIdent() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCBasicGenericType> astmcType = parser.parse_StringMCBasicGenericType("List<List<String>>");
    Optional<ASTMCBasicGenericType> astmcType1 = parser.parse_StringMCBasicGenericType("java.util.List<Integer>");
    Optional<ASTMCBasicGenericType> astmcType2 = parser.parse_StringMCBasicGenericType("Optional<java.util.List<Double>>");
    Optional<ASTMCBasicGenericType> astmcType3 = parser.parse_StringMCBasicGenericType("java.util.Optional<a.b.C>");
    Optional<ASTMCBasicGenericType> astmcType4 = parser.parse_StringMCBasicGenericType("a.b.c.D<d.e.f.G>");

    assertFalse(parser.hasErrors());
    assertTrue(astmcType.isPresent());
    assertTrue(astmcType1.isPresent());
    assertTrue(astmcType2.isPresent());
    assertTrue(astmcType3.isPresent());

    MCSimpleGenericTypesNodeIdentHelper helper = new MCSimpleGenericTypesNodeIdentHelper();
    assertEquals("@List!MCBasicGenericType", helper.getIdent(astmcType.get()));
    assertEquals("@java.util.List!MCBasicGenericType", helper.getIdent(astmcType1.get()));
    assertEquals("@Optional!MCBasicGenericType", helper.getIdent(astmcType2.get()));
    assertEquals("@java.util.Optional!MCBasicGenericType",helper.getIdent(astmcType3.get()));
    assertEquals("@a.b.c.D!MCBasicGenericType",helper.getIdent(astmcType4.get()));
  }
}
