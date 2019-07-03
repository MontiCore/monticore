/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import de.monticore.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.*;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.*;

public class MontiCoreGrammarSymbolTableCreatorTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSymbolTableOfGrammarStatechartDSL() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    final Optional<MCGrammarSymbol> grammar = globalScope
        .resolveMCGrammar("de.monticore.statechart.Statechart");
    
    assertTrue(grammar.isPresent());
    assertTrue(grammar.get().getAstNode().isPresent());
    testGrammarSymbolOfStatechart(grammar.get());
    
  }
  
  private void testGrammarSymbolOfStatechart(MCGrammarSymbol grammar) {
    assertNotNull(grammar);
    assertEquals("de.monticore.statechart.Statechart", grammar.getFullName());
    assertEquals("de.monticore.statechart", grammar.getPackageName());
    assertTrue(grammar.getStartProd().isPresent());
    
    assertTrue(grammar.isComponent());
    assertEquals(1, grammar.getSuperGrammars().size());
    
    assertEquals(12, grammar.getProds().size());
    
    // AST
    assertTrue(grammar.getAstNode().isPresent());
    assertTrue(grammar.getAstNode().get() instanceof ASTMCGrammar);
    assertSame(grammar.getEnclosingScope(), grammar.getAstNode().get().getEnclosingScope2());
    
    final ProdSymbol stateChartProd = grammar.getProd("Statechart").orElse(null);
    assertNotNull(stateChartProd);
    assertEquals("Statechart", stateChartProd.getName());
    assertEquals("de.monticore.statechart.Statechart.Statechart", stateChartProd.getFullName());
    assertEquals("de.monticore.statechart", stateChartProd.getPackageName());
    assertTrue(stateChartProd.isStartProd());
    assertTrue(stateChartProd.isClass());
    // generic vs. specific
    Optional<ProdSymbol> resolvedStateChartProd = grammar.getSpannedScope().resolveProd("Statechart");
    assertTrue(resolvedStateChartProd.isPresent());
    assertSame(stateChartProd, resolvedStateChartProd.get());
    // AST
    testLinkBetweenSymbolAndAst(stateChartProd);
    
    final ProdSymbol entryActionProd = grammar.getProd("EntryAction").orElse(null);
    assertNotNull(entryActionProd);
    assertEquals("EntryAction", entryActionProd.getName());
    assertEquals("de.monticore.statechart.Statechart.EntryAction", entryActionProd.getFullName());
    assertFalse(entryActionProd.isStartProd());
    testLinkBetweenSymbolAndAst(entryActionProd);
    
    // test prod components
    Collection<RuleComponentSymbol> bla = entryActionProd.getProdComponents();
    assertEquals(3, entryActionProd.getProdComponents().size());
    RuleComponentSymbol prodComp = entryActionProd.getProdComponent("entry").orElse(null);
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
    Optional<RuleComponentSymbol> resolvedProdComp = entryActionProd.getSpannedScope()
        .resolveRuleComponent("entry");
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
    
    ProdSymbol scStructure = grammar.getProd("SCStructure").orElse(null);
    assertNotNull(scStructure);
    assertEquals("SCStructure", scStructure.getName());
    assertTrue(scStructure.isInterface());
    assertEquals(0, scStructure.getProdComponents().size());
    testLinkBetweenSymbolAndAst(scStructure);
    
    ProdSymbol abstractAnything = grammar.getProd("AbstractAnything").orElse(null);
    assertNotNull(abstractAnything);
    assertEquals("AbstractAnything", abstractAnything.getName());
    assertEquals("de.monticore.statechart.Statechart.AbstractAnything",
        abstractAnything.getFullName());
    assertFalse(abstractAnything.isInterface());
    assertFalse(abstractAnything.isSymbolDefinition());
    assertEquals(0, abstractAnything.getProdComponents().size());
    testLinkBetweenSymbolAndAst(abstractAnything);
    
    final ProdSymbol stateProd = grammar.getProd("State").orElse(null);
    assertNotNull(stateProd);
    assertEquals("State", stateProd.getName());
    assertEquals("de.monticore.statechart.Statechart.State", stateProd.getFullName());
    assertTrue(stateProd.isClass());
    
    assertEquals(1, stateProd.getSuperInterfaceProds().size());
    final ProdSymbolReference superInterfaceScStructure = stateProd.getSuperInterfaceProds()
        .get(0);
    assertTrue(superInterfaceScStructure.existsReferencedSymbol());
    assertSame(scStructure, superInterfaceScStructure.getReferencedSymbol());
    // TODO PN generic resolving in super prod
    // AST
    testLinkBetweenSymbolAndAst(stateProd);
    
    RuleComponentSymbol initialComponent = stateProd.getProdComponent("initial").orElse(null);
    assertNotNull(initialComponent);
    assertEquals("de.monticore.statechart.Statechart.State.initial",
        initialComponent.getFullName());
    assertEquals("initial", initialComponent.getName());
    assertEquals("initial", initialComponent.getUsageName());
    
    ProdSymbol classBody = grammar.getProd("Classbody").orElse(null);
    assertNotNull(classBody);
    assertEquals("Classbody", classBody.getName());
    assertEquals(0, classBody.getProdComponents().size());
    assertTrue(classBody.isExternal());
    assertFalse(classBody.isSymbolDefinition());
    testLinkBetweenSymbolAndAst(classBody);
    
    ProdSymbol codeProd = grammar.getProd("Code").orElse(null);
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
  
  private void testLinkBetweenSymbolAndAst(ProdSymbol prodSymbol) {
    assertTrue(prodSymbol.getAstNode().isPresent());
    assertSame(prodSymbol, prodSymbol.getAstNode().get().getSymbol2());
    assertSame(prodSymbol.getEnclosingScope(),
        prodSymbol.getAstNode().get().getEnclosingScope2());
    
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
  
  private void testLinkBetweenSymbolAndAst(RuleComponentSymbol prodCompSymbol) {
    assertTrue(prodCompSymbol.getAstNode().isPresent());
    assertSame(prodCompSymbol, prodCompSymbol.getAstNode().get().getSymbol2());
    assertSame(prodCompSymbol.getEnclosingScope(),
        prodCompSymbol.getAstNode().get().getEnclosingScope2());
  }
  
  @Test
  public void testGrammarTypeReferences() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore.TypeReferences").orElse(null);
    assertNotNull(grammar);
    
    assertEquals(5, grammar.getProds().size());
    
    ProdSymbol c = grammar.getProd("C").orElse(null);
    assertNotNull(c);
    assertEquals("C", c.getName());
    assertTrue(c.isInterface());
    assertEquals(0, c.getProdComponents().size());
    
    ProdSymbol q = grammar.getProd("Q").orElse(null);
    assertNotNull(q);
    assertEquals("Q", q.getName());
    assertTrue(q.isClass());
    
    ProdSymbol p = grammar.getProd("P").orElse(null);
    assertNotNull(p);
  }
  
  @Test
  public void testSuperGrammar() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    MCGrammarSymbol grammar = globalScope
        .resolveMCGrammar("de.monticore.statechart.sub.SubStatechart")
        .orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.statechart.sub.SubStatechart", grammar.getFullName());
    assertTrue(grammar.getStartProd().isPresent());
    
    assertEquals(1, grammar.getSuperGrammars().size());
    MCGrammarSymbolReference superGrammarRef = grammar.getSuperGrammars().get(0);
    assertEquals("de.monticore.statechart.Statechart", superGrammarRef.getName());
    assertTrue(superGrammarRef.existsReferencedSymbol());
    testGrammarSymbolOfStatechart(superGrammarRef.getReferencedSymbol());
    
    ProdSymbol firstProd = grammar.getProd("First").orElse(null);
    assertNotNull(firstProd);
    assertTrue(firstProd.isStartProd());
    assertSame(grammar.getStartProd().get(), firstProd);
    
    ProdSymbol secondProd = grammar.getProd("Second").orElse(null);
    assertNotNull(secondProd);
    assertFalse(secondProd.isStartProd());
    
    assertEquals(2, grammar.getProdNames().size());
    assertEquals(19, grammar.getProdsWithInherited().size());
    
    // get prod of super grammar
    assertFalse(grammar.getProd("State").isPresent());
    final ProdSymbol stateProd = grammar.getProdWithInherited("State").orElse(null);
    assertNotNull(stateProd);
    assertEquals("de.monticore.statechart.Statechart.State", stateProd.getFullName());
    
    // generic vs. specific search in super grammar
    Optional<ProdSymbol> resolvedProd = grammar.getSpannedScope().resolveProd("State");
    assertTrue(resolvedProd.isPresent());
    assertSame(stateProd, resolvedProd.get());
    
    Optional<ProdSymbol> resolvedProd2 = firstProd.getEnclosingScope().resolveProd("State");
    assertTrue(resolvedProd2.isPresent());
    assertSame(stateProd, resolvedProd2.get());
    
  }
  
  @Test
  public void testMontiCoreGrammar() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("mc.grammars.TestGrammar").orElse(null);
    assertNotNull(grammar);
    assertEquals("mc.grammars.TestGrammar", grammar.getFullName());
    
    assertEquals(3, countExternalProd(grammar));
    assertEquals(5, countInterfaceAndAbstractProds(grammar));
    
    assertEquals(1, grammar.getSuperGrammars().size());
    final MCGrammarSymbolReference superGrammarRef = grammar.getSuperGrammars().get(0);
    assertTrue(superGrammarRef.existsReferencedSymbol());
    final String superGrammarFullName = superGrammarRef.getReferencedSymbol().getFullName();
    assertEquals("mc.grammars.literals.TestLiterals", superGrammarFullName);
    
    ProdSymbol prod = grammar.getProdWithInherited("StringLiteral").orElse(null);
    assertNotNull(prod);
    assertEquals(superGrammarFullName + ".StringLiteral", prod.getFullName());
  }
  
  @Test
  public void testNonTerminalsWithSameName() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore"
        + ".NonTerminalsWithSameName").orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.NonTerminalsWithSameName", grammar.getFullName());
    
    assertEquals(2, grammar.getProds().size());
    ProdSymbol transition = grammar.getProd("Transition").orElse(null);
    assertNotNull(transition);
    
    try {
      Optional<RuleComponentSymbol> r = transition.getSpannedScope().resolveRuleComponent("arg");
      assertTrue(r.isPresent());
    }
    catch (ResolvedSeveralEntriesException e) {
      fail("Only one prod component should be resolved instead of " + e.getSymbols().size());
    }
  }
  
  private int countExternalProd(MCGrammarSymbol grammar) {
    int num = 0;
    for (ProdSymbol rule : grammar.getProds()) {
      if (rule.isExternal()) {
        num++;
      }
    }
    return num;
  }
  
  private int countInterfaceAndAbstractProds(MCGrammarSymbol grammar) {
    int num = 0;
    for (ProdSymbol rule : grammar.getProds()) {
      if (rule.isInterface() || rule.isAbstract()) {
        num++;
      }
    }
    return num;
  }
  
  @Test
  public void testSymbolTableOfAutomaton() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    // test grammar symbol
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("Automaton").orElse(null);
    assertNotNull(grammar);
    assertTrue(grammar.getAstNode().isPresent());
    
    ProdSymbol autProd = grammar.getSpannedScope()
        .resolveProd("Automaton").orElse(null);
    assertNotNull(autProd);
    assertTrue(autProd.isScopeDefinition());
    assertTrue(autProd.isSymbolDefinition());

    ProdSymbol stateProd = grammar.getSpannedScope().resolveProd("State").orElse(null);
    assertNotNull(stateProd);
    assertFalse(stateProd.isScopeDefinition());
    assertTrue(stateProd.isSymbolDefinition());
  }
  
  @Ignore
  @Test
  public void testRuleWithSymbolReference() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore"
        + ".RuleWithSymbolReference").orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.RuleWithSymbolReference", grammar.getFullName());
    
    assertEquals(7, grammar.getProds().size());
    
    ProdSymbol s = grammar.getProd("S").orElse(null);
    assertNotNull(s);
    assertTrue(s.isSymbolDefinition());
    assertEquals("S", s.getSymbolDefinitionKind().get());
    
    ProdSymbol t = grammar.getProd("T").orElse(null);
    assertTrue(t.isSymbolDefinition());
    assertEquals("S", t.getSymbolDefinitionKind().get());
    
    // The symbol kinds are determined transitively, i.e., A -> T -> S, hence, the symbol kind of
    // prod A is S.
    ProdSymbol a = grammar.getProd("A").orElse(null);
    assertTrue(a.isSymbolDefinition());
    assertEquals("S", a.getSymbolDefinitionKind().get());
    
    ProdSymbol b = grammar.getProd("B").orElse(null);
    assertFalse(b.isSymbolDefinition());
    RuleComponentSymbol aComponent = b.getProdComponent("an").get();
    assertEquals("Name", aComponent.getReferencedProd().get().getName());
    assertEquals(a.getName(), aComponent.getReferencedSymbolName().get());
    
    ProdSymbol e = grammar.getProd("E").orElse(null);
    assertTrue(e.isExternal());
    assertTrue(e.isSymbolDefinition());
    
    ProdSymbol r = grammar.getProd("R").orElse(null);
    assertTrue(r.isAbstract());
    assertFalse(r.isInterface());
    assertTrue(r.isSymbolDefinition());
  }
  
}
