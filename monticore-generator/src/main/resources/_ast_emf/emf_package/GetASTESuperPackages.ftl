<#-- (c) https://github.com/MontiCore/monticore -->
<#assign service = glex.getGlobalVar("service")>
   List<de.monticore.emf._ast.ASTEPackage> eSuperPackages = new ArrayList<>();
    <#list service.getSuperCDsTransitive() as superCD>
      <#assign qualifiedName = service.getQualifiedPackageImplName(superCD)>
   eSuperPackages.add((de.monticore.emf._ast.ASTEPackage)${qualifiedName}.eINSTANCE);
    </#list>
   return eSuperPackages;
