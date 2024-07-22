/* (c) https://github.com/MontiCore/monticore */

import de.monticore.fqn.fqnautomata._ast.ASTAutomaton;
import de.monticore.fqn.fqnautomata._ast.ASTState;
import de.monticore.fqn.fqnautomata._ast.ASTTransition;
import de.monticore.fqn.fqnautomata._tagging.FQNAutomataTagger;
import de.monticore.fqn.fqnautomata._tagging.IFQNAutomataTagger;
import de.monticore.fqn.fqnautomata._visitor.FQNAutomataVisitor2;
import de.monticore.fqn.fqnenhancedautomata.FQNEnhancedAutomataMill;
import de.monticore.fqn.fqnenhancedautomata._ast.ASTRedState;
import de.monticore.fqn.fqnenhancedautomata._ast.ASTRedTransition;
import de.monticore.fqn.fqnenhancedautomata._symboltable.IFQNEnhancedAutomataScope;
import de.monticore.fqn.fqnenhancedautomata._tagging.FQNEnhancedAutomataTagger;
import de.monticore.fqn.fqnenhancedautomata._tagging.IFQNEnhancedAutomataTagger;
import de.monticore.fqn.fqnenhancedautomata._visitor.FQNEnhancedAutomataTraverser;
import de.monticore.fqn.fqnenhancedautomata._visitor.FQNEnhancedAutomataVisitor2;
import de.monticore.fqn.fqnenhancedautomatatagdefinition.FQNEnhancedAutomataTagDefinitionMill;
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
import util.TestUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Copy of TagTest, just with a grammar in a package
// Also tests inheritance
public class FQNInheritedTagTest {

  static ASTAutomaton model;

  static ASTTagUnit tagDefinition;
  static Map<String, ASTState> states = new HashMap<>();
  static Map<String, ASTRedState> red_states = new HashMap<>();
  
  protected IFQNEnhancedAutomataTagger fqnAutomataTagger = FQNEnhancedAutomataTagger.getInstance();

  @BeforeClass
  public static void init() throws Exception {
    LogStub.init();
    Log.enableFailQuick(false);

    // Load all relevant models
    FQNEnhancedAutomataTagDefinitionMill.init();
    tagDefinition = FQNEnhancedAutomataTagDefinitionMill.parser().parse("src/test/resources/models/Enhanced.tags").get();
    FQNEnhancedAutomataTagDefinitionMill.scopesGenitorDelegator().createFromAST(tagDefinition);

    FQNEnhancedAutomataMill.init();
    model = FQNEnhancedAutomataMill.parser().parse("src/test/resources/models/Enhanced.aut").get();
    FQNEnhancedAutomataMill.scopesGenitorDelegator().createFromAST(model);

    FQNEnhancedAutomataTraverser traverser = FQNEnhancedAutomataMill.traverser();
    traverser.add4FQNAutomata(new FQNAutomataVisitor2() {
      @Override
      public void visit(ASTState node) {
        states.put(node.getSymbol().getFullName(), node);
      }
    });
    traverser.add4FQNEnhancedAutomata(new FQNEnhancedAutomataVisitor2() {
      @Override
      public void visit(ASTRedState node) {
        red_states.put(node.getSymbol().getFullName(), node);
      }
    });

    model.accept(traverser);
  }

  @Test
  public void testAutomaton() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(model, Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "Method", "App.call()");
  }

  @Test
  public void testStateA() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(states.get("A"), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "Monitored");
  }

  @Test
  public void testStateB() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(states.get("B"), Collections.singleton(tagDefinition));
    Assert.assertEquals(0, tags.size());
  }

  @Test
  public void testStateBA() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(states.get("BA"), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag1");
  }

  @Test
  public void testStateBB() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(states.get("BB"), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag2");
  }

  @Test
  public void testSomeScopeC() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(model.getEnclosingScope().resolveScopedState("C").get().getAstNode(), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "Log", "doLogC");
  }

  @Test
  public void testStateC_CA() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(states.get("C.CA"), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag1");
  }

  @Test
  public void testStateC_CB() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(states.get("C.CB"), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag2");
  }

  @Test
  public void testSomeScopeC_Transition() {
    List<ASTTag> tags = fqnAutomataTagger.getTags((ASTTransition) model.getEnclosingScope().resolveScopedState("C").get().getAstNode()
            .getScopedStateElement(2), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "Log", "timestamp");
  }

  @Test
  public void testStateD() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(states.get("D"), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "WildcardedTag");
  }

  @Test
  public void testAddStateE() {
    ASTState stateE = states.get("E");
    List<ASTTag> tags = fqnAutomataTagger.getTags(stateE, Collections.singleton(tagDefinition));
    Assert.assertEquals(0, tags.size());
    // Add new Tag
    ASTTag tag = TagsMill.simpleTagBuilder().setName("TestTag").build();
    fqnAutomataTagger.addTag(stateE, tagDefinition, tag);
    tags = fqnAutomataTagger.getTags(stateE, Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "TestTag");
    // Remove tag again
    fqnAutomataTagger.removeTag(stateE, tagDefinition, tag);
    tags = fqnAutomataTagger.getTags(stateE, Collections.singleton(tagDefinition));
    Assert.assertEquals(0, tags.size());
  }

  @Test
  public void testAddTransition() {
    ASTTransition transition = TestUtil.getTransition(model).stream().filter(e->e.getFrom().equals("E") && e.getTo().equals("E")).findAny().get();
    List<ASTTag> tags = fqnAutomataTagger.getTags(transition, Collections.singleton(tagDefinition));
    Assert.assertEquals(0, tags.size());
    // Add new Tag
    ASTTag tag = TagsMill.simpleTagBuilder().setName("TestTag").build();
    fqnAutomataTagger.addTag(transition, tagDefinition, tag);
    tags = fqnAutomataTagger.getTags(transition, Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "TestTag");
    // Remove tag again
    fqnAutomataTagger.removeTag(transition, tagDefinition, tag);
    tags = fqnAutomataTagger.getTags(transition, Collections.singleton(tagDefinition));
    Assert.assertEquals(0, tags.size());
  }

  @Test
  public void testStateRC_CA() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(states.get("RC.CA"), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag1");
  }

  @Test
  public void testStateRC_RCB() {
    List<ASTTag> tags = fqnAutomataTagger.getTags(red_states.get("RC.RCB"), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), "StateTag2");
  }

  @Test
  public void testSomeScopeRC_Transition() {
    List<ASTTag> tags = fqnAutomataTagger.getTags((ASTTransition) ((IFQNEnhancedAutomataScope) model.getEnclosingScope())
            .resolveRedScopedState("RC").get().getAstNode()
            .getScopedStateElement(2), Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertValuedTag(tags.get(0), "Log", "timestamp");
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
