/* (c) https://github.com/MontiCore/monticore */

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.tagging.ISymbolTagger;
import de.monticore.tagging.SimpleSymbolTagger;
import de.monticore.tagging.TagRepository;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTSimpleTag;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTValuedTag;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Optional;

// Also test the SimpleSymbolTagger on a more complex language with scopes and package names
public class GrammarTagTest {

  static ASTMCGrammar fqnAutomata, automata;

  static ISymbolTagger tagger;

  @BeforeAll
  public static void init() throws Exception {
    TagRepository.clearTags();
    LogStub.init();
    Log.enableFailQuick(false);
    TagsMill.init();

    // Load all relevant models
    Optional<?> opt = TagRepository.loadTagModel(new File("src/test/resources/models/SimpleGrammar.tags"));
    if (opt.isEmpty())
      Assertions.fail("Failed to load Simple.tags");

    tagger = new SimpleSymbolTagger(TagRepository::getLoadedTagUnits);

    Grammar_WithConceptsMill.init();
    fqnAutomata = Grammar_WithConceptsMill.parser().parse("src/main/grammars/de/monticore/fqn/FQNAutomata.mc4").get();
    Grammar_WithConceptsMill.scopesGenitorDelegator().createFromAST(fqnAutomata);

    automata = Grammar_WithConceptsMill.parser().parse("src/main/grammars/Automata.mc4").get();
    Grammar_WithConceptsMill.scopesGenitorDelegator().createFromAST(automata);

  }

  @Test
  public void testAutomaton() {
    List<ASTTag> tags = tagger.getTags(automata.getSymbol());
    Assertions.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "Grammar");
  }

  @Test
  public void testAutomatonProdSymbol() {
    List<ASTTag> tags = tagger.getTags(automata.getSpannedScope().resolveProd("Automaton").get());
    Assertions.assertEquals(2, tags.size());
    assertValuedTag(tags.get(0), "SymbolProd", "within");
    assertValuedTag(tags.get(1), "SymbolProd", "fqn");
  }

  @Test
  public void testFQNAutomaton() {
    List<ASTTag> tags = tagger.getTags(fqnAutomata.getSymbol());
    Assertions.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "FQNGrammar");
  }

  @Test
  public void testFQNAutomatonProdSymbol() {
    List<ASTTag> tags = tagger.getTags(fqnAutomata.getSpannedScope().resolveProd("Automaton").get());
    Assertions.assertEquals(2, tags.size());
    assertValuedTag(tags.get(0), "SymbolProd", "within");
    assertValuedTag(tags.get(1), "SymbolProd", "fqn");
  }


  @Test
  public void testAddRemove() {
    List<ASTTag> tags = tagger.getTags(fqnAutomata.getSpannedScope().resolveProd("Automaton").get());
    Assertions.assertEquals(2, tags.size());
    tagger.addTag(fqnAutomata.getSpannedScope().resolveProd("Automaton").get(),
            TagsMill.simpleTagBuilder().setName("newTag").build());
    tags = tagger.getTags(fqnAutomata.getSpannedScope().resolveProd("Automaton").get());
    Assertions.assertEquals(3, tags.size(), "Add on FQN.Automaton");

    tagger.removeTag(fqnAutomata.getSpannedScope().resolveProd("Automaton").get(), tags.get(2));
    tags = tagger.getTags(fqnAutomata.getSpannedScope().resolveProd("Automaton").get());
    Assertions.assertEquals(2, tags.size(), "Remove on FQN.Automaton");


    tagger.addTag(fqnAutomata.getSpannedScope().resolveProd("State").get(),
            TagsMill.simpleTagBuilder().setName("newTag").build());
    tags = tagger.getTags(fqnAutomata.getSpannedScope().resolveProd("State").get());
    Assertions.assertEquals(1, tags.size(), "Add on (freshly tagged) FQN.State");
  }


  protected void assertValuedTag(ASTTag tag, String name, String value) {
    Assertions.assertInstanceOf(ASTValuedTag.class, tag);
    ASTValuedTag valuedTag = (ASTValuedTag) tag;
    Assertions.assertEquals(name, valuedTag.getName());
    Assertions.assertEquals(value, valuedTag.getValue());
  }

  protected void assertSimpleTag(ASTTag tag, String name) {
    Assertions.assertInstanceOf(ASTSimpleTag.class, tag);
    ASTSimpleTag simpleTag = (ASTSimpleTag) tag;
    Assertions.assertEquals(name, simpleTag.getName());
  }
}
