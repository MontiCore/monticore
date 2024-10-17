/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.ruletranslation;

import de.monticore.ast.ASTNode;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._ast.ASTODDefinition;
import de.monticore.tf.odrules._ast.ASTODObject;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.rule2od.Variable2AttributeMap;

import java.util.Map;
import java.util.Stack;

public class Rule2ODState {
  
  public Rule2ODState(Variable2AttributeMap variable2Attributes, Map<ASTNode, ASTNode> parents){
    genRule = ODRulesMill.oDRuleBuilder().uncheckedBuild();
    ASTODDefinition lhs = ODRulesMill.oDDefinitionBuilder().uncheckedBuild();
    this.lhs = lhs;
    lhs.setName("lhs");
    genRule.setLhs(lhs);
    ASTODDefinition rhs = ODRulesMill.oDDefinitionBuilder().uncheckedBuild();
    this.rhs = rhs;
    rhs.setName("rhs");
    genRule.setRhs(rhs);
    this.variable2Attributes = variable2Attributes;
    this.parents = parents;
  }
  
  private ASTODRule genRule;
  
  private ASTODDefinition lhs;
  
  private ASTODDefinition rhs;
  
  private ODRuleNameGenerator nameGen = new ODRuleNameGenerator();

  private Variable2AttributeMap variable2Attributes;

  private Map<ASTNode, ASTNode> parents;

  private boolean isNegative = false;

  private Stack<ASTODObject> hierarchyLHS = new Stack<>();
  private Stack<ASTODObject> hierarchyRHS = new Stack<>();

  private Position position = Position.BOTH;

  public ASTODRule getGenRule() {
    return genRule;
  }

  public void setGenRule(ASTODRule genRule) {
    this.genRule = genRule;
  }

  public ASTODDefinition getLhs() {
    return lhs;
  }

  public void setLhs(ASTODDefinition lhs) {
    this.lhs = lhs;
  }

  public ASTODDefinition getRhs() {
    return rhs;
  }

  public void setRhs(ASTODDefinition rhs) {
    this.rhs = rhs;
  }

  public ODRuleNameGenerator getNameGen() {
    return nameGen;
  }

  public void setNameGen(ODRuleNameGenerator nameGen) {
    this.nameGen = nameGen;
  }

  public Variable2AttributeMap getVariable2Attributes() {
    return variable2Attributes;
  }

  public void setVariable2Attributes(Variable2AttributeMap variable2Attributes) {
    this.variable2Attributes = variable2Attributes;
  }

  public Map<ASTNode, ASTNode> getParents() {
    return parents;
  }

  public void setParents(Map<ASTNode, ASTNode> parents) {
    this.parents = parents;
  }

  public boolean isNegative() {
    return isNegative;
  }

  public void setNegative(boolean negative) {
    isNegative = negative;
  }

  public Stack<ASTODObject> getHierarchyLHS() {
    return hierarchyLHS;
  }

  public void setHierarchyLHS(Stack<ASTODObject> hierarchyLHS) {
    this.hierarchyLHS = hierarchyLHS;
  }

  public Stack<ASTODObject> getHierarchyRHS() {
    return hierarchyRHS;
  }

  public void setHierarchyRHS(Stack<ASTODObject> hierarchyRHS) {
    this.hierarchyRHS = hierarchyRHS;
  }

  public Position getPosition() {
    return position;
  }
  
  public void setPosition(Position position) {
    this.position = position;
  }
  
  


}
