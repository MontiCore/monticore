/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.emf.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.monticore.emf._ast.ASTENodePackage;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class ResourceController {
  private static final ResourceController controller = new ResourceController();
  
  private ResourceSet resourceSet = new ResourceSetImpl();
  
  // Private constructor for Singleton-Pattern
  private ResourceController() {
    // Create a resource set.
  }
  
  public static ResourceController getInstance() {
    return controller;
  }
  
  public void serializeAstToECoreModelFile(EObject eInstance, String name) {
    serializeAstToECoreModelFile(eInstance, name, "");
  }
  
  public void serializeAstToECoreModelFile(EObject eInstance, String name, String path) {
    
    // Register the default resource factory -- only needed for standalone
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
        Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
        
    // For Current Package
    URI fileURI = URI
        .createFileURI(new File(path + name + ".ecore").getAbsolutePath());
    // Create a resource for this file.
    Resource resource = resourceSet.createResource(fileURI);
    // Add instance of package to the contents.
    resource.getContents().add(eInstance);
    
    // For ASTNodePackage
    URI fileURIASTNode = URI.createFileURI(new File(path + "ASTENode.ecore").getAbsolutePath());
    Resource resourceASTNode = resourceSet.createResource(fileURIASTNode);
    resourceASTNode.getContents().add(ASTENodePackage.eINSTANCE);
    
    // For SuperGrammars
    
    // Save the contents of the resources to the file system.
    try {
      resource.save(Collections.EMPTY_MAP);
      resourceASTNode.save(Collections.EMPTY_MAP);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  public void serializeASTClassInstance(EObject object, String fileName) {
    // Register the default resource factory -- only needed for stand-alone!
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    // Get the URI of the model file.
    URI fileURI = URI.createFileURI(new File(fileName + ".xmi").getAbsolutePath());
    // URI fileURI = object.eResource().getURI();
    // Create a resource for this file.
    Resource resource = resourceSet.createResource(fileURI);
    // Add instance of package to the contents.
    resource.getContents().add(object);
    // Save the contents of the resource to the file system.
    Map options = new HashMap();
    options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
    try {
      resource.save(options);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
}
