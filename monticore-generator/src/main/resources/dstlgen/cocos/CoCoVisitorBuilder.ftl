<#-- (c) https://github.com/MontiCore/monticore -->
${signature("visitorClassName", "package", "visitorName")}
package ${package}.${grammarNameLower}tr._cocos;

import ${package}.${grammarNameLower}tr._visitor.*;
import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr.${ast.getName()}TRMill;
import de.monticore.tf.tfcommons._ast.ASTTfIdentifier;
import java.util.HashSet;
import java.util.Set;
import de.monticore.tf.grammartransformation.CollectCoCoInformationState;
import de.monticore.tf.tfcommons._ast.ASTITFPart;

public class ${visitorClassName}Builder  {

  public ${ast.getName()}TRTraverser build(){
    return build(new CollectCoCoInformationState());
  }

  public ${ast.getName()}TRTraverser build(CollectCoCoInformationState state){
    ${ast.getName()}TRTraverser traverser = ${ast.getName()}TRMill.inheritanceTraverser();
    ${visitorClassName} t = new ${visitorClassName}(state);
    traverser.add4${ast.getName()}TR(t);
    traverser.set${ast.getName()}TRHandler(t);
    <#list inheritanceHelper.getSuperGrammars(ast) as superGrammar>
      {
        ${superGrammar.packageName}.tr.${superGrammar.getName()?lower_case}tr._cocos.${superGrammar.getName()}${visitorName} v = new ${superGrammar.packageName}.tr.${superGrammar.getName()?lower_case}tr._cocos.${superGrammar.getName()}${visitorName}(state);
        traverser.add4${superGrammar.getName()}TR(v);
        traverser.set${superGrammar.getName()}TRHandler(v);
      }
    </#list>
    return traverser;
  }
}
