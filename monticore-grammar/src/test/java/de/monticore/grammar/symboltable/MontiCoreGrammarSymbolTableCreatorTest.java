/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import de.monticore.grammar.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.*;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class MontiCoreGrammarSymbolTableCreatorTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    Grammar_WithConceptsMill.reset();
    Grammar_WithConceptsMill.init();
  }
  
  @Test
  public void testSymbolTableOfGrammarStatechartDSL() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    final Optional<MCGrammarSymbol> grammar = globalScope
        .resolveMCGrammar("de.monticore.Statechart");
    
    assertTrue(grammar.isPresent());
    assertTrue(grammar.get().isPresentAstNode());
    testGrammarSymbolOfStatechart(grammar.get());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  private void testGrammarSymbolOfStatechart(MCGrammarSymbol grammar) {
    assertNotNull(grammar);
    assertEquals("de.monticore.Statechart", grammar.getFullName());
    assertEquals("de.monticore", grammar.getPackageName());
    assertTrue(grammar.getStartProd().isPresent());
    
    assertTrue(grammar.isIsComponent());
    assertEquals(1, grammar.getSuperGrammars().size());
    
    assertEquals(12, grammar.getProds().size());
    
    // AST
    assertTrue(grammar.isPresentAstNode());
    assertSame(grammar.getEnclosingScope(), grammar.getAstNode().getEnclosingScope());
    
    final ProdSymbol stateChartProd = grammar.getProd("Statechart").orElse(null);
    assertNotNull(stateChartProd);
    assertEquals("Statechart", stateChartProd.getName());
    assertEquals("de.monticore.Statechart.Statechart", stateChartProd.getFullName());
    assertEquals("de.monticore", stateChartProd.getPackageName());
    assertTrue(stateChartProd.isIsStartProd());
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
    assertEquals("de.monticore.Statechart.EntryAction", entryActionProd.getFullName());
    assertFalse(entryActionProd.isIsStartProd());
    testLinkBetweenSymbolAndAst(entryActionProd);
    
    // test prod components
    Collection<RuleComponentSymbol> rcsList = entryActionProd.getProdComponents();
    assertEquals(1, rcsList.size());

    List<RuleComponentSymbol> prodComps = entryActionProd.getSpannedScope().resolveRuleComponentDownMany("block");
    assertFalse(prodComps.isEmpty());
    assertEquals("block", prodComps.get(0).getName());
    assertTrue(prodComps.get(0).isIsNonterminal());
    assertTrue(prodComps.get(0).isPresentAstNode());
    assertFalse(prodComps.get(0).isIsList());
    assertFalse(prodComps.get(0).isIsOptional());
    assertSame(entryActionProd.getSpannedScope(), prodComps.get(0).getEnclosingScope());
    // reference to defining prod
    assertEquals("BlockStatement", prodComps.get(0).getReferencedProd().get().getName());
    assertTrue(prodComps.get(0).getReferencedProd().get().isIsExternal());
    
    ProdSymbol scStructure = grammar.getProd("SCStructure").orElse(null);
    assertNotNull(scStructure);
    assertEquals("SCStructure", scStructure.getName());
    assertTrue(scStructure.isIsInterface());
    assertEquals(0, scStructure.getProdComponents().size());
    testLinkBetweenSymbolAndAst(scStructure);
    
    ProdSymbol abstractAnything = grammar.getProd("AbstractAnything").orElse(null);
    assertNotNull(abstractAnything);
    assertEquals("AbstractAnything", abstractAnything.getName());
    assertEquals("de.monticore.Statechart.AbstractAnything",
        abstractAnything.getFullName());
    assertFalse(abstractAnything.isIsInterface());
    assertFalse(abstractAnything.isIsSymbolDefinition());
    assertEquals(0, abstractAnything.getProdComponents().size());
    testLinkBetweenSymbolAndAst(abstractAnything);
    
    final ProdSymbol stateProd = grammar.getProd("State").orElse(null);
    assertNotNull(stateProd);
    assertEquals("State", stateProd.getName());
    assertEquals("de.monticore.Statechart.State", stateProd.getFullName());
    assertTrue(stateProd.isClass());
    
    assertEquals(1, stateProd.getSuperInterfaceProds().size());
    final ProdSymbolSurrogate superInterfaceScStructure = stateProd.getSuperInterfaceProds()
        .get(0);
    assertSame(scStructure, superInterfaceScStructure.lazyLoadDelegate());
    // AST
    testLinkBetweenSymbolAndAst(stateProd);
    
    List<RuleComponentSymbol> initialComponents = stateProd.getSpannedScope().resolveRuleComponentDownMany("initial");
    assertFalse(initialComponents.isEmpty());
    RuleComponentSymbol initialComponent = initialComponents.get(0);
    assertEquals("de.monticore.Statechart.State.initial",
        initialComponent.getFullName());
    assertEquals("initial", initialComponent.getName());

    ProdSymbol classBody = grammar.getProd("Classbody").orElse(null);
    assertNotNull(classBody);
    assertEquals("Classbody", classBody.getName());
    assertEquals(0, classBody.getProdComponents().size());
    assertTrue(classBody.isIsExternal());
    assertFalse(classBody.isIsSymbolDefinition());
    testLinkBetweenSymbolAndAst(classBody);
    
    ProdSymbol codeProd = grammar.getProd("Code").orElse(null);
    assertNotNull(codeProd);
    assertEquals("Code", codeProd.getName());
    assertEquals(1, codeProd.getProdComponents().size());
    prodComps = codeProd.getSpannedScope().resolveRuleComponentDownMany("body");
    assertFalse((prodComps.isEmpty()));
    assertTrue(prodComps.get(0).getReferencedProd().isPresent());
    assertSame(classBody, prodComps.get(0).getReferencedProd().get().lazyLoadDelegate());
    testLinkBetweenSymbolAndAst(codeProd);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  private void testLinkBetweenSymbolAndAst(ProdSymbol prodSymbol) {
    assertTrue(prodSymbol.isPresentAstNode());
    assertSame(prodSymbol, prodSymbol.getAstNode().getSymbol());
    assertSame(prodSymbol.getEnclosingScope(),
        prodSymbol.getAstNode().getEnclosingScope());
    
    if (prodSymbol.isClass()) {
      assertTrue(prodSymbol.getAstNode() instanceof ASTClassProd);
    }
    else if (prodSymbol.isIsInterface()) {
      assertTrue(prodSymbol.getAstNode() instanceof ASTInterfaceProd);
    }
    else if (prodSymbol.isIsAbstract()) {
      assertTrue(prodSymbol.getAstNode() instanceof ASTAbstractProd);
    }
    else if (prodSymbol.isIsLexerProd()) {
      assertTrue(prodSymbol.getAstNode() instanceof ASTLexProd);
    }
    else if (prodSymbol.isIsExternal()) {
      assertTrue(prodSymbol.getAstNode() instanceof ASTExternalProd);
    }
  }

  @Test
  public void testGrammarTypeReferences() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore.TypeReferences").orElse(null);
    assertNotNull(grammar);
    
    assertEquals(5, grammar.getProds().size());
    
    ProdSymbol c = grammar.getProd("C").orElse(null);
    assertNotNull(c);
    assertEquals("C", c.getName());
    assertTrue(c.isIsInterface());
    assertEquals(0, c.getProdComponents().size());
    
    ProdSymbol q = grammar.getProd("Q").orElse(null);
    assertNotNull(q);
    assertEquals("Q", q.getName());
    assertTrue(q.isClass());
    
    ProdSymbol p = grammar.getProd("P").orElse(null);
    assertNotNull(p);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testSuperGrammar() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope
        .resolveMCGrammar("de.monticore.SubStatechart")
        .orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.SubStatechart", grammar.getFullName());
    assertTrue(grammar.getStartProd().isPresent());
    
    assertEquals(1, grammar.getSuperGrammars().size());
    MCGrammarSymbolSurrogate superGrammarRef = grammar.getSuperGrammars().get(0);
    assertEquals("de.monticore.Statechart", superGrammarRef.getName());
    testGrammarSymbolOfStatechart(superGrammarRef.lazyLoadDelegate());
    
    ProdSymbol firstProd = grammar.getProd("First").orElse(null);
    assertNotNull(firstProd);
    assertTrue(firstProd.isIsStartProd());
    assertSame(grammar.getStartProd().get(), firstProd);
    
    ProdSymbol secondProd = grammar.getProd("Second").orElse(null);
    assertNotNull(secondProd);
    assertFalse(secondProd.isIsStartProd());
    
    assertEquals(2, grammar.getProdNames().size());
    assertEquals(19, grammar.getProdsWithInherited().size());
    
    // get prod of super grammar
    assertFalse(grammar.getProd("State").isPresent());
    final ProdSymbol stateProd = grammar.getProdWithInherited("State").orElse(null);
    assertNotNull(stateProd);
    assertEquals("de.monticore.Statechart.State", stateProd.getFullName());
    
    // generic vs. specific search in super grammar
    Optional<ProdSymbol> resolvedProd = grammar.getSpannedScope().resolveProd("State");
    assertTrue(resolvedProd.isPresent());
    assertSame(stateProd, resolvedProd.get());
    
    Optional<ProdSymbol> resolvedProd2 = firstProd.getEnclosingScope().resolveProd("State");
    assertTrue(resolvedProd2.isPresent());
    assertSame(stateProd, resolvedProd2.get());
  
    assertTrue(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void testMontiCoreGrammar() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore.TestGrammar").orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.TestGrammar", grammar.getFullName());
    
    assertEquals(3, countExternalProd(grammar));
    assertEquals(5, countInterfaceAndAbstractProds(grammar));
    
    assertEquals(1, grammar.getSuperGrammars().size());
    final MCGrammarSymbolSurrogate superGrammarRef = grammar.getSuperGrammars().get(0);
    final String superGrammarFullName = superGrammarRef.lazyLoadDelegate().getFullName();
    assertEquals("de.monticore.common.TestLiterals", superGrammarFullName);
    
    ProdSymbol prod = grammar.getProdWithInherited("StringLiteral").orElse(null);
    assertNotNull(prod);
    assertEquals(superGrammarFullName + ".StringLiteral", prod.getFullName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testNonTerminalsWithSameName() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore"
        + ".NonTerminalsWithSameName").orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.NonTerminalsWithSameName", grammar.getFullName());
    
    assertEquals(2, grammar.getProds().size());
    ProdSymbol transition = grammar.getProd("Transition").orElse(null);
    assertNotNull(transition);
    
    List<RuleComponentSymbol> r = transition.getSpannedScope().resolveRuleComponentMany("arg");
    assertEquals(2, r.size());
    assertTrue(r.get(0).isIsList());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTokenModes() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();

    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore.Modes").orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.Modes", grammar.getFullName());

    Map<String, Collection<String>> tokenModes = grammar.getTokenModesWithInherited();
    assertEquals(3, tokenModes.size());
    assertEquals(4, tokenModes.get(MCGrammarSymbol.DEFAULT_MODE).size());
    assertEquals(1, tokenModes.get("FOO_MODE").size());
    assertEquals(1, tokenModes.get("BLA_MODE").size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testReplaceKeywords() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();

    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore.Keywords").orElse(null);
    assertNotNull(grammar);

    Map<String, Collection<String>> keywords = grammar.getReplacedKeywordsWithInherited();
    assertEquals(2, keywords.size());
    assertEquals(1, keywords.get("A").size());
    assertEquals(4, keywords.get("B").size());

    assertTrue(Log.getFindings().isEmpty());
  }

  private int countExternalProd(MCGrammarSymbol grammar) {
    int num = 0;
    for (ProdSymbol rule : grammar.getProds()) {
      if (rule.isIsExternal()) {
        num++;
      }
    }
    return num;
  }
  
  private int countInterfaceAndAbstractProds(MCGrammarSymbol grammar) {
    int num = 0;
    for (ProdSymbol rule : grammar.getProds()) {
      if (rule.isIsInterface() || rule.isIsAbstract()) {
        num++;
      }
    }
    return num;
  }
  
  @Test
  public void testSymbolTableOfAutomaton() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("Automaton").orElse(null);
    assertNotNull(grammar);
    assertTrue(grammar.isPresentAstNode());
    
    ProdSymbol autProd = grammar.getSpannedScope()
        .resolveProd("Automaton").orElse(null);
    assertNotNull(autProd);
    assertTrue(autProd.isIsScopeSpanning());
    assertTrue(autProd.isIsSymbolDefinition());

    ProdSymbol stateProd = grammar.getSpannedScope().resolveProd("State").orElse(null);
    assertNotNull(stateProd);
    assertFalse(stateProd.isIsScopeSpanning());
    assertTrue(stateProd.isIsSymbolDefinition());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testRuleWithSymbolReference() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore"
        + ".RuleWithSymbolReference").orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.RuleWithSymbolReference", grammar.getFullName());
    
    assertEquals(7, grammar.getProds().size());
    
    ProdSymbol s = grammar.getProd("S").orElse(null);
    assertNotNull(s);
    assertTrue(s.isIsSymbolDefinition());
    assertEquals("S", s.getName());
    
    ProdSymbol t = grammar.getProd("T").orElse(null);
    assertEquals("T", t.getName());
    assertFalse(t.isIsSymbolDefinition());

    ProdSymbol a = grammar.getProd("A").orElse(null);
    assertEquals("A", a.getName());
    assertFalse(a.isIsSymbolDefinition());

    ProdSymbol b = grammar.getProd("B").orElse(null);
    assertFalse(b.isIsSymbolDefinition());
    List<RuleComponentSymbol> comps = b.getSpannedScope().resolveRuleComponentDownMany("an");
    assertFalse(comps.isEmpty());
    RuleComponentSymbol aComponent = comps.get(0);
    assertEquals("Name", aComponent.getReferencedProd().get().getName());

    ProdSymbol e = grammar.getProd("E").orElse(null);
    assertTrue(e.isIsExternal());
    assertTrue(e.isIsSymbolDefinition());
    
    ProdSymbol r = grammar.getProd("R").orElse(null);
    assertTrue(r.isIsAbstract());
    assertFalse(r.isIsInterface());
    assertTrue(r.isIsSymbolDefinition());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * tests that for ASTKey only a symbol is created if the key has a usage name
   * e.g. key("b") -> no usage name -> no symbol
   *      b:key("b") -> with usage name -> has symbol
   */
  @Test
  public void testASTKeySymbolCreation() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    Optional<MCGrammarSymbol> grammarOpt = globalScope.resolveMCGrammar("de.monticore.KeyAndNext");
    assertTrue(grammarOpt.isPresent());
    MCGrammarSymbol grammar = grammarOpt.get();
    assertNotNull(grammar);
    assertTrue(grammar.isPresentAstNode());

    // no usage name
    Optional<ProdSymbol> aProd = grammar.getSpannedScope().resolveProd("A");
    assertTrue(aProd.isPresent());
    List<RuleComponentSymbol> comps = aProd.get().getSpannedScope().resolveRuleComponentDownMany("b");
    assertFalse(comps.isEmpty());
    RuleComponentSymbol aBRule= comps.get(0);
    assertFalse(aBRule.isIsList());

    // with usage name
    Optional<ProdSymbol> bProd = grammar.getSpannedScope().resolveProd("B");
    assertTrue(bProd.isPresent());
    List<RuleComponentSymbol> bBRules = bProd.get().getSpannedScope().resolveRuleComponentMany("b");
    assertTrue(!bBRules.isEmpty());
    assertTrue(bBRules.get(0).isIsList());

    // no usage name
    Optional<ProdSymbol> cProd = grammar.getSpannedScope().resolveProd("C");
    assertTrue(cProd.isPresent());
    List<RuleComponentSymbol> cBRules= cProd.get().getSpannedScope().resolveRuleComponentMany("b");
    assertFalse(cBRules.isEmpty());
    assertFalse(cBRules.get(0).isIsList());

    // no usage name
    Optional<ProdSymbol> dProd = grammar.getSpannedScope().resolveProd("D");
    assertTrue(dProd.isPresent());
    List<RuleComponentSymbol> dBRules= dProd.get().getSpannedScope().resolveRuleComponentMany("b");
    assertFalse(dBRules.isEmpty());
    assertFalse(dBRules.get(0).isIsList());

    // with usage name
    Optional<ProdSymbol> eProd = grammar.getSpannedScope().resolveProd("E");
    assertTrue(eProd.isPresent());
    List<RuleComponentSymbol> eBRules = eProd.get().getSpannedScope().resolveRuleComponentMany("b");
    assertTrue(!eBRules.isEmpty());
    assertTrue(eBRules.get(0).isIsList());

    // no usage name
    Optional<ProdSymbol> fProd = grammar.getSpannedScope().resolveProd("F");
    assertTrue(fProd.isPresent());
    List<RuleComponentSymbol> fBRules = fProd.get().getSpannedScope().resolveRuleComponentMany("b");
    assertTrue(fBRules.isEmpty());

    // with usage name
    Optional<ProdSymbol> gProd = grammar.getSpannedScope().resolveProd("G");
    assertTrue(gProd.isPresent());
    List<RuleComponentSymbol> gBRules = gProd.get().getSpannedScope().resolveRuleComponentMany("b");
    assertFalse(gBRules.isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
