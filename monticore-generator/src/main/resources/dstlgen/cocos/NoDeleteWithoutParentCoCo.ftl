<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","package")}
package ${package}.${grammarNameLower}tr._cocos;

import ${package}.${grammarNameLower}tr._ast.AST${grammarName}TFRule;

/**
* This CoCo makes sure that no elements are deleted without specifying a parent pattern.
*/
public class NoDeleteWithoutParentCoCo implements ${grammarName}TRAST${grammarName}TFRuleCoCo {

    @Override
    public void check(AST${grammarName}TFRule node) {
        node.accept(new ${grammarName}NoDeleteWithoutParentVisitorBuilder().build());
    }

    public void addTo(${ast.getName()}TRCoCoChecker checker) {
        checker.addCoCo(this);
    }
}
