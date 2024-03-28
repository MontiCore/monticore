/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.symboltable;

import de.monticore.grammar.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.IGrammarScope;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsPhasedSTC;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IGrammarScopeTest {

  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    Grammar_WithConceptsMill.reset();
    Grammar_WithConceptsMill.init();
  }

  @Test
  public void testCombiningGrammarSymbolTable() throws IOException {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    final Optional<MCGrammarSymbol> grammar = globalScope
            .resolveMCGrammar("de.monticore.CombiningGrammar");
    assertTrue(grammar.isPresent());
    assertTrue(grammar.get().isPresentAstNode());
    ASTMCGrammar ast = grammar.get().getAstNode();

    IGrammarScope innerScope = ast.getClassProd(0).getEnclosingScope();

    assertTrue(innerScope.resolveProd("NewProd").isPresent());

    assertTrue(innerScope.resolveProd("Automaton").isPresent());
    assertTrue(innerScope.resolveProd("Transition").isPresent());
    assertTrue(innerScope.resolveProd("State").isPresent());

    assertTrue(innerScope.resolveProd("X").isPresent());
    assertTrue(innerScope.resolveProd("Y").isPresent());
    assertTrue(innerScope.resolveProd("J").isPresent());
    assertTrue(innerScope.resolveProd("G").isPresent());
    assertTrue(innerScope.resolveProd("K").isPresent());
    assertTrue(innerScope.resolveProd("N").isPresent());

    assertFalse(innerScope.resolveProd("Supergrammar").isPresent());
    assertFalse(innerScope.resolveProd("de.monticore.inherited.Supergrammar").isPresent());
    assertFalse(innerScope.resolveProd("CombiningGrammar").isPresent());

    assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.Supergrammar").isPresent());
    assertTrue(innerScope.resolveMCGrammar("CombiningGrammar").isPresent());
    assertTrue(innerScope.resolveMCGrammar("Automaton").isPresent());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCombiningGrammarResolveInSuperGrammars() throws IOException {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    final Optional<MCGrammarSymbol> grammar = globalScope
            .resolveMCGrammar("de.monticore.CombiningGrammar");
    assertTrue(grammar.isPresent());
    assertTrue(grammar.get().isPresentAstNode());
    ASTMCGrammar ast = grammar.get().getAstNode();

    IGrammarScope innerScope = ast.getClassProd(0).getEnclosingScope();

    assertTrue(innerScope.resolveInSuperGrammars("Automaton", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("Transition", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("State", AccessModifier.ALL_INCLUSION).isPresent());

    assertTrue(innerScope.resolveInSuperGrammars("X", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("Y", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("J", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("G", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("K", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("N", AccessModifier.ALL_INCLUSION).isPresent());

    assertFalse(innerScope.resolveInSuperGrammars("Supergrammar", AccessModifier.ALL_INCLUSION).isPresent());
    assertFalse(innerScope.resolveInSuperGrammars("de.monticore.inherited.Supergrammar", AccessModifier.ALL_INCLUSION).isPresent());
    assertFalse(innerScope.resolveInSuperGrammars("CombiningGrammar", AccessModifier.ALL_INCLUSION).isPresent());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubsubgrammarSymbolTable() throws IOException {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    final Optional<MCGrammarSymbol> grammar = globalScope
            .resolveMCGrammar("de.monticore.inherited.Subsubgrammar");
    assertTrue(grammar.isPresent());
    assertTrue(grammar.get().isPresentAstNode());
    ASTMCGrammar ast = grammar.get().getAstNode();

    IGrammarScope innerScope = ast.getClassProd(0).getEnclosingScope();

    assertTrue(innerScope.resolveProd("N").isPresent());
    assertTrue(innerScope.resolveProd("S").isPresent());

    assertTrue(innerScope.resolveProd("A").isPresent());
    assertTrue(innerScope.resolveProd("B").isPresent());
    assertTrue(innerScope.resolveProd("M").isPresent());
    assertTrue(innerScope.resolveProd("D").isPresent());
    assertTrue(innerScope.resolveProd("L").isPresent());
    assertTrue(innerScope.resolveProd("O").isPresent());
    assertTrue(innerScope.resolveProd("M").isPresent());

    assertTrue(innerScope.resolveProd("X").isPresent());
    assertTrue(innerScope.resolveProd("Y").isPresent());
    assertTrue(innerScope.resolveProd("J").isPresent());
    assertTrue(innerScope.resolveProd("G").isPresent());
    assertTrue(innerScope.resolveProd("K").isPresent());
    assertTrue(innerScope.resolveProd("N").isPresent());

    assertFalse(innerScope.resolveProd("Supergrammar").isPresent());
    assertFalse(innerScope.resolveProd("de.monticore.inherited.Supergrammar").isPresent());
    assertFalse(innerScope.resolveProd("Subgrammar").isPresent());
    assertFalse(innerScope.resolveProd("de.monticore.inherited.Subgrammar").isPresent());
    assertFalse(innerScope.resolveProd("Subsubgrammar").isPresent());
    assertFalse(innerScope.resolveProd("de.monticore.inherited.Subsubgrammar").isPresent());

    assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.Supergrammar").isPresent());
    assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.Subgrammar").isPresent());
    assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.Subsubgrammar").isPresent());
    assertTrue(innerScope.resolveMCGrammar("Subsubgrammar").isPresent());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubsubgrammarResolveInSuperGrammars() throws IOException {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    final Optional<MCGrammarSymbol> grammar = globalScope
            .resolveMCGrammar("de.monticore.inherited.Subsubgrammar");
    assertTrue(grammar.isPresent());
    assertTrue(grammar.get().isPresentAstNode());
    ASTMCGrammar ast = grammar.get().getAstNode();
    IGrammarScope innerScope = ast.getClassProd(0).getEnclosingScope();

    assertTrue(innerScope.resolveInSuperGrammars("N", AccessModifier.ALL_INCLUSION).isPresent());

    assertTrue(innerScope.resolveInSuperGrammars("A", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("B", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("M", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("D", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("L", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("O", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("M", AccessModifier.ALL_INCLUSION).isPresent());

    assertTrue(innerScope.resolveInSuperGrammars("X", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("Y", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("J", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("G", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("K", AccessModifier.ALL_INCLUSION).isPresent());
    assertTrue(innerScope.resolveInSuperGrammars("N", AccessModifier.ALL_INCLUSION).isPresent());

    assertFalse(innerScope.resolveInSuperGrammars("Supergrammar", AccessModifier.ALL_INCLUSION).isPresent());
    assertFalse(innerScope.resolveInSuperGrammars("de.monticore.inherited.Supergrammar", AccessModifier.ALL_INCLUSION).isPresent());
    assertFalse(innerScope.resolveInSuperGrammars("Subgrammar", AccessModifier.ALL_INCLUSION).isPresent());
    assertFalse(innerScope.resolveInSuperGrammars("de.monticore.inherited.Subgrammar", AccessModifier.ALL_INCLUSION).isPresent());
    assertFalse(innerScope.resolveInSuperGrammars("Subsubgrammar", AccessModifier.ALL_INCLUSION).isPresent());
    assertFalse(innerScope.resolveInSuperGrammars("de.monticore.inherited.Subsubgrammar", AccessModifier.ALL_INCLUSION).isPresent());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
