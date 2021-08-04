<#-- (c) https://github.com/MontiCore/monticore -->
${signature("classname", "prod", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import ${package}.${grammarNameLower}tr._ast.*;
import de.monticore.tf.ast.IPattern;
import de.monticore.tf.ast.IReplacement;
import de.se_rwth.commons.logging.Log;

/**
* This CoCo makes sure that no replacement is empty.
*/
public class ${classname} implements ${ast.getName()}TRAST${prod.getName()}_PatCoCo {

  @Override
  public void check(AST${prod.getName()}_Pat node) {
    <#if !prod.getSymbol().isIsInterface() && !prod.getSymbol().isIsAbstract()>
    <#list grammarInfo.getComponentLists(prod.getName()) as compList>
      <#if compList.isPresentUsageName()><#assign compListName = compList.getUsageName()?cap_first><#else><#assign compListName = compList.getName()?cap_first></#if>
    for(${attributeHelper.getTypePackage(compList)}.${attributeHelper.getTypeGrammarName(compList)}tr._ast.ASTITF${compList.getName()?keep_before_last("Ext")} _${compListName} : node.get${compListName}List()) {
      if(_${compListName} instanceof IPattern) {
        break;
      } else if (_${compListName} instanceof IReplacement &&
            ((IReplacement) _${compListName}).getReplacementOp().isRelative()) {
        Log.error(String.format("0xF0C10 Can't use relative replacement operator without preceding pattern to specify insert position."),
            _${compListName}.get_SourcePositionStart());
      }
    }

    </#list>
    </#if>
  }

  public void addTo(${ast.getName()}TRCoCoChecker checker) {
    checker.addCoCo(this);
  }

}
