/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;


import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mccollectiontypeswithoutprimitivestest._parser.MCCollectionTypesWithoutPrimitivesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCCollectionTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testBasicGenericsTypes() throws IOException {

    String[] types = new String[]{"List<a.A>", "Optional<String>",
        "Set<String>", "Map<String,String>", "List<socnet.Person>"
        , "List<int>"
    };
    for (String testType : types) {
      MCCollectionTypesTestParser mcBasicTypesParser = new MCCollectionTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_StringMCType(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCObjectType);

      ASTMCObjectType t = (ASTMCObjectType) type.get();
      t.accept(new MCCollectionTypesVisitor() {
        public void visit(ASTMCListType t) {
          assertTrue(true);
          t.getMCTypeArgument().accept(new MCCollectionTypesVisitor() {
            @Override
            public void visit(ASTMCType node) {
              if (!(node instanceof ASTMCQualifiedType)) {
                fail("Found not String");
              }
            }
          });
        }
      });
    }
  }


  private class CheckTypeVisitor implements MCCollectionTypesVisitor {

  }

  @Test
  public void testMCListTypeValid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("List<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCListType);

    //test specific methods
    ASTMCListType listType = (ASTMCListType) type.get();
    assertEquals(listType.getNamesList().size(), 1);

    assertEquals(listType.getNamesList().get(0), "List");

    assertEquals(listType.getMCTypeArgumentsList().size(), 1);

    ASTMCTypeArgument argument = listType.getMCTypeArgumentsList().get(0);
    Optional<ASTMCTypeArgument> argument2 = parser.parse_StringMCTypeArgument("String");
    assertFalse(parser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  }

  @Test
  public void testMCListTypeInvalid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.List<String>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }

  @Test
  public void testMCMapTypeValid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Map<Integer, String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCMapType);

    //test specific methods
    ASTMCMapType mapType = (ASTMCMapType) type.get();
    assertEquals(mapType.getNamesList().size(), 1);

    assertEquals(mapType.getNamesList().get(0), "Map");

    assertEquals(mapType.getMCTypeArgumentsList().size(), 2);

    ASTMCTypeArgument argument = mapType.getMCTypeArgumentsList().get(0);
    Optional<ASTMCTypeArgument> argument2 = parser.parse_StringMCTypeArgument("Integer");
    assertFalse(parser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  }

  @Test
  public void testMCMapTypeInvalid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Map<Integer, String>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }


  @Test
  public void testMCOptionalTypeValid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Optional<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCOptionalType);

    //test specific methods
    ASTMCOptionalType optionalType = (ASTMCOptionalType) type.get();
    assertEquals(optionalType.getNamesList().size(), 1);

    assertEquals(optionalType.getNamesList().get(0), "Optional");

    assertEquals(optionalType.getMCTypeArgumentsList().size(), 1);

    ASTMCTypeArgument argument = optionalType.getMCTypeArgumentsList().get(0);
    Optional<ASTMCTypeArgument> argument2 = parser.parse_StringMCTypeArgument("String");
    assertFalse(parser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  }

  @Test
  public void testMCOptionalTypeInvalid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Optional<String>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }


  @Test
  public void testMCSetTypeValid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Set<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCSetType);

    //test specific methods
    ASTMCSetType setType = (ASTMCSetType) type.get();
    assertEquals(setType.getNamesList().size(), 1);

    assertEquals(setType.getNamesList().get(0), "Set");

    assertEquals(setType.getMCTypeArgumentsList().size(), 1);

    ASTMCTypeArgument argument = setType.getMCTypeArgumentsList().get(0);
    Optional<ASTMCTypeArgument> argument2 = parser.parse_StringMCTypeArgument("String");
    assertFalse(parser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  }

  @Test
  public void testMCSetTypeInvalid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Set<String>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }

  @Test
  public void testMCTypeArgumentValid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("a.b.c");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCBasicTypeArgument);
  }

  @Test
  public void testMCTypeArgumentInvalid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("List<A>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }


  @Test
  public void collectionTypeWithInt() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("List<int>");
    assertTrue(type.isPresent());
    assertEquals("List", type.get().printWithoutTypeArguments());
    assertTrue(type.get().getMCTypeArgumentsList().get(0) instanceof ASTMCPrimitiveTypeArgument);

  }

  @Test
  public void collectionTypeWithIntFail() throws IOException {
    MCCollectionTypesWithoutPrimitivesTestParser parser = new MCCollectionTypesWithoutPrimitivesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("List<int>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }

  @Test
  public void testPrintTypeWithoutTypeArguments() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCListType> listType = parser.parse_StringMCListType("List<String>");
    Optional<ASTMCOptionalType> optionalType = parser.parse_StringMCOptionalType("Optional<Integer>");
    Optional<ASTMCSetType> setType = parser.parse_StringMCSetType("Set<Boolean>");
    Optional<ASTMCMapType> mapType = parser.parse_StringMCMapType("Map<String,Integer>");
    Optional<ASTMCGenericType> genericType = parser.parse_StringMCGenericType("Map<String,Integer>");
    assertTrue(listType.isPresent());
    assertTrue(optionalType.isPresent());
    assertTrue(setType.isPresent());
    assertTrue(mapType.isPresent());
    assertTrue(genericType.isPresent());
    assertEquals("List", listType.get().printWithoutTypeArguments());
    assertEquals("Optional", optionalType.get().printWithoutTypeArguments());
    assertEquals("Set", setType.get().printWithoutTypeArguments());
    assertEquals("Map", genericType.get().printWithoutTypeArguments());
    assertEquals("Map", genericType.get().printWithoutTypeArguments());
    assertFalse(parser.hasErrors());
  }
}
