<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import de.se_rwth.commons.logging.Log;
import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr._visitor.*;
import ${package}.${grammarNameLower}tr.${ast.getName()}TRMill;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.tf.tfcommons._ast.ASTITFPart;

<#assign service = glex.getGlobalVar("service")>
/**
 * This CoCo makes sure that the same schema variable is not used for different elements of a different type.
 */
public class ${className} implements ${ast.getName()}TRVisitor2, ${ast.getName()}TRHandler {

  List<String> variables = Lists.newArrayList();

<#list productions as prod>
  Map<String, AST${prod.getName()}_Pat> vars${prod.getName()}_Pat = Maps.newHashMap();
</#list>

  public final ${ast.getName()}TRTraverser traverser;
  public ${className}(){
    this.traverser = ${ast.getName()}TRMill.inheritanceTraverser();
    this.realTraverser = this.traverser;
    traverser.add4${ast.getName()}TR(this);
    traverser.set${ast.getName()}TRHandler(this);
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
      node.getTFRule().accept(getTraverser());
      for (ASTITFPart part : node.getTFRule().getITFPartList()) {
        part.accept(getTraverser());
      }
    }
  }

<#list productions as prod>
  @Override
  public void visit(AST${prod.getName()}_Pat node) {
    <#if grammarInfo.getStringAttrs(prod.getName())?has_content >
    if (node.isPresentSchemaVarName()) {
      if (variables.contains(node.getSchemaVarName())) {
        if (vars${prod.getName()}_Pat.containsKey(node.getSchemaVarName())) {
          AST${prod.getName()}_Pat existingNode = vars${prod.getName()}_Pat.get(node.getSchemaVarName());
          if (!existingNode.deepEquals(node)) { //ToDo: weakEquals
            Log.error(String.format("0xF0C02${service.getGeneratedErrorCode(className + prod.getName())} The schema variable %s is used several times. The same schema variable must not be"
                + " used for different elements of the same type",
                node.getSchemaVarName()),
                node.get_SourcePositionStart());
          }
        } else {
        Log.error(String.format("0xF0C01${service.getGeneratedErrorCode(className + prod.getName())} The schema variable %s is used several times. The same schema variable must not be"
                + " used for different elements of different type",
                node.getSchemaVarName()),
                node.get_SourcePositionStart());
        }
      } else {
        vars${prod.getName()}_Pat.put(node.getSchemaVarName(), node);
        variables.add(node.getSchemaVarName());
      }
    }
    </#if>
  }

</#list>
}
