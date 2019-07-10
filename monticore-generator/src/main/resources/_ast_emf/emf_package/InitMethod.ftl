${tc.signature("packageName")}
<#assign service = glex.getGlobalVar("service")>

  if (isInited) {
  return (${packageName})org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(${packageName}.eNS_URI);
  }

  // Obtain or create and register package
  ${packageName}Impl ${packageName?uncap_first} = (${packageName}Impl)(org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ${packageName}Impl
      ? org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.get(eNS_URI) : new ${packageName}Impl());

  isInited = true;

  // Obtain or create and register interdependencies
de.monticore.emf._ast.ASTENodePackageImpl ASTENodePackage = (de.monticore.emf._ast.ASTENodePackageImpl)
(org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(de.monticore.emf._ast.ASTENodePackage.eNS_URI)  instanceof de.monticore.emf._ast.ASTENodePackage ?
  org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(de.monticore.emf._ast.ASTENodePackage.eNS_URI) : de.monticore.emf._ast.ASTENodePackage.eINSTANCE);

  <#list service.getSuperCDs() as superCD>
      <#assign qualifiedName = service.getQualifiedPackageImplName(superCD)>
      <#assign identifierName = service.getSimplePackageImplName(superCD)>
      ${qualifiedName} ${identifierName?uncap_first} =
    (${qualifiedName})(org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(
      ${qualifiedName}.eNS_URI) instanceof ${qualifiedName}?
      org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(${qualifiedName}.eNS_URI) :
      ${qualifiedName}.eINSTANCE);
  </#list>

  // Create package meta-data objects
  ${packageName?uncap_first}.createPackageContents();
  ASTENodePackage.createPackageContents();
  <#list service.getSuperCDs() as superCD>
      <#assign identifierName = service.getSimplePackageImplName(superCD)>
    ${identifierName?uncap_first}.createPackageContents();
  </#list>

  // Initialize created meta-data
  ${packageName?uncap_first}.initializePackageContents();
  ASTENodePackage.initializePackageContents();
  <#list service.getSuperCDs() as superCD>
      <#assign identifierName = service.getSimplePackageImplName(superCD)>
    ${identifierName?uncap_first}.initializePackageContents();
  </#list>

  // Mark meta-data to indicate it can't be changed
  //  ${packageName}.freeze();

  // Update the registry and return the package
  org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.put(${packageName}.eNS_URI, ${packageName?uncap_first});
  return ${packageName?uncap_first};