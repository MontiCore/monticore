/* (c) https://github.com/MontiCore/monticore */
package mc.feature.referencesymbol;

import de.monticore.io.paths.ModelPath;
import mc.feature.referencesymbol.reference._ast.*;
import mc.feature.referencesymbol.reference._parser.ReferenceParser;
import mc.feature.referencesymbol.reference._symboltable.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ReferenceTest {

  private ASTRand astRand;
  private TestSymbol a;
  private TestSymbol b;
  private TestSymbol c;

  @Before
  public void setUp() throws IOException {
    ReferenceParser parser = new ReferenceParser();
    Optional<ASTRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/ReferenceModel.ref");
    assertTrue(astRand.isPresent());
    //create symboltable
    ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/feature/referencesymbol"));
    ReferenceLanguage lang = new ReferenceLanguage();
    ReferenceGlobalScope globalScope = new ReferenceGlobalScope(modelPath, lang);
    ReferenceSymbolTableCreatorDelegator symbolTableCreator = lang.getSymbolTableCreator(globalScope);
    ReferenceArtifactScope artifactScope = symbolTableCreator.createFromAST(astRand.get());

    Optional<IReferenceScope> scopeOpt = artifactScope.getSubScopes().stream().findAny();
    assertTrue(scopeOpt.isPresent());
    IReferenceScope innerScope = scopeOpt.get();

    Optional<TestSymbol> a = globalScope.resolveTest("ReferenceTest.A");
    Optional<TestSymbol> b = artifactScope.resolveTest("ReferenceTest.B");
    Optional<TestSymbol> c = innerScope.resolveTest("C");

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
    assertFalse(astb.get().getNameDefinitionOpt().isPresent());
    assertFalse(astb.get().getNameSymbolOpt().isPresent());
  }

  @Test
  public void testMandatory() {
    ASTTest astTest = astRand.getTest(0);


    ASTReferenceToTest astReferenceToTest = astRand.getReferenceToTest(0);

    //test getter
    assertNotNull(astTest.getEnclosingScope());
    assertNotNull(astReferenceToTest.getEnclosingScope());
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
    assertEquals(astReferenceToTest.getNameDefinitionOpt(), b.getAstNodeOpt());

    astReferenceToTest.setName(null);
    assertFalse(astReferenceToTest.isPresentNameDefinition());
    assertFalse(astReferenceToTest.isPresentNameSymbol());

  }

  @Test
  public void testOptional() {
    ASTTest astTest = astRand.getTest(0);


    ASTOptionalRef astOptionalRef = astRand.getOptionalRef(0);

    //test getter
    assertNotNull(astTest.getEnclosingScope());
    assertNotNull(astOptionalRef.getEnclosingScope());
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
    assertEquals(astOptionalRef.getNameDefinition(), b.getAstNodeOpt().get());
    assertEquals(astOptionalRef.getNameSymbolOpt().get(), b);

    astOptionalRef.setNameOpt(Optional.ofNullable("C"));
    assertTrue(astOptionalRef.isPresentNameSymbol());
    assertTrue(astOptionalRef.isPresentNameDefinition());
    assertEquals(astOptionalRef.getNameSymbolOpt().get(), c);
    assertEquals(astOptionalRef.getNameDefinitionOpt(), c.getAstNodeOpt());

    astOptionalRef.setNameAbsent();
    assertFalse(astOptionalRef.isPresentNameSymbol());
    assertFalse(astOptionalRef.isPresentNameDefinition());
  }

  @Test
  public void testListNoSymbolTable() {
    ASTListRef astListRef = astRand.getListRef(0);

    assertNotNull(astListRef.getEnclosingScope());

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


    assertNotNull(astListRef.getEnclosingScope());
    assertFalse(astListRef.isEmptyNamesSymbol());
    assertEquals(astListRef.sizeNamesSymbol(), 3);

    assertTrue(astListRef.getNamesSymbol(0).isPresent());
    assertTrue(astListRef.getNamesSymbol(1).isPresent());
    assertTrue(astListRef.getNamesSymbol(2).isPresent());
    assertEquals(astListRef.getNamesSymbol(0).get(), a);
    assertEquals(astListRef.getNamesSymbol(1).get(), b);
    assertEquals(astListRef.getNamesSymbol(2).get(), c);

    assertTrue(astListRef.containsNamesSymbol(Optional.ofNullable(a)));
    assertTrue(astListRef.containsNamesSymbol(Optional.ofNullable(b)));
    assertTrue(astListRef.containsNamesSymbol(Optional.ofNullable(c)));

  }

  @Test
  public void testListDefinition() {
    ASTListRef astListRef = astRand.getListRef(0);

    assertNotNull(astListRef.getEnclosingScope());

    assertFalse(astListRef.isEmptyNamesDefinition());
    assertEquals(astListRef.sizeNamesDefinition(), 3);

    assertTrue(astListRef.getNamesDefinition(0).isPresent());
    assertTrue(astListRef.getNamesDefinition(1).isPresent());
    assertTrue(astListRef.getNamesDefinition(2).isPresent());
    assertEquals(astListRef.getNamesDefinition(0), a.getAstNodeOpt());
    assertEquals(astListRef.getNamesDefinition(1), b.getAstNodeOpt());
    assertEquals(astListRef.getNamesDefinition(2), c.getAstNodeOpt());


    assertTrue(astListRef.containsNamesDefinition(a.getAstNodeOpt()));
    assertTrue(astListRef.containsNamesDefinition(b.getAstNodeOpt()));
    assertTrue(astListRef.containsNamesDefinition(c.getAstNodeOpt()));


    astListRef.setName(0, "C");
    assertEquals(astListRef.getNamesDefinition(0), c.getAstNodeOpt());
    assertEquals(astListRef.sizeNamesDefinition(), 3);

    astListRef.addName("A");
    assertEquals(astListRef.sizeNamesDefinition(), 4);
    List<Optional<ASTTest>> testList = new ArrayList<>();
    testList.add(c.getAstNodeOpt());
    testList.add(b.getAstNodeOpt());
    testList.add(c.getAstNodeOpt());
    testList.add(a.getAstNodeOpt());
    assertEquals(astListRef.getNamesDefinitionList(), testList);
    assertEquals(astListRef.toArrayNamesDefinition(), testList.toArray());
  }

  @Test
  public void testListEmpty() {
    ASTListRef astListRef = astRand.getListRef(1);
    assertNotNull(astListRef.getEnclosingScope());

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
    assertEquals(astListRef.getNamesSymbol(0).get(), a);
    assertEquals(astListRef.getNamesDefinition(0), a.getAstNodeOpt());
  }

  @Test
  public void testListNoSymbol() {
    //test what happens if the Name has no referenced Symbol
    ASTListRef astListRef = astRand.getListRef(2);
    assertNotNull(astListRef.getEnclosingScope());

    assertFalse(astListRef.isEmptyNamesSymbol());
    assertEquals(astListRef.sizeNamesSymbol(), 4);
    assertFalse(astListRef.isEmptyNamesDefinition());
    assertEquals(astListRef.sizeNamesDefinition(), 4);
    assertFalse(astListRef.isEmptyNames());
    assertEquals(astListRef.sizeNames(), 4);

    //D is no symbol in the model
    assertEquals(astListRef.getName(3), "D");
    assertFalse(astListRef.getNamesSymbol(3).isPresent());
    assertFalse(astListRef.getNamesDefinition(3).isPresent());
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
