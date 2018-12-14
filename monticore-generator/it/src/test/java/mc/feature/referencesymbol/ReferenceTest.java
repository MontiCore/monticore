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
  public void testWithSymbolTable() throws IOException {
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
  public void testWithSymbolTableOptional() throws IOException {
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
  public void testNoReference(){
    ASTNoRef astNoRef = astRand.getNoRef(0);
    assertEquals(astNoRef.getName(), "a");
  }

  @Test
  public void testListNoSymbolTable() {
    ASTTest astTest = astRand.getTest(0);

    ASTListRef astListRef = astRand.getListRef(0);

    assertTrue(astTest.isPresentEnclosingScope());
    assertTrue(astListRef.isPresentEnclosingScope());
    assertTrue(astTest.isPresentTestSymbol());


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

}
