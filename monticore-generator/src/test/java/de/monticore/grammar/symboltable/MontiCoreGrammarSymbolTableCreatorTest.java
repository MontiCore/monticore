/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import de.monticore.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTExternalProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class MontiCoreGrammarSymbolTableCreatorTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSymbolTableOfGrammarStatechartDSL() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    final Optional<MCGrammarSymbol> grammar = globalScope
        .resolve("de.monticore.statechart.Statechart", MCGrammarSymbol.KIND);
    
    assertTrue(grammar.isPresent());
    assertTrue(grammar.get().getAstNode().isPresent());
    testGrammarSymbolOfStatechart(grammar.get());
    
  }
  
  private void testGrammarSymbolOfStatechart(MCGrammarSymbol grammar) {
    assertNotNull(grammar);
    assertEquals("de.monticore.statechart.Statechart", grammar.getFullName());
    assertEquals("de.monticore.statechart", grammar.getPackageName());
    assertTrue(grammar.getKind().isSame(MCGrammarSymbol.KIND));
    assertTrue(grammar.getStartProd().isPresent());
    
    assertTrue(grammar.isComponent());
    assertEquals(1, grammar.getSuperGrammars().size());
    
    assertEquals(12, grammar.getProds().size());
    
    // AST
    assertTrue(grammar.getAstNode().isPresent());
    assertTrue(grammar.getAstNode().get() instanceof ASTMCGrammar);
    assertSame(grammar.getEnclosingScope(), grammar.getAstNode().get().getEnclosingScope().get());
    
    final MCProdSymbol stateChartProd = grammar.getProd("Statechart").orElse(null);
    assertNotNull(stateChartProd);
    assertEquals("Statechart", stateChartProd.getName());
    assertEquals("de.monticore.statechart.Statechart.Statechart", stateChartProd.getFullName());
    assertEquals("de.monticore.statechart", stateChartProd.getPackageName());
    assertTrue(stateChartProd.getKind().isSame(MCProdSymbol.KIND));
    assertTrue(stateChartProd.isStartProd());
    assertTrue(stateChartProd.isClass());
    // generic vs. specific
    Optional<MCProdSymbol> resolvedStateChartProd = grammar.getSpannedScope().resolve("Statechart",
        MCProdSymbol.KIND);
    assertTrue(resolvedStateChartProd.isPresent());
    assertSame(stateChartProd, resolvedStateChartProd.get());
    // AST
    testLinkBetweenSymbolAndAst(stateChartProd);
    
    final MCProdSymbol entryActionProd = grammar.getProd("EntryAction").orElse(null);
    assertNotNull(entryActionProd);
    assertEquals("EntryAction", entryActionProd.getName());
    assertEquals("de.monticore.statechart.Statechart.EntryAction", entryActionProd.getFullName());
    assertFalse(entryActionProd.isStartProd());
    testLinkBetweenSymbolAndAst(entryActionProd);
    
    // test prod components
    assertEquals(3, entryActionProd.getProdComponents().size());
    MCProdComponentSymbol prodComp = entryActionProd.getProdComponent("entry").orElse(null);
    assertNotNull(prodComp);
    assertEquals("entry", prodComp.getName());
    assertEquals("", prodComp.getUsageName());
    assertEquals("de.monticore.statechart.Statechart.EntryAction.entry", prodComp.getFullName());
    assertEquals("de.monticore.statechart", prodComp.getPackageName());
    assertTrue(prodComp.isTerminal());
    assertFalse(prodComp.isList());
    assertFalse(prodComp.isOptional());
    assertSame(entryActionProd.getSpannedScope(), prodComp.getEnclosingScope());
    // generic vs specific
    Optional<MCProdComponentSymbol> resolvedProdComp = entryActionProd.getSpannedScope()
        .resolve("entry", MCProdComponentSymbol.KIND);
    assertTrue(resolvedProdComp.isPresent());
    assertSame(prodComp, resolvedProdComp.get());
    
    // AST
    testLinkBetweenSymbolAndAst(prodComp);
    
    prodComp = entryActionProd.getProdComponent(":").orElse(null);
    assertNotNull(prodComp);
    assertEquals(":", prodComp.getName());
    assertEquals("", prodComp.getUsageName());
    assertTrue(prodComp.isTerminal());
    assertFalse(prodComp.isList());
    assertFalse(prodComp.isOptional());
    assertSame(entryActionProd.getSpannedScope(), prodComp.getEnclosingScope());
    
    prodComp = entryActionProd.getProdComponent("block").orElse(null);
    assertNotNull(prodComp);
    assertEquals("block", prodComp.getName());
    assertEquals("block", prodComp.getUsageName());
    assertTrue(prodComp.isNonterminal());
    assertTrue(prodComp.getAstNode().isPresent());
    assertFalse(prodComp.isList());
    assertFalse(prodComp.isOptional());
    assertSame(entryActionProd.getSpannedScope(), prodComp.getEnclosingScope());
    // reference to defining prod
    assertEquals("BlockStatement", prodComp.getReferencedProd().get().getName());
    assertTrue(prodComp.getReferencedProd().get().existsReferencedSymbol());
    assertTrue(prodComp.getReferencedProd().get().getReferencedSymbol().isExternal());
    
    MCProdSymbol scStructure = grammar.getProd("SCStructure").orElse(null);
    assertNotNull(scStructure);
    assertEquals("SCStructure", scStructure.getName());
    assertTrue(scStructure.isInterface());
    assertEquals(0, scStructure.getProdComponents().size());
    testLinkBetweenSymbolAndAst(scStructure);
    
    MCProdSymbol abstractAnything = grammar.getProd("AbstractAnything").orElse(null);
    assertNotNull(abstractAnything);
    assertEquals("AbstractAnything", abstractAnything.getName());
    assertEquals("de.monticore.statechart.Statechart.AbstractAnything",
        abstractAnything.getFullName());
    assertFalse(abstractAnything.isInterface());
    assertFalse(abstractAnything.isSymbolDefinition());
    assertEquals(0, abstractAnything.getProdComponents().size());
    testLinkBetweenSymbolAndAst(abstractAnything);
    
    final MCProdSymbol stateProd = grammar.getProd("State").orElse(null);
    assertNotNull(stateProd);
    assertEquals("State", stateProd.getName());
    assertEquals("de.monticore.statechart.Statechart.State", stateProd.getFullName());
    assertTrue(stateProd.isClass());
    
    assertEquals(1, stateProd.getSuperInterfaceProds().size());
    final MCProdSymbolReference superInterfaceScStructure = stateProd.getSuperInterfaceProds()
        .get(0);
    assertTrue(superInterfaceScStructure.existsReferencedSymbol());
    assertSame(scStructure, superInterfaceScStructure.getReferencedSymbol());
    // TODO PN generic resolving in super prod
    // AST
    testLinkBetweenSymbolAndAst(stateProd);
    
    MCProdComponentSymbol initialComponent = stateProd.getProdComponent("initial").orElse(null);
    assertNotNull(initialComponent);
    assertEquals("de.monticore.statechart.Statechart.State.initial",
        initialComponent.getFullName());
    assertEquals("initial", initialComponent.getName());
    assertEquals("initial", initialComponent.getUsageName());
    
    MCProdSymbol classBody = grammar.getProd("Classbody").orElse(null);
    assertNotNull(classBody);
    assertEquals("Classbody", classBody.getName());
    assertEquals(0, classBody.getProdComponents().size());
    assertTrue(classBody.isExternal());
    assertFalse(classBody.isSymbolDefinition());
    testLinkBetweenSymbolAndAst(classBody);
    
    MCProdSymbol codeProd = grammar.getProd("Code").orElse(null);
    assertNotNull(codeProd);
    assertEquals("Code", codeProd.getName());
    assertEquals(2, codeProd.getProdComponents().size());
    prodComp = codeProd.getProdComponent("body").get();
    assertEquals("body", prodComp.getUsageName());
    assertTrue(prodComp.getReferencedProd().isPresent());
    assertTrue(prodComp.getReferencedProd().get().existsReferencedSymbol());
    assertSame(classBody, prodComp.getReferencedProd().get().getReferencedSymbol());
    testLinkBetweenSymbolAndAst(codeProd);
  }
  
  private void testLinkBetweenSymbolAndAst(MCProdSymbol prodSymbol) {
    assertTrue(prodSymbol.getAstNode().isPresent());
    assertSame(prodSymbol, prodSymbol.getAstNode().get().getSymbol().get());
    assertSame(prodSymbol.getEnclosingScope(),
        prodSymbol.getAstNode().get().getEnclosingScope().get());
    
    if (prodSymbol.isClass()) {
      assertTrue(prodSymbol.getAstNode().get() instanceof ASTClassProd);
    }
    else if (prodSymbol.isInterface()) {
      assertTrue(prodSymbol.getAstNode().get() instanceof ASTInterfaceProd);
    }
    else if (prodSymbol.isAbstract()) {
      assertTrue(prodSymbol.getAstNode().get() instanceof ASTAbstractProd);
    }
    else if (prodSymbol.isLexerProd()) {
      assertTrue(prodSymbol.getAstNode().get() instanceof ASTLexProd);
    }
    else if (prodSymbol.isExternal()) {
      assertTrue(prodSymbol.getAstNode().get() instanceof ASTExternalProd);
    }
  }
  
  private void testLinkBetweenSymbolAndAst(MCProdComponentSymbol prodCompSymbol) {
    assertTrue(prodCompSymbol.getAstNode().isPresent());
    assertSame(prodCompSymbol, prodCompSymbol.getAstNode().get().getSymbol().get());
    assertSame(prodCompSymbol.getEnclosingScope(),
        prodCompSymbol.getAstNode().get().getEnclosingScope().get());
  }
  
  @Test
  public void testGrammarTypeReferences() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol> resolve("de.monticore.TypeReferences",
        MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    
    assertEquals(5, grammar.getProds().size());
    
    MCProdSymbol c = grammar.getProd("C").orElse(null);
    assertNotNull(c);
    assertEquals("C", c.getName());
    assertTrue(c.isInterface());
    assertEquals(0, c.getProdComponents().size());
    
    MCProdSymbol q = grammar.getProd("Q").orElse(null);
    assertNotNull(q);
    assertEquals("Q", q.getName());
    assertTrue(q.isClass());
    
    MCProdSymbol p = grammar.getProd("P").orElse(null);
    assertNotNull(p);
  }
  
  @Test
  public void testSuperGrammar() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    MCGrammarSymbol grammar = globalScope
        .<MCGrammarSymbol> resolve("de.monticore.statechart.sub.SubStatechart",
            MCGrammarSymbol.KIND)
        .orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.statechart.sub.SubStatechart", grammar.getFullName());
    assertTrue(grammar.getStartProd().isPresent());
    
    assertEquals(1, grammar.getSuperGrammars().size());
    MCGrammarSymbolReference superGrammarRef = grammar.getSuperGrammars().get(0);
    assertEquals("de.monticore.statechart.Statechart", superGrammarRef.getName());
    assertTrue(superGrammarRef.existsReferencedSymbol());
    testGrammarSymbolOfStatechart(superGrammarRef.getReferencedSymbol());
    
    MCProdSymbol firstProd = grammar.getProd("First").orElse(null);
    assertNotNull(firstProd);
    assertTrue(firstProd.isStartProd());
    assertSame(grammar.getStartProd().get(), firstProd);
    
    MCProdSymbol secondProd = grammar.getProd("Second").orElse(null);
    assertNotNull(secondProd);
    assertFalse(secondProd.isStartProd());
    
    assertEquals(2, grammar.getProdNames().size());
    assertEquals(19, grammar.getProdsWithInherited().size());
    
    // get prod of super grammar
    assertFalse(grammar.getProd("State").isPresent());
    final MCProdSymbol stateProd = grammar.getProdWithInherited("State").orElse(null);
    assertNotNull(stateProd);
    assertEquals("de.monticore.statechart.Statechart.State", stateProd.getFullName());
    
    // generic vs. specific search in super grammar
    Optional<MCProdSymbol> resolvedProd = grammar.getSpannedScope().resolve("State",
        MCProdSymbol.KIND);
    assertTrue(resolvedProd.isPresent());
    assertSame(stateProd, resolvedProd.get());
    
    Optional<MCProdSymbol> resolvedProd2 = firstProd.getEnclosingScope().resolve("State",
        MCProdSymbol.KIND);
    assertTrue(resolvedProd2.isPresent());
    assertSame(stateProd, resolvedProd2.get());
    
  }
  
  @Test
  public void testMontiCoreGrammar() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol> resolve("mc.grammars.TestGrammar",
        MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertEquals("mc.grammars.TestGrammar", grammar.getFullName());
    
    assertEquals(3, countExternalProd(grammar));
    assertEquals(5, countInterfaceAndAbstractProds(grammar));
    
    assertEquals(1, grammar.getSuperGrammars().size());
    final MCGrammarSymbolReference superGrammarRef = grammar.getSuperGrammars().get(0);
    assertTrue(superGrammarRef.existsReferencedSymbol());
    final String superGrammarFullName = superGrammarRef.getReferencedSymbol().getFullName();
    assertEquals("mc.grammars.literals.TestLiterals", superGrammarFullName);
    
    MCProdSymbol prod = grammar.getProdWithInherited("StringLiteral").orElse(null);
    assertNotNull(prod);
    assertEquals(superGrammarFullName + ".StringLiteral", prod.getFullName());
  }
  
  @Test
  public void testNonTerminalsWithSameName() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol> resolve("de.monticore"
        + ".NonTerminalsWithSameName", MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.NonTerminalsWithSameName", grammar.getFullName());
    
    assertEquals(2, grammar.getProds().size());
    MCProdSymbol transition = grammar.getProd("Transition").orElse(null);
    assertNotNull(transition);
    
    try {
      Optional<MCProdComponentSymbol> r = transition.getSpannedScope().resolve("args",
          MCProdComponentSymbol.KIND);
      assertTrue(r.isPresent());
    }
    catch (ResolvedSeveralEntriesException e) {
      fail("Only one prod component should be resolved instead of " + e.getSymbols().size());
    }
  }
  
  private int countExternalProd(MCGrammarSymbol grammar) {
    int num = 0;
    for (MCProdSymbol rule : grammar.getProds()) {
      if (rule.isExternal()) {
        num++;
      }
    }
    return num;
  }
  
  private int countInterfaceAndAbstractProds(MCGrammarSymbol grammar) {
    int num = 0;
    for (MCProdSymbol rule : grammar.getProds()) {
      if (rule.isInterface() || rule.isAbstract()) {
        num++;
      }
    }
    return num;
  }
  
  @Test
  public void testSymbolTableOfAutomaton() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    // test grammar symbol
    MCGrammarSymbol grammar = globalScope
        .<MCGrammarSymbol> resolve("Automaton", MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertTrue(grammar.getAstNode().isPresent());
    
    MCProdSymbol autProd = grammar.getSpannedScope()
        .<MCProdSymbol> resolve("Automaton", MCProdSymbol.KIND).orElse(null);
    assertNotNull(autProd);
    assertTrue(autProd.isScopeDefinition());
    assertTrue(autProd.isSymbolDefinition());

    MCProdSymbol stateProd = grammar.getSpannedScope()
        .<MCProdSymbol> resolve("State", MCProdSymbol.KIND).orElse(null);
    assertNotNull(stateProd);
    assertFalse(stateProd.isScopeDefinition());
    assertTrue(stateProd.isSymbolDefinition());
  }
  
  @Test
  public void testRuleWithSymbolReference() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol> resolve("de.monticore"
        + ".RuleWithSymbolReference", MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.RuleWithSymbolReference", grammar.getFullName());
    
    assertEquals(7, grammar.getProds().size());
    
    MCProdSymbol s = grammar.getProd("S").orElse(null);
    assertNotNull(s);
    assertTrue(s.isSymbolDefinition());
    assertEquals("S", s.getProdDefiningSymbolKind().get().getName());
    
    MCProdSymbol t = grammar.getProd("T").orElse(null);
    assertTrue(t.isSymbolDefinition());
    assertEquals("S", t.getProdDefiningSymbolKind().get().getName());
    assertSame(s, t.getProdDefiningSymbolKind().get().getReferencedSymbol());
    
    // The symbol kinds are determined transitively, i.e., A -> T -> S, hence, the symbol kind of
    // prod A is S.
    MCProdSymbol a = grammar.getProd("A").orElse(null);
    assertTrue(a.isSymbolDefinition());
    assertEquals("S", a.getSymbolDefinitionKind().get());
    
    MCProdSymbol b = grammar.getProd("B").orElse(null);
    assertFalse(b.isSymbolDefinition());
    MCProdComponentSymbol aComponent = b.getProdComponent("an").get();
    assertEquals("Name", aComponent.getReferencedProd().get().getName());
    assertEquals(a.getName(), aComponent.getReferencedSymbolName().get());
    
    MCProdSymbol e = grammar.getProd("E").orElse(null);
    assertTrue(e.isExternal());
    assertTrue(e.isSymbolDefinition());
    
    MCProdSymbol r = grammar.getProd("R").orElse(null);
    assertTrue(r.isAbstract());
    assertFalse(r.isInterface());
    assertTrue(r.isSymbolDefinition());
  }
  
}
