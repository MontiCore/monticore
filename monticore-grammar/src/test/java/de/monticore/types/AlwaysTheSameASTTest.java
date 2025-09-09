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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

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
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(list);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(list);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCListType);

    ASTMCListType basicGenericList = (ASTMCListType) basicGenericAst.get();
    ASTMCListType customList = (ASTMCListType) customAst.get();
    ASTMCListType genericList = (ASTMCListType) genericAST.get();

    Assertions.assertTrue(basicGenericList.deepEquals(customList));
    Assertions.assertTrue(basicGenericList.deepEquals(genericList));
    Assertions.assertTrue(genericList.deepEquals(customList));

    Assertions.assertEquals(basicGenericAst.get().printType().split("\\.").length, 1);

    Assertions.assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "List");

    Assertions.assertEquals(basicGenericAst.get().getMCTypeArgumentList().size(), 1);

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("String");
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(argument2.isPresent());
    Assertions.assertTrue(argument.deepEquals(argument2.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testMCListTypeWithCollectionTypeParser() throws IOException {
    String list = "List<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(list);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(list);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(list);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCListType);

    ASTMCListType basicGenericList = (ASTMCListType) basicGenericAst.get();
    ASTMCListType customList = (ASTMCListType) customAst.get();
    ASTMCListType genericList = (ASTMCListType) genericAST.get();

    Assertions.assertTrue(basicGenericList.deepEquals(customList));
    Assertions.assertTrue(basicGenericList.deepEquals(genericList));
    Assertions.assertTrue(genericList.deepEquals(customList));

    Assertions.assertEquals(basicGenericAst.get().printType().split("\\.").length, 1);

    Assertions.assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "List");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCListTypeWithTypeParser() throws IOException {
    String list = "List<String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(list);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCListType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(list);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCListType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(list);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCListType);

    ASTMCListType basicGenericList = (ASTMCListType) basicGenericAst.get();
    ASTMCListType customList = (ASTMCListType) customAst.get();
    ASTMCListType genericList = (ASTMCListType) genericAST.get();

    Assertions.assertTrue(basicGenericList.deepEquals(customList));
    Assertions.assertTrue(basicGenericList.deepEquals(genericList));
    Assertions.assertTrue(genericList.deepEquals(customList));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapTypeWithGenericCollectionTypeParser() throws IOException {
    String map = "Map<Integer, String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(map);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(map);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(map);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCMapType);

    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));

    Assertions.assertEquals(basicGenericAst.get().printType().split("\\.").length, 1);

    Assertions.assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Map");

    Assertions.assertEquals(basicGenericAst.get().getMCTypeArgumentList().size(), 2);

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("Integer");
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(argument2.isPresent());
    Assertions.assertTrue(argument.deepEquals(argument2.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapTypeWithTypeParser() throws IOException {
    String map = "Map<Integer, String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(map);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCMapType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(map);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCMapType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(map);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCMapType);

    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapTypeWithCollectionTypeParser() throws IOException {
    String map = "Map<Integer, String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(map);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(map);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(map);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCMapType);

    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));

    Assertions.assertEquals(basicGenericAst.get().printType().split("\\.").length, 1);

    Assertions.assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Map");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCOptionalType() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(optional);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(optional);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(optional);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCOptionalType);


    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));

    Assertions.assertEquals(basicGenericAst.get().printType().split("\\.").length, 1);

    Assertions.assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Optional");

    Assertions.assertEquals(basicGenericAst.get().getMCTypeArgumentList().size(), 1);

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("String");
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(argument2.isPresent());
    Assertions.assertTrue(argument.deepEquals(argument2.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCOptionalTypeWithTypeParser() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(optional);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(optional);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(optional);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCOptionalType);


    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCOptionalTypeWithCollectionTypeParser() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(optional);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(optional);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(optional);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCOptionalType);


    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));

    Assertions.assertEquals(basicGenericAst.get().printType().split("\\.").length, 1);

    Assertions.assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Optional");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCSetTypeWithGenericCollectionTypeParser() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(set);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(set);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(set);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCSetType);


    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));

    Assertions.assertEquals(basicGenericAst.get().printType().split("\\.").length, 1);

    Assertions.assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Set");

    Assertions.assertEquals(basicGenericAst.get().getMCTypeArgumentList().size(), 1);

    ASTMCTypeArgument argument = basicGenericAst.get().getMCTypeArgumentList().get(0);
    Optional<ASTMCTypeArgument> argument2 = mcCollectionTypesTestParser.parse_StringMCTypeArgument("String");
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(argument2.isPresent());
    Assertions.assertTrue(argument.deepEquals(argument2.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCSetTypeWithTypeParser() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(set);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCSetType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(set);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCSetType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(set);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCSetType);


    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCSetTypeWithCollectionTypeParser() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(set);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(set);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(set);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCSetType);


    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));

    Assertions.assertEquals(basicGenericAst.get().printType().split("\\.").length, 1);

    Assertions.assertEquals(basicGenericAst.get().printWithoutTypeArguments().split("\\.")[0], "Set");
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCBasicTypeArgument() throws IOException {
    String type = "de.monticore.ASTNode";

    Optional<ASTMCTypeArgument> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCTypeArgument(type);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCBasicTypeArgument);

    Optional<ASTMCTypeArgument> customAst = customGenericTypesTestParser.parse_StringMCTypeArgument(type);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCBasicTypeArgument);

    Optional<ASTMCTypeArgument> genericAST = genericTypesTestParser.parse_StringMCTypeArgument(type);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCBasicTypeArgument);

    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCCustomTypeArgument() throws IOException {
    String type = "List<a.b.C>";

    Optional<ASTMCTypeArgument> customAst = customGenericTypesTestParser.parse_StringMCTypeArgument(type);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCCustomTypeArgument);

    Optional<ASTMCTypeArgument> genericAST = genericTypesTestParser.parse_StringMCTypeArgument(type);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCCustomTypeArgument);

    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCQualifiedType() throws IOException {
    String type = "de.monticore.ASTNode";

    Optional<ASTMCType> basicAST = basicTypesTestParser.parse_StringMCType(type);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicAST.isPresent());
    Assertions.assertTrue(basicAST.get() instanceof ASTMCQualifiedType);

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(type);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(basicGenericAst.isPresent());
    Assertions.assertTrue(basicGenericAst.get() instanceof ASTMCQualifiedType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(type);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(customAst.isPresent());
    Assertions.assertTrue(customAst.get() instanceof ASTMCQualifiedType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(type);
    Assertions.assertFalse(mcCollectionTypesTestParser.hasErrors());
    Assertions.assertTrue(genericAST.isPresent());
    Assertions.assertTrue(genericAST.get() instanceof ASTMCQualifiedType);

    Assertions.assertTrue(basicAST.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicAST.get().deepEquals(basicGenericAst.get()));
    Assertions.assertTrue(basicAST.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    Assertions.assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    Assertions.assertTrue(genericAST.get().deepEquals(customAst.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
