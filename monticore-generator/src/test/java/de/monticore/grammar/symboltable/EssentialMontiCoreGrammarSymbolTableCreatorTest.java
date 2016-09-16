/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.grammar.symboltable;

import de.monticore.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTExternalProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class EssentialMontiCoreGrammarSymbolTableCreatorTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSymbolTableOfGrammarStatechartDSL() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.createUsingEssentialMCLanguage();

    final Optional<EssentialMCGrammarSymbol> grammar =
        globalScope.resolve("de.monticore.statechart.Statechart", EssentialMCGrammarSymbol.KIND);

    assertTrue(grammar.isPresent());
    assertTrue(grammar.get().getAstNode().isPresent());
    testGrammarSymbolOfStatechart(grammar.get());
    
  }
  
  private void testGrammarSymbolOfStatechart(EssentialMCGrammarSymbol grammar) {
    assertNotNull(grammar);
    assertEquals("de.monticore.statechart.Statechart", grammar.getFullName());
    assertEquals("de.monticore.statechart", grammar.getPackageName());
    assertEquals("de.monticore.statechart.Statechart", grammar.getFullName());
    assertTrue(grammar.getKind().isSame(EssentialMCGrammarSymbol.KIND));
    assertTrue(grammar.getStartRule().isPresent());

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
    Optional<MCProdSymbol> resolvedStateChartProd = grammar.getSpannedScope().resolve("Statechart", MCProdSymbol.KIND);
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
    Optional<MCProdComponentSymbol> resolvedProdComp = entryActionProd.getSpannedScope().resolve("entry", MCProdComponentSymbol.KIND);
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
    assertEquals("de.monticore.statechart.Statechart.AbstractAnything", abstractAnything.getFullName());
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
    final MCProdSymbolReference superInterfaceScStructure = stateProd.getSuperInterfaceProds().get(0);
    assertTrue(superInterfaceScStructure.existsReferencedSymbol());
    assertSame(scStructure, superInterfaceScStructure.getReferencedSymbol());
    // TODO PN generic resolving in super prod
    // AST
    testLinkBetweenSymbolAndAst(stateProd);


    MCProdComponentSymbol initialComponent = stateProd.getProdComponent("initial").orElse(null);
    assertNotNull(initialComponent);
    assertEquals("de.monticore.statechart.Statechart.State.initial", initialComponent.getFullName());
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
    assertSame(prodSymbol.getEnclosingScope(), prodSymbol.getAstNode().get().getEnclosingScope().get());

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
    assertSame(prodCompSymbol.getEnclosingScope(), prodCompSymbol.getAstNode().get().getEnclosingScope().get());
  }
