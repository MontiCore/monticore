/* (c) https://github.com/MontiCore/monticore */
package mc.feature.referencesymbol;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.referencesymbol.reference._ast.ASTTest;
import mc.feature.referencesymbol.reference._symboltable.TestSymbol;
import mc.feature.referencesymbol.supgrammarref.SupGrammarRefMill;
import mc.feature.referencesymbol.supgrammarref._ast.ASTSupRand;
import mc.feature.referencesymbol.supgrammarref._ast.ASTSupRef;
import mc.feature.referencesymbol.supgrammarref._ast.ASTSupRefList;
import mc.feature.referencesymbol.supgrammarref._ast.ASTSupRefOpt;
import mc.feature.referencesymbol.supgrammarref._parser.SupGrammarRefParser;
import mc.feature.referencesymbol.supgrammarref._symboltable.ISupGrammarRefArtifactScope;
import mc.feature.referencesymbol.supgrammarref._symboltable.ISupGrammarRefGlobalScope;
import mc.feature.referencesymbol.supgrammarref._symboltable.ISupGrammarRefScope;
import mc.feature.referencesymbol.supgrammarref._symboltable.SupGrammarRefScopesGenitorDelegator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupReferenceTest {
  private ASTSupRand astsupRand;
  private TestSymbol a;
  private TestSymbol b;
  private TestSymbol c;
  private TestSymbol d;
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setUp() throws IOException {
    // reset global scope
    ISupGrammarRefGlobalScope globalScope = SupGrammarRefMill.globalScope();
    globalScope.clearLoadedFiles();
    for (ISupGrammarRefScope s : globalScope.getSubScopes()) {
      globalScope.removeSubScope(s);
    }
    for (Path p : globalScope.getSymbolPath().getEntries()) {
      globalScope.getSymbolPath().removeEntry(p);
    }

    // populate symtab
    SupGrammarRefParser parser = new SupGrammarRefParser();
    Optional<ASTSupRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/SupReferenceModel.ref");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astRand.isPresent());
    //create symboltable
    this.astsupRand = astRand.get();

    globalScope.setFileExt("ref");
    globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/referencesymbol"));

    SupGrammarRefScopesGenitorDelegator symbolTableCreator = SupGrammarRefMill
            .scopesGenitorDelegator();
    ISupGrammarRefArtifactScope artifact = symbolTableCreator.createFromAST(astsupRand);
    artifact.setName("SupReferenceModel");
    Optional<? extends ISupGrammarRefScope> scopeOpt = artifact.getSubScopes().stream().findAny();
    Assertions.assertTrue(scopeOpt.isPresent());
    ISupGrammarRefScope innerScope = scopeOpt.get();


    Optional<TestSymbol> a = globalScope.resolveTest("SupReferenceModel.SupReferenceTest.A");
    Optional<TestSymbol> b = artifact.resolveTest("SupReferenceModel.SupReferenceTest.B");
    Optional<TestSymbol> c = innerScope.resolveTest("C");
    Optional<TestSymbol> d = innerScope.resolveTest("D");

    Assertions.assertTrue(b.isPresent());
    Assertions.assertTrue(c.isPresent());
    Assertions.assertTrue(d.isPresent());
    Assertions.assertTrue(a.isPresent());

    this.a = a.get();
    this.b = b.get();
    this.c = c.get();
    this.d = d.get();
  }


  @Test
  public void testWithoutSymbolTable() throws IOException {
    SupGrammarRefParser parser = new SupGrammarRefParser();
    Optional<ASTSupRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/SupReferenceModel.ref");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astRand.isPresent());
    ASTSupRef supRef = astRand.get().getSupRef(0);

    Assertions.assertFalse(supRef.isPresentNameDefinition());
    Assertions.assertFalse(supRef.isPresentNameSymbol());
    Assertions.assertEquals("A", supRef.getName());
    supRef.setName("B");
    Assertions.assertEquals("B", supRef.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testWithoutSymbolTableOpt() throws IOException {
    SupGrammarRefParser parser = new SupGrammarRefParser();
    Optional<ASTSupRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/SupReferenceModel.ref");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astRand.isPresent());
    ASTSupRefOpt supRefOpt = astRand.get().getSupRefOpt(0);

    Assertions.assertFalse(supRefOpt.isPresentNameDefinition());
    Assertions.assertFalse(supRefOpt.isPresentNameSymbol());
    Assertions.assertEquals("A", supRefOpt.getName());
    supRefOpt.setName("B");
    Assertions.assertEquals("B", supRefOpt.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testWithoutSymbolTableList() throws IOException {
    SupGrammarRefParser parser = new SupGrammarRefParser();
    Optional<ASTSupRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/SupReferenceModel.ref");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astRand.isPresent());
    ASTSupRefList supRefList = astRand.get().getSupRefList(0);

    Assertions.assertEquals(3, supRefList.sizeNamesDefinition());
    supRefList.getNamesDefinitionList().forEach(n -> Assertions.assertFalse(n.isPresent()));
    Assertions.assertEquals(3, supRefList.sizeNamesSymbol());
    supRefList.getNamesSymbolList().forEach(n -> Assertions.assertFalse(n.isPresent()));
    Assertions.assertFalse(supRefList.getNameList().isEmpty());
    Assertions.assertEquals(supRefList.sizeNames(), 3);
    Assertions.assertEquals("A", supRefList.getName(0));
    supRefList.setName(0, "B");
    Assertions.assertEquals("B", supRefList.getName(0));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSupRef() {
    ASTSupRef supRef = this.astsupRand.getSupRef(1);
    Assertions.assertTrue(supRef.isPresentNameDefinition());
    Assertions.assertTrue(supRef.isPresentNameSymbol());
    Assertions.assertEquals(supRef.getName(), "B");

    Assertions.assertEquals(supRef.getNameSymbol(), b);

    Assertions.assertEquals(supRef.getNameDefinition(), b.getAstNode());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSupRefSet() {
    ASTSupRef supRef = this.astsupRand.getSupRef(1);

    //setName
    supRef.setName("C");
    Assertions.assertTrue(supRef.isPresentNameDefinition());
    Assertions.assertTrue(supRef.isPresentNameSymbol());
    Assertions.assertEquals(supRef.getName(), "C");

    Assertions.assertEquals(supRef.getNameSymbol(), c);

    Assertions.assertEquals(supRef.getNameDefinition(), c.getAstNode());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSupRefOpt() {
    ASTSupRefOpt supRefOpt = this.astsupRand.getSupRefOpt(0);
    Assertions.assertTrue(supRefOpt.isPresentNameDefinition());
    Assertions.assertTrue(supRefOpt.isPresentNameSymbol());
    Assertions.assertTrue(supRefOpt.isPresentName());
    Assertions.assertEquals(supRefOpt.getName(), "A");

    Assertions.assertEquals(supRefOpt.getNameSymbol(), a);

    Assertions.assertEquals(supRefOpt.getNameDefinition(), a.getAstNode());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSupRefOptSet() {
    ASTSupRefOpt supRefOpt = this.astsupRand.getSupRefOpt(0);
    //setName
    supRefOpt.setName("C");
    Assertions.assertTrue(supRefOpt.isPresentNameDefinition());
    Assertions.assertTrue(supRefOpt.isPresentNameSymbol());
    Assertions.assertTrue(supRefOpt.isPresentName());
    Assertions.assertEquals(supRefOpt.getName(), "C");

    Assertions.assertEquals(supRefOpt.getNameSymbol(), c);

    Assertions.assertEquals(supRefOpt.getNameDefinition(), c.getAstNode());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSupRefOptSetAbsent() {
    ASTSupRefOpt supRefOpt = this.astsupRand.getSupRefOpt(0);

    //setNameAbsent
    supRefOpt.setNameAbsent();
    Assertions.assertFalse(supRefOpt.isPresentNameDefinition());
    Assertions.assertFalse(supRefOpt.isPresentNameSymbol());
    Assertions.assertFalse(supRefOpt.isPresentName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSupRefList() {
    ASTSupRefList supRefList = astsupRand.getSupRefList(2);
    Assertions.assertFalse(supRefList.getNamesDefinitionList().isEmpty());
    Assertions.assertFalse(supRefList.getNamesSymbolList().isEmpty());
    Assertions.assertFalse(supRefList.getNameList().isEmpty());

    Assertions.assertEquals(supRefList.sizeNames(), 4);
    Assertions.assertEquals(supRefList.sizeNamesDefinition(), 4);
    Assertions.assertEquals(supRefList.sizeNamesSymbol(), 4);

    Assertions.assertEquals("A", supRefList.getName(0));

    Assertions.assertTrue(supRefList.getNamesSymbol(0).isPresent());
    Assertions.assertEquals(a, supRefList.getNamesSymbol(0).get());

    Assertions.assertTrue(supRefList.getNamesDefinition(0).isPresent());
    Assertions.assertEquals(a.getAstNode(), supRefList.getNamesDefinition(0).get());

    Assertions.assertTrue(supRefList.containsName("B"));
    Assertions.assertTrue(supRefList.containsNamesDefinition(Optional.ofNullable(b.getAstNode())));
    Assertions.assertTrue(supRefList.containsNamesSymbol(Optional.ofNullable(b)));

    Assertions.assertEquals(supRefList.toArrayNames().length, 4);
    Assertions.assertEquals(supRefList.toArrayNamesSymbol().length, 4);
    Assertions.assertEquals(supRefList.toArrayNamesDefinition().length, 4);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSupRefListSetEmpty() {
    ASTSupRefList supRefList = astsupRand.getSupRefList(0);
    //setEmptyList
    supRefList.setNameList(new ArrayList<>());
    Assertions.assertTrue(supRefList.getNamesDefinitionList().isEmpty());
    Assertions.assertTrue(supRefList.getNamesSymbolList().isEmpty());
    Assertions.assertTrue(supRefList.getNameList().isEmpty());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSupRefListAdd() {
    ASTSupRefList supRefList = astsupRand.getSupRefList(1);

    //add "D"
    supRefList.addName("D");
    Assertions.assertFalse(supRefList.getNamesDefinitionList().isEmpty());
    Assertions.assertFalse(supRefList.getNamesSymbolList().isEmpty());
    Assertions.assertFalse(supRefList.getNameList().isEmpty());

    Assertions.assertEquals("D", supRefList.getName(0));

    Assertions.assertTrue(supRefList.getNamesSymbol(0).isPresent());
    Assertions.assertEquals(d, supRefList.getNamesSymbol(0).get());

    Assertions.assertTrue(supRefList.getNamesDefinition(0).isPresent());
    Assertions.assertEquals(d.getAstNode(), supRefList.getNamesDefinition(0).get());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testSupRefListSet() {
    ASTSupRefList supRefList = astsupRand.getSupRefList(1);
    List<String> list = new ArrayList<>();
    list.add("B");
    list.add("A");
    //setNameList(list)
    supRefList.setNameList(list);

    Assertions.assertEquals(supRefList.sizeNames(), 2);
    Assertions.assertEquals(supRefList.sizeNamesDefinition(), 2);
    Assertions.assertEquals(supRefList.sizeNamesSymbol(), 2);


    Assertions.assertEquals("B", supRefList.getName(0));

    Assertions.assertTrue(supRefList.getNamesSymbol(0).isPresent());
    Assertions.assertEquals(b, supRefList.getNamesSymbol(0).get());

    Assertions.assertTrue(supRefList.getNamesDefinition(0).isPresent());
    Assertions.assertEquals(b.getAstNode(), supRefList.getNamesDefinition(0).get());

    Assertions.assertTrue(supRefList.containsName("A"));
    Assertions.assertTrue(supRefList.containsNamesDefinition(Optional.ofNullable(a.getAstNode())));
    Assertions.assertTrue(supRefList.containsNamesSymbol(Optional.ofNullable(a)));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSupRefListRemove() {
    ASTSupRefList supRefList = astsupRand.getSupRefList(2);
    Assertions.assertEquals(supRefList.sizeNames(), 4);
    Assertions.assertEquals(supRefList.sizeNamesDefinition(), 4);
    Assertions.assertEquals(supRefList.sizeNamesSymbol(), 4);

    //remove "B"
    supRefList.removeName("B");

    Assertions.assertEquals(supRefList.sizeNames(), 3);
    Assertions.assertEquals(supRefList.sizeNamesDefinition(), 3);
    Assertions.assertEquals(supRefList.sizeNamesSymbol(), 3);


    Assertions.assertEquals("C", supRefList.getName(1));

    Assertions.assertTrue(supRefList.getNamesSymbol(1).isPresent());
    Assertions.assertEquals(c, supRefList.getNamesSymbol(1).get());

    Assertions.assertTrue(supRefList.getNamesDefinition(1).isPresent());
    Assertions.assertEquals(c.getAstNode(), supRefList.getNamesDefinition(1).get());

    List<String> list = new ArrayList<>();
    list.add("A");
    list.add("C");
    list.add("D");
    Assertions.assertEquals(supRefList.getNameList(), list);

    List<Optional<TestSymbol>> symbolList = new ArrayList<>();
    symbolList.add(Optional.ofNullable(a));
    symbolList.add(Optional.ofNullable(c));
    symbolList.add(Optional.ofNullable(d));
    Assertions.assertEquals(supRefList.getNamesSymbolList(), symbolList);

    List<Optional<ASTTest>> definitionList = new ArrayList<>();
    definitionList.add(Optional.ofNullable(a.getAstNode()));
    definitionList.add(Optional.ofNullable(c.getAstNode()));
    definitionList.add(Optional.ofNullable(d.getAstNode()));
    Assertions.assertEquals(supRefList.getNamesDefinitionList(), definitionList);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
