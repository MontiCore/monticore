/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class AlwaysTheSameASTTest {

  private MCBasicTypesTestParser basicTypesTestParser;
  private MCCollectionTypesTestParser mcCollectionTypesTestParser;
  private MCSimpleGenericTypesTestParser customGenericTypesTestParser;
  private MCFullGenericTypesTestParser genericTypesTestParser;

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    this.mcCollectionTypesTestParser = new MCCollectionTypesTestParser();
    this.basicTypesTestParser = new MCBasicTypesTestParser();
    this.customGenericTypesTestParser = new MCSimpleGenericTypesTestParser();
    this.genericTypesTestParser = new MCFullGenericTypesTestParser();
  }

  @Test
  public void testMCListType() throws IOException {
    String list = "List<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCListType);

    ASTMCListType basicGenericList = (ASTMCListType) basicGenericAst.get();
    ASTMCListType customList = (ASTMCListType) customAst.get();
    ASTMCListType genericList = (ASTMCListType) genericAST.get();

    assertTrue(basicGenericList.deepEquals(customList));
    assertTrue(basicGenericList.deepEquals(genericList));
    assertTrue(genericList.deepEquals(customList));

    assertEquals(basicGenericAst.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.").length, 1);

    assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "List");

    assertEquals(basicGenericAst.get().getMCTypeArgumentsList().size(), 1);

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentsList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("String");
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  }


  @Test
  public void testMCListTypeWithCollectionTypeParser() throws IOException {
    String list = "List<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCListType);

    ASTMCListType basicGenericList = (ASTMCListType) basicGenericAst.get();
    ASTMCListType customList = (ASTMCListType) customAst.get();
    ASTMCListType genericList = (ASTMCListType) genericAST.get();

    assertTrue(basicGenericList.deepEquals(customList));
    assertTrue(basicGenericList.deepEquals(genericList));
    assertTrue(genericList.deepEquals(customList));

    assertEquals(basicGenericAst.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.").length, 1);

    assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "List");
  }

  @Test
  public void testMCListTypeWithTypeParser() throws IOException {
    String list = "List<String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCListType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCListType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCListType);

    ASTMCListType basicGenericList = (ASTMCListType) basicGenericAst.get();
    ASTMCListType customList = (ASTMCListType) customAst.get();
    ASTMCListType genericList = (ASTMCListType) genericAST.get();

    assertTrue(basicGenericList.deepEquals(customList));
    assertTrue(basicGenericList.deepEquals(genericList));
    assertTrue(genericList.deepEquals(customList));
  }

  @Test
  public void testMCMapTypeWithGenericCollectionTypeParser() throws IOException {
    String map = "Map<Integer, String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCMapType);

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(basicGenericAst.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.").length, 1);

    assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Map");

    assertEquals(basicGenericAst.get().getMCTypeArgumentsList().size(), 2);

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentsList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("Integer");
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  }

  @Test
  public void testMCMapTypeWithTypeParser() throws IOException {
    String map = "Map<Integer, String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCMapType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCMapType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCMapType);

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCMapTypeWithCollectionTypeParser() throws IOException {
    String map = "Map<Integer, String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCMapType);

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(basicGenericAst.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.").length, 1);

    assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Map");
  }

  @Test
  public void testMCOptionalType() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCOptionalType);


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(basicGenericAst.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.").length, 1);

    assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Optional");

    assertEquals(basicGenericAst.get().getMCTypeArgumentsList().size(), 1);

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentsList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("String");
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  }

  @Test
  public void testMCOptionalTypeWithTypeParser() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCOptionalType);


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCOptionalTypeWithCollectionTypeParser() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCOptionalType);


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(basicGenericAst.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.").length, 1);

    assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Optional");
  }

  @Test
  public void testMCSetTypeWithGenericCollectionTypeParser() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCSetType);


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(basicGenericAst.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.").length, 1);

    assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Set");

    assertEquals(basicGenericAst.get().getMCTypeArgumentsList().size(), 1);

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentsList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("String");
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  }

  @Test
  public void testMCSetTypeWithTypeParser() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCSetType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCSetType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCSetType);


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCSetTypeWithCollectionTypeParser() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCSetType);


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(basicGenericAst.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.").length, 1);

    assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Set");

  }

  @Test
  public void testMCBasicTypeArgument() throws IOException {
    String type = "de.monticore.ASTNode";

    Optional<ASTMCTypeArgument> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCBasicTypeArgument);

    Optional<ASTMCTypeArgument> customAst = customGenericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCBasicTypeArgument);

    Optional<ASTMCTypeArgument> genericAST = genericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCBasicTypeArgument);

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCCustomTypeArgument() throws IOException {
    String type = "List<a.b.C>";

    Optional<ASTMCTypeArgument> customAst = customGenericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCCustomTypeArgument);

    Optional<ASTMCTypeArgument> genericAST = genericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCCustomTypeArgument);

    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCQualifiedType() throws IOException {
    String type = "de.monticore.ASTNode";

    Optional<ASTMCType> basicAST = basicTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicAST.isPresent());
    assertTrue(basicAST.get() instanceof ASTMCQualifiedType);

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCQualifiedType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCQualifiedType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCQualifiedType);

    assertTrue(basicAST.get().deepEquals(customAst.get()));
    assertTrue(basicAST.get().deepEquals(basicGenericAst.get()));
    assertTrue(basicAST.get().deepEquals(genericAST.get()));
    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }
}
