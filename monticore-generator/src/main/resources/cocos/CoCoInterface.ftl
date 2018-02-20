<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astPackage")}

<#assign genHelper = glex.getGlobalVar("coCoHelper")>
<#assign astName = genHelper.getPlainName(ast)>

package ${genHelper.getCoCoPackage()};

import ${astPackage}.${astName};

public interface ${genHelper.getCdName()}${astName}CoCo {
  public void check(${astName} node);
}
