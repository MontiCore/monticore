/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCFullGenericTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCFullGenericTypesHelperTest {

  @Test
  public void testPrintType() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCTypeArgument> astmcTypeArgument = parser.parse_StringMCTypeArgument("List<String>");
    Optional<ASTMCTypeArgument> astmcTypeArgument1 = parser.parse_StringMCTypeArgument("Optional<List<Integer>>");
    Optional<ASTMCTypeArgument> astmcTypeArgument2 = parser.parse_StringMCTypeArgument("? extends String");
    Optional<ASTMCTypeArgument> astmcTypeArgument3 = parser.parse_StringMCTypeArgument("? extends Map<Character,Float>");
    Optional<ASTMCTypeArgument> astmcTypeArgument4 = parser.parse_StringMCTypeArgument("? super Double");
    Optional<ASTMCTypeArgument> astmcTypeArgument5 = parser.parse_StringMCTypeArgument("java.util.Set");
    Optional<ASTMCTypeArgument> astmcTypeArgument6 = parser.parse_StringMCTypeArgument("?");
    assertFalse(parser.hasErrors());
    assertTrue(astmcTypeArgument.isPresent());
    assertTrue(astmcTypeArgument1.isPresent());
    assertTrue(astmcTypeArgument2.isPresent());
    assertTrue(astmcTypeArgument3.isPresent());
    assertTrue(astmcTypeArgument4.isPresent());
    assertTrue(astmcTypeArgument5.isPresent());
    assertTrue(astmcTypeArgument6.isPresent());
    assertTrue("List<String>".equals(MCFullGenericTypesHelper.printType(astmcTypeArgument.get())));
    assertTrue("Optional<List<Integer>>".equals(MCFullGenericTypesHelper.printType(astmcTypeArgument1.get())));
    assertTrue("? extends String".equals(MCFullGenericTypesHelper.printType(astmcTypeArgument2.get())));
    assertTrue("? extends Map<Character,Float>".equals(MCFullGenericTypesHelper.printType(astmcTypeArgument3.get())));
    assertTrue("? super Double".equals(MCFullGenericTypesHelper.printType(astmcTypeArgument4.get())));
    assertTrue("java.util.Set".equals(MCFullGenericTypesHelper.printType(astmcTypeArgument5.get())));
    assertTrue("?".equals(MCFullGenericTypesHelper.printType(astmcTypeArgument6.get())));
  }

  @Test
  public void testGetGenericTypeFromOptional() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCGenericType> astmcGenericType1 = parser.parse_StringMCGenericType("Optional<List<String>.Set<Integer>>");
    Optional<ASTMCGenericType> astmcGenericType = parser.parse_StringMCGenericType("List<String>.Set<Integer>");
    assertFalse(parser.hasErrors());
    assertTrue(astmcGenericType.isPresent());
    assertTrue(astmcGenericType1.isPresent());
    assertTrue(astmcGenericType.get().deepEquals(MCFullGenericTypesHelper.getGenericTypeFromOptional(astmcGenericType1.get())));
  }

  @Test
  public void testGetReferenceTypeFromOptional() throws IOException{
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCGenericType> astmcGenericType = parser.parse_StringMCGenericType("Optional<? extends List<Integer>>");
    Optional<ASTMCGenericType> astmcGenericType1 = parser.parse_StringMCGenericType("Optional<String>");
    Optional<ASTMCGenericType> astmcGenericType2 = parser.parse_StringMCGenericType("Optional<char>");
    Optional<ASTMCGenericType> astmcGenericType3 = parser.parse_StringMCGenericType("Optional<java.util.Set>");
    assertFalse(parser.hasErrors());
    assertTrue(astmcGenericType1.isPresent());
    assertTrue(astmcGenericType.isPresent());
    assertTrue(astmcGenericType2.isPresent());
    assertTrue(astmcGenericType3.isPresent());
    assertEquals("String",MCFullGenericTypesHelper.getReferenceNameFromOptional(astmcGenericType1.get()));
    assertEquals("List<Integer>",(MCFullGenericTypesHelper.getReferenceNameFromOptional(astmcGenericType.get())));
    assertEquals("char",MCFullGenericTypesHelper.getReferenceNameFromOptional(astmcGenericType2.get()));
    assertEquals("java.util.Set",MCFullGenericTypesHelper.getReferenceNameFromOptional(astmcGenericType3.get()));
  }

  @Test
  public void testGetArrayDimensionIfArrayOrZero() throws IOException{
    //have to use ASTMCType because of left recursion in ASTMCArrayType there is no parse Method
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCType> astmcArrayType = parser.parse_StringMCType("String[][]");
    Optional<ASTMCType> astmcType = parser.parse_StringMCType("String");
    assertFalse(parser.hasErrors());
    assertTrue(astmcArrayType.isPresent());
    assertTrue(astmcType.isPresent());
    assertTrue(astmcArrayType.get() instanceof ASTMCArrayType);
    assertFalse(astmcType.get() instanceof ASTMCArrayType);
    assertEquals(2,MCFullGenericTypesHelper.getArrayDimensionIfArrayOrZero(astmcArrayType.get()));
    assertEquals(0,MCFullGenericTypesHelper.getArrayDimensionIfArrayOrZero(astmcType.get()));
  }

  @Test
  public void testGetQualifiedReferenceNameFromOptional() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCGenericType> astmcGenericType = parser.parse_StringMCGenericType("Optional<java.util.List>");
    assertFalse(parser.hasErrors());
    assertTrue(astmcGenericType.isPresent());
    assertEquals("java.util.List",MCFullGenericTypesHelper.getQualifiedReferenceNameFromOptional(astmcGenericType.get()));
  }

  @Test
  public void testGetSimpleReferenceTypeFromOptional() throws IOException{
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCGenericType> astmcGenericType = parser.parse_StringMCGenericType("Optional<List<Integer>>");
    Optional<ASTMCGenericType> astmcGenericType1 = parser.parse_StringMCGenericType("List<Integer>");
    assertFalse(parser.hasErrors());
    assertTrue(astmcGenericType.isPresent());
    assertTrue(astmcGenericType1.isPresent());
    assertTrue(astmcGenericType1.get().deepEquals(MCFullGenericTypesHelper.getSimpleReferenceTypeFromOptional(astmcGenericType.get())));
  }
}
