<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "scopeClassName", "ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign ruleName = ruleSymbol.getName()?cap_first>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

import java.util.Collection;
import java.util.Optional;

public class ${className} extends de.monticore.symboltable.CommonScopeSpanningSymbol {

  ${includeArgs("symboltable.symbols.KindConstantDeclaration", ruleName)}

  public ${className}(String name) {
    super(name, KIND);
  }


  @Override
  protected ${scopeClassName} createSpannedScope() {
    return new ${scopeClassName}();
  }

  <#-- Get methods for  containing symbols -->
  <#assign fields = genHelper.symbolRuleComponents2JavaFields(ruleSymbol)>
  /* Possible methods for containinig symbols
  <#list fields?keys as fname>
    <#assign type = fields[fname]>

  public Collection<${type}> get${fname?cap_first}() {
    return sortSymbolsByPosition(getSpannedScope().resolveLocally(${type}.KIND));
  }
  </#list>
  */

  ${includeArgs("symboltable.symbols.GetAstNodeMethod", ruleName)}

  ${includeArgs("symboltable.SymbolBuilder", className)}
}
