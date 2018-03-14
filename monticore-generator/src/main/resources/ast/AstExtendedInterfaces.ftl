<#-- (c) https://github.com/MontiCore/monticore -->
<#if ast.getInterfaceList()?size == 0>  de.monticore.ast.ASTNode,
<#else>
  ${ast.printInterfaces()},
</#if>
