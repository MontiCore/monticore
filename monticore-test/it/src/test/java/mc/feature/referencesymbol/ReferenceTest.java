/* (c) https://github.com/MontiCore/monticore */
package mc.feature.referencesymbol;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.referencesymbol.reference.ReferenceMill;
import mc.feature.referencesymbol.reference._ast.*;
import mc.feature.referencesymbol.reference._parser.ReferenceParser;
import mc.feature.referencesymbol.reference._symboltable.IReferenceArtifactScope;
import mc.feature.referencesymbol.reference._symboltable.IReferenceGlobalScope;
import mc.feature.referencesymbol.reference._symboltable.IReferenceScope;
import mc.feature.referencesymbol.reference._symboltable.TestSymbol;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
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
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);

    // reset global scope
    IReferenceGlobalScope globalScope = ReferenceMill.globalScope();
    globalScope.clearLoadedFiles();
    for (IReferenceScope s : globalScope.getSubScopes()) {
      globalScope.removeSubScope(s);
    }
    for (Path p : globalScope.getSymbolPath().getEntries()) {
      globalScope.getSymbolPath().removeEntry(p);
    }

    // populate symtab
    ReferenceParser parser = new ReferenceParser();
    Optional<ASTRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/ReferenceModel.ref");
    assertFalse(parser.hasErrors());
    assertTrue(astRand.isPresent());
    //create symboltable
    globalScope.setFileExt("ref");
    globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/referencesymbol"));

    IReferenceArtifactScope artifactScope = ReferenceMill
        .scopesGenitorDelegator().createFromAST(astRand.get());
    artifactScope.setName("ReferenceTest");

    Optional<? extends IReferenceScope> scopeOpt = artifactScope.getSubScopes().stream().findAny();
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
  }

  @Test
  public void testMandatory() {
    ASTTest astTest = astRand.getTest(0);

    ASTReferenceToTest astReferenceToTest = astRand.getReferenceToTest(0);

    //test getter
    assertNotNull(astTest.getEnclosingScope());
    assertNotNull(astReferenceToTest.getEnclosingScope());
    assertTrue(astReferenceToTest.isPresentNameSymbol());
    assertTrue(astTest.isPresentSymbol());
    assertTrue(astReferenceToTest.isPresentNameSymbol());

    assertTrue(astReferenceToTest.isPresentNameDefinition());

    assertEquals(astReferenceToTest.getNameDefinition(), astTest);
    assertEquals(astReferenceToTest.getNameSymbol(), a);

    //test setter
    astReferenceToTest = astRand.getReferenceToTest(1);
    assertTrue(astReferenceToTest.isPresentNameSymbol());
    assertTrue(astReferenceToTest.isPresentNameDefinition());
    assertEquals(astReferenceToTest.getNameSymbol(), b);
    assertEquals(astReferenceToTest.getNameDefinition(), b.getAstNode());
  }

  @Test
  public void testMandatoryWrongReference() {
    ASTReferenceToTest astReferenceToTest = astRand.getReferenceToTest(2);

    //test getter
    assertNotNull(astReferenceToTest.getEnclosingScope());
    assertFalse(astReferenceToTest.isPresentNameSymbol());
    assertEquals("Z", astReferenceToTest.getName());
    assertFalse(astReferenceToTest.isPresentNameSymbol());
    assertFalse(astReferenceToTest.isPresentNameDefinition());
  }

  @Test
  public void testOptional() {
    ASTTest astTest = astRand.getTest(0);


    ASTOptionalRef astOptionalRef = astRand.getOptionalRef(0);

    //test getter
    assertNotNull(astTest.getEnclosingScope());
    assertNotNull(astOptionalRef.getEnclosingScope());
    assertTrue(astOptionalRef.isPresentNameSymbol());
    assertTrue(astTest.isPresentSymbol());
    assertTrue(astOptionalRef.isPresentNameSymbol());

    assertTrue(astOptionalRef.isPresentNameDefinition());

    assertEquals(astOptionalRef.getNameDefinition(), astTest);
    assertEquals(astOptionalRef.getNameSymbol(), a);

    astOptionalRef = astRand.getOptionalRef(1);
    assertFalse(astOptionalRef.isPresentNameSymbol());
    assertFalse(astOptionalRef.isPresentNameDefinition());

    String name = astOptionalRef.getName();
    astOptionalRef.setNameAbsent();
    assertFalse(astOptionalRef.isPresentNameSymbol());
    assertFalse(astOptionalRef.isPresentNameDefinition());
    astOptionalRef.setName(name);
  }

  @Test
  public void testOptionalWrongReference() {
    ASTOptionalRef astReferenceToTest = astRand.getOptionalRef(1);

    //test getter
    assertNotNull(astReferenceToTest.getEnclosingScope());
    assertFalse(astReferenceToTest.isPresentNameSymbol());
    assertEquals("Z", astReferenceToTest.getName());
    assertFalse(astReferenceToTest.isPresentNameSymbol());
    assertFalse(astReferenceToTest.isPresentNameDefinition());
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

    // clear name list
    astListRef.clearNames();
    assertTrue(astListRef.isEmptyNamesSymbol());
    assertTrue(astListRef.isEmptyNamesDefinition());

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
    assertEquals(astListRef.getNamesDefinition(0).get(), a.getAstNode());
    assertEquals(astListRef.getNamesDefinition(1).get(), b.getAstNode());
    assertEquals(astListRef.getNamesDefinition(2).get(), c.getAstNode());


    assertTrue(astListRef.containsNamesDefinition(Optional.ofNullable(a.getAstNode())));
    assertTrue(astListRef.containsNamesDefinition(Optional.ofNullable(b.getAstNode())));
    assertTrue(astListRef.containsNamesDefinition(Optional.ofNullable(c.getAstNode())));


    astListRef.setName(0, "C");
    assertEquals(astListRef.getNamesDefinition(0), Optional.ofNullable(c.getAstNode()));
    assertEquals(astListRef.sizeNamesDefinition(), 3);

    astListRef.addName("A");
    assertEquals(astListRef.sizeNamesDefinition(), 4);
    List<Optional<ASTTest>> testList = new ArrayList<>();
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(b.getAstNode()));
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(a.getAstNode()));
    assertEquals(astListRef.getNamesDefinitionList(), testList);
    assertEquals(astListRef.toArrayNamesDefinition(), testList.toArray());
  }

  @Test
  public void testListWithNameDefinition() {
    ASTListRefWithName astListRef = astRand.getListRefWithName(0);

    assertNotNull(astListRef.getEnclosingScope());

    assertFalse(astListRef.isEmptyBlaDefinition());
    assertEquals(astListRef.sizeBlaDefinition(), 3);

    assertTrue(astListRef.getBlaDefinition(0).isPresent());
    assertTrue(astListRef.getBlaDefinition(1).isPresent());
    assertTrue(astListRef.getBlaDefinition(2).isPresent());
    assertEquals(astListRef.getBlaDefinition(0).get(), a.getAstNode());
    assertEquals(astListRef.getBlaDefinition(1).get(), b.getAstNode());
    assertEquals(astListRef.getBlaDefinition(2).get(), c.getAstNode());


    assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(a.getAstNode())));
    assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(b.getAstNode())));
    assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(c.getAstNode())));


    astListRef.setBla(0, "C");
    assertEquals(astListRef.getBlaDefinition(0), Optional.ofNullable(c.getAstNode()));
    assertEquals(astListRef.sizeBlaDefinition(), 3);

    astListRef.addBla("A");
    assertEquals(astListRef.sizeBlaDefinition(), 4);
    List<Optional<ASTTest>> testList = new ArrayList<>();
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(b.getAstNode()));
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(a.getAstNode()));
    assertEquals(astListRef.getBlaDefinitionList(), testList);
    assertEquals(astListRef.toArrayBlaDefinition(), testList.toArray());
  }

  @Test
  public void testListNTSDefinition() {
    ASTListRefNonTerminalSep astListRef = astRand.getListRefNonTerminalSep(0);

    assertNotNull(astListRef.getEnclosingScope());

    assertFalse(astListRef.isEmptyBlaDefinition());
    assertEquals(astListRef.sizeBlaDefinition(), 3);

    assertTrue(astListRef.getBlaDefinition(0).isPresent());
    assertTrue(astListRef.getBlaDefinition(1).isPresent());
    assertTrue(astListRef.getBlaDefinition(2).isPresent());
    assertEquals(astListRef.getBlaDefinition(0).get(), a.getAstNode());
    assertEquals(astListRef.getBlaDefinition(1).get(), b.getAstNode());
    assertEquals(astListRef.getBlaDefinition(2).get(), c.getAstNode());


    assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(a.getAstNode())));
    assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(b.getAstNode())));
    assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(c.getAstNode())));


    astListRef.setBla(0, "C");
    assertEquals(astListRef.getBlaDefinition(0), Optional.ofNullable(c.getAstNode()));
    assertEquals(astListRef.sizeBlaDefinition(), 3);

    astListRef.addBla("A");
    assertEquals(astListRef.sizeBlaDefinition(), 4);
    List<Optional<ASTTest>> testList = new ArrayList<>();
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(b.getAstNode()));
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(a.getAstNode()));
    assertEquals(astListRef.getBlaDefinitionList(), testList);
    assertEquals(astListRef.toArrayBlaDefinition(), testList.toArray());
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
    assertEquals(astListRef.getNamesDefinition(0).get(), a.getAstNode());
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
  public void testListWrongReference() {
    ASTListRef astReferenceToTest = astRand.getListRef(3);

    //test getter
    assertNotNull(astReferenceToTest.getEnclosingScope());
    assertFalse(astReferenceToTest.isEmptyNames());
    assertFalse(astReferenceToTest.isEmptyNamesDefinition());
    assertFalse(astReferenceToTest.isEmptyNamesSymbol());
    for (Optional<ASTTest> astTest : astReferenceToTest.getNamesDefinitionList()) {
      assertFalse(astTest.isPresent());
    }
    for (Optional<TestSymbol> testSymbol : astReferenceToTest.getNamesSymbolList()) {
      assertFalse(testSymbol.isPresent());
    }
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
    ASTListRef astListRef = builder.setNamesList(names).build();
    assertFalse(astListRef.isEmptyNames());
    assertEquals(astListRef.getName(0), "C");
    assertEquals(astListRef.getName(1), "B");
    assertEquals(astListRef.getName(2), "A");
  }

  @Test
  public void testFactoryMandatory() {
    ASTReferenceToTest astReferenceToTest = ReferenceMill.referenceToTestBuilder().setName("B").build();
    assertEquals(astReferenceToTest.getName(), "B");
  }

  @Test
  public void testFactoryOptional() {
    ASTOptionalRef astOptionalRef = ReferenceMill.optionalRefBuilder().setName("C").build();
    assertEquals(astOptionalRef.getName(), "C");
  }

  @Test
  public void testFactoryList() {
    List<String> names = new ArrayList<>();
    names.add("C");
    names.add("B");
    names.add("A");
    ASTListRef astListRef = ReferenceMill.listRefBuilder().setNamesList(names).build();
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
