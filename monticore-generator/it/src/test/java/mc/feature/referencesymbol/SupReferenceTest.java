package mc.feature.referencesymbol;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.feature.referencesymbol.reference._ast.ASTTest;
import mc.feature.referencesymbol.reference._symboltable.TestSymbol;
import mc.feature.referencesymbol.supgrammarref._ast.ASTSupRand;
import mc.feature.referencesymbol.supgrammarref._ast.ASTSupRef;
import mc.feature.referencesymbol.supgrammarref._ast.ASTSupRefList;
import mc.feature.referencesymbol.supgrammarref._ast.ASTSupRefOpt;
import mc.feature.referencesymbol.supgrammarref._parser.SupGrammarRefParser;
import mc.feature.referencesymbol.supgrammarref._symboltable.SupGrammarRefLanguage;
import mc.feature.referencesymbol.supgrammarref._symboltable.SupGrammarRefScope;
import mc.feature.referencesymbol.supgrammarref._symboltable.SupGrammarRefSymbolTableCreator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SupReferenceTest {
  ASTSupRand astsupRand;
  TestSymbol a;
  TestSymbol b;
  TestSymbol c;
  TestSymbol d;

  @Before
  public void setUp() throws IOException {

    SupGrammarRefParser parser = new SupGrammarRefParser();
    Optional<ASTSupRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/SupReferenceModel.ref");
    assertFalse(parser.hasErrors());
    assertTrue(astRand.isPresent());
    //create symboltable
    this.astsupRand = astRand.get();


    ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/feature/referencesymbol"));
    ModelingLanguage lang = new SupGrammarRefLanguage();
    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(lang.getResolvingFilters());
    GlobalScope globalScope = new GlobalScope(modelPath, lang, resolvingConfiguration);
    SupGrammarRefSymbolTableCreator symbolTableCreator = new SupGrammarRefSymbolTableCreator(resolvingConfiguration, globalScope);
    SupGrammarRefScope fromAST = (SupGrammarRefScope) symbolTableCreator.createFromAST(astsupRand);

    Optional<TestSymbol> a = globalScope.resolve("SupReferenceTest.A", TestSymbol.KIND);
    Optional<TestSymbol> b = fromAST.resolve("B", TestSymbol.KIND);
    Optional<TestSymbol> c = fromAST.resolve("C", TestSymbol.KIND);
    Optional<TestSymbol> d = fromAST.resolve("D", TestSymbol.KIND);

    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    assertTrue(c.isPresent());
    assertTrue(d.isPresent());
    this.a = a.get();
    this.b = b.get();
    this.c = c.get();
    this.d = d.get();
  }


  @Test
  public void testWithoutSymbolTable() throws IOException {
    SupGrammarRefParser parser = new SupGrammarRefParser();
    Optional<ASTSupRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/SupReferenceModel.ref");
    assertFalse(parser.hasErrors());
    assertTrue(astRand.isPresent());
    ASTSupRef supRef = astRand.get().getSupRef(0);

    assertFalse(supRef.isPresentNameDefinition());
    assertFalse(supRef.isPresentNameSymbol());
    assertEquals(supRef.getNameDefinitionOpt(), Optional.empty());
    assertEquals(supRef.getNameSymbolOpt(), Optional.empty());
    assertEquals("A", supRef.getName());
    supRef.setName("B");
    assertEquals("B", supRef.getName());
  }

  @Test
  public void testWithoutSymbolTableOpt() throws IOException {
    SupGrammarRefParser parser = new SupGrammarRefParser();
    Optional<ASTSupRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/SupReferenceModel.ref");
    assertFalse(parser.hasErrors());
    assertTrue(astRand.isPresent());
    ASTSupRefOpt supRefOpt = astRand.get().getSupRefOpt(0);

    assertFalse(supRefOpt.isPresentNameDefinition());
    assertFalse(supRefOpt.isPresentNameSymbol());
    assertTrue(supRefOpt.getNameDefinitionOpt().equals(Optional.empty()));
    assertTrue(supRefOpt.getNameSymbolOpt().equals(Optional.empty()));
    assertEquals("A", supRefOpt.getName());
    supRefOpt.setName("B");
    assertEquals("B", supRefOpt.getName());
  }

  @Test
  public void testWithoutSymbolTableList() throws IOException {
    SupGrammarRefParser parser = new SupGrammarRefParser();
    Optional<ASTSupRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/SupReferenceModel.ref");
    assertFalse(parser.hasErrors());
    assertTrue(astRand.isPresent());
    ASTSupRefList supRefList = astRand.get().getSupRefList(0);

    assertTrue(supRefList.getNamesDefinitionList().isEmpty());
    assertTrue(supRefList.getNamesSymbolList().isEmpty());
    assertFalse(supRefList.getNameList().isEmpty());
    assertEquals(supRefList.sizeNames(), 3);
    assertEquals(supRefList.sizeNamesDefinition(), 0);
    assertEquals(supRefList.sizeNamesSymbol(), 0);
    assertEquals("A", supRefList.getName(0));
    supRefList.setName(0, "B");
    assertEquals("B", supRefList.getName(0));
  }

  @Test
  public void testSupRef() {
    ASTSupRef supRef = this.astsupRand.getSupRef(1);
    assertTrue(supRef.isPresentNameDefinition());
    assertTrue(supRef.isPresentNameSymbol());
    assertEquals(supRef.getName(), "B");

    assertEquals(supRef.getNameSymbolOpt(), Optional.ofNullable(b));
    assertEquals(supRef.getNameSymbol(), b);

    assertEquals(supRef.getNameDefinitionOpt(), b.getTestNode());
    assertEquals(supRef.getNameDefinition(), b.getTestNode().get());
  }

  @Test
  public void testSupRefSet() {
    ASTSupRef supRef = this.astsupRand.getSupRef(1);

    //setName
    supRef.setName("C");
    assertTrue(supRef.isPresentNameDefinition());
    assertTrue(supRef.isPresentNameSymbol());
    assertEquals(supRef.getName(), "C");

    assertEquals(supRef.getNameSymbolOpt(), Optional.ofNullable(c));
    assertEquals(supRef.getNameSymbol(), c);

    assertEquals(supRef.getNameDefinitionOpt(), c.getTestNode());
    assertEquals(supRef.getNameDefinition(), c.getTestNode().get());
  }

  @Test
  public void testSupRefOpt() {
    ASTSupRefOpt supRefOpt = this.astsupRand.getSupRefOpt(0);
    assertTrue(supRefOpt.isPresentNameDefinition());
    assertTrue(supRefOpt.isPresentNameSymbol());
    assertTrue(supRefOpt.isPresentName());
    assertEquals(supRefOpt.getName(), "A");

    assertEquals(supRefOpt.getNameSymbolOpt(), Optional.ofNullable(a));
    assertEquals(supRefOpt.getNameSymbol(), a);

    assertEquals(supRefOpt.getNameDefinitionOpt(), a.getTestNode());
    assertEquals(supRefOpt.getNameDefinition(), a.getTestNode().get());
  }

  @Test
  public void testSupRefOptSet() {
    ASTSupRefOpt supRefOpt = this.astsupRand.getSupRefOpt(0);
    //setName
    supRefOpt.setName("C");
    assertTrue(supRefOpt.isPresentNameDefinition());
    assertTrue(supRefOpt.isPresentNameSymbol());
    assertTrue(supRefOpt.isPresentName());
    assertEquals(supRefOpt.getName(), "C");

    assertEquals(supRefOpt.getNameSymbolOpt(), Optional.ofNullable(c));
    assertEquals(supRefOpt.getNameSymbol(), c);

    assertEquals(supRefOpt.getNameDefinitionOpt(), c.getTestNode());
    assertEquals(supRefOpt.getNameDefinition(), c.getTestNode().get());
  }

  @Test
  public void testSupRefOptSetAbsent() {
    ASTSupRefOpt supRefOpt = this.astsupRand.getSupRefOpt(0);

    //setNameAbsent
    supRefOpt.setNameAbsent();
    assertFalse(supRefOpt.isPresentNameDefinition());
    assertFalse(supRefOpt.isPresentNameSymbol());
    assertFalse(supRefOpt.isPresentName());
  }

  @Test
  public void testSupRefList() {
    ASTSupRefList supRefList = astsupRand.getSupRefList(2);
    assertFalse(supRefList.getNamesDefinitionList().isEmpty());
    assertFalse(supRefList.getNamesSymbolList().isEmpty());
    assertFalse(supRefList.getNameList().isEmpty());

    assertEquals(supRefList.sizeNames(), 4);
    assertEquals(supRefList.sizeNamesDefinition(), 4);
    assertEquals(supRefList.sizeNamesSymbol(), 4);

    assertEquals("A", supRefList.getName(0));

    assertTrue(supRefList.getNameSymbol(0).isPresent());
    assertEquals(a, supRefList.getNameSymbol(0).get());

    assertTrue(supRefList.getNameDefinition(0).isPresent());
    assertEquals(a.getTestNode(), supRefList.getNameDefinition(0));

    assertTrue(supRefList.containsName("B"));
    assertTrue(supRefList.containsNameDefinition(b.getTestNode()));
    assertTrue(supRefList.containsNameSymbol(Optional.ofNullable(b)));

    assertEquals(supRefList.toArrayNames().length, 4);
    assertEquals(supRefList.toArrayNamesSymbol().length, 4);
    assertEquals(supRefList.toArrayNamesDefinition().length, 4);
  }

  @Test
  public void testSupRefListSetEmpty() {
    ASTSupRefList supRefList = astsupRand.getSupRefList(0);
    //setEmptyList
    supRefList.setNameList(new ArrayList<>());
    assertTrue(supRefList.getNamesDefinitionList().isEmpty());
    assertTrue(supRefList.getNamesSymbolList().isEmpty());
    assertTrue(supRefList.getNameList().isEmpty());
  }

  @Test
  public void testSupRefListAdd() {
    ASTSupRefList supRefList = astsupRand.getSupRefList(1);

    //add "D"
    supRefList.addName("D");
    assertFalse(supRefList.getNamesDefinitionList().isEmpty());
    assertFalse(supRefList.getNamesSymbolList().isEmpty());
    assertFalse(supRefList.getNameList().isEmpty());

    assertEquals("D", supRefList.getName(0));

    assertTrue(supRefList.getNameSymbol(0).isPresent());
    assertEquals(d, supRefList.getNameSymbol(0).get());

    assertTrue(supRefList.getNameDefinition(0).isPresent());
    assertEquals(d.getTestNode(), supRefList.getNameDefinition(0));
  }


  @Test
  public void testSupRefListSet() {
    ASTSupRefList supRefList = astsupRand.getSupRefList(1);
    List<String> list = new ArrayList<>();
    list.add("B");
    list.add("A");
    //setNameList(list)
    supRefList.setNameList(list);

    assertEquals(supRefList.sizeNames(), 2);
    assertEquals(supRefList.sizeNamesDefinition(), 2);
    assertEquals(supRefList.sizeNamesSymbol(), 2);


    assertEquals("B", supRefList.getName(0));

    assertTrue(supRefList.getNameSymbol(0).isPresent());
    assertEquals(b, supRefList.getNameSymbol(0).get());

    assertTrue(supRefList.getNameDefinition(0).isPresent());
    assertEquals(b.getTestNode(), supRefList.getNameDefinition(0));

    assertTrue(supRefList.containsName("A"));
    assertTrue(supRefList.containsNameDefinition(a.getTestNode()));
    assertTrue(supRefList.containsNameSymbol(Optional.ofNullable(a)));
  }

  @Test
  public void testSupRefListRemove() {
    ASTSupRefList supRefList = astsupRand.getSupRefList(2);
    assertEquals(supRefList.sizeNames(), 4);
    assertEquals(supRefList.sizeNamesDefinition(), 4);
    assertEquals(supRefList.sizeNamesSymbol(), 4);

    //remove "B"
    supRefList.removeName("B");

    assertEquals(supRefList.sizeNames(), 3);
    assertEquals(supRefList.sizeNamesDefinition(), 3);
    assertEquals(supRefList.sizeNamesSymbol(), 3);


    assertEquals("C", supRefList.getName(1));

    assertTrue(supRefList.getNameSymbol(1).isPresent());
    assertEquals(c, supRefList.getNameSymbol(1).get());

    assertTrue(supRefList.getNameDefinition(1).isPresent());
    assertEquals(c.getTestNode(), supRefList.getNameDefinition(1));

    List<String> list = new ArrayList<>();
    list.add("A");
    list.add("C");
    list.add("D");
    assertEquals(supRefList.getNameList(), list);

    List<Optional<TestSymbol>> symbolList = new ArrayList<>();
    symbolList.add(Optional.ofNullable(a));
    symbolList.add(Optional.ofNullable(c));
    symbolList.add(Optional.ofNullable(d));
    assertEquals(supRefList.getNamesSymbolList(), symbolList);

    List<Optional<ASTTest>> definitionList = new ArrayList<>();
    definitionList.add(a.getTestNode());
    definitionList.add(c.getTestNode());
    definitionList.add(d.getTestNode());
    assertEquals(supRefList.getNamesDefinitionList(), definitionList);
  }
}
