<#if (op.callCalculator("mc.codegen.ast.files.IsFileInstanceOfClassDefCalculator") && !ast.isNoastclass()) && op.callCalculator("mc.codegen.ast.files.NameCalculator")>    
    public void serializeASTClassInstance (${fileName} object) {
        serializeASTClassInstance(object, "${fileName}");
    }
    
    
    public void serializeASTClassInstance (${fileName} object, String fileName) {
         // Create a resource set. 
        ResourceSet resourceSet = new ResourceSetImpl(); 
        // Register the default resource factory -- only needed for stand-alone! 
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl()); 
        // Get the URI of the model file. 
        URI fileURI = URI.createFileURI(new File(fileName + ".xmi").getAbsolutePath()); 
        // Create a resource for this file. 
        Resource resource = resourceSet.createResource(fileURI);
         // Add instance of package to the contents. 
        resource.getContents().add(object);
        // Save the contents of the resource to the file system. 
        try { resource.save(Collections.EMPTY_MAP); } catch (IOException e) {e.printStackTrace();}
    }
</#if>
<#if (op.callCalculator("mc.codegen.ast.files.IsFileInstanceOfInterfaceCalculator") && !ast.isNoastclass()) && op.callCalculator("mc.codegen.ast.files.NameCalculator")>    
    public void serializeASTClassInstance (${fileName} object) {
        serializeASTClassInstance(object, "${fileName}");
    }
    
    
    public void serializeASTClassInstance (${fileName} object, String fileName) {
         // Create a resource set. 
        ResourceSet resourceSet = new ResourceSetImpl(); 
        // Register the default resource factory -- only needed for stand-alone! 
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl()); 
        // Get the URI of the model file. 
        URI fileURI = URI.createFileURI(new File(fileName + ".xmi").getAbsolutePath()); 
        // Create a resource for this file. 
        Resource resource = resourceSet.createResource(fileURI);
         // Add instance of package to the contents. 
        resource.getContents().add(object);
        // Save the contents of the resource to the file system. 
        try { resource.save(Collections.EMPTY_MAP); } catch (IOException e) {e.printStackTrace();}
    }
</#if>