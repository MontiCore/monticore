<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "supergrammars", "rootNode")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign topAstName = genHelper.getQualifiedStartRuleName()>
<#assign scopeName = "I"+grammarName+"Scope">

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;

import ${fqn}._ast.*;
import ${fqn}._visitor.${genHelper.getDelegatorVisitorType()};
import de.monticore.ast.ASTNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class ${className} extends ${grammarName}DelegatorVisitor {

  protected Deque<${scopeName}> scopeStack = new ArrayDeque<>();

  protected final ${grammarName}SymbolTableCreator ${grammarName?uncap_first}STC;

  protected I${grammarName}GlobalScope globalScope;

  public ${className}(I${grammarName}GlobalScope globalScope) {
    this.scopeStack.push(globalScope);
    this.globalScope = globalScope;
  <#list supergrammars as sup>
    ${sup.getName()}STCFor${grammarName} ${sup.getName()?uncap_first}SymbolTableCreator = new ${sup.getName()}STCFor${grammarName}(scopeStack);
    set${sup.getName()}Visitor(${sup.getName()?uncap_first}SymbolTableCreator);
  </#list>

    ${grammarName?uncap_first}STC = new ${grammarName}SymbolTableCreator(scopeStack);
    set${grammarName}Visitor(${grammarName?uncap_first}STC);
  }

  public ${grammarName}ArtifactScope createFromAST(${genHelper.getQualifiedASTName(rootNode)} rootNode) {
    ${grammarName}ArtifactScope as =  ${grammarName?uncap_first}STC.createFromAST(rootNode);
    if (as.getName().isPresent()){
      globalScope.cache(as.getName().get());
    }
    return as;
  }
}
