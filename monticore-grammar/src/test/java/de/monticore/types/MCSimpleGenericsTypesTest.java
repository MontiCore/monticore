/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCSimpleGenericsTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testCustomGenericsTypes() throws IOException {
    String[] types = new String[]{"List<List<b.B>>","socnet.Person<socnet.Person<B>, SecondaryParam>"};

    for (String testType : types) {
      System.out.println("Teste "+testType);
      MCSimpleGenericTypesTestParser mcBasicTypesParser = new MCSimpleGenericTypesTestParser();

      Optional<ASTMCType> type = mcBasicTypesParser.parse_StringMCType(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCObjectType);
      System.out.println(type.get().getClass());

      ASTMCObjectType t = (ASTMCObjectType) type.get();
    }
  }

  @Test
  public void testMCListTypeValid() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("List<Integer>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCListType);
  }

  @Test
  public void testMCListTypeValid2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.List<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCBasicGenericType);
  }

  @Test
  public void testMCMapTypeValid() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Map<Integer, String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCMapType);
  }

  @Test
  public void testMCMapTypeValid2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Map<java.util.List<Integer>, String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCBasicGenericType);
  }

  @Test
  public void testMCMapTypeValid3() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.HashMap<String,java.util.List<String>>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCBasicGenericType);
  }

  @Test
  public void testMCOptionalTypeValid() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Optional<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCOptionalType);
  }

  @Test
  public void testMCOptionalTypeValid2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Optional<Set<String>>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCBasicGenericType);
  }


  @Test
  public void testMCSetTypeValid() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Set<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCSetType);
  }

  @Test
  public void testMCSetTypeValid2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Set<List<String>>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCBasicGenericType);
  }

  @Test
  public void testMCTypeArgumentValid() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("a.b.c");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCBasicTypeArgument);
  }

  @Test
  public void testMCTypeArgumentValid2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("List<A>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCListType);
  }

  @Test
  public void testMCComplexReferenceTypeInvalid() throws IOException {
    //not defined in that grammar, only in MCGenericsTypes
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCType> type = parser.parse_StringMCType("java.util.List<A>.Set<C>.some.Collection<B>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }

  @Test
  public void testPrintTypeWithoutTypeArguments() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCBasicGenericType> basicGenericType = parser.parse_StringMCBasicGenericType("a.B<C, D>");
    Optional<ASTMCGenericType> genericType = parser.parse_StringMCGenericType("a.B<C, D>");
    assertTrue(genericType.isPresent());
    assertTrue(basicGenericType.isPresent());
    assertEquals("a.B", basicGenericType.get().printWithoutTypeArguments());
    assertEquals("a.B", genericType.get().printWithoutTypeArguments());
    assertFalse(parser.hasErrors());
  }
}
