package mc.feature.referencesymbol;

import de.monticore.ModelingLanguage;
import de.monticore.grammar.symboltable.MontiCoreGrammarLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import mc.GeneratorIntegrationsTest;
import mc.feature.referencesymbol.reference._ast.ASTA;
import mc.feature.referencesymbol.reference._ast.ASTReferenceNode;
import mc.feature.referencesymbol.reference._ast.ASTReferenceToA;
import mc.feature.referencesymbol.reference._parser.ReferenceParser;
import mc.feature.referencesymbol.reference._symboltable.ASymbol;
import mc.feature.referencesymbol.reference._symboltable.ReferenceSymbolTableCreator;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReferenceTest extends GeneratorIntegrationsTest {

  @Test
  public void testNoSymbolTable() throws IOException {
    ReferenceParser parser = new ReferenceParser();
    Optional<ASTA> asta = parser.parse_StringA("TestA +");
    Optional<ASTReferenceToA> astb = parser.parse_StringReferenceToA("TestA");
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
    Optional<ASTA> asta = parser.parse_StringA("TestA +");
    Optional<ASTReferenceToA> astb = parser.parse_StringReferenceToA("TestA");
    assertTrue(asta.isPresent());
    assertTrue(astb.isPresent());
    //create symboltable
    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    ModelingLanguage grammarLanguage = new MontiCoreGrammarLanguage();
    Path path = Paths.get("src/main/grammars/mc/feature/referencesymbol");
    ModelPath modelPath = new ModelPath(path);
    MutableScope globalScope = new GlobalScope(modelPath, grammarLanguage);
    ReferenceSymbolTableCreator symbolTableCreator = new ReferenceSymbolTableCreator(resolvingConfiguration,globalScope);
    symbolTableCreator.createFromAST(asta.get());

    assertFalse(parser.hasErrors());

    assertTrue(astb.get().isPresentRefSymbol());

    assertTrue(astb.get().isPresentRefDefinition());

  }
}
