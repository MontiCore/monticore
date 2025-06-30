/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._symboltable.ScopedStateSymbol;
import automata._symboltable.StateSymbol;
import automata._visitor.AutomataTraverser;
import automata._visitor.AutomataVisitor2;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TagTest {

  static ASTAutomaton model;

  static ISymbolTagger tagger;

  static Map<String, ASTState> states = new HashMap<>();
  
  @BeforeAll
  public static void init() throws Exception {
    TagRepository.clearTags();
    LogStub.init();
    Log.enableFailQuick(false);

    // Load all relevant models
    var emptyTagsOpt1 = TagRepository.loadTagModel(new File("src/test/resources/models/Empty.tags"));
    Assertions.assertTrue(emptyTagsOpt1.isPresent(), "Failed to load Empty.tags");
    var opt = TagRepository.loadTagModel(new File("src/test/resources/models/Simple.tags"));
    Assertions.assertTrue(opt.isPresent(), "Failed to load Simple.tags");
    var emptyTagsOpt2 = TagRepository.loadTagModel(new File("src/test/resources/models/Empty.tags"));
    Assertions.assertTrue(emptyTagsOpt2.isPresent(), "Failed to load Empty.tags");

    tagger = new SimpleSymbolTagger(TagRepository::getLoadedTagUnits);

    AutomataMill.init();
    model = AutomataMill.parser().parse("src/test/resources/models/Simple.aut").get();
    AutomataMill.scopesGenitorDelegator().createFromAST(model);

    AutomataTraverser traverser = AutomataMill.traverser();
    traverser.add4Automata(new AutomataVisitor2() {
      @Override
      public void visit(ASTState node) {
        states.put(node.getSymbol().getFullName(), node);
      }
    });

    model.accept(traverser);
  }

  @Test
  public void testAutomaton() {
    List<ASTTag> tags = tagger.getTags(model.getSymbol());
    Assertions.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "Method", "App.call()");
  }


  @Test
  public void testStateA() {
    List<ASTTag> tags = tagger.getTags(states.get("A").getSymbol());
    Assertions.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "Monitored");
  }


  @Test
  public void testStateBSymbol() {
    List<ASTTag> tags = tagger.getTags(states.get("B").getSymbol());
    Assertions.assertEquals(0, tags.size());
  }

  @Test
  public void testStateBASymbol() {
    List<ASTTag> tags = tagger.getTags(states.get("BA").getSymbol());
    Assertions.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag1");
  }

  @Test
  public void testStateBBSymbol() {
    List<ASTTag> tags = tagger.getTags(states.get("BB").getSymbol());
    Assertions.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag2");
  }


  @Test
  public void testSomeScopeCSymbol() {
    List<ASTTag> tags = tagger.getTags(model.getEnclosingScope().resolveScopedState("C").get());
    Assertions.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "VerboseLog", "doLogC");
  }

  @Test
  public void testStateC_CASymbol() {
    List<ASTTag> tags = tagger.getTags(states.get("C.CA").getSymbol());
    Assertions.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag1");
  }

  @Test
  public void testStateC_CBSymbol() {
    List<ASTTag> tags = tagger.getTags(states.get("C.CB").getSymbol());
    Assertions.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag2");
  }

  @Test
  public void testStateDSymbol() {
    List<ASTTag> tags = tagger.getTags(states.get("D").getSymbol());
    Assertions.assertEquals(2, tags.size());
    assertSimpleTag(tags.get(0), "WildcardedTag");
  }

  @Test
  public void testDupSymbols() {
    Optional<StateSymbol> stateSymbolOpt = model.getEnclosingScope().resolveState("Dup");
    Assertions.assertTrue(stateSymbolOpt.isPresent());
    Optional<ScopedStateSymbol> scopedStateSymbolOpt = model.getEnclosingScope().resolveScopedState("Dup");
    Assertions.assertTrue(scopedStateSymbolOpt.isPresent());
    // Discuss if this type-unaware duplication is desired?
    List<ASTTag> tags = tagger.getTags(stateSymbolOpt.get());
    Assertions.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "WildcardedTag");

    tags = tagger.getTags(scopedStateSymbolOpt.get());
    Assertions.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "WildcardedTag");
  }

  @Test
  public void testAddStateE() {
    ASTState stateE = states.get("E");
    List<ASTTag> tags = tagger.getTags(stateE.getSymbol());
    Assertions.assertEquals(0, tags.size());
    // Add new Tag
    ASTTag tag = TagsMill.simpleTagBuilder().setName("TestTag").build();
    tagger.addTag(stateE.getSymbol(), tag);
    tags = tagger.getTags(stateE.getSymbol());
    Assertions.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "TestTag");
    // Remove tag again
    tagger.removeTag(stateE.getSymbol(), tag);
    tags = tagger.getTags(stateE.getSymbol());
    Assertions.assertEquals(0, tags.size());
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
