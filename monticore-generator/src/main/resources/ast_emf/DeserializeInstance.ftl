<#if (op.callCalculator("mc.codegen.ast.files.IsFileInstanceOfClassDefCalculator") && !ast.isNoastclass()) && op.callCalculator("mc.codegen.ast.files.NameCalculator")>
    public ${fileName} deserialize${fileName} () {
       return deserialize${fileName} ("${fileName}");
    }
        
    public ${fileName} deserialize${fileName} (String fileName) {
        // Initialize the model
        ${ePackageInterfaceName}.eINSTANCE.eClass();

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl()); 
        URI fileURI = URI.createFileURI(new File(fileName + ".xmi").getAbsolutePath());
        Resource resource = resourceSet.getResource(fileURI, true);
        return (${fileName}) resource.getContents().get(0);        
    }
</#if>
<#if (op.callCalculator("mc.codegen.ast.files.IsFileInstanceOfInterfaceCalculator") && !ast.isNoastclass()) && op.callCalculator("mc.codegen.ast.files.NameCalculator")>
    public ${fileName} deserialize${fileName} () {
       return deserialize${fileName} ("${fileName}");
    }
    
    public ${fileName} deserialize${fileName} (String fileName) {
        // Initialize the model
        ${ePackageInterfaceName}.eINSTANCE.eClass();

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl()); 
        URI fileURI = URI.createFileURI(new File(fileName + ".xmi").getAbsolutePath());
        Resource resource = resourceSet.getResource(fileURI, true);
        return (${fileName}) resource.getContents().get(0);        
    }
</#if>