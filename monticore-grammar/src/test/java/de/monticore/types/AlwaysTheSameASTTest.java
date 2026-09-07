/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest.MCBasicTypesTestMill;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypestest.MCCollectionTypesTestMill;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mcfullgenerictypestest.MCFullGenericTypesTestMill;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypestest.MCSimpleGenericTypesTestMill;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AlwaysTheSameASTTest {

  private MCBasicTypesTestParser basicTypesTestParser;
  private MCCollectionTypesTestParser mcCollectionTypesTestParser;
  private MCSimpleGenericTypesTestParser customGenericTypesTestParser;
  private MCFullGenericTypesTestParser genericTypesTestParser;
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    
    //only initializing this way as we only use the parser
    MCCollectionTypesTestMill.reset();
    MCCollectionTypesTestMill.init();

    MCBasicTypesTestMill.reset();
    MCBasicTypesTestMill.init();

    MCSimpleGenericTypesTestMill.reset();
    MCSimpleGenericTypesTestMill.init();

    MCFullGenericTypesTestMill.reset();
    MCFullGenericTypesTestMill.init();

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
    assertInstanceOf(ASTMCListType.class, basicGenericAst.get());

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCListType.class, customAst.get());

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCListType.class, genericAST.get());

    ASTMCListType basicGenericList = (ASTMCListType) basicGenericAst.get();
    ASTMCListType customList = (ASTMCListType) customAst.get();
    ASTMCListType genericList = (ASTMCListType) genericAST.get();

    assertTrue(basicGenericList.deepEquals(customList));
    assertTrue(basicGenericList.deepEquals(genericList));
    assertTrue(genericList.deepEquals(customList));

    assertEquals(1, basicGenericAst.get().printType().split("\\.").length);

    assertEquals("List", basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0]);

    assertEquals(1, basicGenericAst.get().getMCTypeArgumentList().size());

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("String");
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testMCListTypeWithCollectionTypeParser() throws IOException {
    String list = "List<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCListType.class, basicGenericAst.get());

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCListType.class, customAst.get());

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCListType.class, genericAST.get());

    ASTMCListType basicGenericList = (ASTMCListType) basicGenericAst.get();
    ASTMCListType customList = (ASTMCListType) customAst.get();
    ASTMCListType genericList = (ASTMCListType) genericAST.get();

    assertTrue(basicGenericList.deepEquals(customList));
    assertTrue(basicGenericList.deepEquals(genericList));
    assertTrue(genericList.deepEquals(customList));

    assertEquals(1, basicGenericAst.get().printType().split("\\.").length);

    assertEquals("List", basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0]);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCListTypeWithTypeParser() throws IOException {
    String list = "List<String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCListType.class, basicGenericAst.get());

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCListType.class, customAst.get());

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCListType.class, genericAST.get());

    ASTMCListType basicGenericList = (ASTMCListType) basicGenericAst.get();
    ASTMCListType customList = (ASTMCListType) customAst.get();
    ASTMCListType genericList = (ASTMCListType) genericAST.get();

    assertTrue(basicGenericList.deepEquals(customList));
    assertTrue(basicGenericList.deepEquals(genericList));
    assertTrue(genericList.deepEquals(customList));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapTypeWithGenericCollectionTypeParser() throws IOException {
    String map = "Map<Integer, String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCMapType.class, basicGenericAst.get());

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCMapType.class, customAst.get());

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCMapType.class, genericAST.get());

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(1, basicGenericAst.get().printType().split("\\.").length);

    assertEquals("Map", basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0]);

    assertEquals(2, basicGenericAst.get().getMCTypeArgumentList().size());

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("Integer");
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapTypeWithTypeParser() throws IOException {
    String map = "Map<Integer, String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCMapType.class, basicGenericAst.get());

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCMapType.class, customAst.get());

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCMapType.class, genericAST.get());

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapTypeWithCollectionTypeParser() throws IOException {
    String map = "Map<Integer, String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCMapType.class, basicGenericAst.get());

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCMapType.class, customAst.get());

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCMapType.class, genericAST.get());

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(1, basicGenericAst.get().printType().split("\\.").length);

    assertEquals("Map", basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0]);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCOptionalType() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCOptionalType.class, basicGenericAst.get());

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCOptionalType.class, customAst.get());

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCOptionalType.class, genericAST.get());


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(1, basicGenericAst.get().printType().split("\\.").length);

    assertEquals("Optional", basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0]);

    assertEquals(1, basicGenericAst.get().getMCTypeArgumentList().size());

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("String");
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCOptionalTypeWithTypeParser() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCOptionalType.class, basicGenericAst.get());

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCOptionalType.class, customAst.get());

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCOptionalType.class, genericAST.get());


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCOptionalTypeWithCollectionTypeParser() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCOptionalType.class, basicGenericAst.get());

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCOptionalType.class, customAst.get());

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCOptionalType.class, genericAST.get());


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(1, basicGenericAst.get().printType().split("\\.").length);

    assertEquals("Optional", basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0]);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCSetTypeWithGenericCollectionTypeParser() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCSetType.class, basicGenericAst.get());

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCSetType.class, customAst.get());

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCSetType.class, genericAST.get());


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(1, basicGenericAst.get().printType().split("\\.").length);

    assertEquals("Set", basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0]);

    assertEquals(1, basicGenericAst.get().getMCTypeArgumentList().size());

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("String");
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(argument2.isPresent());
    assertTrue(argument.deepEquals(argument2.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCSetTypeWithTypeParser() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCSetType.class, basicGenericAst.get());

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCSetType.class, customAst.get());

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCSetType.class, genericAST.get());


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCSetTypeWithCollectionTypeParser() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCSetType.class, basicGenericAst.get());

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCSetType.class, customAst.get());

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCSetType.class, genericAST.get());


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));

    assertEquals(1, basicGenericAst.get().printType().split("\\.").length);

    assertEquals("Set", basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0]);
    
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCBasicTypeArgument() throws IOException {
    String type = "de.monticore.ASTNode";

    Optional<ASTMCTypeArgument> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCBasicTypeArgument.class, basicGenericAst.get());

    Optional<ASTMCTypeArgument> customAst = customGenericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCBasicTypeArgument.class, customAst.get());

    Optional<ASTMCTypeArgument> genericAST = genericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCBasicTypeArgument.class, genericAST.get());

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCCustomTypeArgument() throws IOException {
    String type = "List<a.b.C>";

    Optional<ASTMCTypeArgument> customAst = customGenericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCCustomTypeArgument.class, customAst.get());

    Optional<ASTMCTypeArgument> genericAST = genericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCCustomTypeArgument.class, genericAST.get());

    assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCQualifiedType() throws IOException {
    String type = "de.monticore.ASTNode";

    Optional<ASTMCType> basicAST = basicTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicAST.isPresent());
    assertInstanceOf(ASTMCQualifiedType.class, basicAST.get());

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertInstanceOf(ASTMCQualifiedType.class, basicGenericAst.get());

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertInstanceOf(ASTMCQualifiedType.class, customAst.get());

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertInstanceOf(ASTMCQualifiedType.class, genericAST.get());

    assertTrue(basicAST.get().deepEquals(customAst.get()));
    assertTrue(basicAST.get().deepEquals(basicGenericAst.get()));
    assertTrue(basicAST.get().deepEquals(genericAST.get()));
    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
