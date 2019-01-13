package de.monticore.types;

import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardType;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCGenericsTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testBasicGenericsTypes() throws IOException {
    Class foo = boolean.class;
    String[] types = new String[]{"Foo<String>.Bar<List<Integer>>","List<? extends Person>","List<P<String>>","Optional<String>","Set<String>","Map<String,String>","List<socnet.Person>"};

    for (String testType : types) {
      MCFullGenericTypesTestParser mcBasicTypesParser = new MCFullGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);
      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCObjectType);
      System.out.println(type.get().getClass());
      ASTMCObjectType t = (ASTMCObjectType) type.get();
    }
  }

  @Test
  public void testArrayTypes() throws IOException {
    Class foo = boolean.class;
    String[] types = new String[]{"String[][]"," java.util.List<Foo>[][]"," boolean[][]"};

    for (String testType : types) {
      MCFullGenericTypesTestParser mcBasicTypesParser = new MCFullGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCArrayType);
      ASTMCArrayType t = (ASTMCArrayType) type.get();
      assertEquals(2,t.getDimensions());
    }
  }

  @Test
  public void testMCComplexReferenceTypeValid() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCType> type = parser.parse_StringMCType("java.util.List<A>.Set<C>.some.Collection<B>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCMultipleGenericType);
  }

  @Test
  public void testMcWildcardType() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("? extends java.util.Set<Foo>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCWildcardType);
  }

}
