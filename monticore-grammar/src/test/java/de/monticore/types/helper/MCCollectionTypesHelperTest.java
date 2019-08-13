/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.MCCollectionTypesTest;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCCollectionTypesHelperTest {
  @Test
  public void testIsOptional() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCOptionalType> astmcOptionalType = parser.parse_StringMCOptionalType("Optional<String>");
    assertTrue(astmcOptionalType.isPresent());
    assertTrue(MCCollectionTypesHelper.isOptional(astmcOptionalType.get()));
    assertFalse(parser.hasErrors());
  }

  @Test
  public void testGetReferenceTypeFromOptional() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCOptionalType> astmcOptionalType = parser.parse_StringMCOptionalType("Optional<String>");
    Optional<ASTMCTypeArgument> astmcTypeArgument = parser.parse_StringMCTypeArgument("String");
    assertTrue(astmcOptionalType.isPresent());
    assertTrue(astmcTypeArgument.isPresent());
    assertTrue(astmcTypeArgument.get().deepEquals(MCCollectionTypesHelper.getReferenceTypeFromOptional(astmcOptionalType.get())));
    assertFalse(parser.hasErrors());
  }

  @Test
  public void testGetSimpleName() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> astmcGenericType = parser.parse_StringMCGenericType("List<String>");
    assertTrue(astmcGenericType.isPresent());
    assertTrue(MCCollectionTypesHelper.getSimpleName(astmcGenericType.get()).equals("List"));
    assertFalse(parser.hasErrors());
  }

  @Test
  public void testPrintType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCType> astmcType = parser.parse_StringMCType("List<String>");
    Optional<ASTMCType> astmcType1 = parser.parse_StringMCType("boolean");
    Optional<ASTMCType> astmcType2 = parser.parse_StringMCType("TestType");
    Optional<ASTMCType> astmcType3 = parser.parse_StringMCType("Map<String,Integer>");
    Optional<ASTMCTypeArgument> astmcTypeArgument = parser.parse_StringMCTypeArgument("String");
    Optional<ASTMCTypeArgument> astmcTypeArgument1 = parser.parse_StringMCTypeArgument("TestType");
    assertTrue(astmcType.isPresent());
    assertTrue(astmcType1.isPresent());
    assertTrue(astmcType2.isPresent());
    assertTrue(astmcType3.isPresent());
    assertEquals("List<String>",MCCollectionTypesHelper.printType(astmcType.get())); // funktioniert nicht
    assertEquals("boolean",MCCollectionTypesHelper.printType(astmcType1.get()));
    assertEquals("TestType",MCCollectionTypesHelper.printType(astmcType2.get()));
    assertEquals("Map<String,Integer>",MCCollectionTypesHelper.printType(astmcType3.get()));
    assertEquals("String",MCCollectionTypesHelper.printType(astmcTypeArgument.get()));
    assertEquals("TestType",MCCollectionTypesHelper.printType(astmcTypeArgument1.get()));
    assertFalse(parser.hasErrors());
  }

  @Test
  public void testGetFirstTypeArgumentOfGenericType() throws IOException{
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> astmcGenericType = parser.parse_StringMCGenericType("List<Character>");
    Optional<ASTMCType> astmcType = parser.parse_StringMCType("Character");
    assertTrue(astmcGenericType.isPresent());
    assertTrue(astmcType.isPresent());
    ASTMCType type = astmcType.get();
    Optional<ASTMCTypeArgument> typeArgument = MCCollectionTypesHelper.getFirstTypeArgumentOfGenericType(astmcGenericType.get(),"List");
    assertTrue(typeArgument.isPresent());
    assertTrue(typeArgument.get() instanceof ASTMCBasicTypeArgument);
    ASTMCBasicTypeArgument astmcBasicTypeArgument = (ASTMCBasicTypeArgument) typeArgument.get();
    assertTrue(astmcBasicTypeArgument.getMCQualifiedType().deepEquals(type));
    assertFalse(parser.hasErrors());
  }

  @Test
  public void testGetFirstArgumentOfOptional() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> astmcGenericType = parser.parse_StringMCGenericType("Optional<String>");
    Optional<ASTMCType> astmcType = parser.parse_StringMCType("String");
    assertTrue(astmcGenericType.isPresent());
    assertTrue(astmcType.isPresent());
    ASTMCType type = astmcType.get();
    Optional<ASTMCTypeArgument> typeArgument = MCCollectionTypesHelper.getFirstTypeArgumentOfGenericType(astmcGenericType.get(),"Optional");
    assertTrue(typeArgument.isPresent());
    assertTrue(typeArgument.get() instanceof ASTMCBasicTypeArgument);
    ASTMCBasicTypeArgument astmcBasicTypeArgument = (ASTMCBasicTypeArgument) typeArgument.get();
    assertTrue(astmcBasicTypeArgument.getMCQualifiedType().deepEquals(type));
    assertFalse(parser.hasErrors());
  }

  @Test
  public void testPrintSimpleRefType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> astmcGenericType = parser.parse_StringMCGenericType("List<String>");
    Optional<ASTMCGenericType> astmcGenericType1 = parser.parse_StringMCGenericType("Map<String,Integer>");
    Optional<ASTMCGenericType> astmcGenericType2 = parser.parse_StringMCGenericType("Set<Character>");
    Optional<ASTMCGenericType> astmcGenericType3 = parser.parse_StringMCGenericType("Optional<Double>");
    assertTrue(astmcGenericType.isPresent());
    assertTrue(astmcGenericType1.isPresent());
    assertTrue(astmcGenericType2.isPresent());
    assertTrue(astmcGenericType3.isPresent());
    assertTrue("List<String>".equals(MCCollectionTypesHelper.printSimpleRefType(astmcGenericType.get()))); //null
    assertTrue("Map<String,Integer>".equals(MCCollectionTypesHelper.printSimpleRefType(astmcGenericType1.get())));
    assertTrue("Set<Character>".equals(MCCollectionTypesHelper.printSimpleRefType(astmcGenericType2.get())));
    assertTrue("Optional<Double>".equals(MCCollectionTypesHelper.printSimpleRefType(astmcGenericType3.get())));
    assertFalse(parser.hasErrors());
  }

  @Test
  public void testIsGenericWithOneTypeArgument() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCType> astmcType = parser.parse_StringMCType("Optional<String>");
    Optional<ASTMCType> astmcType1 = parser.parse_StringMCType("Map<String,Integer>");
    Optional<ASTMCType> astmcType2 = parser.parse_StringMCType("List<Character>");
    Optional<ASTMCType> astmcType3 = parser.parse_StringMCType("java.util.List");
    Optional<ASTMCType> astmcType4 = parser.parse_StringMCType("String");
    assertTrue(astmcType.isPresent());
    assertTrue(astmcType1.isPresent());
    assertTrue(astmcType2.isPresent());
    assertTrue(astmcType3.isPresent());
    assertTrue(astmcType4.isPresent());
    assertTrue(MCCollectionTypesHelper.isGenericTypeWithOneTypeArgument(astmcType.get(),"Optional"));
    assertFalse(MCCollectionTypesHelper.isGenericTypeWithOneTypeArgument(astmcType1.get(),"Map"));
    assertTrue(MCCollectionTypesHelper.isGenericTypeWithOneTypeArgument(astmcType2.get(),"List"));
    assertFalse(MCCollectionTypesHelper.isGenericTypeWithOneTypeArgument(astmcType3.get(),"List"));
    assertFalse(MCCollectionTypesHelper.isGenericTypeWithOneTypeArgument(astmcType4.get(),"String"));
    assertFalse(parser.hasErrors());
  }

}
