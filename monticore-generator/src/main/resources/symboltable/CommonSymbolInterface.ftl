<#-- (c) https://github.com/MontiCore/monticore -->
${signature("interfaceName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>

<#-- set package -->
package ${genHelper.getTargetPackage()};

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.ISymbol;

public interface ${interfaceName}<A extends ASTNode> extends ISymbol<A> {
 <#assign langVisitorType = names.getQualifiedName(genHelper.getVisitorPackage(), genHelper.getGrammarSymbol().getName() + "SymbolVisitor")>
 public void accept(${langVisitorType} visitor);

  I${genHelper.getGrammarSymbol().getName()}Scope getEnclosingScope();

  void setEnclosingScope(I${genHelper.getGrammarSymbol().getName()}Scope scope);

}

