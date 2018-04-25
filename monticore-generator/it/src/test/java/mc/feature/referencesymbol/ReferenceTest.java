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
    Optional<ASTRand> astRand = parser.parse_StringRand("begin ReferenceTest {\n" +
        "  symbol A;\n" +
        "  ref A ;\n" +
        "} end");
        //.parse("src/test/resources/mc/feature/referencesymbol/ReferenceModel.ref");



    //create symboltable
    ModelPath modelPath = new ModelPath(Paths.get("src/tests/resources/mc/feature/referencesymbol"));
    ModelingLanguage lang = new ReferenceLanguage();
    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(lang.getResolvingFilters());
    GlobalScope globalScope = new GlobalScope(modelPath, lang, resolvingConfiguration);
    ReferenceSymbolTableCreator symbolTableCreator = new ReferenceSymbolTableCreator(resolvingConfiguration,globalScope);
    symbolTableCreator.createFromAST(astRand.get());
   // symbolTableCreator.createFromAST(asta.get());
    //symbolTableCreator.createFromAST(astb.get());
    Optional<TestSymbol> a = globalScope.resolve("A", TestSymbol.KIND);

    assertTrue(a.isPresent());

    ASTReferenceToTest astReferenceToTest = astRand.get().getReferenceToTest(0);
    ASTTest astTest = astRand.get().getTest(0);
    //muss man das machen?
    astReferenceToTest.setEnclosingScope(astTest.getEnclosingScope());

    assertTrue(astTest.isPresentEnclosingScope());


    assertTrue(astReferenceToTest.isPresentRefSymbol());

    assertTrue(astReferenceToTest.isPresentRefDefinition());
    assertTrue(astReferenceToTest.isPresentEnclosingScope());

    assertEquals(astReferenceToTest.getRefDefinition(), astTest);
    assertEquals(astReferenceToTest.getRefSymbolOpt(), a);


  }
}
