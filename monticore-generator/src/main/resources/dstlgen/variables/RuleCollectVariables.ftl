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
public class ${className} {


  private Rule2ODState state;

  protected ${ast.getName()}TRTraverser traverser;

  public ${className}(Rule2ODState state){
    this.state = state;
    this.traverser = ${ast.getName()}TRMill.inheritanceTraverser();
    ${className}Visitor thisV = new ${className}Visitor(state);
    traverser.add4${ast.getName()}TR(thisV);
    traverser.set${ast.getName()}TRHandler(thisV);
    <#list inheritanceHelper.getSuperGrammars(ast) as super>
        {
        ${super.getPackageName()}.tr.translation.
        ${super.getName()}RuleCollectVariablesVisitor v = new ${super.getPackageName()}.tr.translation.
        ${super.getName()}RuleCollectVariablesVisitor(state);
        traverser.set${super.getName()}TRHandler(v);
        traverser.add4${super.getName()}TR(v);
        }
    </#list>
  }


     public ${ast.getName()}TRTraverser getTraverser() {
        return this.traverser;
    }

    public Variable2AttributeMap getCollectedVariables() {
        return state.getVariable2Attributes();
    }

}
