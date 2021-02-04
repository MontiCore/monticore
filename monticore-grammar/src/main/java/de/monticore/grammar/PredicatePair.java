/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTRuleReference;

public class PredicatePair {
  private String classname;
  
  private ASTRuleReference ruleReference;
  
  public ASTRuleReference getRuleReference() {
    return ruleReference;
  }
  
  public void setRuleReference(ASTRuleReference ruleReference) {
    this.ruleReference = ruleReference;
  }
  
  public String getClassname() {
    return classname;
  }
  
  public PredicatePair(String classname, ASTRuleReference ruleReference) {
    this.classname = classname;
    this.ruleReference = ruleReference;
  }
  
  @Override
  public boolean equals(Object o) {
    return (o instanceof PredicatePair) && classname.equals(((PredicatePair) o).classname);
  }
  
  @Override
  public int hashCode() {
    return classname.hashCode();
  }
}
