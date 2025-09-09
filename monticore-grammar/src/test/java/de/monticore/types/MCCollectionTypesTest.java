/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;


import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor2;
import de.monticore.types.mccollectiontypestest.MCCollectionTypesTestMill;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mccollectiontypeswithoutprimitivestest._parser.MCCollectionTypesWithoutPrimitivesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCCollectionTypesTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCCollectionTypesTestMill.reset();
    MCCollectionTypesTestMill.init();
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

      Assertions.assertNotNull(type);
      Assertions.assertTrue(type.isPresent());
      Assertions.assertTrue(type.get() instanceof ASTMCObjectType);

      ASTMCObjectType t = (ASTMCObjectType) type.get();
      MCCollectionTypesTraverser traverser = MCCollectionTypesMill.traverser();
      traverser.add4MCCollectionTypes(new CheckTypeVisitor());
      t.accept(traverser);
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  private class CheckTypeVisitor implements MCCollectionTypesVisitor2 {
    public void visit(ASTMCType node) {
      if (!(node instanceof ASTMCQualifiedType)) {
        Assertions.fail("Found not String");
      }
    }
  }

  @Test
  public void testMCListTypeValid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("List<String>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCListType);

    //test specific methods
    ASTMCListType listType = (ASTMCListType) type.get();
    Assertions.assertEquals(listType.getNameList().size(), 1);

    Assertions.assertEquals(listType.getNameList().get(0), "List");

    Assertions.assertEquals(listType.getMCTypeArgumentList().size(), 1);

    ASTMCTypeArgument argument = listType.getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = parser.parse_StringMCTypeArgument("String");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(argument2.isPresent());
    Assertions.assertTrue(argument.deepEquals(argument2.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCListTypeInvalid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.List<String>");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertFalse(type.isPresent());
  }

  @Test
  public void testMCMapTypeValid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Map<Integer, String>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCMapType);

    //test specific methods
    ASTMCMapType mapType = (ASTMCMapType) type.get();
    Assertions.assertEquals(mapType.getNameList().size(), 1);

    Assertions.assertEquals(mapType.getNameList().get(0), "Map");

    Assertions.assertEquals(mapType.getMCTypeArgumentList().size(), 2);

    ASTMCTypeArgument argument = mapType.getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = parser.parse_StringMCTypeArgument("Integer");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(argument2.isPresent());
    Assertions.assertTrue(argument.deepEquals(argument2.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapTypeInvalid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Map<Integer, String>");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertFalse(type.isPresent());
  }


  @Test
  public void testMCOptionalTypeValid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Optional<String>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCOptionalType);

    //test specific methods
    ASTMCOptionalType optionalType = (ASTMCOptionalType) type.get();
    Assertions.assertEquals(optionalType.getNameList().size(), 1);

    Assertions.assertEquals(optionalType.getNameList().get(0), "Optional");

    Assertions.assertEquals(optionalType.getMCTypeArgumentList().size(), 1);

    ASTMCTypeArgument argument = optionalType.getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = parser.parse_StringMCTypeArgument("String");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(argument2.isPresent());
    Assertions.assertTrue(argument.deepEquals(argument2.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCOptionalTypeInvalid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Optional<String>");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertFalse(type.isPresent());
  }


  @Test
  public void testMCSetTypeValid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Set<String>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCSetType);

    //test specific methods
    ASTMCSetType setType = (ASTMCSetType) type.get();
    Assertions.assertEquals(setType.getNameList().size(), 1);

    Assertions.assertEquals(setType.getNameList().get(0), "Set");

    Assertions.assertEquals(setType.getMCTypeArgumentList().size(), 1);

    ASTMCTypeArgument argument = setType.getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = parser.parse_StringMCTypeArgument("String");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(argument2.isPresent());
    Assertions.assertTrue(argument.deepEquals(argument2.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCSetTypeInvalid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Set<String>");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertFalse(type.isPresent());
  }

  @Test
  public void testMCTypeArgumentValid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("a.b.c");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCBasicTypeArgument);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCTypeArgumentInvalid() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("List<A>");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertFalse(type.isPresent());
  }


  @Test
  public void collectionTypeWithInt() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("List<int>");
    Assertions.assertTrue(type.isPresent());
    Assertions.assertEquals("List", type.get().printWithoutTypeArguments());
    Assertions.assertTrue(type.get().getMCTypeArgumentList().get(0) instanceof ASTMCPrimitiveTypeArgument);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());

  }

  @Test
  public void collectionTypeWithIntFail() throws IOException {
    MCCollectionTypesWithoutPrimitivesTestParser parser = new MCCollectionTypesWithoutPrimitivesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("List<int>");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertFalse(type.isPresent());
  }

  @Test
  public void testPrintTypeWithoutTypeArguments() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCListType> listType = parser.parse_StringMCListType("List<String>");
    Optional<ASTMCOptionalType> optionalType = parser.parse_StringMCOptionalType("Optional<Integer>");
    Optional<ASTMCSetType> setType = parser.parse_StringMCSetType("Set<Boolean>");
    Optional<ASTMCMapType> mapType = parser.parse_StringMCMapType("Map<String,Integer>");
    Optional<ASTMCGenericType> genericType = parser.parse_StringMCGenericType("Map<String,Integer>");
    Assertions.assertTrue(listType.isPresent());
    Assertions.assertTrue(optionalType.isPresent());
    Assertions.assertTrue(setType.isPresent());
    Assertions.assertTrue(mapType.isPresent());
    Assertions.assertTrue(genericType.isPresent());
    Assertions.assertEquals("List", listType.get().printWithoutTypeArguments());
    Assertions.assertEquals("Optional", optionalType.get().printWithoutTypeArguments());
    Assertions.assertEquals("Set", setType.get().printWithoutTypeArguments());
    Assertions.assertEquals("Map", genericType.get().printWithoutTypeArguments());
    Assertions.assertEquals("Map", genericType.get().printWithoutTypeArguments());
    Assertions.assertFalse(parser.hasErrors());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
