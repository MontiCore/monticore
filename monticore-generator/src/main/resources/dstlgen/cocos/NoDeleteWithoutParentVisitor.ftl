<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;
import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr.${ast.getName()}TRMill;
import ${package}.${grammarNameLower}tr._visitor.*;
import de.monticore.tf.grammartransformation.CollectCoCoInformationState;
import de.monticore.tf.tfcommons._ast.ASTITFPart;

<#assign service = glex.getGlobalVar("service")>
/**
* This Visitor makes sure that no elements are deleted without specifying a parent pattern.
*/
public class ${className} implements ${ast.getName()}TRVisitor2,${ast.getName()}TRHandler  {

    private CollectCoCoInformationState state;
    private ${ast.getName()}TRTraverser realTraverser;

    public ${className}(CollectCoCoInformationState state) {
        this.state = state;
        this.realTraverser = null;
    }

    public ${className}() {
        this(new CollectCoCoInformationState());
    }

    @Override public ${ast.getName()}TRTraverser getTraverser() {
        return this.realTraverser;
    }
    @Override public void setTraverser(${ast.getName()}TRTraverser t) {
        this.realTraverser = t;
    }

<#list productions as prod>
    @Override
    public void visit(AST${prod.getName()}_Pat node) {
        state.incrementParentNest();
    }

    @Override
    public void endVisit(AST${prod.getName()}_Pat node) {
        state.decrementParentNest();
    }

    @Override
    public void visit(AST${prod.getName()}_Rep node) {
        if(state.getParentNest() == 0 && !node.isPresentRhs()) {
            Log.error("0xF0C11${service.getGeneratedErrorCode(className + prod.getName())} Elements must not be deleted without a parent pattern.", node.get_SourcePositionStart());
        }
    }

</#list>

}