//
//
//
//  @Test
//  public void testGrammarTypeReferences() {
//    final GlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
//
//    // test grammar symbol
//    MCGrammarSymbol grammar = (MCGrammarSymbol) globalScope.resolve("de.monticore.TypeReferences",
//        MCGrammarSymbol.KIND).orElse(null);
//    assertNotNull(grammar);
//
//    assertEquals(5, grammar.getRules().size());
//
//    MCInterfaceOrAbstractRuleSymbol c = (MCInterfaceOrAbstractRuleSymbol) grammar.getRule("C");
//    assertNotNull(c);
//    assertEquals("C", c.getName());
//    assertEquals(0, c.getRuleComponents().size());
//    MCTypeSymbol cType = c.getDefinedType();
//    assertEquals("C", cType.getName());
//
//    MCClassRuleSymbol q = (MCClassRuleSymbol) grammar.getRule("Q");
//    assertNotNull(q);
//    assertEquals("Q", q.getName());
//    // TODO PN definedType weiter testen. Auch resolven usw.
//    MCTypeSymbol qType = q.getDefinedType();
//    assertNotNull(qType);
//    assertEquals("Q", qType.getName());
//    assertEquals(1, qType.getSuperInterfaces().size());
//    MCTypeSymbol qTypeSuperInterface = qType.getSuperInterfaces().get(0);
//    assertEquals(cType.getName(), qTypeSuperInterface.getName());
//
//    assertNotSame(cType, qTypeSuperInterface);
//    // TODO PN, GV
//  /*  assertTrue(qTypeSuperInterface instanceof MCTypeSymbolReference);
//    MCTypeSymbolReference qTypeSuperInterfaceProxy = (MCTypeSymbolReference) qTypeSuperInterface;
//    assertSame(cType, qTypeSuperInterfaceProxy.getReferencedSymbol());
//
//    assertEquals(1, qTypeSuperInterface.getSuperInterfaces().size());
//
//    MCClassRuleSymbol p = (MCClassRuleSymbol) grammar.getRule("P");
//    assertNotNull(p);*/
//  }
//
//  @Test
//  public void testSuperGrammar() {
//    GlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
//
//    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol> resolve("de.monticore.statechart.sub.SubStatechart",
//        MCGrammarSymbol.KIND).orElse(null);
//    assertNotNull(grammar);
//    assertEquals("de.monticore.statechart.sub.SubStatechart", grammar.getFullName());
//    assertTrue(grammar.getStartRule().isPresent());
//
//    assertEquals(1, grammar.getSuperGrammars().size());
//    MCGrammarSymbol superGrammar = grammar.getSuperGrammars().get(0);
//    testGrammarSymbolOfStatechart(superGrammar);
//
//    MCRuleSymbol firstRule = grammar.getRule("First");
//    assertNotNull(firstRule);
//    assertTrue(firstRule.isStartRule());
//    assertSame(grammar.getStartRule().get(), firstRule);
//
//    MCRuleSymbol secondRule = grammar.getRule("Second");
//    assertNotNull(secondRule);
//    assertFalse(secondRule.isStartRule());
//
//    assertEquals(2, grammar.getRuleNames().size());
//    assertEquals(19, grammar.getRulesWithInherited().size());
//
//    assertNotNull(grammar.getRuleWithInherited("EntryAction"));
//
//  }
//
//  @Test
//  public void testMontiCoreGrammar() {
//    GlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
//
//    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol> resolve("mc.grammars.TestGrammar",
//        MCGrammarSymbol.KIND).orElse(null);
//    assertNotNull(grammar);
//    assertEquals("mc.grammars.TestGrammar", grammar.getFullName());
//
//    assertEquals(3, countExternalRules(grammar));
//    assertEquals(5, countInterfaceAndAbstractRules(grammar));
//
//    assertEquals(1, grammar.getSuperGrammars().size());
//    MCGrammarSymbol superGrammar = grammar.getSuperGrammars().get(0);
//    assertTrue(superGrammar instanceof MCGrammarSymbolReference);
//    assertEquals("mc.grammars.literals.TestLiterals", superGrammar.getFullName());
//
//    MCRuleSymbol rule = grammar.getRuleWithInherited("StringLiteral");
//    assertNotNull(rule);
//    assertEquals(superGrammar.getFullName(), rule.getGrammarSymbol().getFullName());
//  }
//
//  @Test
//  public void testNonTerminalsWithSameName() {
//    GlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
//
//    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol>resolve("de.monticore"
//        + ".NonTerminalsWithSameName", MCGrammarSymbol.KIND).orElse(null);
//    assertNotNull(grammar);
//    assertEquals("de.monticore.NonTerminalsWithSameName", grammar.getFullName());
//
//    assertEquals(2, grammar.getRules().size());
//    MCRuleSymbol transition = grammar.getRule("Transition");
//    assertNotNull(transition);
//
//    try {
//      transition.getSpannedScope().resolve("Arguments", MCRuleComponentSymbol.KIND);
//    }
//    catch(ResolvedSeveralEntriesException e) {
//      fail("Only one rule component should be resolved instead of " + e.getSymbols().size());
//    }
//  }
//
//  private int countExternalRules(MCGrammarSymbol grammar) {
//    int num = 0;
//    for (MCRuleSymbol rule : grammar.getRules()) {
//      if (rule instanceof MCExternalRuleSymbol) {
//        num++;
//      }
//    }
//    return num;
//  }
//
//  private int countInterfaceAndAbstractRules(MCGrammarSymbol grammar) {
//    int num = 0;
//    for (MCRuleSymbol rule : grammar.getRules()) {
//      if (rule instanceof MCInterfaceOrAbstractRuleSymbol) {
//        num++;
//      }
//    }
//    return num;
//  }
//
//  @Test
//  public void testSymbolTableOfAutomaton() {
//    final Scope globalScope = GrammarGlobalScopeTestFactory.create();
//
//    // test grammar symbol
//    MCGrammarSymbol grammar = (MCGrammarSymbol) globalScope.resolve("Automaton",
//        MCGrammarSymbol.KIND).orElse(null);
//    assertNotNull(grammar);
//    assertNotNull(grammar.getASTGrammar());
//    grammar.getSpannedScope().resolve("State", MCTypeSymbol.KIND);
//  }
//
//  @Test
//  public void testMCTypeSymbol() {
//    final Scope globalScope = GrammarGlobalScopeTestFactory.create();
//
//
//    // test grammar symbol
//    final MCGrammarSymbol grammar =
//        (MCGrammarSymbol) globalScope.resolve("de.monticore.TypeSymbols", MCGrammarSymbol.KIND).orElse(null);
//    assertNotNull(grammar);
//    assertNotNull(grammar.getASTGrammar());
//
//
//    final MCTypeSymbol cType = grammar.getType("C");
//    assertNotNull(cType);
//
//    final MCTypeSymbol dType = grammar.getType("D");
//    assertNotNull(cType);
//
//    final MCTypeSymbol fType = grammar.getType("F");
//    assertNotNull(cType);
//
//
//
//    final MCRuleSymbol aRule = grammar.getRule("A");
//    assertNotNull(aRule);
//
//    final MCTypeSymbol aType = grammar.getType("A");
//    assertNotNull(aType);
//
//    assertSame(aType, aRule.getType());
//    assertSame(aType, aRule.getDefinedType());
//
//    assertEquals(3, aType.getAttributes().size());
//
//
//    // Terminal "t" in production A does not have an explicit name, e.g., t:"t". Hence, it is not
//    // stored as attribute in the A type
//    assertNull(aType.getAttribute("t"));
//
//    // The non-terminal C in production A, is stored as attribute named c in the A type
//    final MCAttributeSymbol cAttr = aType.getAttribute("c");
//    assertNotNull(cAttr);
//    assertEquals(1, cAttr.getMin());
//    assertEquals(1, cAttr.getMax());
//    assertSame(cType, cAttr.getType());
//
//
//    // The non-terminal D with the usage name r (i.e., r:D) in production A, is stored as attribute
//    // named r in the A type
//    final MCAttributeSymbol rAttr = aType.getAttribute("r");
//    assertNotNull(rAttr);
//    assertEquals(0, rAttr.getMin());
//    assertEquals(MCAttributeSymbol.STAR, rAttr.getMax());
//    assertSame(dType, rAttr.getType());
//
//    // The non-terminal F in production A, is stored as attribute named fs (because of +) in the A type
//    final MCAttributeSymbol fAttr = aType.getAttribute("fs");
//    assertNotNull(fAttr);
//    // TODO PN fAttr.getMin() should be 1 (not zero), since + is used in the grammar
////    assertEquals(1, fAttr.getMin());
//    assertEquals(MCAttributeSymbol.STAR, fAttr.getMax());
//    assertSame(fType, fAttr.getType());
//
//    // Variable names are not stored in the type symbol. E.g, ret=G, where ret is the
//    // variable  name.
//    assertNull(aType.getAttribute("ret"));
//
//  }
//
//  @Test
//  public void testRuleWithSymbolReference() {
//    GlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
//
//    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol>resolve("de.monticore"
//        + ".RuleWithSymbolReference", MCGrammarSymbol.KIND).orElse(null);
//    assertNotNull(grammar);
//    assertEquals("de.monticore.RuleWithSymbolReference", grammar.getFullName());
//
//    assertEquals(7, grammar.getRules().size());
//
//    MCRuleSymbol s = grammar.getRule("S");
//    assertTrue(s.isSymbolDefinition());
//    assertEquals("S", s.getSymbolDefinitionKind().get());
//
//    MCRuleSymbol t = grammar.getRule("T");
//    assertTrue(t.isSymbolDefinition());
//    assertEquals("S", t.getSymbolDefinitionKind().get());
//
//    // The symbol kinds are determined transitively, i.e., A -> T -> S, hence, the symbol kind of rule A is S.
//    MCRuleSymbol a = grammar.getRule("A");
//    assertTrue(a.isSymbolDefinition());
//    assertEquals("S", a.getSymbolDefinitionKind().get());
//
//    MCRuleSymbol b = grammar.getRule("B");
//    assertFalse(b.isSymbolDefinition());
//    MCRuleComponentSymbol aComponent = b.getRuleComponent("an").get();
//    assertEquals("Name", aComponent.getReferencedRuleName());
//    assertEquals(a.getName(), aComponent.getReferencedSymbolName().get());
//
//    MCRuleSymbol e = grammar.getRule("E");
//    // is external
//    assertTrue(e.getKindSymbolRule().equals(KindSymbolRule.HOLERULE));
//    assertTrue(e.isSymbolDefinition());
//
//    MCRuleSymbol r = grammar.getRule("R");
//    assertTrue(r.getKindSymbolRule().equals(KindSymbolRule.INTERFACEORABSTRACTRULE));
//    assertTrue(!((MCInterfaceOrAbstractRuleSymbol)r).isInterface());
//    assertTrue(r.isSymbolDefinition());
//  }

}
