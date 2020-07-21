/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.symboltable;

import de.monticore.MontiCoreScript;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.IGrammarScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsLanguage;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsSymbolTableCreatorDelegator;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IGrammarScopeTest {

  private static ModelPath modelPath = new ModelPath(Paths.get("src/test/resources"));

  @Test
  public void testCombiningGrammarSymbolTable() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/CombiningGrammar.mc4").getAbsolutePath()));

    assertTrue(ast.isPresent());
    Grammar_WithConceptsLanguage language = new Grammar_WithConceptsLanguage();
    Grammar_WithConceptsGlobalScope grammar_withConceptsGlobalScope = new Grammar_WithConceptsGlobalScope(modelPath, language);

    Grammar_WithConceptsSymbolTableCreatorDelegator stCreator = language.getSymbolTableCreator(grammar_withConceptsGlobalScope);
    stCreator.createFromAST(ast.get());

    IGrammarScope innerScope = ast.get().getClassProd(0).getEnclosingScope();

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
  }

  @Test
  public void testCombiningGrammarResolveInSuperGrammars() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/CombiningGrammar.mc4").getAbsolutePath()));

    assertTrue(ast.isPresent());
    Grammar_WithConceptsLanguage language = new Grammar_WithConceptsLanguage();
    Grammar_WithConceptsGlobalScope grammar_withConceptsGlobalScope = new Grammar_WithConceptsGlobalScope(modelPath, language);

    Grammar_WithConceptsSymbolTableCreatorDelegator stCreator = language.getSymbolTableCreator(grammar_withConceptsGlobalScope);
    stCreator.createFromAST(ast.get());

    IGrammarScope innerScope = ast.get().getClassProd(0).getEnclosingScope();

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
  }

  @Test
  public void testSubsubgrammarSymbolTable() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());

    Grammar_WithConceptsLanguage language = new Grammar_WithConceptsLanguage();
    Grammar_WithConceptsGlobalScope grammar_withConceptsGlobalScope = new Grammar_WithConceptsGlobalScope(modelPath, language);
    Grammar_WithConceptsSymbolTableCreatorDelegator stCreator = language.getSymbolTableCreator(grammar_withConceptsGlobalScope);
    stCreator.createFromAST(ast.get());
    IGrammarScope innerScope = ast.get().getClassProd(0).getEnclosingScope();

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
    assertFalse(innerScope.resolveProd("de.monticore.inherited.sub.Subgrammar").isPresent());
    assertFalse(innerScope.resolveProd("Subsubgrammar").isPresent());
    assertFalse(innerScope.resolveProd("de.monticore.inherited.subsub.Subsubgrammar").isPresent());

    assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.Supergrammar").isPresent());
    assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.sub.Subgrammar").isPresent());
    assertTrue(innerScope.resolveMCGrammar("de.monticore.inherited.subsub.Subsubgrammar").isPresent());
    assertTrue(innerScope.resolveMCGrammar("Subsubgrammar").isPresent());
  }

  @Test
  public void testSubsubgrammarResolveInSuperGrammars() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());

    Grammar_WithConceptsLanguage language = new Grammar_WithConceptsLanguage();
    Grammar_WithConceptsGlobalScope grammar_withConceptsGlobalScope = new Grammar_WithConceptsGlobalScope(modelPath, language);
    Grammar_WithConceptsSymbolTableCreatorDelegator stCreator = language.getSymbolTableCreator(grammar_withConceptsGlobalScope);
    stCreator.createFromAST(ast.get());
    IGrammarScope innerScope = ast.get().getClassProd(0).getEnclosingScope();

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
    assertFalse(innerScope.resolveInSuperGrammars("de.monticore.inherited.sub.Subgrammar", AccessModifier.ALL_INCLUSION).isPresent());
    assertFalse(innerScope.resolveInSuperGrammars("Subsubgrammar", AccessModifier.ALL_INCLUSION).isPresent());
    assertFalse(innerScope.resolveInSuperGrammars("de.monticore.inherited.subsub.Subsubgrammar", AccessModifier.ALL_INCLUSION).isPresent());
  }
}
