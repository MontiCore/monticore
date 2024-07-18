/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.symboltable;

import de.monticore.grammar.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.IGrammarScope;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IGrammarScopeTest {

  @BeforeEach
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
    Assertions.assertTrue(grammar.isPresent());
    Assertions.assertTrue(grammar.get().isPresentAstNode());
    ASTMCGrammar ast = grammar.get().getAstNode();

    IGrammarScope innerScope = ast.getClassProd(0).getEnclosingScope();

    Assertions.assertTrue(innerScope.resolveProd("NewProd").isPresent());

    Assertions.assertTrue(innerScope.resolveProd("Automaton").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("Transition").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("State").isPresent());

    Assertions.assertTrue(innerScope.resolveProd("X").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("Y").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("J").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("G").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("K").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("N").isPresent());

    Assertions.assertFalse(innerScope.resolveProd("Supergrammar").isPresent());
    Assertions.assertFalse(innerScope.resolveProd("de.monticore.inherited.Supergrammar").isPresent());
    Assertions.assertFalse(innerScope.resolveProd("CombiningGrammar").isPresent());

    Assertions.assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.Supergrammar").isPresent());
    Assertions.assertTrue(innerScope.resolveMCGrammar("CombiningGrammar").isPresent());
    Assertions.assertTrue(innerScope.resolveMCGrammar("Automaton").isPresent());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCombiningGrammarResolveInSuperGrammars() throws IOException {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    final Optional<MCGrammarSymbol> grammar = globalScope
            .resolveMCGrammar("de.monticore.CombiningGrammar");
    Assertions.assertTrue(grammar.isPresent());
    Assertions.assertTrue(grammar.get().isPresentAstNode());
    ASTMCGrammar ast = grammar.get().getAstNode();

    IGrammarScope innerScope = ast.getClassProd(0).getEnclosingScope();

    Assertions.assertTrue(innerScope.resolveInSuperGrammars("Automaton", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("Transition", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("State", AccessModifier.ALL_INCLUSION).isPresent());

    Assertions.assertTrue(innerScope.resolveInSuperGrammars("X", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("Y", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("J", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("G", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("K", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("N", AccessModifier.ALL_INCLUSION).isPresent());

    Assertions.assertFalse(innerScope.resolveInSuperGrammars("Supergrammar", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertFalse(innerScope.resolveInSuperGrammars("de.monticore.inherited.Supergrammar", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertFalse(innerScope.resolveInSuperGrammars("CombiningGrammar", AccessModifier.ALL_INCLUSION).isPresent());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubsubgrammarSymbolTable() throws IOException {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    final Optional<MCGrammarSymbol> grammar = globalScope
            .resolveMCGrammar("de.monticore.inherited.Subsubgrammar");
    Assertions.assertTrue(grammar.isPresent());
    Assertions.assertTrue(grammar.get().isPresentAstNode());
    ASTMCGrammar ast = grammar.get().getAstNode();

    IGrammarScope innerScope = ast.getClassProd(0).getEnclosingScope();

    Assertions.assertTrue(innerScope.resolveProd("N").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("S").isPresent());

    Assertions.assertTrue(innerScope.resolveProd("A").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("B").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("M").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("D").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("L").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("O").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("M").isPresent());

    Assertions.assertTrue(innerScope.resolveProd("X").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("Y").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("J").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("G").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("K").isPresent());
    Assertions.assertTrue(innerScope.resolveProd("N").isPresent());

    Assertions.assertFalse(innerScope.resolveProd("Supergrammar").isPresent());
    Assertions.assertFalse(innerScope.resolveProd("de.monticore.inherited.Supergrammar").isPresent());
    Assertions.assertFalse(innerScope.resolveProd("Subgrammar").isPresent());
    Assertions.assertFalse(innerScope.resolveProd("de.monticore.inherited.Subgrammar").isPresent());
    Assertions.assertFalse(innerScope.resolveProd("Subsubgrammar").isPresent());
    Assertions.assertFalse(innerScope.resolveProd("de.monticore.inherited.Subsubgrammar").isPresent());

    Assertions.assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.Supergrammar").isPresent());
    Assertions.assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.Subgrammar").isPresent());
    Assertions.assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.Subsubgrammar").isPresent());
    Assertions.assertTrue(innerScope.resolveMCGrammar("Subsubgrammar").isPresent());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubsubgrammarResolveInSuperGrammars() throws IOException {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    final Optional<MCGrammarSymbol> grammar = globalScope
            .resolveMCGrammar("de.monticore.inherited.Subsubgrammar");
    Assertions.assertTrue(grammar.isPresent());
    Assertions.assertTrue(grammar.get().isPresentAstNode());
    ASTMCGrammar ast = grammar.get().getAstNode();
    IGrammarScope innerScope = ast.getClassProd(0).getEnclosingScope();

    Assertions.assertTrue(innerScope.resolveInSuperGrammars("N", AccessModifier.ALL_INCLUSION).isPresent());

    Assertions.assertTrue(innerScope.resolveInSuperGrammars("A", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("B", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("M", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("D", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("L", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("O", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("M", AccessModifier.ALL_INCLUSION).isPresent());

    Assertions.assertTrue(innerScope.resolveInSuperGrammars("X", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("Y", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("J", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("G", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("K", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertTrue(innerScope.resolveInSuperGrammars("N", AccessModifier.ALL_INCLUSION).isPresent());

    Assertions.assertFalse(innerScope.resolveInSuperGrammars("Supergrammar", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertFalse(innerScope.resolveInSuperGrammars("de.monticore.inherited.Supergrammar", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertFalse(innerScope.resolveInSuperGrammars("Subgrammar", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertFalse(innerScope.resolveInSuperGrammars("de.monticore.inherited.Subgrammar", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertFalse(innerScope.resolveInSuperGrammars("Subsubgrammar", AccessModifier.ALL_INCLUSION).isPresent());
    Assertions.assertFalse(innerScope.resolveInSuperGrammars("de.monticore.inherited.Subsubgrammar", AccessModifier.ALL_INCLUSION).isPresent());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
