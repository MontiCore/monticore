package mc.feature.referencesymbol;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.feature.referencesymbol.reference._ast.*;
import mc.feature.referencesymbol.reference._parser.ReferenceParser;
import mc.feature.referencesymbol.reference._symboltable.ReferenceLanguage;
import mc.feature.referencesymbol.reference._symboltable.ReferenceSymbolTableCreator;
import mc.feature.referencesymbol.reference._symboltable.TestSymbol;
import org.junit.Before;
import org.junit.Test;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ReferenceTest {

  ASTRand astRand;
  TestSymbol a;
  TestSymbol b;
  TestSymbol c;

  @Before
  public void setUp() throws IOException {
    ReferenceParser parser = new ReferenceParser();
    Optional<ASTRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/ReferenceModel.ref");
    assertTrue(astRand.isPresent());
    //create symboltable
    ModelPath modelPath = new ModelPath(Paths.get("src/tests/resources/mc/feature/referencesymbol"));
    ModelingLanguage lang = new ReferenceLanguage();
    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(lang.getResolvingFilters());
    GlobalScope globalScope = new GlobalScope(modelPath, lang, resolvingConfiguration);
    ReferenceSymbolTableCreator symbolTableCreator = new ReferenceSymbolTableCreator(resolvingConfiguration, globalScope);
    symbolTableCreator.createFromAST(astRand.get());

    Optional<TestSymbol> a = globalScope.resolve("A", TestSymbol.KIND);
    Optional<TestSymbol> b = globalScope.resolve("B", TestSymbol.KIND);
    Optional<TestSymbol> c = globalScope.resolve("C", TestSymbol.KIND);

    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    assertTrue(c.isPresent());
    this.a = a.get();
    this.b = b.get();
    this.c = c.get();
    this.astRand = astRand.get();
  }

  @Test
  public void testNoSymbolTable() throws IOException {
    ReferenceParser parser = new ReferenceParser();
    Optional<ASTTest> asta = parser.parse_StringTest("symbol TestA ;");
    Optional<ASTReferenceToTest> astb = parser.parse_StringReferenceToTest("ref TestA ;");
    assertFalse(parser.hasErrors());
    assertTrue(asta.isPresent());
    assertTrue(astb.isPresent());
    assertFalse(astb.get().isPresentNameDefinition());
    assertFalse(astb.get().isPresentNameSymbol());
    assertTrue(astb.get().getNameDefinitionOpt().equals(Optional.empty()));
    assertTrue(astb.get().getNameSymbolOpt().equals(Optional.empty()));
  }

  @Test
  public void testMandatory() {
    ASTTest astTest = astRand.getTest(0);


    ASTReferenceToTest astReferenceToTest = astRand.getReferenceToTest(0);

    //test getter
    assertTrue(astTest.isPresentEnclosingScope());
    assertTrue(astReferenceToTest.isPresentEnclosingScope());
    assertTrue(astReferenceToTest.isPresentNameSymbol());
    assertTrue(astTest.isPresentTestSymbol());
    assertTrue(astReferenceToTest.isPresentNameSymbol());

    assertTrue(astReferenceToTest.isPresentNameDefinition());

    assertEquals(astReferenceToTest.getNameDefinition(), astTest);
    assertEquals(astReferenceToTest.getNameSymbolOpt().get(), a);

    //test setter
    astReferenceToTest.setName("B");
    assertTrue(astReferenceToTest.isPresentNameSymbol());
    assertTrue(astReferenceToTest.isPresentNameDefinition());
    assertEquals(astReferenceToTest.getNameSymbolOpt().get(), b);
    assertEquals(astReferenceToTest.getNameDefinitionOpt(), b.getTestNode());

    astReferenceToTest.setName(null);
    assertFalse(astReferenceToTest.isPresentNameDefinition());
    assertFalse(astReferenceToTest.isPresentNameSymbol());

  }

  @Test
  public void testOptional() throws IOException {
    ASTTest astTest = astRand.getTest(0);


    ASTOptionalRef astOptionalRef = astRand.getOptionalRef(0);

    //test getter
    assertTrue(astTest.isPresentEnclosingScope());
    assertTrue(astOptionalRef.isPresentEnclosingScope());
    assertTrue(astOptionalRef.isPresentNameSymbol());
    assertTrue(astTest.isPresentTestSymbol());
    assertTrue(astOptionalRef.isPresentNameSymbol());

    assertTrue(astOptionalRef.isPresentNameDefinition());

    assertEquals(astOptionalRef.getNameDefinition(), astTest);
    assertEquals(astOptionalRef.getNameSymbolOpt().get(), a);

    //test setter
    astOptionalRef.setName("B");
    assertTrue(astOptionalRef.isPresentNameDefinition());
    assertTrue(astOptionalRef.isPresentNameSymbol());
    assertEquals(astOptionalRef.getNameDefinition(), b.getTestNode().get());
    assertEquals(astOptionalRef.getNameSymbolOpt().get(), b);

    astOptionalRef.setNameOpt(Optional.ofNullable("C"));
    assertTrue(astOptionalRef.isPresentNameSymbol());
    assertTrue(astOptionalRef.isPresentNameDefinition());
    assertEquals(astOptionalRef.getNameSymbolOpt().get(), c);
    assertEquals(astOptionalRef.getNameDefinitionOpt(), c.getTestNode());

    astOptionalRef.setNameAbsent();
    assertFalse(astOptionalRef.isPresentNameSymbol());
    assertFalse(astOptionalRef.isPresentNameDefinition());
  }

  @Test
  public void testListNoSymbolTable() {
    ASTListRef astListRef = astRand.getListRef(0);

    assertTrue(astListRef.isPresentEnclosingScope());

    //test setter
    assertFalse(astListRef.isEmptyNames());
    assertEquals(astListRef.sizeNames(), 3);
    assertEquals(astListRef.getName(0), "A");
    assertEquals(astListRef.getName(1), "B");
    assertEquals(astListRef.getName(2), "C");
    assertTrue(astListRef.containsName("A"));
    assertFalse(astListRef.removeName("D"));
    assertEquals(astListRef.removeName(1), "B");

    List<String> list = new ArrayList<>();
    list.add("A");
    list.add("C");
    assertEquals(astListRef.getNameList(), list);
  }

  @Test
  public void testListSymbolGet() {
    ASTListRef astListRef = astRand.getListRef(0);


    assertTrue(astListRef.isPresentEnclosingScope());
    assertFalse(astListRef.isEmptyNamesSymbol());
    assertEquals(astListRef.sizeNamesSymbol(), 3);

    assertTrue(astListRef.getNameSymbol(0).isPresent());
    assertTrue(astListRef.getNameSymbol(1).isPresent());
    assertTrue(astListRef.getNameSymbol(2).isPresent());
    assertEquals(astListRef.getNameSymbol(0).get(), a);
    assertEquals(astListRef.getNameSymbol(1).get(), b);
    assertEquals(astListRef.getNameSymbol(2).get(), c);

    assertTrue(astListRef.containsNameSymbol(Optional.ofNullable(a)));
    assertTrue(astListRef.containsNameSymbol(Optional.ofNullable(b)));
    assertTrue(astListRef.containsNameSymbol(Optional.ofNullable(c)));

  }

  @Test
  public void testListDefinition() {
    ASTListRef astListRef = astRand.getListRef(0);

    assertTrue(astListRef.isPresentEnclosingScope());

    assertFalse(astListRef.isEmptyNamesDefinition());
    assertEquals(astListRef.sizeNamesDefinition(), 3);

    assertTrue(astListRef.getNameDefinition(0).isPresent());
    assertTrue(astListRef.getNameDefinition(1).isPresent());
    assertTrue(astListRef.getNameDefinition(2).isPresent());
    assertEquals(astListRef.getNameDefinition(0), a.getTestNode());
    assertEquals(astListRef.getNameDefinition(1), b.getTestNode());
    assertEquals(astListRef.getNameDefinition(2), c.getTestNode());


    assertTrue(astListRef.containsNameDefinition(a.getTestNode()));
    assertTrue(astListRef.containsNameDefinition(b.getTestNode()));
    assertTrue(astListRef.containsNameDefinition(c.getTestNode()));


    astListRef.setName(0, "C");
    assertEquals(astListRef.getNameDefinition(0), c.getTestNode());
    assertEquals(astListRef.sizeNamesDefinition(), 3);

    astListRef.addName("A");
    assertEquals(astListRef.sizeNamesDefinition(), 4);
    List<Optional<ASTTest>> testList = new ArrayList<>();
    testList.add(c.getTestNode());
    testList.add(b.getTestNode());
    testList.add(c.getTestNode());
    testList.add(a.getTestNode());
    assertEquals(astListRef.getNamesDefinitionList(), testList);
    assertEquals(astListRef.toArrayNamesDefinition(), testList.toArray());
  }

  @Test
  public void testListEmpty() {
    ASTListRef astListRef = astRand.getListRef(1);
    assertTrue(astListRef.isPresentEnclosingScope());

    assertTrue(astListRef.isEmptyNamesSymbol());
    assertEquals(astListRef.sizeNamesSymbol(), 0);
    assertTrue(astListRef.isEmptyNamesDefinition());
    assertEquals(astListRef.sizeNamesDefinition(), 0);
    assertTrue(astListRef.isEmptyNames());
    assertEquals(astListRef.sizeNames(), 0);

    //add a name
    astListRef.addName("A");
    assertFalse(astListRef.isEmptyNamesSymbol());
    assertEquals(astListRef.sizeNamesSymbol(), 1);
    assertFalse(astListRef.isEmptyNamesDefinition());
    assertEquals(astListRef.sizeNamesDefinition(), 1);
    assertFalse(astListRef.isEmptyNames());
    assertEquals(astListRef.sizeNames(), 1);

    assertEquals(astListRef.getName(0), "A");
    assertEquals(astListRef.getNameSymbol(0).get(), a);
    assertEquals(astListRef.getNameDefinition(0), a.getTestNode());
  }

  @Test
  public void testListNoSymbol() {
    //test what happens if the Name has no referenced Symbol
    ASTListRef astListRef = astRand.getListRef(2);
    assertTrue(astListRef.isPresentEnclosingScope());

    assertFalse(astListRef.isEmptyNamesSymbol());
    assertEquals(astListRef.sizeNamesSymbol(), 4);
    assertFalse(astListRef.isEmptyNamesDefinition());
    assertEquals(astListRef.sizeNamesDefinition(), 4);
    assertFalse(astListRef.isEmptyNames());
    assertEquals(astListRef.sizeNames(), 4);

    //D is no symbol in the model
    assertEquals(astListRef.getName(3), "D");
    assertFalse(astListRef.getNameSymbol(3).isPresent());
    assertFalse(astListRef.getNameDefinition(3).isPresent());
  }

  @Test
  public void testBuilderMandatory() {
    ASTReferenceToTestBuilder builder = ReferenceMill.referenceToTestBuilder();
    ASTReferenceToTest astReferenceToTest = builder.setName("A").build();
    assertEquals(astReferenceToTest.getName(), "A");
  }

  @Test
  public void testBuilderOptional() {
    ASTOptionalRefBuilder builder = ReferenceMill.optionalRefBuilder();
    ASTOptionalRef astOptionalRef = builder.setName("B").build();
    assertEquals(astOptionalRef.getName(), "B");
  }

  @Test
  public void testBuilderList() {
    ASTListRefBuilder builder = ReferenceMill.listRefBuilder();
    List<String> names = new ArrayList<>();
    names.add("C");
    names.add("B");
    names.add("A");
    ASTListRef astListRef = builder.setNameList(names).build();
    assertFalse(astListRef.isEmptyNames());
    assertEquals(astListRef.getName(0), "C");
    assertEquals(astListRef.getName(1), "B");
    assertEquals(astListRef.getName(2), "A");
  }

  @Test
  public void testFactoryMandatory() {
    ASTReferenceToTest astReferenceToTest = ReferenceNodeFactory.createASTReferenceToTest("B");
    assertEquals(astReferenceToTest.getName(), "B");
  }

  @Test
  public void testFactoryOptional() {
    ASTOptionalRef astOptionalRef = ReferenceNodeFactory.createASTOptionalRef(Optional.of("C"));
    assertEquals(astOptionalRef.getName(), "C");
  }

  @Test
  public void testFactoryList() {
    List<String> names = new ArrayList<>();
    names.add("C");
    names.add("B");
    names.add("A");
    ASTListRef astListRef = ReferenceNodeFactory.createASTListRef(names);
    assertFalse(astListRef.isEmptyNames());
    assertEquals(astListRef.getName(0), "C");
    assertEquals(astListRef.getName(1), "B");
    assertEquals(astListRef.getName(2), "A");
  }

  @Test
  public void testNoReference() {
    ASTNoRef astNoRef = astRand.getNoRef(0);
    assertEquals(astNoRef.getName(), "a");
  }

}
