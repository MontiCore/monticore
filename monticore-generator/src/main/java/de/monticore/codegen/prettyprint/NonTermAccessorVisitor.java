// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.grammar.Multiplicity;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.AdditionalAttributeSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.se_rwth.commons.StringTransformations;

import java.util.*;


public class NonTermAccessorVisitor implements GrammarVisitor2 {

  protected final Map<String, ClassProdNonTermPrettyPrintData> classProds = new HashMap<>();
  protected ClassProdNonTermPrettyPrintData currentData;

  protected Collection<AdditionalAttributeSymbol> astAttributes;

  public Map<String, ClassProdNonTermPrettyPrintData> getClassProds() {
    return this.classProds;
  }

  @Override
  public void visit(ASTClassProd node) {
    this.currentData = this.classProds.computeIfAbsent(node.getName(), x -> new ClassProdNonTermPrettyPrintData());

    this.currentData.effectiveIterationStack.push(ASTConstantsGrammar.DEFAULT);

    this.astAttributes = node.getSpannedScope().getLocalAdditionalAttributeSymbols();
  }

  @Override
  public void visit(ASTMCGrammar astGrammar) {
    for (ProdSymbol prodSymbol : astGrammar.getSymbol().getProds()) {
      Collection<AdditionalAttributeSymbol> astAttributes = prodSymbol.getSpannedScope().getLocalAdditionalAttributeSymbols();
      for (String compName : prodSymbol.getSpannedScope().getRuleComponentSymbols().keySet()) {
        Optional<AdditionalAttributeSymbol> attribute = astAttributes.stream()
                .filter(a -> a.getName().equals(compName)).findAny();
        Multiplicity multiplicity = Multiplicity.STANDARD;
        if (attribute.isPresent()) {
          multiplicity = Multiplicity.determineMultiplicity(attribute.get().getAstNode());
        } else {
          for (RuleComponentSymbol component : prodSymbol.getSpannedScope().getRuleComponentSymbols().get(compName)) {
            if (component.isIsNonterminal()) {
              Multiplicity mult = Multiplicity.determineMultiplicity(component.getAstNode());
              multiplicity = Collections.max(Lists.newArrayList(mult, multiplicity));
            }
          }
        }
        ClassProdNonTermPrettyPrintData data = classProds.computeIfAbsent(prodSymbol.getName(), x -> new ClassProdNonTermPrettyPrintData());
        data.nonTerminalMultiplicities.put(compName, multiplicity);
      }
    }
  }

  @Override
  public void visit(ASTNonTerminal node) {
    if (this.currentData == null) return;
    String refName = node.isPresentUsageName() ? node.getUsageName() : node.getName();

    int n = this.currentData.nonTerminals.getOrDefault(refName, 0);
    this.currentData.nonTerminals.put(refName, n + 1);
    this.currentData.nonTerminalIteration.put(refName, Math.max(this.currentData.nonTerminalIteration.getOrDefault(refName, 0), node.getIteration()));
    this.currentData.nonTerminalNodes.put(refName, node);


    if (this.currentData.exhaustedNonTerminals.contains(refName))
      this.currentData.erroringNonTerminals.add(refName);

    int outerEffectiveIteration = this.currentData.effectiveIterationStack.peek();
    int effectiveIteration = getEffectiveIteration(outerEffectiveIteration, node.getIteration());
    if (effectiveIteration == ASTConstantsGrammar.STAR || effectiveIteration == ASTConstantsGrammar.PLUS) {
      this.currentData.exhaustedNonTerminals.add(refName);
      if (node.getIteration() != effectiveIteration // in a repeated block
              || (outerEffectiveIteration == ASTConstantsGrammar.STAR || outerEffectiveIteration == ASTConstantsGrammar.PLUS)) { // repeat in repeat
        this.currentData.effectiveIterationIteratorNonTerminals.add(refName);
      }
    }
  }

  @Override
  public void visit(ASTBlock node) {
    if (this.currentData == null) return;
    int prev = this.currentData.effectiveIterationStack.peek();
    this.currentData.effectiveIterationStack.push(getEffectiveIteration(prev, node.getIteration()));
  }

  protected int getEffectiveIteration(int outer, int self) {
    if (outer == ASTConstantsGrammar.STAR)
      return ASTConstantsGrammar.STAR;
    if (outer == ASTConstantsGrammar.PLUS)
      return ASTConstantsGrammar.PLUS;
    if (outer == ASTConstantsGrammar.QUESTION) {
      if (self == ASTConstantsGrammar.STAR || self == ASTConstantsGrammar.PLUS)
        return self;
      return outer;
    }
    return self;
  }

  @Override
  public void endVisit(ASTBlock node) {
    if (this.currentData == null) return;
    this.currentData.effectiveIterationStack.pop();
  }

  /**
   * Data class
   */
  public static class ClassProdNonTermPrettyPrintData {
    // Count NonTerminal (references)
    protected final Map<String, Integer> nonTerminals = new HashMap<>();
    protected final Map<String, Integer> nonTerminalIteration = new HashMap<>();
    // As soon as a NT* was used, we won't be able to automatically generate in case of further NT references
    protected final Set<String> exhaustedNonTerminals = new HashSet<>();
    // Referenced NTs after being exhausted - e.g. NT* NT
    protected final Set<String> erroringNonTerminals = new HashSet<>();
    protected final Map<String, ASTNode> nonTerminalNodes = new HashMap<>();
    protected final Map<String, Multiplicity> nonTerminalMultiplicities = new HashMap<>();
    protected final Set<String> effectiveIterationIteratorNonTerminals = new HashSet<>();
    protected final Stack<Integer> effectiveIterationStack = new Stack<>();
    protected final Set<String> forceIterators = new HashSet<>(); // Due to AST Rules

    public Map<String, Integer> getNonTerminals() {
      return this.nonTerminals;
    }

    public Map<String, ASTNode> getNonTerminalNodes() {
      return this.nonTerminalNodes;
    }

    public Set<String> getErroringNonTerminals() {
      return this.erroringNonTerminals;
    }

    public Multiplicity getMultiplicity(String uncapRefName) {
      return this.nonTerminalMultiplicities.get(uncapRefName);
    }

    /*
     * We use Iterators in two cases:
     * - NonTerminals referenced multiple times:  NT NT, NT ("." NT)* (NonTerminalSep)
     * - NonTerminals in iterated blocks:  ("term" NT)*, NT ("." NT)* (NonTerminalSep too)
     */
    public boolean isIteratorNeeded(String refName) {
      if (this.forceIterators.contains(refName)) return true;
      if (this.getMultiplicity(StringTransformations.uncapitalize(refName)) != Multiplicity.LIST) {
        // e.g., in case of ASTRules setting the cardinality to max=1
        return false;
      }
      if (this.nonTerminals.getOrDefault(refName, 0) > 1) // Watch out: Only if the type is a list
        return true;
      return this.effectiveIterationIteratorNonTerminals.contains(refName);
    }
  }
}
