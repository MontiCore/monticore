/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._ast.ASTTransition;
import automata._symboltable.ScopedStateSymbol;
import automata._symboltable.StateSymbol;
import automata._tagging.AutomataTagger;
import automata._tagging.IAutomataTagger;
import automata._visitor.AutomataTraverser;
import automata._visitor.AutomataVisitor2;
import automatatagdefinition.AutomataTagDefinitionMill;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTSimpleTag;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tags._ast.ASTValuedTag;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TagTest {

  static ASTAutomaton model;

  static ASTTagUnit tagDefinition;

  static Map<String, ASTState> states = new HashMap<>();
  
  protected IAutomataTagger automataTagger = AutomataTagger.getInstance(); 

  @BeforeClass
  public static void init() throws Exception {
    LogStub.init();
    Log.enableFailQuick(false);

    // Load all relevant models
    AutomataTagDefinitionMill.init();
    tagDefinition = AutomataTagDefinitionMill.parser().parse("src/test/resources/models/Simple.tags").get();
    AutomataTagDefinitionMill.scopesGenitorDelegator().createFromAST(tagDefinition);

    // No AutomataMill.init() required
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
    List<ASTTag> tags = automataTagger.getTags(model, tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "Method", "App.call()");
  }

  @Test
  public void testAutomatonSymbol() {
    List<ASTTag> tags = automataTagger.getTags(model.getSymbol(), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "Method", "App.call()");
  }

  @Test
  public void testStateA() {
    List<ASTTag> tags = automataTagger.getTags(states.get("A"), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "Monitored");
  }

  @Test
  public void testStateASymbol() {
    List<ASTTag> tags = automataTagger.getTags(states.get("A").getSymbol(), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "Monitored");
  }


  @Test
  public void testStateB() {
    List<ASTTag> tags = automataTagger.getTags(states.get("B"), tagDefinition);
    Assert.assertEquals(0, tags.size());
  }

  @Test
  public void testStateBSymbol() {
    List<ASTTag> tags = automataTagger.getTags(states.get("B").getSymbol(), tagDefinition);
    Assert.assertEquals(0, tags.size());
  }

  @Test
  public void testStateBA() {
    List<ASTTag> tags = automataTagger.getTags(states.get("BA"), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag1");
  }

  @Test
  public void testStateBASymbol() {
    List<ASTTag> tags = automataTagger.getTags(states.get("BA").getSymbol(), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag1");
  }

  @Test
  public void testStateBB() {
    List<ASTTag> tags = automataTagger.getTags(states.get("BB"), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag2");
  }

  @Test
  public void testStateBBSymbol() {
    List<ASTTag> tags = automataTagger.getTags(states.get("BB").getSymbol(), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag2");
  }

  @Test
  public void testSomeScopeC() {
    List<ASTTag> tags = automataTagger.getTags(model.getEnclosingScope().resolveScopedState("C").get().getAstNode(), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "VerboseLog", "doLogC");
  }

  @Test
  public void testSomeScopeCSymbol() {
    List<ASTTag> tags = automataTagger.getTags(model.getEnclosingScope().resolveScopedState("C").get(), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "VerboseLog", "doLogC");
  }

  @Test
  public void testStateC_CA() {
    List<ASTTag> tags = automataTagger.getTags(states.get("C.CA"), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag1");
  }

  @Test
  public void testStateC_CASymbol() {
    List<ASTTag> tags = automataTagger.getTags(states.get("C.CA").getSymbol(), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag1");
  }

  @Test
  public void testStateC_CB() {
    List<ASTTag> tags = automataTagger.getTags(states.get("C.CB"), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag2");
  }

  @Test
  public void testStateC_CBSymbol() {
    List<ASTTag> tags = automataTagger.getTags(states.get("C.CB").getSymbol(), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag2");
  }

  @Test
  public void testSomeScopeC_Transition() {
    List<ASTTag> tags = automataTagger.getTags((ASTTransition) model.getEnclosingScope().resolveScopedState("C").get().getAstNode()
            .getScopedStateElement(2), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "Log", "timestamp");
  }

  @Test
  public void testStateD() {
    List<ASTTag> tags = automataTagger.getTags(states.get("D"), tagDefinition);
    Assert.assertEquals(2, tags.size());
    assertSimpleTag(tags.get(0), "WildcardedTag");
  }

  @Test
  public void testStateDSymbol() {
    List<ASTTag> tags = automataTagger.getTags(states.get("D").getSymbol(), tagDefinition);
    Assert.assertEquals(2, tags.size());
    assertSimpleTag(tags.get(0), "WildcardedTag");
  }

  @Test
  public void testDupSymbols() {
    Optional<StateSymbol> stateSymbolOpt = model.getEnclosingScope().resolveState("Dup");
    Assert.assertTrue(stateSymbolOpt.isPresent());
    Optional<ScopedStateSymbol> scopedStateSymbolOpt = model.getEnclosingScope().resolveScopedState("Dup");
    Assert.assertTrue(scopedStateSymbolOpt.isPresent());
    // Discuss if this type-unaware duplication is desired?
    List<ASTTag> tags = automataTagger.getTags(stateSymbolOpt.get(), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "WildcardedTag");

    tags = automataTagger.getTags(scopedStateSymbolOpt.get(), tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "WildcardedTag");
  }

  @Test
  public void testAddStateE() {
    ASTState stateE = states.get("E");
    List<ASTTag> tags = automataTagger.getTags(stateE, tagDefinition);
    Assert.assertEquals(0, tags.size());
    // Add new Tag
    ASTTag tag = TagsMill.simpleTagBuilder().setName("TestTag").build();
    automataTagger.addTag(stateE, tagDefinition, tag);
    tags = automataTagger.getTags(stateE, tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "TestTag");
    // Remove tag again
    automataTagger.removeTag(stateE, tagDefinition, tag);
    tags = automataTagger.getTags(stateE, tagDefinition);
    Assert.assertEquals(0, tags.size());
  }

  @Test
  public void testAddTransition() {
    ASTTransition transition = model.getTransitionList().stream().filter(e->e.getFrom().equals("E") && e.getTo().equals("E")).findAny().get();
    List<ASTTag> tags = automataTagger.getTags(transition, tagDefinition);
    Assert.assertEquals(0, tags.size());
    // Add new Tag
    ASTTag tag = TagsMill.simpleTagBuilder().setName("TestTag").build();
    automataTagger.addTag(transition, tagDefinition, tag);
    tags = automataTagger.getTags(transition, tagDefinition);
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "TestTag");
    // Remove tag again
    automataTagger.removeTag(transition, tagDefinition, tag);
    tags = automataTagger.getTags(transition, tagDefinition);
    Assert.assertEquals(0, tags.size());
  }


  protected void assertValuedTag(ASTTag tag, String name, String value) {
    Assert.assertTrue(tag instanceof ASTValuedTag);
    ASTValuedTag valuedTag = (ASTValuedTag) tag;
    Assert.assertEquals(name, valuedTag.getName());
    Assert.assertEquals(value, valuedTag.getValue());
  }

  protected void assertSimpleTag(ASTTag tag, String name) {
    Assert.assertTrue(tag instanceof ASTSimpleTag);
    ASTSimpleTag simpleTag = (ASTSimpleTag) tag;
    Assert.assertEquals(name, simpleTag.getName());
  }
}
