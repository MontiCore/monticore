<#-- (c) https://github.com/MontiCore/monticore -->
<#assign genHelper = glex.getGlobalVar("astHelper")>

import ${genHelper.getQualifiedASTNodeType()};
import ${astHelper.getVisitorPackage()}.${genHelper.getCdName()}Visitor;

/**
 * Interface for all AST nodes of the ${genHelper.getCdName()} language.
 */
public interface ${ast.getName()} extends ${ast.printInterfaces()} {

  public void accept(${genHelper.getCdName()}Visitor visitor);

}
