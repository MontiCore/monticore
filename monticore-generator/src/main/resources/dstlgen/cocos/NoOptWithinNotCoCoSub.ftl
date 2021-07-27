<#-- (c) https://github.com/MontiCore/monticore -->
${signature("classname", "prod", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr._visitor.${ast.getName()}TRTraverser;


public class ${classname} implements ${ast.getName()}TRAST${prod.getName()}_NegCoCo {
  private ${ast.getName()}TRTraverser NoOptWithinNotTraverser;

  public NoOptWithinNotCoCo_${prod.getName()}(${ast.getName()}TRTraverser NoOptWithinNotTraverser) {
    this.NoOptWithinNotTraverser = NoOptWithinNotTraverser;
  }

  @Override
  public void check(AST${prod.getName()}_Neg node) {
    node.accept(NoOptWithinNotTraverser);
  }
}
