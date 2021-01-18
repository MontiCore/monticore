// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;

import java.util.List;
import java.util.Stack;

public class MultiplicityVisitor implements GrammarVisitor2 {


  private Stack<ASTGrammarNode> components = new Stack<>();
  private List<ASTGrammarNode> result = Lists.newArrayList();

  private ASTNode last;

  public MultiplicityVisitor(ASTGrammarNode last) {
    this.last = last;
  }

  public List<ASTGrammarNode> getComponents() {
    return result;
  }


  @Override
  public void visit(ASTClassProd node) {
    components.push(node);
    if (node==last) {
      result.addAll(components);
    }
  }

  @Override
  public void endVisit(ASTClassProd node) {
    components.pop();
    if (node==last) {
      result.addAll(components);
    }
  }

  @Override
  public void visit(ASTAlt node) {
    components.push(node);
    if (node==last) {
      result.addAll(components);
    }
  }

  @Override
  public void endVisit(ASTAlt node) {
    components.pop();
    if (node==last) {
      result.addAll(components);
    }
  }

  @Override
  public void visit(ASTBlock node) {
    components.push(node);
    if (node==last) {
      result.addAll(components);
    }
  }

  @Override
  public void endVisit(ASTBlock node) {
    components.pop();
  }

  @Override
  public void visit(ASTNonTerminal node) {
    components.push(node);
    if (node==last) {
      result.addAll(components);
    }
  }

  @Override
  public void endVisit(ASTNonTerminal node) {
    components.pop();
  }

  @Override
  public void visit(ASTTerminal node) {
    components.push(node);
    if (node==last) {
      result.addAll(components);
    }
  }

  @Override
  public void endVisit(ASTTerminal node) {
    components.pop();
  }
  @Override
  public void visit(ASTKeyTerminal node) {
    components.push(node);
    if (node==last) {
      result.addAll(components);
    }
  }

  @Override
  public void endVisit(ASTKeyTerminal node) {
    components.pop();
  }

  @Override
  public void visit(ASTTokenTerminal node) {
    components.push(node);
    if (node==last) {
      result.addAll(components);
    }
  }

  @Override
  public void endVisit(ASTTokenTerminal node) {
    components.pop();
  }

  @Override
  public void visit(ASTConstantGroup node) {
    components.push(node);
    if (node==last) {
      result.addAll(components);
    }
  }

  @Override
  public void endVisit(ASTConstantGroup node) {
    components.pop();
  }

}
