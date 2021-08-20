<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import ${package}.${grammarNameLower}tr._visitor.*;
import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr.${ast.getName()}TRMill;
import de.monticore.tf.grammartransformation.CollectCoCoInformationState;
import de.monticore.tf.tfcommons._ast.ASTITFPart;

/**
 * This visitor counts occurrences of replacement elements
 */
public class ${className} implements ${ast.getName()}TRVisitor2, ${ast.getName()}TRHandler  {

  private CollectCoCoInformationState state;
  private ${ast.getName()}TRTraverser traverser;

  public ${className}(CollectCoCoInformationState state) {
    this.state = state;
    this.traverser = ${ast.getName()}TRMill.inheritanceTraverser();
    this.realTraverser = traverser;

    traverser.add4${ast.getName()}TR(this);
    traverser.set${ast.getName()}TRHandler(this);
    <#list inheritanceHelper.getSuperGrammars(ast) as superGrammar>
      {
        ${superGrammar.packageName}.tr.${superGrammar.getName()?lower_case}tr._cocos.${superGrammar.getName()}RepElemVisitor v = new ${superGrammar.packageName}.tr.${superGrammar.getName()?lower_case}tr._cocos.${superGrammar.getName()}RepElemVisitor(state);
        traverser.add4${superGrammar.getName()}TR(v);
        traverser.set${superGrammar.getName()}TRHandler(v);
      }
    </#list>
  }

  public ${className}() {
    this(new CollectCoCoInformationState());
  }

private ${ast.getName()}TRTraverser realTraverser;
@Override public ${ast.getName()}TRTraverser getTraverser() {
return this.realTraverser;
}
@Override public void setTraverser(${ast.getName()}TRTraverser t) {
this.realTraverser = t;
}

  public int getRepElements() {
    return state.getRepElements();
  }

  @Override
  public void traverse(AST${ast.getName()}TFRule node) {
    if (null != node.getTFRule()) {
      node.getTFRule().accept(getTraverser());
      for (ASTITFPart part : node.getTFRule().getITFPartList()) {
        part.accept(getTraverser());
      }
    }
  }

  <#list productions as prod>
  @Override
  public void visit(AST${prod.getName()}_Rep node) {
    state.incrementRepElements();
  }
  </#list>

}
