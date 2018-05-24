package mc.feature.referencesymbol;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.*;
import mc.GeneratorIntegrationsTest;
import mc.feature.referencesymbol.reference._ast.*;
import mc.feature.referencesymbol.reference._parser.ReferenceParser;
import mc.feature.referencesymbol.reference._symboltable.ReferenceLanguage;
import mc.feature.referencesymbol.reference._symboltable.ReferenceSymbolTableCreator;
import mc.feature.referencesymbol.reference._symboltable.TestSymbol;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReferenceTest extends GeneratorIntegrationsTest {

  @Test
  public void testNoSymbolTable() throws IOException {
    ReferenceParser parser = new ReferenceParser();
    Optional<ASTTest> asta = parser.parse_StringTest("symbol TestA ;");
    Optional<ASTReferenceToTest> astb = parser.parse_StringReferenceToTest("ref TestA ;");
    assertFalse(parser.hasErrors());
    assertTrue(asta.isPresent());
    assertTrue(astb.isPresent());
    assertFalse(astb.get().isPresentRefDefinition());
    assertFalse(astb.get().isPresentRefSymbol());
    assertTrue(astb.get().getRefDefinitionOpt().equals(Optional.empty()));
    assertTrue(astb.get().getRefSymbolOpt().equals(Optional.empty()));
  }

  @Test
  public void testWithSymbolTable() throws IOException {
    ReferenceParser parser = new ReferenceParser();
    Optional<ASTRand> astRand = parser.parse("src/test/resources/mc/feature/referencesymbol/ReferenceModel.ref");
    ASTTest astTest = astRand.get().getTest(0);
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

    ASTReferenceToTest astReferenceToTest = astRand.get().getReferenceToTest(0);

    //test getter
    assertTrue(astTest.isPresentEnclosingScope());
    assertTrue(astReferenceToTest.isPresentEnclosingScope());
    assertTrue(astReferenceToTest.isPresentRefSymbol());
    assertTrue(astTest.isPresentTestSymbol());
    assertTrue(astReferenceToTest.isPresentRefSymbol());

    assertTrue(astReferenceToTest.isPresentRefDefinition());

    assertEquals(astReferenceToTest.getRefDefinition(), astTest);
    assertEquals(astReferenceToTest.getRefSymbolOpt(), a);

    //test setter
    astReferenceToTest.setRef("B");
    assertTrue(astReferenceToTest.isPresentRefSymbol());
    assertEquals(astReferenceToTest.getRefSymbolOpt(), b);

    astReferenceToTest.setRefDefinition(astTest);
    assertTrue(astReferenceToTest.isPresentRefSymbol());
    assertEquals(astReferenceToTest.getRefSymbolOpt(), a);
    assertEquals(astReferenceToTest.getRefDefinition(), astTest);

    //test setOpt
    astReferenceToTest.setRefDefinitionOpt(Optional.ofNullable(astTest));
    assertTrue(astReferenceToTest.isPresentRefSymbol());
    assertEquals(astReferenceToTest.getRefSymbolOpt(), a);
    assertEquals(astReferenceToTest.getRefDefinition(), astTest);
  }
}
