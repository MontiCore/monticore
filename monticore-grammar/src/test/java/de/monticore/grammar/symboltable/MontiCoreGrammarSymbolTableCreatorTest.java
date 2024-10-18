/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import de.monticore.grammar.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.*;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MontiCoreGrammarSymbolTableCreatorTest {
  
  @BeforeEach
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
    
    Assertions.assertTrue(grammar.isPresent());
    Assertions.assertTrue(grammar.get().isPresentAstNode());
    testGrammarSymbolOfStatechart(grammar.get());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  private void testGrammarSymbolOfStatechart(MCGrammarSymbol grammar) {
    Assertions.assertNotNull(grammar);
    Assertions.assertEquals("de.monticore.Statechart", grammar.getFullName());
    Assertions.assertEquals("de.monticore", grammar.getPackageName());
    Assertions.assertTrue(grammar.getStartProd().isPresent());
    
    Assertions.assertTrue(grammar.isIsComponent());
    Assertions.assertEquals(1, grammar.getSuperGrammars().size());
    
    Assertions.assertEquals(12, grammar.getProds().size());
    
    // AST
    Assertions.assertTrue(grammar.isPresentAstNode());
    Assertions.assertSame(grammar.getEnclosingScope(), grammar.getAstNode().getEnclosingScope());
    
    final ProdSymbol stateChartProd = grammar.getProd("Statechart").orElse(null);
    Assertions.assertNotNull(stateChartProd);
    Assertions.assertEquals("Statechart", stateChartProd.getName());
    Assertions.assertEquals("de.monticore.Statechart.Statechart", stateChartProd.getFullName());
    Assertions.assertEquals("de.monticore", stateChartProd.getPackageName());
    Assertions.assertTrue(stateChartProd.isIsStartProd());
    Assertions.assertTrue(stateChartProd.isClass());
    // generic vs. specific
    Optional<ProdSymbol> resolvedStateChartProd = grammar.getSpannedScope().resolveProd("Statechart");
    Assertions.assertTrue(resolvedStateChartProd.isPresent());
    Assertions.assertSame(stateChartProd, resolvedStateChartProd.get());
    // AST
    testLinkBetweenSymbolAndAst(stateChartProd);
    
    final ProdSymbol entryActionProd = grammar.getProd("EntryAction").orElse(null);
    Assertions.assertNotNull(entryActionProd);
    Assertions.assertEquals("EntryAction", entryActionProd.getName());
    Assertions.assertEquals("de.monticore.Statechart.EntryAction", entryActionProd.getFullName());
    Assertions.assertFalse(entryActionProd.isIsStartProd());
    testLinkBetweenSymbolAndAst(entryActionProd);
    
    // test prod components
    Collection<RuleComponentSymbol> rcsList = entryActionProd.getProdComponents();
    Assertions.assertEquals(1, rcsList.size());

    List<RuleComponentSymbol> prodComps = entryActionProd.getSpannedScope().resolveRuleComponentDownMany("block");
    Assertions.assertFalse(prodComps.isEmpty());
    Assertions.assertEquals("block", prodComps.get(0).getName());
    Assertions.assertTrue(prodComps.get(0).isIsNonterminal());
    Assertions.assertTrue(prodComps.get(0).isPresentAstNode());
    Assertions.assertFalse(prodComps.get(0).isIsList());
    Assertions.assertFalse(prodComps.get(0).isIsOptional());
    Assertions.assertSame(entryActionProd.getSpannedScope(), prodComps.get(0).getEnclosingScope());
    // reference to defining prod
    Assertions.assertEquals("BlockStatement", prodComps.get(0).getReferencedProd().get().getName());
    Assertions.assertTrue(prodComps.get(0).getReferencedProd().get().isIsExternal());
    
    ProdSymbol scStructure = grammar.getProd("SCStructure").orElse(null);
    Assertions.assertNotNull(scStructure);
    Assertions.assertEquals("SCStructure", scStructure.getName());
    Assertions.assertTrue(scStructure.isIsInterface());
    Assertions.assertEquals(0, scStructure.getProdComponents().size());
    testLinkBetweenSymbolAndAst(scStructure);
    
    ProdSymbol abstractAnything = grammar.getProd("AbstractAnything").orElse(null);
    Assertions.assertNotNull(abstractAnything);
    Assertions.assertEquals("AbstractAnything", abstractAnything.getName());
    Assertions.assertEquals("de.monticore.Statechart.AbstractAnything", abstractAnything.getFullName());
    Assertions.assertFalse(abstractAnything.isIsInterface());
    Assertions.assertFalse(abstractAnything.isIsSymbolDefinition());
    Assertions.assertEquals(0, abstractAnything.getProdComponents().size());
    testLinkBetweenSymbolAndAst(abstractAnything);
    
    final ProdSymbol stateProd = grammar.getProd("State").orElse(null);
    Assertions.assertNotNull(stateProd);
    Assertions.assertEquals("State", stateProd.getName());
    Assertions.assertEquals("de.monticore.Statechart.State", stateProd.getFullName());
    Assertions.assertTrue(stateProd.isClass());
    
    Assertions.assertEquals(1, stateProd.getSuperInterfaceProds().size());
    final ProdSymbolSurrogate superInterfaceScStructure = stateProd.getSuperInterfaceProds()
        .get(0);
    Assertions.assertSame(scStructure, superInterfaceScStructure.lazyLoadDelegate());
    // AST
    testLinkBetweenSymbolAndAst(stateProd);
    
    List<RuleComponentSymbol> initialComponents = stateProd.getSpannedScope().resolveRuleComponentDownMany("initial");
    Assertions.assertFalse(initialComponents.isEmpty());
    RuleComponentSymbol initialComponent = initialComponents.get(0);
    Assertions.assertEquals("de.monticore.Statechart.State.initial", initialComponent.getFullName());
    Assertions.assertEquals("initial", initialComponent.getName());

    ProdSymbol classBody = grammar.getProd("Classbody").orElse(null);
    Assertions.assertNotNull(classBody);
    Assertions.assertEquals("Classbody", classBody.getName());
    Assertions.assertEquals(0, classBody.getProdComponents().size());
    Assertions.assertTrue(classBody.isIsExternal());
    Assertions.assertFalse(classBody.isIsSymbolDefinition());
    testLinkBetweenSymbolAndAst(classBody);
    
    ProdSymbol codeProd = grammar.getProd("Code").orElse(null);
    Assertions.assertNotNull(codeProd);
    Assertions.assertEquals("Code", codeProd.getName());
    Assertions.assertEquals(1, codeProd.getProdComponents().size());
    prodComps = codeProd.getSpannedScope().resolveRuleComponentDownMany("body");
    Assertions.assertFalse((prodComps.isEmpty()));
    Assertions.assertTrue(prodComps.get(0).getReferencedProd().isPresent());
    Assertions.assertSame(classBody, prodComps.get(0).getReferencedProd().get().lazyLoadDelegate());
    testLinkBetweenSymbolAndAst(codeProd);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  private void testLinkBetweenSymbolAndAst(ProdSymbol prodSymbol) {
    Assertions.assertTrue(prodSymbol.isPresentAstNode());
    Assertions.assertSame(prodSymbol, prodSymbol.getAstNode().getSymbol());
    Assertions.assertSame(prodSymbol.getEnclosingScope(), prodSymbol.getAstNode().getEnclosingScope());
    
    if (prodSymbol.isClass()) {
      Assertions.assertTrue(prodSymbol.getAstNode() instanceof ASTClassProd);
    }
    else if (prodSymbol.isIsInterface()) {
      Assertions.assertTrue(prodSymbol.getAstNode() instanceof ASTInterfaceProd);
    }
    else if (prodSymbol.isIsAbstract()) {
      Assertions.assertTrue(prodSymbol.getAstNode() instanceof ASTAbstractProd);
    }
    else if (prodSymbol.isIsLexerProd()) {
      Assertions.assertTrue(prodSymbol.getAstNode() instanceof ASTLexProd);
    }
    else if (prodSymbol.isIsExternal()) {
      Assertions.assertTrue(prodSymbol.getAstNode() instanceof ASTExternalProd);
    }
  }

  @Test
  public void testGrammarTypeReferences() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore.TypeReferences").orElse(null);
    Assertions.assertNotNull(grammar);
    
    Assertions.assertEquals(5, grammar.getProds().size());
    
    ProdSymbol c = grammar.getProd("C").orElse(null);
    Assertions.assertNotNull(c);
    Assertions.assertEquals("C", c.getName());
    Assertions.assertTrue(c.isIsInterface());
    Assertions.assertEquals(0, c.getProdComponents().size());
    
    ProdSymbol q = grammar.getProd("Q").orElse(null);
    Assertions.assertNotNull(q);
    Assertions.assertEquals("Q", q.getName());
    Assertions.assertTrue(q.isClass());
    
    ProdSymbol p = grammar.getProd("P").orElse(null);
    Assertions.assertNotNull(p);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testSuperGrammar() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope
        .resolveMCGrammar("de.monticore.SubStatechart")
        .orElse(null);
    Assertions.assertNotNull(grammar);
    Assertions.assertEquals("de.monticore.SubStatechart", grammar.getFullName());
    Assertions.assertTrue(grammar.getStartProd().isPresent());
    
    Assertions.assertEquals(1, grammar.getSuperGrammars().size());
    MCGrammarSymbolSurrogate superGrammarRef = grammar.getSuperGrammars().get(0);
    Assertions.assertEquals("Statechart", superGrammarRef.getName());
    Assertions.assertEquals("de.monticore.Statechart", superGrammarRef.getFullName());
    testGrammarSymbolOfStatechart(superGrammarRef.lazyLoadDelegate());
    
    ProdSymbol firstProd = grammar.getProd("First").orElse(null);
    Assertions.assertNotNull(firstProd);
    Assertions.assertTrue(firstProd.isIsStartProd());
    Assertions.assertSame(grammar.getStartProd().get(), firstProd);
    
    ProdSymbol secondProd = grammar.getProd("Second").orElse(null);
    Assertions.assertNotNull(secondProd);
    Assertions.assertFalse(secondProd.isIsStartProd());
    
    Assertions.assertEquals(2, grammar.getProdNames().size());
    Assertions.assertEquals(19, grammar.getProdsWithInherited().size());
    
    // get prod of super grammar
    Assertions.assertFalse(grammar.getProd("State").isPresent());
    final ProdSymbol stateProd = grammar.getProdWithInherited("State").orElse(null);
    Assertions.assertNotNull(stateProd);
    Assertions.assertEquals("de.monticore.Statechart.State", stateProd.getFullName());
    
    // generic vs. specific search in super grammar
    Optional<ProdSymbol> resolvedProd = grammar.getSpannedScope().resolveProd("State");
    Assertions.assertTrue(resolvedProd.isPresent());
    Assertions.assertSame(stateProd, resolvedProd.get());
    
    Optional<ProdSymbol> resolvedProd2 = firstProd.getEnclosingScope().resolveProd("State");
    Assertions.assertTrue(resolvedProd2.isPresent());
    Assertions.assertSame(stateProd, resolvedProd2.get());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void testMontiCoreGrammar() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore.TestGrammar").orElse(null);
    Assertions.assertNotNull(grammar);
    Assertions.assertEquals("de.monticore.TestGrammar", grammar.getFullName());
    
    Assertions.assertEquals(3, countExternalProd(grammar));
    Assertions.assertEquals(5, countInterfaceAndAbstractProds(grammar));
    
    Assertions.assertEquals(1, grammar.getSuperGrammars().size());
    final MCGrammarSymbolSurrogate superGrammarRef = grammar.getSuperGrammars().get(0);
    final String superGrammarFullName = superGrammarRef.lazyLoadDelegate().getFullName();
    Assertions.assertEquals("de.monticore.common.TestLiterals", superGrammarFullName);
    
    ProdSymbol prod = grammar.getProdWithInherited("StringLiteral").orElse(null);
    Assertions.assertNotNull(prod);
    Assertions.assertEquals(superGrammarFullName + ".StringLiteral", prod.getFullName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testNonTerminalsWithSameName() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore"
        + ".NonTerminalsWithSameName").orElse(null);
    Assertions.assertNotNull(grammar);
    Assertions.assertEquals("de.monticore.NonTerminalsWithSameName", grammar.getFullName());
    
    Assertions.assertEquals(2, grammar.getProds().size());
    ProdSymbol transition = grammar.getProd("Transition").orElse(null);
    Assertions.assertNotNull(transition);
    
    List<RuleComponentSymbol> r = transition.getSpannedScope().resolveRuleComponentMany("arg");
    Assertions.assertEquals(2, r.size());
    Assertions.assertTrue(r.get(0).isIsList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTokenModes() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();

    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore.Modes").orElse(null);
    Assertions.assertNotNull(grammar);
    Assertions.assertEquals("de.monticore.Modes", grammar.getFullName());

    Map<String, Collection<String>> tokenModes = grammar.getTokenModesWithInherited();
    Assertions.assertEquals(3, tokenModes.size());
    Assertions.assertEquals(4, tokenModes.get(MCGrammarSymbol.DEFAULT_MODE).size());
    Assertions.assertEquals(1, tokenModes.get("FOO_MODE").size());
    Assertions.assertEquals(1, tokenModes.get("BLA_MODE").size());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testReplaceKeywords() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();

    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore.Keywords").orElse(null);
    Assertions.assertNotNull(grammar);

    Map<String, Collection<String>> keywords = grammar.getReplacedKeywordsWithInherited();
    Assertions.assertEquals(2, keywords.size());
    Assertions.assertEquals(1, keywords.get("A").size());
    Assertions.assertEquals(4, keywords.get("B").size());

    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    Assertions.assertNotNull(grammar);
    Assertions.assertTrue(grammar.isPresentAstNode());
    
    ProdSymbol autProd = grammar.getSpannedScope()
        .resolveProd("Automaton").orElse(null);
    Assertions.assertNotNull(autProd);
    Assertions.assertTrue(autProd.isIsScopeSpanning());
    Assertions.assertTrue(autProd.isIsSymbolDefinition());

    ProdSymbol stateProd = grammar.getSpannedScope().resolveProd("State").orElse(null);
    Assertions.assertNotNull(stateProd);
    Assertions.assertFalse(stateProd.isIsScopeSpanning());
    Assertions.assertTrue(stateProd.isIsSymbolDefinition());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testRuleWithSymbolReference() {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope.resolveMCGrammar("de.monticore"
        + ".RuleWithSymbolReference").orElse(null);
    Assertions.assertNotNull(grammar);
    Assertions.assertEquals("de.monticore.RuleWithSymbolReference", grammar.getFullName());
    
    Assertions.assertEquals(7, grammar.getProds().size());
    
    ProdSymbol s = grammar.getProd("S").orElse(null);
    Assertions.assertNotNull(s);
    Assertions.assertTrue(s.isIsSymbolDefinition());
    Assertions.assertEquals("S", s.getName());
    
    ProdSymbol t = grammar.getProd("T").orElse(null);
    Assertions.assertEquals("T", t.getName());
    Assertions.assertFalse(t.isIsSymbolDefinition());

    ProdSymbol a = grammar.getProd("A").orElse(null);
    Assertions.assertEquals("A", a.getName());
    Assertions.assertFalse(a.isIsSymbolDefinition());

    ProdSymbol b = grammar.getProd("B").orElse(null);
    Assertions.assertFalse(b.isIsSymbolDefinition());
    List<RuleComponentSymbol> comps = b.getSpannedScope().resolveRuleComponentDownMany("an");
    Assertions.assertFalse(comps.isEmpty());
    RuleComponentSymbol aComponent = comps.get(0);
    Assertions.assertEquals("Name", aComponent.getReferencedProd().get().getName());

    ProdSymbol e = grammar.getProd("E").orElse(null);
    Assertions.assertTrue(e.isIsExternal());
    Assertions.assertTrue(e.isIsSymbolDefinition());
    
    ProdSymbol r = grammar.getProd("R").orElse(null);
    Assertions.assertTrue(r.isIsAbstract());
    Assertions.assertFalse(r.isIsInterface());
    Assertions.assertTrue(r.isIsSymbolDefinition());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    Assertions.assertTrue(grammarOpt.isPresent());
    MCGrammarSymbol grammar = grammarOpt.get();
    Assertions.assertNotNull(grammar);
    Assertions.assertTrue(grammar.isPresentAstNode());

    // no usage name
    Optional<ProdSymbol> aProd = grammar.getSpannedScope().resolveProd("A");
    Assertions.assertTrue(aProd.isPresent());
    List<RuleComponentSymbol> comps = aProd.get().getSpannedScope().resolveRuleComponentDownMany("b");
    Assertions.assertFalse(comps.isEmpty());
    RuleComponentSymbol aBRule= comps.get(0);
    Assertions.assertFalse(aBRule.isIsList());

    // with usage name
    Optional<ProdSymbol> bProd = grammar.getSpannedScope().resolveProd("B");
    Assertions.assertTrue(bProd.isPresent());
    List<RuleComponentSymbol> bBRules = bProd.get().getSpannedScope().resolveRuleComponentMany("b");
    Assertions.assertTrue(!bBRules.isEmpty());
    Assertions.assertTrue(bBRules.get(0).isIsList());

    // no usage name
    Optional<ProdSymbol> cProd = grammar.getSpannedScope().resolveProd("C");
    Assertions.assertTrue(cProd.isPresent());
    List<RuleComponentSymbol> cBRules= cProd.get().getSpannedScope().resolveRuleComponentMany("b");
    Assertions.assertFalse(cBRules.isEmpty());
    Assertions.assertFalse(cBRules.get(0).isIsList());

    // no usage name
    Optional<ProdSymbol> dProd = grammar.getSpannedScope().resolveProd("D");
    Assertions.assertTrue(dProd.isPresent());
    List<RuleComponentSymbol> dBRules= dProd.get().getSpannedScope().resolveRuleComponentMany("b");
    Assertions.assertFalse(dBRules.isEmpty());
    Assertions.assertFalse(dBRules.get(0).isIsList());

    // with usage name
    Optional<ProdSymbol> eProd = grammar.getSpannedScope().resolveProd("E");
    Assertions.assertTrue(eProd.isPresent());
    List<RuleComponentSymbol> eBRules = eProd.get().getSpannedScope().resolveRuleComponentMany("b");
    Assertions.assertTrue(!eBRules.isEmpty());
    Assertions.assertTrue(eBRules.get(0).isIsList());

    // no usage name
    Optional<ProdSymbol> fProd = grammar.getSpannedScope().resolveProd("F");
    Assertions.assertTrue(fProd.isPresent());
    List<RuleComponentSymbol> fBRules = fProd.get().getSpannedScope().resolveRuleComponentMany("b");
    Assertions.assertTrue(fBRules.isEmpty());

    // with usage name
    Optional<ProdSymbol> gProd = grammar.getSpannedScope().resolveProd("G");
    Assertions.assertTrue(gProd.isPresent());
    List<RuleComponentSymbol> gBRules = gProd.get().getSpannedScope().resolveRuleComponentMany("b");
    Assertions.assertFalse(gBRules.isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
