/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.se_rwth.commons.Joiners;

import java.util.HashMap;
import java.util.Map;

/**
 * Enable the overriding of generated grammars using hand-coded grammars
 * Traverse/visit the hand-coded grammar to apply it to a provided grammar
 */
public class TagLanguageOverrideVisitor implements GrammarVisitor2 {
  protected final ASTMCGrammar grammar;

  protected final Map<String, ASTClassProd> classProdMap = new HashMap<>();
  protected final Map<String, ASTEnumProd> enumProdMap = new HashMap<>();
  protected final Map<String, ASTInterfaceProd> interfaceProdMap = new HashMap<>();
  protected final Map<String, ASTAbstractProd> abstractProdMap = new HashMap<>();
  protected final Map<String, ASTExternalProd> externalProdMap = new HashMap<>();
  protected final Map<String, ASTLexProd> lexProdMap = new HashMap<>();
  protected final Map<String, ASTASTRule> astRuleMap = new HashMap<>();

  /**
   *
   * @param grammar the grammar to be enriched/modified - will be updated
   */
  public TagLanguageOverrideVisitor(ASTMCGrammar grammar) {
    this.grammar = grammar;

    this.grammar.getClassProdList().forEach(e -> classProdMap.put(e.getName(), e));
    this.grammar.getEnumProdList().forEach(e -> enumProdMap.put(e.getName(), e));
    this.grammar.getInterfaceProdList().forEach(e -> interfaceProdMap.put(e.getName(), e));
    this.grammar.getAbstractProdList().forEach(e -> abstractProdMap.put(e.getName(), e));
    this.grammar.getExternalProdList().forEach(e -> externalProdMap.put(e.getName(), e));
    this.grammar.getLexProdList().forEach(e -> lexProdMap.put(e.getName(), e));
    this.grammar.getASTRuleList().forEach(e -> astRuleMap.put(e.getType(), e));
  }

  @Override
  public void visit(ASTMCGrammar srcNode) {
    this.grammar.setComponent(srcNode.isComponent());
    for (ASTGrammarReference overrideSuperGrammar : srcNode.getSupergrammarList()) {
      boolean isNewSuper = true;
      for (ASTGrammarReference existingSuperGrammar : this.grammar.getSupergrammarList()) {
        String overrideSuperGrammarName = Joiners.DOT.join(overrideSuperGrammar.getNameList());
        String existingSuperGrammarName = Joiners.DOT.join(existingSuperGrammar.getNameList());
        if (overrideSuperGrammarName.equals(existingSuperGrammarName)) {
          isNewSuper = false;
        }
      }
      if (isNewSuper) {
        this.grammar.addSupergrammar(overrideSuperGrammar);
      }
    }
  }

  @Override
  public void visit(ASTClassProd node) {
    this.grammar.removeClassProd(classProdMap.get(node.getName()));
    this.grammar.addClassProd(node);
  }

  @Override
  public void visit(ASTEnumProd node) {
    this.grammar.removeEnumProd(enumProdMap.get(node.getName()));
    this.grammar.addEnumProd(node);
  }

  @Override
  public void visit(ASTInterfaceProd node) {
    this.grammar.removeInterfaceProd(interfaceProdMap.get(node.getName()));
    this.grammar.addInterfaceProd(node);
  }

  @Override
  public void visit(ASTAbstractProd node) {
    this.grammar.removeAbstractProd(abstractProdMap.get(node.getName()));
    this.grammar.addAbstractProd(node);
  }

  @Override
  public void visit(ASTExternalProd node) {
    this.grammar.removeExternalProd(externalProdMap.get(node.getName()));
    this.grammar.addExternalProd(node);
  }

  @Override
  public void visit(ASTLexProd node) {
    this.grammar.removeLexProd(lexProdMap.get(node.getName()));
    this.grammar.addLexProd(node);
  }

  @Override
  public void visit(ASTASTRule node) {
    this.grammar.removeASTRule(astRuleMap.get(node.getType()));
    this.grammar.addASTRule(node);
  }

  @Override
  public void visit(ASTGrammarOption srcNode) {
    this.grammar.setGrammarOption(srcNode);
  }

}
