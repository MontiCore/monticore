/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypeswithoutprimitivestest._parser.MCCollectionTypesWithoutPrimitivesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCollectionTypesCorrectStateTest {

  private ASTMCListType listTypeParser;

  private ASTMCOptionalType optTypeParser;

  private ASTMCSetType setTypeParser;

  private ASTMCMapType mapTypeParser;

  private ASTMCBasicTypeArgument typeArgumentInt;

  private ASTMCBasicTypeArgument typeArgumentString;
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setUp() throws IOException {
    MCCollectionTypesWithoutPrimitivesTestParser parser = new MCCollectionTypesWithoutPrimitivesTestParser();
    Optional<ASTMCListType> listTypeParser = parser.parse_StringMCListType("List<Integer>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(listTypeParser.isPresent());
    this.listTypeParser = listTypeParser.get();

    Optional<ASTMCOptionalType> optionalTypeParser = parser.parse_StringMCOptionalType("Optional<Integer>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(optionalTypeParser.isPresent());
    this.optTypeParser = optionalTypeParser.get();

    Optional<ASTMCSetType> setType = parser.parse_StringMCSetType("Set<Integer>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(setType.isPresent());
    this.setTypeParser = setType.get();

    Optional<ASTMCMapType> mapType = parser.parse_StringMCMapType("Map<String, Integer>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(mapType.isPresent());
    this.mapTypeParser = mapType.get();


    ASTMCQualifiedName integerName = MCCollectionTypesMill.mCQualifiedNameBuilder().setPartsList(Lists.newArrayList("Integer")).build();
    ASTMCQualifiedType integerType = MCCollectionTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(integerName).build();
    typeArgumentInt = MCCollectionTypesMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(integerType).build();

    ASTMCQualifiedName stringName = MCCollectionTypesMill.mCQualifiedNameBuilder().setPartsList(Lists.newArrayList("String")).build();
    ASTMCQualifiedType stringType = MCCollectionTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(stringName).build();
    typeArgumentString = MCCollectionTypesMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(stringType).build();
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void mCListTypeNameListFinal() {
    // test that MCListType only contains one element 'List'
    Assertions.assertEquals(1, listTypeParser.getNameList().size());
    Assertions.assertEquals("List", listTypeParser.getName(0));

    // set name over getter
    listTypeParser.getNameList().set(0, "Foo");
    Assertions.assertEquals(1, listTypeParser.getNameList().size());
    Assertions.assertEquals("List", listTypeParser.getName(0));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void mCOptionalTypeNameListFinal() {
    // test that MCListType only contains one element 'Optional'
    Assertions.assertEquals(1, optTypeParser.getNameList().size());
    Assertions.assertEquals("Optional", optTypeParser.getName(0));

    // set name over getter
    optTypeParser.getNameList().set(0, "Foo");
    Assertions.assertEquals(1, optTypeParser.getNameList().size());
    Assertions.assertEquals("Optional", optTypeParser.getName(0));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void mCSetTypeNameListFinal() {
    // test that MCListType only contains one element 'Set'
    Assertions.assertEquals(1, setTypeParser.getNameList().size());
    Assertions.assertEquals("Set", setTypeParser.getName(0));

    // set name over getter
    setTypeParser.getNameList().set(0, "Foo");
    Assertions.assertEquals(1, setTypeParser.getNameList().size());
    Assertions.assertEquals("Set", setTypeParser.getName(0));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void mCMapTypeNameListFinal() throws IOException {
    // test that MCListType only contains one element 'Map'

    Assertions.assertEquals(1, mapTypeParser.getNameList().size());
    Assertions.assertEquals("Map", mapTypeParser.getName(0));

    // set name over getter
    mapTypeParser.getNameList().set(0, "Foo");
    Assertions.assertEquals(1, mapTypeParser.getNameList().size());
    Assertions.assertEquals("Map", mapTypeParser.getName(0));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void mCListTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'List'
    ASTMCListType listBuild = MCCollectionTypesMill.mCListTypeBuilder()
        .setMCTypeArgument(typeArgumentInt)
        .build();
    Assertions.assertEquals(1, listBuild.getNameList().size());
    Assertions.assertEquals("List", listBuild.getName(0));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void mCOptionalTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'Optional'
    ASTMCOptionalType optBuild = MCCollectionTypesMill.mCOptionalTypeBuilder()
        .setMCTypeArgument(typeArgumentInt)
        .build();
    Assertions.assertEquals(1, optBuild.getNameList().size());
    Assertions.assertEquals("Optional", optBuild.getName(0));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void mCSetTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'Set'
    ASTMCSetType setBuild = MCCollectionTypesMill.mCSetTypeBuilder()
        .setMCTypeArgument(typeArgumentInt)
        .build();
    Assertions.assertEquals(1, setBuild.getNameList().size());
    Assertions.assertEquals("Set", setBuild.getName(0));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void mCMapTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'Map'
    ASTMCMapType mapBuildNoName = MCCollectionTypesMill.mCMapTypeBuilder()
        .setKey(typeArgumentInt)
        .setValue(typeArgumentString)
        .build();
    Assertions.assertEquals(1, mapBuildNoName.getNameList().size());
    Assertions.assertEquals("Map", mapBuildNoName.getName(0));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void mCListTypeSetTypeArgument() {
    listTypeParser.setMCTypeArgument(typeArgumentString);
    Assertions.assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    Assertions.assertEquals("String", listTypeParser.getMCTypeArgument().printType());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void mcOptionalTypeSetTypeArgument() {
    optTypeParser.setMCTypeArgument(typeArgumentString);
    Assertions.assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    Assertions.assertEquals("String", optTypeParser.getMCTypeArgument().printType());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void mcMapTypeSetKey() {
    mapTypeParser.setKey(typeArgumentString);
    Assertions.assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    Assertions.assertEquals("String", mapTypeParser.getKey().printType());
    Assertions.assertEquals("Integer", mapTypeParser.getValue().printType());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
