package de.monticore.types;


import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCCollectionTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testBasicGenericsTypes() throws IOException {

    String[] types = new String[]{"List<a.A>","Optional<String>",
            "Set<String>","Map<String,String>","List<socnet.Person>"
            ,"List<int>"
    };
    for (String testType : types) {
      MCCollectionTypesTestParser mcBasicTypesParser = new MCCollectionTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);

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
    assertEquals(listType.getNameList().size(), 1);

    assertEquals(listType.getNameList().get(0), "List");

    assertEquals(listType.getMCTypeArgumentList().size(), 1);

    ASTMCTypeArgument argument = listType.getMCTypeArgumentList().get(0);
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
    assertEquals(mapType.getNameList().size(), 1);

    assertEquals(mapType.getNameList().get(0), "Map");

    assertEquals(mapType.getMCTypeArgumentList().size(), 2);

    ASTMCTypeArgument argument = mapType.getMCTypeArgumentList().get(0);
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
    assertEquals(optionalType.getNameList().size(), 1);

    assertEquals(optionalType.getNameList().get(0), "Optional");

    assertEquals(optionalType.getMCTypeArgumentList().size(), 1);

    ASTMCTypeArgument argument = optionalType.getMCTypeArgumentList().get(0);
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
    assertEquals(setType.getNameList().size(), 1);

    assertEquals(setType.getNameList().get(0), "Set");

    assertEquals(setType.getMCTypeArgumentList().size(), 1);

    ASTMCTypeArgument argument = setType.getMCTypeArgumentList().get(0);
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

}
