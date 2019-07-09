${tc.signature("packageName")}
<#assign service = glex.getGlobalVar("service")>

  if (isInited) {
  return (${packageName})org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(${packageName}.eNS_URI);
  }

  // Obtain or create and register package
  ${packageName} the${packageName} = (${packageName})(org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ${packageName}
      ? org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.get(eNS_URI) : new ${packageName}());

  isInited = true;

  // Obtain or create and register interdependencies
  ASTENodePackageImpl theASTENodePackage = (ASTENodePackageImpl) (org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(ASTENodePackage.eNS_URI)  instanceof ASTENodePackage ?
  org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(ASTENodePackage.eNS_URI) : ASTENodePackage.eINSTANCE);

  <#list service.getSuperCDs() as superCD>
      <#assign qualifiedName = service.getQualifiedPackageImplName(superCD)>
      <#assign identifierName = service.getSimplePackageImplName(superCD)>
      ${qualifiedName}Impl ${identifierName?uncap_first} =
    (${qualifiedName}Impl)(org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(
      ${qualifiedName}.eNS_URI) instanceof ${qualifiedName}?
      org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(${qualifiedName}.eNS_URI) :
      ${qualifiedName}.eINSTANCE);
  </#list>

  // Create package meta-data objects
  the${packageName}.createPackageContents();
  theASTENodePackage.createPackageContents();
  <#list service.getSuperCDs() as superCD>
      <#assign identifierName = service.getSimplePackageImplName(superCD)>
    ${identifierName?uncap_first}.createPackageContents();
  </#list>

  // Initialize created meta-data
  the${packageName}.initializePackageContents();
  theASTENodePackage.initializePackageContents();
  <#list service.getSuperCDs() as superCD>
      <#assign identifierName = service.getSimplePackageImplName(superCD)>
    the${identifierName?uncap_first}.initializePackageContents();
  </#list>

  // Mark meta-data to indicate it can't be changed
  //  the${packageName}.freeze();

  // Update the registry and return the package
  org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.put(${packageName}.eNS_URI, the${packageName});
  return the${packageName};
}