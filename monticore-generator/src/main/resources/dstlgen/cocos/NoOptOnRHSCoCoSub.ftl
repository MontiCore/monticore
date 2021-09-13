<#-- (c) https://github.com/MontiCore/monticore -->
${signature("classname", "prod", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr._visitor.${ast.getName()}TRTraverser;


public class ${classname} implements ${ast.getName()}TRAST${prod.getName()}_RepCoCo {
  private ${ast.getName()}TRTraverser noOptOnRHSTraverser;

  public NoOptOnRHSCoCo_${prod.getName()}(${ast.getName()}TRTraverser noOptOnRHSTraverser) {
    this.noOptOnRHSTraverser = noOptOnRHSTraverser;
  }

  @Override
  public void check(AST${prod.getName()}_Rep node) {
    if (node.isPresentRhs()) {
      node.getRhs().accept(noOptOnRHSTraverser);
    }
  }
}


