/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.*;
import automata._tagging.AutomataTagger;
import automata._tagging.IAutomataTagger;
import automatatagdefinition.AutomataTagDefinitionMill;
import de.monticore.ast.ASTNode;
import de.monticore.tagging.tags._ast.ASTSimpleTag;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class NotQuiteAutomataTest {

  static ASTAutomaton model;

  static ASTTagUnit tagDefinition;


  protected IAutomataTagger automataTagger = AutomataTagger.getInstance();

  protected static Map<String, ASTNode> nodes = new HashMap<>();

  @BeforeClass
  public static void init() throws Exception {
    LogStub.init();
    Log.enableFailQuick(false);

    // Load all relevant models
    AutomataTagDefinitionMill.init();
    tagDefinition = AutomataTagDefinitionMill.parser().parse("src/test/resources/models/NotQuiteAutomata.tags").get();
    AutomataTagDefinitionMill.scopesGenitorDelegator().createFromAST(tagDefinition);

    // No AutomataMill.init() required
    model = AutomataMill.parser().parse("src/test/resources/models/NotQuiteAutomata.aut").get();
    AutomataMill.scopesGenitorDelegator().createFromAST(model);

    // Store relevant objects in a map to access them via their concrete syntax
    for (ASTRandomSymbolInterface a : model.getNotQuiteAutomataProductions().getRandomSymbolInterfaceList()) {
      nodes.put(AutomataTagDefinitionMill.prettyPrint(a, false), a);
    }

    for (ASTAnotherSymbolProd a : model.getNotQuiteAutomataProductions().getAnotherSymbolProdList()) {
      nodes.put(AutomataTagDefinitionMill.prettyPrint(a, false), a);
    }

    for (ASTYetAnotherSymbolProd a : model.getNotQuiteAutomataProductions().getYetAnotherSymbolProdList()) {
      nodes.put(AutomataTagDefinitionMill.prettyPrint(a, false), a);
    }
  }

  @Test
  public void testParseCorrect() {
    Assert.assertTrue(model.isPresentNotQuiteAutomataProductions());
    Assert.assertEquals(8, nodes.size());
  }

  @Test
  public void testSomeScopeProd() {
    doTest((ASTSomeScopeProd) nodes.get("SomeScopeProd SPP1"), automataTagger::getTags, "SPPTag");
    doTest((ASTSomeScopeProd) nodes.get("SomeScopeProd SPP1"), (a, d) -> automataTagger.getTags(a.getSymbol(), d), "SPPTag");
    doTest((ASTRandomSymbolInterface) nodes.get("SomeScopeProd SPP1"), automataTagger::getTags, "SPPTag");
    doTest((ASTRandomSymbolInterface) nodes.get("SomeScopeProd SPP1"), (a, d) -> automataTagger.getTags(a.getSymbol(), d), "SPPTag");
  }

  @Test
  public void testSomeScopeProdCS() {
    doTest((ASTSomeScopeProd) nodes.get("SomeScopeProd SPP2"), automataTagger::getTags, "SPPTag");
    doTest((ASTRandomSymbolInterface) nodes.get("SomeScopeProd SPP2"), automataTagger::getTags, "SPPTag");
  }


  @Test
  public void testSomeProd() {
    doTest((ASTSomeProd) nodes.get("SomeProd SP1"), automataTagger::getTags, "SPTag");
    doTest((ASTSomeProd) nodes.get("SomeProd SP1"), (a, d) -> automataTagger.getTags(a.getSymbol(), d), "SPTag");
    doTest((ASTRandomSymbolInterface) nodes.get("SomeProd SP1"), automataTagger::getTags, "SPTag");
    doTest((ASTRandomSymbolInterface) nodes.get("SomeProd SP1"), (a, d) -> automataTagger.getTags(a.getSymbol(), d), "SPTag");
  }

  @Test
  public void testSomeProdCS() {
    doTest((ASTSomeProd) nodes.get("SomeProd SP2"), automataTagger::getTags, "SPTag");
    doTest((ASTRandomSymbolInterface) nodes.get("SomeProd SP2"), automataTagger::getTags, "SPTag");
  }


  @Test
  public void testAnotherSymbolProd() {
    doTest((ASTAnotherSymbolProd) nodes.get("AnotherSymbolProd ASP1"), automataTagger::getTags, "ASPTag");
    doTest((ASTAnotherSymbolProd) nodes.get("AnotherSymbolProd ASP1"), (a, d) -> automataTagger.getTags(a.getSymbol(), d), "ASPTag");
  }


  @Test
  public void testAnotherSymbolProdCS() {
    doTest((ASTAnotherSymbolProd) nodes.get("AnotherSymbolProd ASP2"), automataTagger::getTags, "ASPTag");
  }

  @Test
  public void testYetAnotherSymbolProd() {
    doTest((ASTAnotherSymbolProd) nodes.get("YetAnotherSymbolProd YASP1"), automataTagger::getTags, "YASPTag");
    doTest((ASTAnotherSymbolProd) nodes.get("YetAnotherSymbolProd YASP1"), (a, d) -> automataTagger.getTags(a.getSymbol(), d), "YASPTag");
    doTest((ASTYetAnotherSymbolProd) nodes.get("YetAnotherSymbolProd YASP1"), automataTagger::getTags, "YASPTag");
    doTest((ASTYetAnotherSymbolProd) nodes.get("YetAnotherSymbolProd YASP1"), (a, d) -> automataTagger.getTags(a.getSymbol(), d), "YASPTag");
  }

  @Test
  public void testYetAnotherSymbolProdCS() {
    doTest((ASTAnotherSymbolProd) nodes.get("YetAnotherSymbolProd YASP2"), automataTagger::getTags, "YASPTag");
    doTest((ASTYetAnotherSymbolProd) nodes.get("YetAnotherSymbolProd YASP2"), automataTagger::getTags, "YASPTag");
  }

  protected <A extends ASTNode> void doTest(A ast, BiFunction<A, Iterable<ASTTagUnit>, List<ASTTag>> getTags, String name) {
    Assert.assertNotNull(ast);
    List<ASTTag> tags = getTags.apply(ast, Collections.singleton(tagDefinition));
    Assert.assertEquals(1, tags.size());
    assertSimpleTag(tags.get(0), name);
  }

  protected void assertSimpleTag(ASTTag tag, String name) {
    Assert.assertTrue(tag instanceof ASTSimpleTag);
    ASTSimpleTag simpleTag = (ASTSimpleTag) tag;
    Assert.assertEquals(name, simpleTag.getName());
  }
}
