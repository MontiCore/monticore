package de.monticore.types;


import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcbasictypes._ast.ASTMCReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCCollectionTypesReferenceType;
import de.monticore.types.mccustomgenerictypestest._parser.MCCustomGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCCustomGenericsTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testCustomGenericsTypes() throws IOException {
    String[] types = new String[]{"List<List<b.B>>","socnet.Person<socnet.Person<B>, SecondaryParam>"};

    for (String testType : types) {
      System.out.println("Teste "+testType);
      MCCustomGenericTypesTestParser mcBasicTypesParser = new MCCustomGenericTypesTestParser();

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCReferenceType);
      System.out.println(type.get().getClass());

      ASTMCReferenceType t = (ASTMCReferenceType) type.get();
    }
  }

  @Test
  public void testMCListTypeValid() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("List<Integer>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCListType);
  }

  @Test
  public void testMCListTypeValid2() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("java.util.List<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCCollectionTypesReferenceType);
  }

  @Test
  public void testMCMapTypeValid() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("Map<Integer, String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCMapType);
  }

  @Test
  public void testMCMapTypeValid2() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("java.util.Map<java.util.List<Integer>, String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCCollectionTypesReferenceType);
  }


  @Test
  public void testMCOptionalTypeValid() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("Optional<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCOptionalType);
  }

  @Test
  public void testMCOptionalTypeValid2() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("java.util.Optional<Set<String>>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCCollectionTypesReferenceType);
  }


  @Test
  public void testMCSetTypeValid() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("Set<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCSetType);
  }

  @Test
  public void testMCSetTypeValid2() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("java.util.Set<List<String>>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCCollectionTypesReferenceType);
  }

  @Test
  public void testMCTypeArgumentValid() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("a.b.c");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCBasicTypeArgument);
  }

  @Test
  public void testMCTypeArgumentValid2() throws IOException {
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("List<A>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCListType);
  }

  @Test
  public void testMCComplexReferenceTypeInvalid() throws IOException {
    //not defined in that grammar, only in MCGenericsTypes
    MCCustomGenericTypesTestParser parser = new MCCustomGenericTypesTestParser();
    Optional<ASTMCType> type = parser.parse_StringMCType("java.util.List<A>.Set<C>.some.Collection<B>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }
}
