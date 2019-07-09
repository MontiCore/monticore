<#assign service = glex.getGlobalVar("service")>
   List<org.eclipse.emf.ecore.EPackage> eSuperPackages = new ArrayList<>();
    <#list service.getSuperCDs() as superCD>
      <#assign qualifiedName = service.getQualifiedPackageImplName(superCD)>
   eSuperPackages.add((de.monticore.emf._ast.ASTEPackage)${qualifiedName}.eINSTANCE);
    </#list>
   return eSuperPackages;