/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.emf.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
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
public class AST2ModelFiles {
  
  public static final String EMF_TEST_OUTPUT_MODELS = "target/generated-test-sources/emf/models/";
  
  public static final String EMF_TEST_OUTPUT_MODELINSTANCES = "target/generated-test-sources/emf/modelinstances/";
  
  private static final AST2ModelFiles instance = new AST2ModelFiles();
  
  private ResourceSet resourceSet;
  
  // Private constructor for Singleton-Pattern
  private AST2ModelFiles() {
    resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
        Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
  }
  
  public static AST2ModelFiles get() {
    return instance;
  }
  
  public void serializeAST(ASTEPackage eInstance) throws IOException {
    createModelFile(eInstance, createResource(eInstance));
  }
  
  public void serializeASTInstance(ASTENode astNode, String instanceName) throws IOException {
    serializeASTInstance(astNode, astNode.eClass().getName(), instanceName);
  }
  
  public void serializeASTInstance(ASTENode astNode, String modelName, String instanceName) throws IOException {
    // Get the URI of the model file.
    serializeASTIfNotExists((ASTEPackage) astNode.eClass().getEPackage());
    
    String packageName = astNode.eClass().getEPackage().getName().toLowerCase() + File.separator;
    String fileName = modelName + "_" + instanceName + ".xmi";
    URI fileURI = URI
        .createFileURI(new File(EMF_TEST_OUTPUT_MODELINSTANCES + packageName + fileName)
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
  
  public EObject deserializeASTInstance(String fileName, ASTEPackage eInstance) {
    String packageName = EMF_TEST_OUTPUT_MODELINSTANCES + eInstance.getName().toLowerCase() + File.separator;
    return deserializeASTInstance(fileName, packageName, eInstance);
  }
  
  public EObject deserializeASTInstance(String fileName, String packageName, ASTEPackage eInstance) {
    // Initialize the model
    eInstance.eClass();
    
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    
    URI fileURI = URI
        .createFileURI(new File(packageName + fileName + ".xmi")
            .getAbsolutePath());
    
    Resource resource = resourceSet.getResource(fileURI, true);
    return resource.getContents().get(0);
  }
  
  private Resource createResource(ASTEPackage ePackage) {
    URI fileURI = URI
        .createFileURI(
            new File(EMF_TEST_OUTPUT_MODELS + Names.getPathFromPackage(ePackage.getPackageName())
                + "/" + ePackage.getName() + ".ecore")
                    .getAbsolutePath());
    // Create a resource for this file.
    return resourceSet.createResource(fileURI);
  }
  
  private Optional<Resource> createResourceIfNotExists(ASTEPackage ePackage) {
    URI fileURI = URI
        .createFileURI(
            new File(EMF_TEST_OUTPUT_MODELS + Names.getPathFromPackage(ePackage.getPackageName())
                + File.separator + ePackage.getName() + ".ecore")
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
      serializeASTIfNotExists(superPackage);
    }
    
    // Save the contents of the resources to the file system.
    resource.save(Collections.EMPTY_MAP);
  }
  
}
