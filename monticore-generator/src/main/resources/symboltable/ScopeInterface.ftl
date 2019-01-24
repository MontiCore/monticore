<#-- (c) https://github.com/MontiCore/monticore -->
${signature("interfaceName", "symbolNames", "superScopes")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;

public interface ${interfaceName} <#if superScopes?size != 0>extends ${superScopes?join(", ")} </#if>{

<#list symbolNames?keys as symbol>
  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name);

  public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name);

</#list>

}

