/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
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


    ASTMCQualifiedName integerName = MCCollectionTypesMill.mCQualifiedNameBuilder().setPartsList(Lists.newArrayList("Integer")).build();
    ASTMCQualifiedType integerType = MCCollectionTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(integerName).build();
    typeArgumentInt = MCCollectionTypesMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(integerType).build();

    ASTMCQualifiedName stringName = MCCollectionTypesMill.mCQualifiedNameBuilder().setPartsList(Lists.newArrayList("String")).build();
    ASTMCQualifiedType stringType = MCCollectionTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(stringName).build();
    typeArgumentString = MCCollectionTypesMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(stringType).build();
  }

  @Test
  public void mCListTypeNameListFinal() {
    // test that MCListType only contains one element 'List'
    assertEquals(1, listTypeParser.getNamesList().size());
    assertEquals("List", listTypeParser.getNames(0));

    // set new name
    listTypeParser.setNames(0, "Foo");
    assertEquals(1, listTypeParser.getNamesList().size());
    assertEquals("List", listTypeParser.getNames(0));

    // set name over getter
    listTypeParser.getNamesList().set(0, "Foo");
    assertEquals(1, listTypeParser.getNamesList().size());
    assertEquals("List", listTypeParser.getNames(0));

    // remove element
    listTypeParser.removeNames(0);
    assertEquals(1, listTypeParser.getNamesList().size());
    assertEquals("List", listTypeParser.getNames(0));

    // clear list
    listTypeParser.clearNames();
    assertEquals(1, listTypeParser.getNamesList().size());
    assertEquals("List", listTypeParser.getNames(0));
  }

  @Test
  public void mCOptionalTypeNameListFinal() {
    // test that MCListType only contains one element 'Optional'
    assertEquals(1, optTypeParser.getNamesList().size());
    assertEquals("Optional", optTypeParser.getNames(0));

    // set new name
    optTypeParser.setNames(0, "Foo");
    assertEquals(1, optTypeParser.getNamesList().size());
    assertEquals("Optional", optTypeParser.getNames(0));

    // set name over getter
    optTypeParser.getNamesList().set(0, "Foo");
    assertEquals(1, optTypeParser.getNamesList().size());
    assertEquals("Optional", optTypeParser.getNames(0));

    // remove element
    optTypeParser.removeNames(0);
    assertEquals(1, optTypeParser.getNamesList().size());
    assertEquals("Optional", optTypeParser.getNames(0));

    // clear list
    optTypeParser.clearNames();
    assertEquals(1, optTypeParser.getNamesList().size());
    assertEquals("Optional", optTypeParser.getNames(0));
  }

  @Test
  public void mCSetTypeNameListFinal() {
    // test that MCListType only contains one element 'Set'
    assertEquals(1, setTypeParser.getNamesList().size());
    assertEquals("Set", setTypeParser.getNames(0));

    // set new name
    setTypeParser.setNames(0, "Foo");
    assertEquals(1, setTypeParser.getNamesList().size());
    assertEquals("Set", setTypeParser.getNames(0));

    // set name over getter
    setTypeParser.getNamesList().set(0, "Foo");
    assertEquals(1, setTypeParser.getNamesList().size());
    assertEquals("Set", setTypeParser.getNames(0));

    // remove element
    setTypeParser.removeNames(0);
    assertEquals(1, setTypeParser.getNamesList().size());
    assertEquals("Set", setTypeParser.getNames(0));

    // clear list
    setTypeParser.clearNames();
    assertEquals(1, setTypeParser.getNamesList().size());
    assertEquals("Set", setTypeParser.getNames(0));
  }

  @Test
  public void mCMapTypeNameListFinal() throws IOException {
    // test that MCListType only contains one element 'Map'

    assertEquals(1, mapTypeParser.getNamesList().size());
    assertEquals("Map", mapTypeParser.getNames(0));

    // set new name
    mapTypeParser.setNames(0, "Foo");
    assertEquals(1, mapTypeParser.getNamesList().size());
    assertEquals("Map", mapTypeParser.getNames(0));

    // set name over getter
    mapTypeParser.getNamesList().set(0, "Foo");
    assertEquals(1, mapTypeParser.getNamesList().size());
    assertEquals("Map", mapTypeParser.getNames(0));

    // remove element
    mapTypeParser.removeNames(0);
    assertEquals(1, mapTypeParser.getNamesList().size());
    assertEquals("Map", mapTypeParser.getNames(0));

    // clear list
    mapTypeParser.clearNames();
    assertEquals(1, mapTypeParser.getNamesList().size());
    assertEquals("Map", mapTypeParser.getNames(0));
  }

  @Test
  public void mCListTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'List'
    ASTMCListType listBuild = MCCollectionTypesMill.mCListTypeBuilder()
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, listBuild.getNamesList().size());
    assertEquals("List", listBuild.getNames(0));

    ASTMCListType listBuildWithWrongName = MCCollectionTypesMill.mCListTypeBuilder()
        .setNamesList(Lists.newArrayList("Foo"))
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, listBuildWithWrongName.getNamesList().size());
    assertEquals("List", listBuildWithWrongName.getNames(0));

    ASTMCListType lsitBuildManyNames = MCCollectionTypesMill.mCListTypeBuilder()
        .setNamesList(Lists.newArrayList("java", "util", "List"))
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, lsitBuildManyNames.getNamesList().size());
    assertEquals("List", lsitBuildManyNames.getNames(0));
  }

  @Test
  public void mCOptionalTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'Optional'
    ASTMCOptionalType optBuild = MCCollectionTypesMill.mCOptionalTypeBuilder()
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, optBuild.getNamesList().size());
    assertEquals("Optional", optBuild.getNames(0));

    ASTMCOptionalType optBuildWithWrongName = MCCollectionTypesMill.mCOptionalTypeBuilder()
        .setNamesList(Lists.newArrayList("Foo")).setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, optBuildWithWrongName.getNamesList().size());
    assertEquals("Optional", optBuildWithWrongName.getNames(0));

    ASTMCOptionalType optBuildManyNames = MCCollectionTypesMill.mCOptionalTypeBuilder()
        .setNamesList(Lists.newArrayList("java", "util", "Optional"))
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, optBuildManyNames.getNamesList().size());
    assertEquals("Optional", optBuildManyNames.getNames(0));
  }

  @Test
  public void mCSetTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'Set'
    ASTMCSetType setBuild = MCCollectionTypesMill.mCSetTypeBuilder()
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, setBuild.getNamesList().size());
    assertEquals("Set", setBuild.getNames(0));

    ASTMCSetType setBuildWithWrongName = MCCollectionTypesMill.mCSetTypeBuilder()
        .setNamesList(Lists.newArrayList("Foo"))
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, setBuildWithWrongName.getNamesList().size());
    assertEquals("Set", setBuildWithWrongName.getNames(0));

    ASTMCSetType setBuildManyNames = MCCollectionTypesMill.mCSetTypeBuilder()
        .setNamesList(Lists.newArrayList("java", "util", "Set"))
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt))
        .build();
    assertEquals(1, setBuildManyNames.getNamesList().size());
    assertEquals("Set", setBuildManyNames.getNames(0));
  }

  @Test
  public void mCMapTypeNameListFinalFromBuilder() {
    // test that MCListType only contains one element 'Map'
    ASTMCMapType mapBuildNoName = MCCollectionTypesMill.mCMapTypeBuilder()
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt, typeArgumentString))
        .build();
    assertEquals(1, mapBuildNoName.getNamesList().size());
    assertEquals("Map", mapBuildNoName.getNames(0));

    ASTMCMapType mapBuildWithWrongName = MCCollectionTypesMill.mCMapTypeBuilder()
        .setNamesList(Lists.newArrayList("Foo"))
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt, typeArgumentString))
        .build();
    assertEquals(1, mapBuildWithWrongName.getNamesList().size());
    assertEquals("Map", mapBuildWithWrongName.getNames(0));

    ASTMCMapType mapBuildManyNames = MCCollectionTypesMill.mCMapTypeBuilder()
        .setNamesList(Lists.newArrayList("java", "util", "Map"))
        .setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt, typeArgumentString)).build();
    assertEquals(1, mapBuildManyNames.getNamesList().size());
    assertEquals("Map", mapBuildManyNames.getNames(0));

    ASTMCMapType mapBuildWithKeyValue = MCCollectionTypesMill.mCMapTypeBuilder()
        .setKey(typeArgumentInt)
        .setValue(typeArgumentString)
        .build();
    assertEquals(1, mapBuildWithKeyValue.getNamesList().size());
    assertEquals("Map", mapBuildWithKeyValue.getNames(0));
  }


  @Test
  public void mCListTypeSetTypeArgument() {
    listTypeParser.setMCTypeArguments(0, typeArgumentString);
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", listTypeParser.getMCTypeArgument().printType(printer));
  }

  @Test
  public void mCListTypeSetTypeArgumentList() {
    listTypeParser.setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer",listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListTypeClearTypeArguments() {
    listTypeParser.clearMCTypeArguments();
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListAddTypeArgument() {
    listTypeParser.addMCTypeArguments(typeArgumentInt);
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    assertEquals("0xA6001 Not allowed to add an element to MCTypeArgumentList of ASTMCListType. " +
        "A MCTypeArgumentList must always have one element.", Log.getFindings().get(0).getMsg());
    Log.getFindings().clear();
  }

  @Test
  public void mCListAddAllTypeArgument() {
    listTypeParser.addAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListRemoveTypeArgument() {
    listTypeParser.removeMCTypeArguments(typeArgumentInt);
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListRemoveAllTypeArgument() {
    listTypeParser.removeAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListRemoveTypeArgumentIntParam() {
    listTypeParser.removeMCTypeArguments(1);
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListRetainTypeArgument() {
    // okay when original type is contained
    listTypeParser.retainAllMCTypeArguments(Lists.newArrayList(listTypeParser.getMCTypeArgument(), typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());


    // not okay if it will conclude in empty list
    listTypeParser.retainAllMCTypeArguments(Lists.newArrayList(typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListRemoveIfTypeArgument() {
    // okay when original type is contained
    listTypeParser.removeIfMCTypeArguments(x -> false);
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());

    // not okay if it will conclude in empty list
    listTypeParser.removeIfMCTypeArguments(x -> true);
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListForEachTypeArgumentIntParam() {
    listTypeParser.forEachMCTypeArguments(x -> x = typeArgumentString);
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void mCListAddTypeArgumentIntParam() {
    listTypeParser.addMCTypeArguments(1, typeArgumentInt);
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListAddAllTypeArgumentIntParam() {
    listTypeParser.addAllMCTypeArguments(1, Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mCListTypeSetTypeArgumentListIntParam() {
    listTypeParser.setMCTypeArguments(1, typeArgumentInt);
    assertEquals(1, listTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", listTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalTypeSetTypeArgument() {
    optTypeParser.setMCTypeArguments(0, typeArgumentString);
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", optTypeParser.getMCTypeArgument().printType(printer));
  }

  @Test
  public void mcOptionalTypeSetTypeArgumentList() {
    optTypeParser.setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalTypeClearTypeArguments() {
    optTypeParser.clearMCTypeArguments();
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalAddTypeArgument() {
    optTypeParser.addMCTypeArguments(typeArgumentInt);
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalAddAllTypeArgument() {
    optTypeParser.addAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalRemoveTypeArgument() {
    optTypeParser.removeMCTypeArguments(typeArgumentInt);
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalRemoveAllTypeArgument() {
    optTypeParser.removeAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalRemoveTypeArgumentIntParam() {
    optTypeParser.removeMCTypeArguments(1);
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalRetainTypeArgument() {
    // okay when original type is contained
    optTypeParser.retainAllMCTypeArguments(Lists.newArrayList(optTypeParser.getMCTypeArgument(), typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());


    // not okay if it will conclude in empty list
    optTypeParser.retainAllMCTypeArguments(Lists.newArrayList(typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalRemoveIfTypeArgument() {
    // okay when original type is contained
    optTypeParser.removeIfMCTypeArguments(x -> false);
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());

    // not okay if it will conclude in empty list
    optTypeParser.removeIfMCTypeArguments(x -> true);
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalForEachTypeArgumentIntParam() {
    optTypeParser.forEachMCTypeArguments(x -> x = typeArgumentString);
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void mcOptionalAddTypeArgumentIntParam() {
    optTypeParser.addMCTypeArguments(1, typeArgumentInt);
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalAddAllTypeArgumentIntParam() {
    optTypeParser.addAllMCTypeArguments(1, Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcOptionalTypeSetTypeArgumentListIntParam() {
    optTypeParser.setMCTypeArguments(1, typeArgumentInt);
    assertEquals(1, optTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", optTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetTypeSetTypeArgument() {
    setTypeParser.setMCTypeArguments(0, typeArgumentString);
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", setTypeParser.getMCTypeArgument().printType(printer));
  }

  @Test
  public void mcSetTypeSetTypeArgumentList() {
    setTypeParser.setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetTypeClearTypeArguments() {
    setTypeParser.clearMCTypeArguments();
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetAddTypeArgument() {
    setTypeParser.addMCTypeArguments(typeArgumentInt);
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetAddAllTypeArgument() {
    setTypeParser.addAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetRemoveTypeArgument() {
    setTypeParser.removeMCTypeArguments(typeArgumentInt);
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetRemoveAllTypeArgument() {
    setTypeParser.removeAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetRemoveTypeArgumentIntParam() {
    setTypeParser.removeMCTypeArguments(1);
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetRetainTypeArgument() {
    // okay when original type is contained
    setTypeParser.retainAllMCTypeArguments(Lists.newArrayList(setTypeParser.getMCTypeArgument(), typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());


    // not okay if it will conclude in empty list
    setTypeParser.retainAllMCTypeArguments(Lists.newArrayList(typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetRemoveIfTypeArgument() {
    // okay when original type is contained
    setTypeParser.removeIfMCTypeArguments(x -> false);
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());

    // not okay if it will conclude in empty list
    setTypeParser.removeIfMCTypeArguments(x -> true);
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetForEachTypeArgumentIntParam() {
    setTypeParser.forEachMCTypeArguments(x -> x = typeArgumentString);
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void mcSetAddTypeArgumentIntParam() {
    setTypeParser.addMCTypeArguments(1, typeArgumentInt);
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetAddAllTypeArgumentIntParam() {
    setTypeParser.addAllMCTypeArguments(1, Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcSetTypeSetTypeArgumentListIntParam() {
    setTypeParser.setMCTypeArguments(1, typeArgumentInt);
    assertEquals(1, setTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", setTypeParser.getMCTypeArgument().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapTypeSetTypeArgument() {
    mapTypeParser.setMCTypeArguments(0, typeArgumentString);
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
  }

  @Test
  public void mcMapTypeSetTypeArgumentList() {
    mapTypeParser.setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", mapTypeParser.getKey().printType(printer));
    assertEquals("String", mapTypeParser.getValue().printType(printer));
    assertEquals(0, Log.getErrorCount());

    mapTypeParser.setMCTypeArgumentsList(Lists.newArrayList(typeArgumentInt, typeArgumentString, typeArgumentInt));
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("Integer", mapTypeParser.getKey().printType(printer));
    assertEquals("String", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapTypeClearTypeArguments() {
    mapTypeParser.clearMCTypeArguments();
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapAddTypeArgument() {
    mapTypeParser.addMCTypeArguments(typeArgumentInt);
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapAddAllTypeArgument() {
    mapTypeParser.addAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapRemoveTypeArgument() {
    mapTypeParser.removeMCTypeArguments(typeArgumentInt);
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapRemoveAllTypeArgument() {
    mapTypeParser.removeAllMCTypeArguments(Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapRemoveTypeArgumentIntParam() {
    mapTypeParser.removeMCTypeArguments(1);
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapRetainTypeArgument() {
    // okay when original type is contained
    mapTypeParser.retainAllMCTypeArguments(Lists.newArrayList(mapTypeParser.getKey(), mapTypeParser.getValue(), typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    ;
    assertEquals(0, Log.getErrorCount());


    // not okay if it will conclude in empty list
    mapTypeParser.retainAllMCTypeArguments(Lists.newArrayList(typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapRemoveIfTypeArgument() {
    // okay when original type is contained
    mapTypeParser.removeIfMCTypeArguments(x -> false);
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(0, Log.getErrorCount());

    // not okay if it will conclude in empty list
    mapTypeParser.removeIfMCTypeArguments(x -> true);
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapForEachTypeArgumentIntParam() {
    mapTypeParser.forEachMCTypeArguments(x -> x = typeArgumentString);
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void mcMapAddTypeArgumentIntParam() {
    mapTypeParser.addMCTypeArguments(1, typeArgumentInt);
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapAddAllTypeArgumentIntParam() {
    mapTypeParser.addAllMCTypeArguments(1, Lists.newArrayList(typeArgumentInt, typeArgumentString));
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }

  @Test
  public void mcMapTypeSetTypeArgumentListIntParam() {
    mapTypeParser.setMCTypeArguments(1, typeArgumentInt);
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(0, Log.getErrorCount());

    mapTypeParser.setMCTypeArguments(3, typeArgumentInt);
    assertEquals(2, mapTypeParser.getMCTypeArgumentsList().size());
    assertEquals("String", mapTypeParser.getKey().printType(printer));
    assertEquals("Integer", mapTypeParser.getValue().printType(printer));
    assertEquals(1, Log.getErrorCount());
    Log.getFindings().clear();
  }
}
