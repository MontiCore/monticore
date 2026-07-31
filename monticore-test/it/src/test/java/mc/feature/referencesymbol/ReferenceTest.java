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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReferenceTest {

  private ASTRand astRand;
  private TestSymbol a;
  private TestSymbol b;
  private TestSymbol c;
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setUp() throws IOException {
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
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astRand.isPresent());
    //create symboltable
    globalScope.setFileExt("ref");
    globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/referencesymbol"));

    IReferenceArtifactScope artifactScope = ReferenceMill
        .scopesGenitorDelegator().createFromAST(astRand.get());
    artifactScope.setName("ReferenceTest");

    Optional<? extends IReferenceScope> scopeOpt = artifactScope.getSubScopes().stream().findAny();
    Assertions.assertTrue(scopeOpt.isPresent());
    IReferenceScope innerScope = scopeOpt.get();

    Optional<TestSymbol> a = globalScope.resolveTest("ReferenceTest.A");
    Optional<TestSymbol> b = artifactScope.resolveTest("ReferenceTest.B");
    Optional<TestSymbol> c = innerScope.resolveTest("C");

    Assertions.assertTrue(a.isPresent());
    Assertions.assertTrue(b.isPresent());
    Assertions.assertTrue(c.isPresent());
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
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(asta.isPresent());
    Assertions.assertTrue(astb.isPresent());
    Assertions.assertFalse(astb.get().isPresentNameDefinition());
    Assertions.assertFalse(astb.get().isPresentNameSymbol());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMandatory() {
    ASTTest astTest = astRand.getTest(0);

    ASTReferenceToTest astReferenceToTest = astRand.getReferenceToTest(0);

    //test getter
    Assertions.assertNotNull(astTest.getEnclosingScope());
    Assertions.assertNotNull(astReferenceToTest.getEnclosingScope());
    Assertions.assertTrue(astReferenceToTest.isPresentNameSymbol());
    Assertions.assertTrue(astTest.isPresentSymbol());
    Assertions.assertTrue(astReferenceToTest.isPresentNameSymbol());

    Assertions.assertTrue(astReferenceToTest.isPresentNameDefinition());

    Assertions.assertEquals(astReferenceToTest.getNameDefinition(), astTest);
    Assertions.assertEquals(astReferenceToTest.getNameSymbol(), a);

    //test setter
    astReferenceToTest = astRand.getReferenceToTest(1);
    Assertions.assertTrue(astReferenceToTest.isPresentNameSymbol());
    Assertions.assertTrue(astReferenceToTest.isPresentNameDefinition());
    Assertions.assertEquals(astReferenceToTest.getNameSymbol(), b);
    Assertions.assertEquals(astReferenceToTest.getNameDefinition(), b.getAstNode());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMandatoryWrongReference() {
    ASTReferenceToTest astReferenceToTest = astRand.getReferenceToTest(2);

    //test getter
    Assertions.assertNotNull(astReferenceToTest.getEnclosingScope());
    Assertions.assertFalse(astReferenceToTest.isPresentNameSymbol());
    Assertions.assertEquals("Z", astReferenceToTest.getName());
    Assertions.assertFalse(astReferenceToTest.isPresentNameSymbol());
    Assertions.assertFalse(astReferenceToTest.isPresentNameDefinition());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOptional() {
    ASTTest astTest = astRand.getTest(0);


    ASTOptionalRef astOptionalRef = astRand.getOptionalRef(0);

    //test getter
    Assertions.assertNotNull(astTest.getEnclosingScope());
    Assertions.assertNotNull(astOptionalRef.getEnclosingScope());
    Assertions.assertTrue(astOptionalRef.isPresentNameSymbol());
    Assertions.assertTrue(astTest.isPresentSymbol());
    Assertions.assertTrue(astOptionalRef.isPresentNameSymbol());

    Assertions.assertTrue(astOptionalRef.isPresentNameDefinition());

    Assertions.assertEquals(astOptionalRef.getNameDefinition(), astTest);
    Assertions.assertEquals(astOptionalRef.getNameSymbol(), a);

    astOptionalRef = astRand.getOptionalRef(1);
    Assertions.assertFalse(astOptionalRef.isPresentNameSymbol());
    Assertions.assertFalse(astOptionalRef.isPresentNameDefinition());

    String name = astOptionalRef.getName();
    astOptionalRef.setNameAbsent();
    Assertions.assertFalse(astOptionalRef.isPresentNameSymbol());
    Assertions.assertFalse(astOptionalRef.isPresentNameDefinition());
    astOptionalRef.setName(name);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOptionalWrongReference() {
    ASTOptionalRef astReferenceToTest = astRand.getOptionalRef(1);

    //test getter
    Assertions.assertNotNull(astReferenceToTest.getEnclosingScope());
    Assertions.assertFalse(astReferenceToTest.isPresentNameSymbol());
    Assertions.assertEquals("Z", astReferenceToTest.getName());
    Assertions.assertFalse(astReferenceToTest.isPresentNameSymbol());
    Assertions.assertFalse(astReferenceToTest.isPresentNameDefinition());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testListNoSymbolTable() {
    ASTListRef astListRef = astRand.getListRef(0);

    Assertions.assertNotNull(astListRef.getEnclosingScope());

    //test setter
    Assertions.assertFalse(astListRef.isEmptyNames());
    Assertions.assertEquals(3, astListRef.sizeNames());
    Assertions.assertEquals("A", astListRef.getName(0));
    Assertions.assertEquals("B", astListRef.getName(1));
    Assertions.assertEquals("C", astListRef.getName(2));
    Assertions.assertTrue(astListRef.containsName("A"));
    Assertions.assertFalse(astListRef.removeName("D"));
    Assertions.assertEquals("B", astListRef.removeName(1));

    List<String> list = new ArrayList<>();
    list.add("A");
    list.add("C");
    Assertions.assertEquals(astListRef.getNameList(), list);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testListSymbolGet() {
    ASTListRef astListRef = astRand.getListRef(0);

    Assertions.assertNotNull(astListRef.getEnclosingScope());
    Assertions.assertFalse(astListRef.isEmptyNamesSymbol());
    Assertions.assertEquals(3, astListRef.sizeNamesSymbol());

    Assertions.assertTrue(astListRef.getNamesSymbol(0).isPresent());
    Assertions.assertTrue(astListRef.getNamesSymbol(1).isPresent());
    Assertions.assertTrue(astListRef.getNamesSymbol(2).isPresent());
    Assertions.assertEquals(astListRef.getNamesSymbol(0).get(), a);
    Assertions.assertEquals(astListRef.getNamesSymbol(1).get(), b);
    Assertions.assertEquals(astListRef.getNamesSymbol(2).get(), c);

    Assertions.assertTrue(astListRef.containsNamesSymbol(Optional.ofNullable(a)));
    Assertions.assertTrue(astListRef.containsNamesSymbol(Optional.ofNullable(b)));
    Assertions.assertTrue(astListRef.containsNamesSymbol(Optional.ofNullable(c)));

    // clear name list
    astListRef.clearNames();
    Assertions.assertTrue(astListRef.isEmptyNamesSymbol());
    Assertions.assertTrue(astListRef.isEmptyNamesDefinition());
    Assertions.assertTrue(Log.getFindings().isEmpty());

  }

  @Test
  public void testListDefinition() {
    ASTListRef astListRef = astRand.getListRef(0);

    Assertions.assertNotNull(astListRef.getEnclosingScope());

    Assertions.assertFalse(astListRef.isEmptyNamesDefinition());
    Assertions.assertEquals(3, astListRef.sizeNamesDefinition());

    Assertions.assertTrue(astListRef.getNamesDefinition(0).isPresent());
    Assertions.assertTrue(astListRef.getNamesDefinition(1).isPresent());
    Assertions.assertTrue(astListRef.getNamesDefinition(2).isPresent());
    Assertions.assertEquals(astListRef.getNamesDefinition(0).get(), a.getAstNode());
    Assertions.assertEquals(astListRef.getNamesDefinition(1).get(), b.getAstNode());
    Assertions.assertEquals(astListRef.getNamesDefinition(2).get(), c.getAstNode());


    Assertions.assertTrue(astListRef.containsNamesDefinition(Optional.ofNullable(a.getAstNode())));
    Assertions.assertTrue(astListRef.containsNamesDefinition(Optional.ofNullable(b.getAstNode())));
    Assertions.assertTrue(astListRef.containsNamesDefinition(Optional.ofNullable(c.getAstNode())));


    astListRef.setName(0, "C");
    Assertions.assertEquals(astListRef.getNamesDefinition(0), Optional.ofNullable(c.getAstNode()));
    Assertions.assertEquals(3, astListRef.sizeNamesDefinition());

    astListRef.addName("A");
    Assertions.assertEquals(4, astListRef.sizeNamesDefinition());
    List<Optional<ASTTest>> testList = new ArrayList<>();
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(b.getAstNode()));
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(a.getAstNode()));
    Assertions.assertEquals(astListRef.getNamesDefinitionList(), testList);
    Assertions.assertArrayEquals(astListRef.toArrayNamesDefinition(), testList.toArray());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testListWithNameDefinition() {
    ASTListRefWithName astListRef = astRand.getListRefWithName(0);

    Assertions.assertNotNull(astListRef.getEnclosingScope());

    Assertions.assertFalse(astListRef.isEmptyBlaDefinition());
    Assertions.assertEquals(3, astListRef.sizeBlaDefinition());

    Assertions.assertTrue(astListRef.getBlaDefinition(0).isPresent());
    Assertions.assertTrue(astListRef.getBlaDefinition(1).isPresent());
    Assertions.assertTrue(astListRef.getBlaDefinition(2).isPresent());
    Assertions.assertEquals(astListRef.getBlaDefinition(0).get(), a.getAstNode());
    Assertions.assertEquals(astListRef.getBlaDefinition(1).get(), b.getAstNode());
    Assertions.assertEquals(astListRef.getBlaDefinition(2).get(), c.getAstNode());


    Assertions.assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(a.getAstNode())));
    Assertions.assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(b.getAstNode())));
    Assertions.assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(c.getAstNode())));


    astListRef.setBla(0, "C");
    Assertions.assertEquals(astListRef.getBlaDefinition(0), Optional.ofNullable(c.getAstNode()));
    Assertions.assertEquals(3, astListRef.sizeBlaDefinition());

    astListRef.addBla("A");
    Assertions.assertEquals(4, astListRef.sizeBlaDefinition());
    List<Optional<ASTTest>> testList = new ArrayList<>();
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(b.getAstNode()));
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(a.getAstNode()));
    Assertions.assertEquals(astListRef.getBlaDefinitionList(), testList);
    Assertions.assertArrayEquals(astListRef.toArrayBlaDefinition(), testList.toArray());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testListNTSDefinition() {
    ASTListRefNonTerminalSep astListRef = astRand.getListRefNonTerminalSep(0);

    Assertions.assertNotNull(astListRef.getEnclosingScope());

    Assertions.assertFalse(astListRef.isEmptyBlaDefinition());
    Assertions.assertEquals(3, astListRef.sizeBlaDefinition());

    Assertions.assertTrue(astListRef.getBlaDefinition(0).isPresent());
    Assertions.assertTrue(astListRef.getBlaDefinition(1).isPresent());
    Assertions.assertTrue(astListRef.getBlaDefinition(2).isPresent());
    Assertions.assertEquals(astListRef.getBlaDefinition(0).get(), a.getAstNode());
    Assertions.assertEquals(astListRef.getBlaDefinition(1).get(), b.getAstNode());
    Assertions.assertEquals(astListRef.getBlaDefinition(2).get(), c.getAstNode());


    Assertions.assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(a.getAstNode())));
    Assertions.assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(b.getAstNode())));
    Assertions.assertTrue(astListRef.containsBlaDefinition(Optional.ofNullable(c.getAstNode())));


    astListRef.setBla(0, "C");
    Assertions.assertEquals(astListRef.getBlaDefinition(0), Optional.ofNullable(c.getAstNode()));
    Assertions.assertEquals(3, astListRef.sizeBlaDefinition());

    astListRef.addBla("A");
    Assertions.assertEquals(4, astListRef.sizeBlaDefinition());
    List<Optional<ASTTest>> testList = new ArrayList<>();
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(b.getAstNode()));
    testList.add(Optional.ofNullable(c.getAstNode()));
    testList.add(Optional.ofNullable(a.getAstNode()));
    Assertions.assertEquals(astListRef.getBlaDefinitionList(), testList);
    Assertions.assertArrayEquals(astListRef.toArrayBlaDefinition(), testList.toArray());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testListEmpty() {
    ASTListRef astListRef = astRand.getListRef(1);
    Assertions.assertNotNull(astListRef.getEnclosingScope());

    Assertions.assertTrue(astListRef.isEmptyNamesSymbol());
    Assertions.assertEquals(0, astListRef.sizeNamesSymbol());
    Assertions.assertTrue(astListRef.isEmptyNamesDefinition());
    Assertions.assertEquals(0, astListRef.sizeNamesDefinition());
    Assertions.assertTrue(astListRef.isEmptyNames());
    Assertions.assertEquals(0, astListRef.sizeNames());

    //add a name
    astListRef.addName("A");
    Assertions.assertFalse(astListRef.isEmptyNamesSymbol());
    Assertions.assertEquals(1, astListRef.sizeNamesSymbol());
    Assertions.assertFalse(astListRef.isEmptyNamesDefinition());
    Assertions.assertEquals(1, astListRef.sizeNamesDefinition());
    Assertions.assertFalse(astListRef.isEmptyNames());
    Assertions.assertEquals(1, astListRef.sizeNames());

    Assertions.assertEquals("A", astListRef.getName(0));
    Assertions.assertEquals(astListRef.getNamesSymbol(0).get(), a);
    Assertions.assertEquals(astListRef.getNamesDefinition(0).get(), a.getAstNode());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testListNoSymbol() {
    //test what happens if the Name has no referenced Symbol
    ASTListRef astListRef = astRand.getListRef(2);
    Assertions.assertNotNull(astListRef.getEnclosingScope());

    Assertions.assertFalse(astListRef.isEmptyNamesSymbol());
    Assertions.assertEquals(4, astListRef.sizeNamesSymbol());
    Assertions.assertFalse(astListRef.isEmptyNamesDefinition());
    Assertions.assertEquals(4, astListRef.sizeNamesDefinition());
    Assertions.assertFalse(astListRef.isEmptyNames());
    Assertions.assertEquals(4, astListRef.sizeNames());

    //D is no symbol in the model
    Assertions.assertEquals("D", astListRef.getName(3));
    Assertions.assertFalse(astListRef.getNamesSymbol(3).isPresent());
    Assertions.assertFalse(astListRef.getNamesDefinition(3).isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testListWrongReference() {
    ASTListRef astReferenceToTest = astRand.getListRef(3);

    //test getter
    Assertions.assertNotNull(astReferenceToTest.getEnclosingScope());
    Assertions.assertFalse(astReferenceToTest.isEmptyNames());
    Assertions.assertFalse(astReferenceToTest.isEmptyNamesDefinition());
    Assertions.assertFalse(astReferenceToTest.isEmptyNamesSymbol());
    for (Optional<ASTTest> astTest : astReferenceToTest.getNamesDefinitionList()) {
      Assertions.assertFalse(astTest.isPresent());
    }
    for (Optional<TestSymbol> testSymbol : astReferenceToTest.getNamesSymbolList()) {
      Assertions.assertFalse(testSymbol.isPresent());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBuilderMandatory() {
    ASTReferenceToTestBuilder builder = ReferenceMill.referenceToTestBuilder();
    ASTReferenceToTest astReferenceToTest = builder.setName("A").build();
    Assertions.assertEquals("A", astReferenceToTest.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBuilderOptional() {
    ASTOptionalRefBuilder builder = ReferenceMill.optionalRefBuilder();
    ASTOptionalRef astOptionalRef = builder.setName("B").build();
    Assertions.assertEquals("B", astOptionalRef.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBuilderList() {
    ASTListRefBuilder builder = ReferenceMill.listRefBuilder();
    List<String> names = new ArrayList<>();
    names.add("C");
    names.add("B");
    names.add("A");
    ASTListRef astListRef = builder.setNamesList(names).build();
    Assertions.assertFalse(astListRef.isEmptyNames());
    Assertions.assertEquals("C", astListRef.getName(0));
    Assertions.assertEquals("B", astListRef.getName(1));
    Assertions.assertEquals("A", astListRef.getName(2));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFactoryMandatory() {
    ASTReferenceToTest astReferenceToTest = ReferenceMill.referenceToTestBuilder().setName("B").build();
    Assertions.assertEquals("B", astReferenceToTest.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFactoryOptional() {
    ASTOptionalRef astOptionalRef = ReferenceMill.optionalRefBuilder().setName("C").build();
    Assertions.assertEquals("C", astOptionalRef.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFactoryList() {
    List<String> names = new ArrayList<>();
    names.add("C");
    names.add("B");
    names.add("A");
    ASTListRef astListRef = ReferenceMill.listRefBuilder().setNamesList(names).build();
    Assertions.assertFalse(astListRef.isEmptyNames());
    Assertions.assertEquals("C", astListRef.getName(0));
    Assertions.assertEquals("B", astListRef.getName(1));
    Assertions.assertEquals("A", astListRef.getName(2));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoReference() {
    ASTNoRef astNoRef = astRand.getNoRef(0);
    Assertions.assertEquals("a", astNoRef.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
