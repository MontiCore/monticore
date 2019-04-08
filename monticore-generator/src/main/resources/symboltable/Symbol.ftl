<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "prodSymbol", "ruleSymbol", "imports")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>
<#assign ruleName = prodSymbol.getName()>
<#assign superClass = "">
<#assign superInterfaces = "implements ICommon" + genHelper.getGrammarSymbol().getName() + "Symbol<"+ruleName+">">
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

import java.util.Optional;
<#list imports as imp>
import ${imp}._ast.*;
</#list>

public class ${className} ${superClass} ${superInterfaces} {

  public ${className}(String name) {
    super(name, KIND);
  }

  ${includeArgs("symboltable.symbols.GetAstNodeMethod", ruleName)}

  <#assign langVisitorType = names.getQualifiedName(genHelper.getVisitorPackage(), genHelper.getGrammarSymbol().getName() + "SymbolVisitor")>
   public void accept(${langVisitorType} visitor) {
  <#if genHelper.isSupertypeOfHWType(className, "")>
  <#assign plainName = className?remove_ending("TOP")>
    if (this instanceof ${plainName}) {
      visitor.handle((${plainName}) this);
    } else {
      throw new UnsupportedOperationException("0xA7010{genHelper.getGeneratedErrorCode(ast)} Only handwritten class ${plainName} is supported for the visitor");
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
    this.enclosing${genHelper.getGrammarSymbol().getName()}Scope = newEnclosingScope;

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

    // set new enclosing scope (or null)
     this.enclosingScope = newEnclosingScope;
    */
  }
}
