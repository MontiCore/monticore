/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._ast;


public interface ASTRuleComponent extends ASTRuleComponentTOP {

  default String getName() {
    return "";
  }

}