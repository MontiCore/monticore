/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypeswithoutprimitivestest._parser.MCCollectionTypesWithoutPrimitivesTestParser;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCollectionTypesCorrectStateTest {

  private ASTMCListType listTypeParser;

  private ASTMCOptionalType optTypeParser;

  private ASTMCSetType setTypeParser;

  private ASTMCMapType mapTypeParser;

  private ASTMCBasicTypeArgument typeArgumentInt;

  private ASTMCBasicTypeArgument typeArgumentString;
  
  private MCCollectionTypesPrettyPrinter printer = MCCollectionTypesMill.mcCollectionTypesPrettyPrinter();

  @Before
  public void setUp() throws IOException {
    LogStub.init();
    Log.enableFailQuick(false);

    MCCollectionTypesWithoutPrimitivesTestParser parser = new MCCollectionTypesWithoutPrimitivesTestParser();
    Optional<ASTMCListType> listTypeParser = parser.parse_StringMCListType("List<Integer>");
    assertFalse(parser.hasErrors());
    assertTrue(listTypeParser.isPresent());
    this.listTypeParser = listTypeParser.get();

    Optional<ASTMCOptionalType> optionalTypeParser = parser.parse_StringMCOptionalType("Optional<Integer>");
    assertFalse(parser.hasErrors());
    assertTrue(optionalTypeParser.isPresent());
    this.optTypeParser = optionalTypeParser.get();

    Optional<ASTMCSetType> setType = parser.parse_StringMCSetType("Set<Integer>");
    assertFalse(parser.hasErrors());
    assertTrue(setType.isPresent());
    this.setTypeParser = setType.get();

    Optional<ASTMCMapType> mapType = parser.parse_StringMCMapType("Map<String, Integer>");
    assertFalse(parser.hasErrors());
    assertTrue(mapType.isPresent());
    this.mapTypeParser = mapType.get();


    ASTMCQualifiedName integerName = MCCollectionTypesMill.mCQualifiedNameBuilder().setPartList(Lists.newArrayList("Integer")).build();
    ASTMCQualifiedType integerType = MCCollectionTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(integerName).build();
    typeArgumentInt = MCCollectionTypesMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(integerType).build();

    ASTMCQualifiedName stringName = MCCollectionTypesMill.mCQualifiedNameBuilder().setPartList(Lists.newArrayList("String")).build();
    ASTMCQualifiedType stringType = MCCollectionTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(stringName).build();
    typeArgumentString = MCCollectionTypesMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(stringType).build();
  }

  @Test
  public void mCListTypeNameListFinal() {
    // test that MCListType only contains one element 'List'
    assertEquals(1, listTypeParser.getNameList().size());
    assertEquals("List", listTypeParser.getName(0));

    // set new name
    listTypeParser.setName(0, "Foo");
    assertEquals(1, listTypeParser.getNameList().size());
    assertEquals("List", listTypeParser.getName(0));

    // set name over getter
    listTypeParser.getNameList().set(0, "Foo");
    assertEquals(1, listTypeParser.getNameList().size());
    assertEquals("List", listTypeParser.getName(0));

    // remove element
    listTypeParser.removeName(0);
    assertEquals(1, listTypeParser.getNameList().size());
    assertEquals("List", listTypeParser.getName(0));

    // clear list
    listTypeParser.clearNames();
    assertEquals(1, listTypeParser.getNameList().size());
    assertEquals("List", listTypeParser.getName(0));
  }

  @Test
  public void mCOptionalTypeNameListFinal() {
    // test that MCListType only contains one element 'Optional'
    assertEquals(1, optTypeParser.getNameList().size());
    assertEquals("Optional", optTypeParser.getName(0));

    // set new name
    optTypeParser.setName(0, "Foo");
    assertEquals(1, optTypeParser.getNameList().size());
    assertEquals("Optional", optTypeParser.getName(0));

    // set name over getter
    optTypeParser.getNameList().set(0, "Foo");
    assertEquals(1, optTypeParser.getNameList().size());
    assertEquals("Optional", optTypeParser.getName(0));

    // remove element
    optTypeParser.removeName(0);
    assertEquals(1, optTypeParser.getNameList().size());
    assertEquals("Optional", optTypeParser.getName(0));

    // clear list
    optTypeParser.clearNames();
    assertEquals(1, optTypeParser.getNameList().size());
    assertEquals("Optional", optTypeParser.getName(0));
  }

  @Test
  public void mCSetTypeNameListFinal() {
    // test that MCListType only contains one element 'Set'
    assertEquals(1, setTypeParser.getNameList().size());
    assertEquals("Set", setTypeParser.getName(0));

    // set new name
    setTypeParser.setName(0, "Foo");
    assertEquals(1, setTypeParser.getNameList().size());
    assertEquals("Set", setTypeParser.getName(0));

    // set name over getter
    setTypeParser.getNameList().set(0, "Foo");
    assertEquals(1, setTypeParser.getNameList().size());
    assertEquals("Set", setTypeParser.getName(0));

    // remove element
    setTypeParser.removeName(0);
    assertEquals(1, setTypeParser.getNameList().size());
    assertEquals("Set", setTypeParser.getName(0));

    // clear list
    setTypeParser.clearNames();
    assertEquals(1, setTypeParser.getNameList().size());
    assertEquals("Set", setTypeParser.getName(0));
  }

  @Test
  public void mCMapTypeNameListFinal() throws IOException {
    // test that MCListType only contains one element 'Map'

    assertEquals(1, mapTypeParser.getNameList().size());
    assertEquals("Map", mapTypeParser.getName(0));

    // set new name
    mapTypeParser.setName(0, "Foo");
    assertEquals(1, mapTypeParser.getNameList().size());
    assertEquals("Map", mapTypeParser.getName(0));

    // set name over getter
    mapTypeParser.getNameList().set(0, "Foo");
    assertEquals(1, mapTypeParser.getNameList().size());
    assertEquals("Map", mapTypeParser.getName(0));

    // remove element
    mapTypeParser.removeName(0);
    assertEquals(1, mapTypeParser.getNameList().size());
    assertEquals("Map", mapTypeParser.getName(0));

    // clear list
    mapTypeParser.clearNames();
    assertEquals(1, mapTypeParser.getNameList().size());
    assertEquals("Map", mapTypeParser.getName(0));
  }

  @Test
  public void mCListTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'List'
    ASTMCListType listBuild = MCCollectionTypesMill.mCListTypeBuilder()
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, listBuild.getNameList().size());
    assertEquals("List", listBuild.getName(0));

    ASTMCListType listBuildWithWrongName = MCCollectionTypesMill.mCListTypeBuilder()
        .setNameList(Lists.newArrayList("Foo"))
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, listBuildWithWrongName.getNameList().size());
    assertEquals("List", listBuildWithWrongName.getName(0));

    ASTMCListType lsitBuildManyNames = MCCollectionTypesMill.mCListTypeBuilder()
        .setNameList(Lists.newArrayList("java", "util", "List"))
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, lsitBuildManyNames.getNameList().size());
    assertEquals("List", lsitBuildManyNames.getName(0));
  }

  @Test
  public void mCOptionalTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'Optional'
    ASTMCOptionalType optBuild = MCCollectionTypesMill.mCOptionalTypeBuilder()
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, optBuild.getNameList().size());
    assertEquals("Optional", optBuild.getName(0));

    ASTMCOptionalType optBuildWithWrongName = MCCollectionTypesMill.mCOptionalTypeBuilder()
        .setNameList(Lists.newArrayList("Foo")).setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, optBuildWithWrongName.getNameList().size());
    assertEquals("Optional", optBuildWithWrongName.getName(0));

    ASTMCOptionalType optBuildManyNames = MCCollectionTypesMill.mCOptionalTypeBuilder()
        .setNameList(Lists.newArrayList("java", "util", "Optional"))
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, optBuildManyNames.getNameList().size());
    assertEquals("Optional", optBuildManyNames.getName(0));
  }

  @Test
  public void mCSetTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'Set'
    ASTMCSetType setBuild = MCCollectionTypesMill.mCSetTypeBuilder()
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, setBuild.getNameList().size());
    assertEquals("Set", setBuild.getName(0));

    ASTMCSetType setBuildWithWrongName = MCCollectionTypesMill.mCSetTypeBuilder()
        .setNameList(Lists.newArrayList("Foo"))
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, setBuildWithWrongName.getNameList().size());
    assertEquals("Set", setBuildWithWrongName.getName(0));

    ASTMCSetType setBuildManyNames = MCCollectionTypesMill.mCSetTypeBuilder()
        .setNameList(Lists.newArrayList("java", "util", "Set"))
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, setBuildManyNames.getNameList().size());
    assertEquals("Set", setBuildManyNames.getName(0));
  }

  @Test
  public void mCMapTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'Map'
    ASTMCMapType mapBuildNoName = MCCollectionTypesMill.mCMapTypeBuilder()
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt, typeArgumentString))
        .build();
    assertEquals(1, mapBuildNoName.getNameList().size());
    assertEquals("Map", mapBuildNoName.getName(0));

    ASTMCMapType mapBuildWithWrongName = MCCollectionTypesMill.mCMapTypeBuilder()
        .setNameList(Lists.newArrayList("Foo"))
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt, typeArgumentString))
        .build();
    assertEquals(1, mapBuildWithWrongName.getNameList().size());
    assertEquals("Map", mapBuildWithWrongName.getName(0));

    ASTMCMapType mapBuildManyNames = MCCollectionTypesMill.mCMapTypeBuilder()
        .setNameList(Lists.newArrayList("java", "util", "Map"))
        .setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt, typeArgumentString)).build();
    assertEquals(1, mapBuildManyNames.getNameList().size());
    assertEquals("Map", mapBuildManyNames.getName(0));

    ASTMCMapType mapBuildWithKeyValue = MCCollectionTypesMill.mCMapTypeBuilder()
        .setKey(typeArgumentInt)
        .setValue(typeArgumentString)
        .build();
    assertEquals(1, mapBuildWithKeyValue.getNameList().size());
    assertEquals("Map", mapBuildWithKeyValue.getName(0));
  }


  @Test
  public void mCListTypeSetTypeArgument() {
    listTypeParser.setMCTypeArgument(0, typeArgumentString);
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", listTypeParser.getMCTypeArgument().printType(printer));
  }

  @Test
  public void mCListTypeSetTypeArgumentList() {
    listTypeParser.setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer",listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListTypeClearTypeArguments() {
    listTypeParser.clearMCTypeArguments();
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListAddTypeArgument() {
    listTypeParser.addMCTypeArgument(typeArgumentInt);
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    assertEquals("0xA6001 Not allowed to add an element to MCTypeArgumentList of ASTMCListType. " +
        "A MCTypeArgumentList must always have one element.", Log.getFindings().get(0).getMsg());
    Log.getFindings().clear();
  }

  @Test
  public void mCListAddAllTypeArgument() {
    listTypeParser.addAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListRemoveTypeArgument() {
    listTypeParser.removeMCTypeArgument(typeArgumentInt);
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListRemoveAllTypeArgument() {
    listTypeParser.removeAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListRemoveTypeArgumentIntParam() {
    listTypeParser.removeMCTypeArgument(1);
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListRetainTypeArgument() {
    // okay when original type is contained
    listTypeParser.retainAllMCTypeArguments(Lists.newArrayList(listTypeParser.getMCTypeArgument(), typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());


    // not okay if it will conclude in empty list
    listTypeParser.retainAllMCTypeArguments(Lists.newArrayList(typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListRemoveIfTypeArgument() {
    // okay when original type is contained
    listTypeParser.removeIfMCTypeArgument(x -> false);
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());

    // not okay if it will conclude in empty list
    listTypeParser.removeIfMCTypeArgument(x -> true);
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListForEachTypeArgumentIntParam() {
    listTypeParser.forEachMCTypeArguments(x -> x = typeArgumentString);
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void mCListAddTypeArgumentIntParam() {
    listTypeParser.addMCTypeArgument(1, typeArgumentInt);
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListAddAllTypeArgumentIntParam() {
    listTypeParser.addAllMCTypeArguments(1, Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListTypeSetTypeArgumentListIntParam() {
    listTypeParser.setMCTypeArgument(1, typeArgumentInt);
    assertEquals(1, listTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalTypeSetTypeArgument() {
    optTypeParser.setMCTypeArgument(0, typeArgumentString);
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", optTypeParser.getMCTypeArgument().printType(printer));
  }

  @Test
  public void mcOptionalTypeSetTypeArgumentList() {
    optTypeParser.setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalTypeClearTypeArguments() {
    optTypeParser.clearMCTypeArguments();
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalAddTypeArgument() {
    optTypeParser.addMCTypeArgument(typeArgumentInt);
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalAddAllTypeArgument() {
    optTypeParser.addAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalRemoveTypeArgument() {
    optTypeParser.removeMCTypeArgument(typeArgumentInt);
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalRemoveAllTypeArgument() {
    optTypeParser.removeAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalRemoveTypeArgumentIntParam() {
    optTypeParser.removeMCTypeArgument(1);
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalRetainTypeArgument() {
    // okay when original type is contained
    optTypeParser.retainAllMCTypeArguments(Lists.newArrayList(optTypeParser.getMCTypeArgument(), typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());


    // not okay if it will conclude in empty list
    optTypeParser.retainAllMCTypeArguments(Lists.newArrayList(typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalRemoveIfTypeArgument() {
    // okay when original type is contained
    optTypeParser.removeIfMCTypeArgument(x -> false);
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());

    // not okay if it will conclude in empty list
    optTypeParser.removeIfMCTypeArgument(x -> true);
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalForEachTypeArgumentIntParam() {
    optTypeParser.forEachMCTypeArguments(x -> x = typeArgumentString);
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void mcOptionalAddTypeArgumentIntParam() {
    optTypeParser.addMCTypeArgument(1, typeArgumentInt);
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalAddAllTypeArgumentIntParam() {
    optTypeParser.addAllMCTypeArguments(1, Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalTypeSetTypeArgumentListIntParam() {
    optTypeParser.setMCTypeArgument(1, typeArgumentInt);
    assertEquals(1, optTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetTypeSetTypeArgument() {
    setTypeParser.setMCTypeArgument(0, typeArgumentString);
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", setTypeParser.getMCTypeArgument().printType(printer));
  }

  @Test
  public void mcSetTypeSetTypeArgumentList() {
    setTypeParser.setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetTypeClearTypeArguments() {
    setTypeParser.clearMCTypeArguments();
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetAddTypeArgument() {
    setTypeParser.addMCTypeArgument(typeArgumentInt);
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetAddAllTypeArgument() {
    setTypeParser.addAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetRemoveTypeArgument() {
    setTypeParser.removeMCTypeArgument(typeArgumentInt);
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetRemoveAllTypeArgument() {
    setTypeParser.removeAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetRemoveTypeArgumentIntParam() {
    setTypeParser.removeMCTypeArgument(1);
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetRetainTypeArgument() {
    // okay when original type is contained
    setTypeParser.retainAllMCTypeArguments(Lists.newArrayList(setTypeParser.getMCTypeArgument(), typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());


    // not okay if it will conclude in empty list
    setTypeParser.retainAllMCTypeArguments(Lists.newArrayList(typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetRemoveIfTypeArgument() {
    // okay when original type is contained
    setTypeParser.removeIfMCTypeArgument(x -> false);
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());

    // not okay if it will conclude in empty list
    setTypeParser.removeIfMCTypeArgument(x -> true);
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetForEachTypeArgumentIntParam() {
    setTypeParser.forEachMCTypeArguments(x -> x = typeArgumentString);
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void mcSetAddTypeArgumentIntParam() {
    setTypeParser.addMCTypeArgument(1, typeArgumentInt);
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetAddAllTypeArgumentIntParam() {
    setTypeParser.addAllMCTypeArguments(1, Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetTypeSetTypeArgumentListIntParam() {
    setTypeParser.setMCTypeArgument(1, typeArgumentInt);
    assertEquals(1, setTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapTypeSetTypeArgument() {
    mapTypeParser.setMCTypeArgument(0, typeArgumentString);
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
  }

  @Test
  public void mcMapTypeSetTypeArgumentList() {
    mapTypeParser.setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", mapTypeParser.getKey().printType(printer));
    assertEquals("String", mapTypeParser.getValue().printType(printer));
    assertEquals(0, Log.getErrorCount());

    mapTypeParser.setMCTypeArgumentList(Lists.newArrayList(typeArgumentInt, typeArgumentString, typeArgumentInt));
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("Integer", mapTypeParser.getKey().printType(printer));
    assertEquals("String", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapTypeClearTypeArguments() {
    mapTypeParser.clearMCTypeArguments();
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapAddTypeArgument() {
    mapTypeParser.addMCTypeArgument(typeArgumentInt);
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapAddAllTypeArgument() {
    mapTypeParser.addAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapRemoveTypeArgument() {
    mapTypeParser.removeMCTypeArgument(typeArgumentInt);
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapRemoveAllTypeArgument() {
    mapTypeParser.removeAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapRemoveTypeArgumentIntParam() {
    mapTypeParser.removeMCTypeArgument(1);
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapRetainTypeArgument() {
    // okay when original type is contained
    mapTypeParser.retainAllMCTypeArguments(Lists.newArrayList(mapTypeParser.getKey(), mapTypeParser.getValue(), typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    ;
    assertEquals(0, Log.getErrorCount());


    // not okay if it will conclude in empty list
    mapTypeParser.retainAllMCTypeArguments(Lists.newArrayList(typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapRemoveIfTypeArgument() {
    // okay when original type is contained
    mapTypeParser.removeIfMCTypeArgument(x -> false);
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(0, Log.getErrorCount());

    // not okay if it will conclude in empty list
    mapTypeParser.removeIfMCTypeArgument(x -> true);
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapForEachTypeArgumentIntParam() {
    mapTypeParser.forEachMCTypeArguments(x -> x = typeArgumentString);
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void mcMapAddTypeArgumentIntParam() {
    mapTypeParser.addMCTypeArgument(1, typeArgumentInt);
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapAddAllTypeArgumentIntParam() {
    mapTypeParser.addAllMCTypeArguments(1, Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapTypeSetTypeArgumentListIntParam() {
    mapTypeParser.setMCTypeArgument(1, typeArgumentInt);
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(0, Log.getErrorCount());

    mapTypeParser.setMCTypeArgument(3, typeArgumentInt);
    assertEquals(2, mapTypeParser.getMCTypeArgumentList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }
}
