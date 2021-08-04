<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "package")}
package ${package}.translation;

import de.monticore.tf.rule2od.Variable2AttributeMap;
import ${package}.${grammarNameLower}tr._ast.*;
import de.monticore.tf.tfcommons._ast.ASTTfIdentifier;
import ${package}.${ast.getName()?lower_case}tr._visitor.*;
import de.monticore.tf.tfcommons._ast.ASTITFPart;
import ${package}.${ast.getName()?lower_case}tr.${ast.getName()}TRMill;

import de.monticore.tf.ruletranslation.Rule2ODState;

/**
 * collects all variables declared on the LHS of a rule in concrete syntax
 * and stores them in a maps that maps each variable to the attribute declaration
 * where it is used first
 */
public class ${className} implements ${ast.getName()}TRVisitor2, ${ast.getName()}TRHandler {


  private Rule2ODState state;


  public ${className}(Rule2ODState state){
    this.state = state;
    this.realTraverser = null;
  }



private ${ast.getName()}TRTraverser realTraverser;
    @Override public ${ast.getName()}TRTraverser getTraverser() {
        return this.realTraverser;
    }
    @Override public void setTraverser(${ast.getName()}TRTraverser t) {
        this.realTraverser = t;
    }


  @Override
  public void traverse(AST${ast.getName()}TFRule node) {
    if (null != node.getTFRule()) {
      node.getTFRule().accept(this.getTraverser());
      for (ASTITFPart part : node.getTFRule().getITFPartList()) {
        part.accept(this.getTraverser());
      }
    }
  }

  ${tc.include(var_visit_pattern, productions)}
  ${tc.include(var_traverse_replacement, productions)}
  ${tc.include(visit_negation, productions)}


}
