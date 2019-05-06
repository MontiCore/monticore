<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "scopeClassName", "prodSymbol", "ruleSymbol", "imports")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>
<#if prodSymbol.getSymbolDefinitionKind().isPresent()>
  <#assign ruleName = prodSymbol.getSymbolDefinitionKind().get()>
<#else>
  <#assign ruleName = prodSymbol.getName()>
</#if>
<#assign astName = prodSymbol.getName()?cap_first>
<#assign superClass = " extends de.monticore.symboltable.CommonScopeSpanningSymbol">
<#assign superInterfaces = "implements ICommon" + genHelper.getGrammarSymbol().getName() + "Symbol">
<#if ruleSymbol.isPresent()>
  <#if !ruleSymbol.get().isEmptySuperInterfaces()>
    <#assign superInterfaces = ", " + stHelper.printGenericTypes(ruleSymbol.get().getSuperInterfaceList())>
  </#if>
  <#if !ruleSymbol.get().isEmptySuperClasss()>
    <#assign superClass = " extends " + stHelper.printGenericTypes(ruleSymbol.get().getSuperClassList())>
  </#if>
</#if>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

import java.util.Collection;
import java.util.Optional;

<#list imports as imp>
import ${imp}._ast.*;
</#list>

public class ${className} ${superClass} ${superInterfaces} {

  ${includeArgs("symboltable.symbols.KindConstantDeclaration", ruleName)}

  public ${className}(String name) {
    super(name, KIND);
  }


  @Override
  protected ${scopeClassName} createSpannedScope() {
    return new ${scopeClassName}();
  }

  <#-- Get methods for  containing symbols -->
  <#assign fields = genHelper.symbolRuleComponents2JavaFields(prodSymbol)>
  /* Possible methods for containinig symbols
  <#list fields?keys as fname>
    <#assign type = fields[fname]>

  public Collection<${type}> get${fname?cap_first}() {
    return sortSymbolsByPosition(getSpannedScope().resolveLocally(${type}.KIND));
  }
  </#list>
  */

  ${includeArgs("symboltable.symbols.GetAstNodeMethod", astName)}

  <#assign langVisitorType = names.getQualifiedName(genHelper.getVisitorPackage(), genHelper.getGrammarSymbol().getName() + "SymbolVisitor")>
   public void accept(${langVisitorType} visitor) {
  <#if genHelper.isSupertypeOfHWType(className, "")>
  <#assign plainName = className?remove_ending("TOP")>
    if (this instanceof ${plainName}) {
      visitor.handle((${plainName}) this);
    } else {
      throw new UnsupportedOperationException("0xA7010${genHelper.getGeneratedErrorCode(ast)} Only handwritten class ${plainName} is supported for the visitor");
    }
  <#else>
    visitor.handle(this);
  </#if>
  }

  <#if ruleSymbol.isPresent()>
    ${includeArgs("symboltable.symbols.SymbolRule", ruleSymbol.get())}
  </#if>

  protected I${genHelper.getGrammarSymbol().getName()}Scope enclosing${genHelper.getGrammarSymbol().getName()}Scope;

  public I${genHelper.getGrammarSymbol().getName()}Scope getEnclosing${genHelper.getGrammarSymbol().getName()}Scope(){
    return this.enclosing${genHelper.getGrammarSymbol().getName()}Scope;
  }

  public void setEnclosing${genHelper.getGrammarSymbol().getName()}Scope(I${genHelper.getGrammarSymbol().getName()}Scope newEnclosingScope){
  /*
  if ((this.enclosing${genHelper.getGrammarSymbol().getName()}Scope != null) && (newEnclosingScope != null)) {
    if (this.enclosing${genHelper.getGrammarSymbol().getName()}Scope == newEnclosingScope) {
      return;
    }
    warn("0xA1042 Scope \"" + getName() + "\" has already an enclosing scope.");
  }

  // remove this scope from current (old) enclosing scope, if exists.
  if (this.enclosing${genHelper.getGrammarSymbol().getName()}Scope != null) {
    this.enclosing${genHelper.getGrammarSymbol().getName()}Scope.removeSubScope(this);
  }

  // add this scope to new enclosing scope, if exists.
  if (newEnclosingScope != null) {
    newEnclosingScope.addSubScope(this);
  }

  */
  // set new enclosing scope (or null)
  this.enclosing${genHelper.getGrammarSymbol().getName()}Scope = newEnclosingScope;
  }

}
