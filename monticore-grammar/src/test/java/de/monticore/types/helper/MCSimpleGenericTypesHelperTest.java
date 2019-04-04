package de.monticore.types.helper;

import de.monticore.types.MCSimpleGenericTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCSimpleGenericTypesHelperTest {
  @Test
  public void testPrintType() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCType> astmcType = parser.parse_StringMCType("List<List<String>>");
    Optional<ASTMCType> astmcType1 = parser.parse_StringMCType("java.util.List<Integer>");
    Optional<ASTMCType> astmcType2 = parser.parse_StringMCType("Optional<java.util.List<Double>>");
    Optional<ASTMCType> astmcType3 = parser.parse_StringMCType("java.util.Optional<a.b.C>");
    assertTrue(astmcType.isPresent());
    assertTrue(astmcType1.isPresent());
    assertTrue(astmcType2.isPresent());
    assertTrue(astmcType3.isPresent());
    assertFalse(parser.hasErrors());
    MCSimpleGenericTypesHelper helper = new MCSimpleGenericTypesHelper();
    assertEquals("List<List<String>>", helper.printType(astmcType.get()));
    assertEquals("java.util.List<Integer>", helper.printType(astmcType1.get()));
    assertEquals("Optional<java.util.List<Double>>", helper.printType(astmcType2.get()));
    assertEquals("java.util.Optional<a.b.C>", helper.printType(astmcType3.get()));
  }
}
