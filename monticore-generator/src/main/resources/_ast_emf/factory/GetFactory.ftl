${tc.signature("factoryClassName", "grammarName")}
try {
  ${factoryClassName} eFactory = (${factoryClassName})org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEFactory("http://${grammarName}/1.0");
  if (eFactory != null) {
    return eFactory;
  }
}
catch (Exception exception) {
  org.eclipse.emf.ecore.plugin.EcorePlugin.INSTANCE.log(exception);
}
if (factory == null) {
  factory = new ${factoryClassName}();
}
return factory;
