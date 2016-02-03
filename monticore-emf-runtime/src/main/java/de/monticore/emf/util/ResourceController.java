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
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.monticore.emf._ast.ASTENode;
import de.monticore.emf._ast.ASTEPackage;
import de.se_rwth.commons.Names;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class ResourceController {
  private static final ResourceController controller = new ResourceController();
  
  public static final String EMF_TEST_OUTPUT_MODELS = "target/generated-test-sources/emf/models/";
  
  public static final String EMF_TEST_OUTPUT_MODELINSTANCES = "target/generated-test-sources/emf/modelinstances/";
  
  private ResourceSet resourceSet;
  
  // Private constructor for Singleton-Pattern
  private ResourceController() {
    resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
        Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
  }
  
  public static ResourceController getInstance() {
    return controller;
  }
  
  public void serializeAST(ASTEPackage eInstance) throws IOException {
    createModelFile(eInstance, createResource(eInstance));
  }
  
  public void serializeASTInstance(ASTENode astNode, String instanceName) throws IOException {
    // Get the URI of the model file.
    System.err.println(
        " ECLASS " + astNode.eClass().getName() + " hh " + astNode.eClass().getEPackage().getName()
            + " ns " + astNode.eClass().getEPackage().getNsURI());
            
    serializeASTIfNotExists((ASTEPackage)astNode.eClass().getEPackage());
    
    String packageName = astNode.eClass().getEPackage().getName().toLowerCase() + "/";
    String fileName = astNode.eClass().getName() + "_" + instanceName;
    URI fileURI = URI
        .createFileURI(new File(EMF_TEST_OUTPUT_MODELINSTANCES + packageName + fileName + ".xmi")
            .getAbsolutePath());
    // Create a resource for this file.
    Resource resource = resourceSet.createResource(fileURI);
    // Add instance of package to the contents.
    resource.getContents().add(astNode);
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
  
  public void serializeASTIfNotExists(ASTEPackage eInstance) throws IOException {
    // Create a resource for this file.
    Optional<Resource> resource = createResourceIfNotExists(eInstance);
    if (resource.isPresent()) {
      createModelFile(eInstance, resource.get());
    }
  }
  
  private Resource createResource(EPackage ePackage) {
    String packageName = Names.getPathFromPackage(Names
        .getQualifier(Names.getQualifier(ePackage.getEFactoryInstance().getClass().getName())));
    String simpleName = ePackage.getEFactoryInstance().getClass().getSimpleName();
    URI fileURI = URI
        .createFileURI(new File(EMF_TEST_OUTPUT_MODELS + packageName + "/" + simpleName + ".ecore")
            .getAbsolutePath());
    // Create a resource for this file.
    return resourceSet.createResource(fileURI);
  }
  
  private Optional<Resource> createResourceIfNotExists(EPackage ePackage) {
    String packageName = Names.getPathFromPackage(Names
        .getQualifier(Names.getQualifier(ePackage.getEFactoryInstance().getClass().getName())));
    String simpleName = ePackage.getEFactoryInstance().getClass().getSimpleName();
    URI fileURI = URI
        .createFileURI(new File(EMF_TEST_OUTPUT_MODELS + packageName + "/" + simpleName + ".ecore")
            .getAbsolutePath());
    // Create a resource for this file if doesn't exist
    if (resourceSet.getResource(fileURI, false) == null) {
      return Optional.of(resourceSet.createResource(fileURI));
    }
    return Optional.empty();
  }
  
  private void createModelFile(ASTEPackage eInstance, Resource resource) throws IOException {
    // Add instance of package to the contents.
    resource.getContents().add(eInstance);
    
    for (ASTEPackage superPackage : eInstance.getASTESuperPackages()) {
      System.err.println(superPackage + " for " + eInstance.getName());
      System.err.println(" pack " +superPackage.getName() + "   "
          + superPackage.getClass().getName());
      serializeASTIfNotExists(superPackage);
    }

    // Save the contents of the resources to the file system.
    resource.save(Collections.EMPTY_MAP);
  }
  
}

