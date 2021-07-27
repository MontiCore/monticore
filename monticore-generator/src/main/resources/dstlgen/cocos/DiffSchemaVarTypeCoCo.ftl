<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import ${package}.${grammarNameLower}tr._ast.AST${grammarName}TFRule;

/**
* This CoCo makes sure that the same schema variable is not used for different elements of a different type.
*/
public class DiffSchemaVarTypeCoCo implements ${grammarName}TRAST${grammarName}TFRuleCoCo {

    @Override
    public void check(AST${grammarName}TFRule node) {
        DiffSchemaVarTypeVisitor v = new DiffSchemaVarTypeVisitor();
        node.accept(v.getTraverser());
    }

    public void addTo(${ast.getName()}TRCoCoChecker checker) {
        checker.addCoCo(this);
    }
}
