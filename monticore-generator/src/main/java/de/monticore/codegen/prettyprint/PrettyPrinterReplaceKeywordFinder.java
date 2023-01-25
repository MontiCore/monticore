// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint;

import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;

import java.util.*;

/**
 * Traverse the AST (of e.g. a grammar) and mark all productions with which keywords should be replaced
 * Ignores keywords replaced by a replacekeyword rule already handled in the PrettyPrinter generation
 * (being in the same grammar/super-grammars as the keywords' grammar)
 */
public class PrettyPrinterReplaceKeywordFinder {
  // Keywords -> [replacing keywords]
  protected final Map<String, Collection<String>> replacedKeywordsWithInherited;
  protected Map<String, Collection<String>> replacedKeywordsLocalGrammar;
  // ProdSymbol -> {keyword -> [replacing]}
  protected Map<ProdSymbol, Map<String, Collection<String>>> replacedKeywordsInProductions;

  protected GrammarTraverser traverser;

  public PrettyPrinterReplaceKeywordFinder(Map<String, Collection<String>> replacedKeywordsWithInherited) {
    // Note: ProdRule#getEnclosingScope might fail due to NonSeps being modified => unable to use scopes here
    this.replacedKeywordsWithInherited = replacedKeywordsWithInherited;

    this.traverser = GrammarMill.traverser();
    this.traverser.add4Grammar(new GrammarVisitor2() {
      Optional<ProdSymbol> prodSymbol = Optional.empty();
      @Override
      public void visit(ASTClassProd node) {
        this.prodSymbol = Optional.of(node.getSymbol());
      }

      @Override
      public void visit(ASTAbstractProd node) {
        this.prodSymbol = Optional.of(node.getSymbol());
      }

      @Override
      public void visit(ASTInterfaceProd node) {
        this.prodSymbol = Optional.of(node.getSymbol());
      }

      @Override
      public void endVisit(ASTClassProd node) {
        this.prodSymbol = Optional.empty();
      }

      @Override
      public void endVisit(ASTAbstractProd node) {
        this.prodSymbol = Optional.empty();
      }

      @Override
      public void endVisit(ASTInterfaceProd node) {
        this.prodSymbol = Optional.empty();
      }

      /**
       * Handle a keyword being use and check if replacing is required
       * @param keyword the keyword
       */
      void mark(String keyword) {
        // Abort in case we are not in a production, but e.g., in a follow option
        if (this.prodSymbol.isEmpty()) return;

        if (replacedKeywordsWithInherited.containsKey(keyword)) {
          Collection<String> replacingKeywords = replacedKeywordsWithInherited.get(keyword);
          Collection<String> localReplacedKeywords = replacedKeywordsLocalGrammar.get(keyword);
          // skip replacekeywords which are already present during the PP-generation of a grammar and do NOT require overwriting of a handle method
          if (localReplacedKeywords == null || !localReplacedKeywords.equals(replacingKeywords)) {
            // get keyword -> [replacing keywords] for symbol
            Map<String, Collection<String>> replacedKeywords = replacedKeywordsInProductions.computeIfAbsent(prodSymbol.get(), x -> new HashMap<>());
            // get [replacing keywords] and add
            replacedKeywords.computeIfAbsent(keyword, x -> new ArrayList<>()).addAll(replacingKeywords);
          }
        }
      }
      @Override
      public void visit(ASTTerminal node) {
        mark(node.getName());
      }
      @Override
      public void visit(ASTKeyTerminal node) {
        mark(node.getName());
      }
      @Override
      public void visit(ASTTokenTerminal node) {
        mark(node.getName());
      }
    });
  }

  public Map<ProdSymbol, Map<String, Collection<String>>> check(ASTMCGrammar astmcGrammar) {
    replacedKeywordsInProductions = new HashMap<>();
    replacedKeywordsLocalGrammar = astmcGrammar.getSymbol().getReplacedKeywordsWithInherited();
    traverser.clearTraversedElements();
    astmcGrammar.accept(traverser);
    return replacedKeywordsInProductions;
  }

}
