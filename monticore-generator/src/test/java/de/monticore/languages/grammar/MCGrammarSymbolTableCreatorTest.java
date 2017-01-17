/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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

package de.monticore.languages.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.monticore.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTExternalProd;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.languages.grammar.MCRuleSymbol.KindSymbolRule;
import de.monticore.languages.grammar.symbolreferences.MCGrammarSymbolReference;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO: Write me!
 *
 * @author Pedram Mir Seyed Nazari
 * @version $Revision$, $Date$
 */
public class MCGrammarSymbolTableCreatorTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSymbolTableOfGrammarStatechartDSL() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.create();


    // test grammar symbol
    final MCGrammarSymbol grammar = (MCGrammarSymbol) globalScope.resolve("de.monticore.statechart.Statechart",
        MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertNotNull(grammar.getASTGrammar());
    testGrammarSymbolOfStatechart(grammar);
    
  }
  
  private void testGrammarSymbolOfStatechart(MCGrammarSymbol grammar) {
    assertNotNull(grammar);
    assertEquals("de.monticore.statechart.Statechart", grammar.getFullName());
    assertEquals("de.monticore.statechart", grammar.getPackageName());
    assertEquals("de.monticore.statechart.Statechart", grammar.getFullName());
    assertTrue(grammar.getKind().isSame(MCGrammarSymbol.KIND));
    assertTrue(grammar.getStartRule().isPresent());
    
    assertTrue(grammar.isComponent());
    assertEquals(1, grammar.getSuperGrammars().size());
    
    assertEquals(12, grammar.getRules().size());
    // TODO PN test weitere Methoden von grammar

    // AST
    assertTrue(grammar.getAstNode().isPresent());
    assertTrue(grammar.getAstNode().get() instanceof ASTMCGrammar);
    if (!(grammar instanceof MCGrammarSymbolReference)) {
      assertSame(grammar, grammar.getAstNode().get().getSymbol().get());
    }
    assertSame(grammar.getEnclosingScope(), grammar.getAstNode().get().getEnclosingScope().get());
    
    // TODO PN weiter testen
    final MCClassRuleSymbol stateChartRule = (MCClassRuleSymbol) grammar.getRule("Statechart");
    assertNotNull(stateChartRule);
    assertTrue(stateChartRule.getKind().isSame(MCRuleSymbol.KIND));
    assertTrue(stateChartRule.isStartRule());
    // AST
    testLinkBetweenSymbolAndAst(stateChartRule);
    
    final MCClassRuleSymbol entryActionRule = (MCClassRuleSymbol) grammar.getRule("EntryAction");
    assertNotNull(entryActionRule);
    assertEquals("EntryAction", entryActionRule.getName());
    // TODO PN definedType weiter testen. Auch resolven usw.
    assertNotNull(entryActionRule.getDefinedType());
    assertEquals("EntryAction", entryActionRule.getDefinedType().getName());
    testLinkBetweenSymbolAndAst(entryActionRule);
    // TODO PN test entryActionRule.getFollow()
    assertTrue(entryActionRule.getRuleNode().isPresent());
    assertTrue(entryActionRule.getAstNode().isPresent());
    assertSame(entryActionRule.getAstNode().get(), entryActionRule.getRuleNode().get());
    assertEquals(KindSymbolRule.PARSERRULE, entryActionRule.getKindSymbolRule());
    assertSame(entryActionRule.getDefinedType(), entryActionRule.getType());
    
    if (grammar instanceof MCGrammarSymbolReference) {
      assertSame(((MCGrammarSymbolReference) grammar).getReferencedSymbol(),
          entryActionRule.getGrammarSymbol());
    }
    else {
      assertSame(grammar, entryActionRule.getGrammarSymbol());
    }
    assertEquals(3, entryActionRule.getRuleComponents().size());
    // test rule components
    MCRuleComponentSymbol componentSymbol =
        (MCRuleComponentSymbol) entryActionRule.getSpannedScope().resolve("entry",
            MCRuleComponentSymbol.KIND).get();
    assertEquals("entry", componentSymbol.getName());
    assertEquals("", componentSymbol.getUsageName());
    assertSame(entryActionRule, componentSymbol.getEnclosingRule());
    assertSame(entryActionRule.getSpannedScope(), componentSymbol.getEnclosingScope());
    if (grammar instanceof MCGrammarSymbolReference) {
      assertSame(((MCGrammarSymbolReference) grammar).getReferencedSymbol(),
          componentSymbol.getGrammarSymbol());
    }
    else {
      assertSame(grammar, componentSymbol.getGrammarSymbol());
    }
    assertTrue(componentSymbol.getKind().isSame(MCRuleComponentSymbol.KIND));
    assertEquals(MCRuleComponentSymbol.KindRuleComponent.TERMINAL,
        componentSymbol.getKindOfRuleComponent());
    assertFalse(componentSymbol.isList());
    assertFalse(componentSymbol.isOptional());
    // AST
    testLinkBetweenSymbolAndAst(componentSymbol);
    
    componentSymbol = (MCRuleComponentSymbol) entryActionRule.getSpannedScope()
        .resolve(":", MCRuleComponentSymbol.KIND).get();
    assertEquals(":", componentSymbol.getName());
    assertEquals("", componentSymbol.getUsageName());
    assertSame(entryActionRule, componentSymbol.getEnclosingRule());
    assertSame(entryActionRule.getSpannedScope(), componentSymbol.getEnclosingScope());
    
    if (grammar instanceof MCGrammarSymbolReference) {
      assertSame(((MCGrammarSymbolReference) grammar).getReferencedSymbol(),
          componentSymbol.getGrammarSymbol());
    }
    else {
      assertSame(grammar, componentSymbol.getGrammarSymbol());
    }
    assertTrue(componentSymbol.getKind().isSame(MCRuleComponentSymbol.KIND));
    assertEquals(MCRuleComponentSymbol.KindRuleComponent.TERMINAL,
        componentSymbol.getKindOfRuleComponent());
    assertTrue(componentSymbol.getAstNode().isPresent());
    assertFalse(componentSymbol.isList());
    assertFalse(componentSymbol.isOptional());
    
    componentSymbol = (MCRuleComponentSymbol) entryActionRule.getSpannedScope()
        .resolve("block", MCRuleComponentSymbol.KIND).get();
    assertEquals("block", componentSymbol.getName());
    assertEquals("block", componentSymbol.getUsageName());
    assertEquals("BlockStatement", componentSymbol.getReferencedRuleName());
    assertSame(entryActionRule, componentSymbol.getEnclosingRule());
    assertSame(entryActionRule.getSpannedScope(), componentSymbol.getEnclosingScope());
    if (grammar instanceof MCGrammarSymbolReference) {
      assertSame(((MCGrammarSymbolReference) grammar).getReferencedSymbol(),
          componentSymbol.getGrammarSymbol());
    }
    else {
      assertSame(grammar, componentSymbol.getGrammarSymbol());
    }
    assertTrue(componentSymbol.getKind().isSame(MCRuleComponentSymbol.KIND));
    assertEquals(MCRuleComponentSymbol.KindRuleComponent.NONTERMINAL,
        componentSymbol.getKindOfRuleComponent());
    assertTrue(componentSymbol.getAstNode().isPresent());
    assertFalse(componentSymbol.isList());
    assertFalse(componentSymbol.isOptional());
    
    MCInterfaceOrAbstractRuleSymbol scStructure = (MCInterfaceOrAbstractRuleSymbol) grammar
        .getRule("SCStructure");
    assertNotNull(scStructure);
    assertEquals("SCStructure", scStructure.getName());
    assertTrue(scStructure.isInterface());
    assertEquals(0, scStructure.getRuleComponents().size());
    MCTypeSymbol scStructureType = scStructure.getDefinedType();
    assertEquals("SCStructure", scStructureType.getName());
    testLinkBetweenSymbolAndAst(scStructure);

    MCInterfaceOrAbstractRuleSymbol abstractAnything = (MCInterfaceOrAbstractRuleSymbol) grammar
        .getRule("AbstractAnything");
    assertNotNull(abstractAnything);
    assertEquals("AbstractAnything", abstractAnything.getName());
    assertFalse(abstractAnything.isInterface());
    assertFalse(abstractAnything.isSymbolDefinition());
    assertEquals(0, abstractAnything.getRuleComponents().size());
    MCTypeSymbol abstractAnythingType = abstractAnything.getDefinedType();
    assertEquals("AbstractAnything", abstractAnythingType.getName());
    testLinkBetweenSymbolAndAst(abstractAnything);
    
    MCClassRuleSymbol stateRule = (MCClassRuleSymbol) grammar.getRule("State");
    assertNotNull(stateRule);
    assertEquals("State", stateRule.getName());
    // TODO PN definedType weiter testen. Auch resolven usw.
    MCTypeSymbol stateType = stateRule.getDefinedType();
    assertNotNull(stateType);
    assertEquals("State", stateType.getName());
    assertEquals(1, stateType.getSuperInterfaces().size());
    assertEquals(scStructureType.getName(), stateType.getSuperInterfaces().get(0)
        .getName());
    testLinkBetweenSymbolAndAst(stateRule);

    MCRuleComponentSymbol initialComponent = stateRule.getRuleComponent("initial").orElse(null);
    assertNotNull(initialComponent);
    assertEquals("initial", initialComponent.getName());
    // TODO PN set in Grammar: init:["initial"] and fix this test
    //assertEquals("init", initialComponent.getUsageName());

    MCExternalRuleSymbol classBody = (MCExternalRuleSymbol) grammar.getRule("Classbody");
    assertNotNull(classBody);
    assertEquals("Classbody", classBody.getName());
    assertEquals(0, classBody.getRuleComponents().size());
    MCTypeSymbol classBodyType = classBody.getDefinedType();
    assertTrue(classBodyType.isExternal());
    assertFalse(classBody.isSymbolDefinition());
    testLinkBetweenSymbolAndAst(classBody);
    
    MCClassRuleSymbol codeRule = (MCClassRuleSymbol) grammar.getRule("Code");
    assertNotNull(codeRule);
    assertEquals("Code", codeRule.getName());
    // TODO PN definedType weiter testen. Auch resolven usw.
    MCTypeSymbol codeType = codeRule.getDefinedType();
    assertNotNull(codeType);
    assertEquals("Code", codeType.getName());
    
    assertEquals(2, codeRule.getRuleComponents().size());
    componentSymbol = (MCRuleComponentSymbol) codeRule.getSpannedScope()
        .resolve("body", MCRuleComponentSymbol.KIND).get();
    assertEquals("body", componentSymbol.getUsageName());
    assertEquals("Classbody", componentSymbol.getReferencedRuleName());
    testLinkBetweenSymbolAndAst(codeRule);
  }

  private void testLinkBetweenSymbolAndAst(MCClassRuleSymbol rule) {
    assertTrue(rule.getAstNode().isPresent());
    assertTrue(rule.getAstNode().get() instanceof ASTClassProd);
    assertSame(rule, rule.getAstNode().get().getSymbol().get());
    assertSame(rule.getEnclosingScope(), rule.getAstNode().get().getEnclosingScope().get());
  }

  private void testLinkBetweenSymbolAndAst(MCInterfaceOrAbstractRuleSymbol rule) {
    assertTrue(rule.getAstNode().isPresent());
    assertSame(rule, rule.getAstNode().get().getSymbol().get());
    assertSame(rule.getEnclosingScope(), rule.getAstNode().get().getEnclosingScope().get());
  }

  private void testLinkBetweenSymbolAndAst(MCExternalRuleSymbol rule) {
    assertTrue(rule.getAstNode().isPresent());
    assertTrue(rule.getAstNode().get() instanceof ASTExternalProd);
    assertSame(rule, rule.getAstNode().get().getSymbol().get());
    assertSame(rule.getEnclosingScope(), rule.getAstNode().get().getEnclosingScope().get());
  }

  private void testLinkBetweenSymbolAndAst(MCLexRuleSymbol rule) {
    assertTrue(rule.getAstNode().isPresent());
    assertTrue(rule.getAstNode().get() instanceof ASTLexProd);
    assertSame(rule, rule.getAstNode().get().getSymbol().get());
    assertSame(rule.getEnclosingScope(), rule.getAstNode().get().getEnclosingScope().get());
  }

  private void testLinkBetweenSymbolAndAst(MCRuleComponentSymbol rule) {
    assertTrue(rule.getAstNode().isPresent());
    assertSame(rule, rule.getAstNode().get().getSymbol().get());
    assertSame(rule.getEnclosingScope(), rule.getAstNode().get().getEnclosingScope().get());
  }



  @Test
  public void testGrammarTypeReferences() {
    final GlobalScope globalScope = GrammarGlobalScopeTestFactory.create();

    // test grammar symbol
    MCGrammarSymbol grammar = (MCGrammarSymbol) globalScope.resolve("de.monticore.TypeReferences",
        MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    
    assertEquals(5, grammar.getRules().size());
    
    MCInterfaceOrAbstractRuleSymbol c = (MCInterfaceOrAbstractRuleSymbol) grammar.getRule("C");
    assertNotNull(c);
    assertEquals("C", c.getName());
    assertEquals(0, c.getRuleComponents().size());
    MCTypeSymbol cType = c.getDefinedType();
    assertEquals("C", cType.getName());
    
    MCClassRuleSymbol q = (MCClassRuleSymbol) grammar.getRule("Q");
    assertNotNull(q);
    assertEquals("Q", q.getName());
    // TODO PN definedType weiter testen. Auch resolven usw.
    MCTypeSymbol qType = q.getDefinedType();
    assertNotNull(qType);
    assertEquals("Q", qType.getName());
    assertEquals(1, qType.getSuperInterfaces().size());
    MCTypeSymbol qTypeSuperInterface = qType.getSuperInterfaces().get(0);
    assertEquals(cType.getName(), qTypeSuperInterface.getName());
    
    assertNotSame(cType, qTypeSuperInterface);
    // TODO PN, GV
  /*  assertTrue(qTypeSuperInterface instanceof MCTypeSymbolReference);
    MCTypeSymbolReference qTypeSuperInterfaceProxy = (MCTypeSymbolReference) qTypeSuperInterface;
    assertSame(cType, qTypeSuperInterfaceProxy.getReferencedSymbol());
    
    assertEquals(1, qTypeSuperInterface.getSuperInterfaces().size());
    
    MCClassRuleSymbol p = (MCClassRuleSymbol) grammar.getRule("P");
    assertNotNull(p);*/
  }
  
  @Test
  public void testSuperGrammar() {
    GlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol> resolve("de.monticore.statechart.sub.SubStatechart",
        MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.statechart.sub.SubStatechart", grammar.getFullName());
    assertTrue(grammar.getStartRule().isPresent());
    
    assertEquals(1, grammar.getSuperGrammars().size());
    MCGrammarSymbol superGrammar = grammar.getSuperGrammars().get(0);
    testGrammarSymbolOfStatechart(superGrammar);

    MCRuleSymbol firstRule = grammar.getRule("First");
    assertNotNull(firstRule);
    assertTrue(firstRule.isStartRule());
    assertSame(grammar.getStartRule().get(), firstRule);

    MCRuleSymbol secondRule = grammar.getRule("Second");
    assertNotNull(secondRule);
    assertFalse(secondRule.isStartRule());

    assertEquals(2, grammar.getRuleNames().size());
    assertEquals(19, grammar.getRulesWithInherited().size());
    
    assertNotNull(grammar.getRuleWithInherited("EntryAction"));
    
  }
  
  @Test
  public void testMontiCoreGrammar() {
    GlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol> resolve("mc.grammars.TestGrammar",
        MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertEquals("mc.grammars.TestGrammar", grammar.getFullName());
    
    assertEquals(3, countExternalRules(grammar));
    assertEquals(5, countInterfaceAndAbstractRules(grammar));

    assertEquals(1, grammar.getSuperGrammars().size());
    MCGrammarSymbol superGrammar = grammar.getSuperGrammars().get(0);
    assertTrue(superGrammar instanceof MCGrammarSymbolReference);
    assertEquals("mc.grammars.literals.TestLiterals", superGrammar.getFullName());
    
    MCRuleSymbol rule = grammar.getRuleWithInherited("StringLiteral");
    assertNotNull(rule);
    assertEquals(superGrammar.getFullName(), rule.getGrammarSymbol().getFullName());
  }

  @Test
  public void testNonTerminalsWithSameName() {
    GlobalScope globalScope = GrammarGlobalScopeTestFactory.create();

    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol>resolve("de.monticore"
        + ".NonTerminalsWithSameName", MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.NonTerminalsWithSameName", grammar.getFullName());

    assertEquals(2, grammar.getRules().size());
    MCRuleSymbol transition = grammar.getRule("Transition");
    assertNotNull(transition);

    try {
      transition.getSpannedScope().resolve("Arguments", MCRuleComponentSymbol.KIND);
    }
    catch(ResolvedSeveralEntriesException e) {
      fail("Only one rule component should be resolved instead of " + e.getSymbols().size());
    }
  }

  private int countExternalRules(MCGrammarSymbol grammar) {
    int num = 0;
    for (MCRuleSymbol rule : grammar.getRules()) {
      if (rule instanceof MCExternalRuleSymbol) {
        num++;
      }
    }
    return num;
  }
  
  private int countInterfaceAndAbstractRules(MCGrammarSymbol grammar) {
    int num = 0;
    for (MCRuleSymbol rule : grammar.getRules()) {
      if (rule instanceof MCInterfaceOrAbstractRuleSymbol) {
        num++;
      }
    }
    return num;
  }

  @Test
  public void testSymbolTableOfAutomaton() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.create();

    // test grammar symbol
    MCGrammarSymbol grammar = (MCGrammarSymbol) globalScope.resolve("Automaton",
        MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertNotNull(grammar.getASTGrammar());
    grammar.getSpannedScope().resolve("State", MCTypeSymbol.KIND);
  }

  @Test
  public void testMCTypeSymbol() {
    final Scope globalScope = GrammarGlobalScopeTestFactory.create();


    // test grammar symbol
    final MCGrammarSymbol grammar =
        (MCGrammarSymbol) globalScope.resolve("de.monticore.TypeSymbols", MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertNotNull(grammar.getASTGrammar());


    final MCTypeSymbol cType = grammar.getType("C");
    assertNotNull(cType);

    final MCTypeSymbol dType = grammar.getType("D");
    assertNotNull(cType);

    final MCTypeSymbol fType = grammar.getType("F");
    assertNotNull(cType);



    final MCRuleSymbol aRule = grammar.getRule("A");
    assertNotNull(aRule);

    final MCTypeSymbol aType = grammar.getType("A");
    assertNotNull(aType);

    assertSame(aType, aRule.getType());
    assertSame(aType, aRule.getDefinedType());

    assertEquals(3, aType.getAttributes().size());


    // Terminal "t" in production A does not have an explicit name, e.g., t:"t". Hence, it is not
    // stored as attribute in the A type
    assertNull(aType.getAttribute("t"));

    // The non-terminal C in production A, is stored as attribute named c in the A type
    final MCAttributeSymbol cAttr = aType.getAttribute("c");
    assertNotNull(cAttr);
    assertEquals(1, cAttr.getMin());
    assertEquals(1, cAttr.getMax());
    assertSame(cType, cAttr.getType());


    // The non-terminal D with the usage name r (i.e., r:D) in production A, is stored as attribute
    // named r in the A type
    final MCAttributeSymbol rAttr = aType.getAttribute("r");
    assertNotNull(rAttr);
    assertEquals(0, rAttr.getMin());
    assertEquals(MCAttributeSymbol.STAR, rAttr.getMax());
    assertSame(dType, rAttr.getType());

    // The non-terminal F in production A, is stored as attribute named fs (because of +) in the A type
    final MCAttributeSymbol fAttr = aType.getAttribute("fs");
    assertNotNull(fAttr);
    // TODO PN fAttr.getMin() should be 1 (not zero), since + is used in the grammar
//    assertEquals(1, fAttr.getMin());
    assertEquals(MCAttributeSymbol.STAR, fAttr.getMax());
    assertSame(fType, fAttr.getType());

    // Variable names are not stored in the type symbol. E.g, ret=G, where ret is the
    // variable  name.
    assertNull(aType.getAttribute("ret"));

  }

  @Test
  public void testRuleWithSymbolReference() {
    GlobalScope globalScope = GrammarGlobalScopeTestFactory.create();

    MCGrammarSymbol grammar = globalScope.<MCGrammarSymbol>resolve("de.monticore"
        + ".RuleWithSymbolReference", MCGrammarSymbol.KIND).orElse(null);
    assertNotNull(grammar);
    assertEquals("de.monticore.RuleWithSymbolReference", grammar.getFullName());

    assertEquals(7, grammar.getRules().size());

    MCRuleSymbol s = grammar.getRule("S");
    assertTrue(s.isSymbolDefinition());
    assertEquals("S", s.getSymbolDefinitionKind().get());

    MCRuleSymbol t = grammar.getRule("T");
    assertTrue(t.isSymbolDefinition());
    assertEquals("S", t.getSymbolDefinitionKind().get());

    // The symbol kinds are determined transitively, i.e., A -> T -> S, hence, the symbol kind of rule A is S.
    MCRuleSymbol a = grammar.getRule("A");
    assertTrue(a.isSymbolDefinition());
    assertEquals("S", a.getSymbolDefinitionKind().get());

    MCRuleSymbol b = grammar.getRule("B");
    assertFalse(b.isSymbolDefinition());
    MCRuleComponentSymbol aComponent = b.getRuleComponent("an").get();
    assertEquals("Name", aComponent.getReferencedRuleName());
    assertEquals(a.getName(), aComponent.getReferencedSymbolName().get());

    MCRuleSymbol e = grammar.getRule("E");
    // is external
    assertTrue(e.getKindSymbolRule().equals(KindSymbolRule.HOLERULE));
    assertTrue(e.isSymbolDefinition());

    MCRuleSymbol r = grammar.getRule("R");
    assertTrue(r.getKindSymbolRule().equals(KindSymbolRule.INTERFACEORABSTRACTRULE));
    assertTrue(!((MCInterfaceOrAbstractRuleSymbol)r).isInterface());
    assertTrue(r.isSymbolDefinition());
  }

}
