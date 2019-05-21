<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "scopeName","superGrammarName", "scopeNameSuperGrammar", "superGrammarPackage")}

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

import de.se_rwth.commons.logging.Log;

import ${fqn}._symboltable.*;
import ${superGrammarPackage}._symboltable.*;
import ${superGrammarPackage}._visitor.*;
import de.monticore.ast.ASTNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class ${className} extends ${superGrammarName}SymbolTableCreator {

  public ${className}(final Deque<? extends ${scopeNameSuperGrammar}> scopeStack) {
    super(scopeStack);
  }

  public ${scopeName} createScope() {
    return  ${grammarName}SymTabMill.${grammarName?uncap_first+"Scope"}Builder().build();
  }
}
