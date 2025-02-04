/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar;

import com.google.common.collect.Lists;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;

import java.util.List;

public class ComponentCollector {

  public static List<ASTRuleComponent> getAllComponents(ASTGrammarNode node) {
    CollectRuleComponents cv = new CollectRuleComponents();
    GrammarTraverser traverser = GrammarMill.traverser();
    traverser.add4Grammar(cv);
    node.accept(traverser);
    return cv.getRuleComponents();
  }

  protected static class CollectRuleComponents implements GrammarVisitor2 {

    public List<ASTRuleComponent> ruleComponentList = Lists.newArrayList();

    public List<ASTRuleComponent> getRuleComponents() {
      return ruleComponentList;
    }

    @Override
    public void visit(ASTNonTerminal node) {
      ruleComponentList.add(node);
    }

    @Override
    public void visit(ASTTerminal node) {
      ruleComponentList.add(node);
    }

    @Override
    public void visit(ASTKeyTerminal node) {
      ruleComponentList.add(node);
    }

    @Override
    public void visit(ASTTokenTerminal node) {
      ruleComponentList.add(node);
    }

    @Override
    public void visit(ASTConstantGroup node) {
      ruleComponentList.add(node);
    }
  }

}
