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

package de.monticore.emf._ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/** The package for the model object ASTENode **/
public class ASTENodePackageImpl extends EPackageImpl implements ASTENodePackage {

  private EEnum constantsASTENodeEEnum = null;
  
  private EClass eNodeEClass = null;
  
  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the
   * package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory
   * method {@link #init init()}, which also performs initialization of the
   * package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   */
  private ASTENodePackageImpl() {
    super(eNS_URI, ASTENodeFactory.eINSTANCE);
  }
  
  private static boolean isInited = false;
  
  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and
   * for any others upon which it depends. This method is used to initialize
   * eInstance when that field is accessed. Clients should not invoke it
   * directly. Instead, they should simply access that field to obtain the
   * package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   */
  public static ASTENodePackage init() {
    if (isInited) {
      return (ASTENodePackage) EPackage.Registry.INSTANCE.getEPackage(ASTENodePackage.eNS_URI);
    }
    // Obtain or create and register package
    ASTENodePackageImpl theASTENodePackage = (ASTENodePackageImpl) (EPackage.Registry.INSTANCE
        .get(eNS_URI) instanceof ASTENodePackageImpl
        ? EPackage.Registry.INSTANCE.get(eNS_URI)
        : new ASTENodePackageImpl());
    
    isInited = true;
    
    // Obtain or create and register interdependencies
    
    // Create package meta-data objects
    theASTENodePackage.createPackageContents();
    
    // Initialize created meta-data
    theASTENodePackage.initializePackageContents();
    
    // Mark meta-data to indicate it can't be changed
   // theASTENodePackage.freeze();
    
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(ASTENodePackage.eNS_URI, theASTENodePackage);
    return theASTENodePackage;
  }
  
  public EEnum getConstantsASTENode() {
    return constantsASTENodeEEnum;
  }
  
  public EClass getENode() {
    return eNodeEClass;
  }
  
  public ASTENodeFactory getASTENodeFactory() {
    return (ASTENodeFactory) getEFactoryInstance();
  }
  
  private boolean isCreated = false;
  
  /**
   * Creates the meta-model objects for the package. This method is guarded to
   * have no affect on any invocation but its first.
   */
  public void createPackageContents() {
    if (isCreated) {
      return;
    }
    isCreated = true;
    // Create classes and their features
    constantsASTENodeEEnum = createEEnum(CONSTANTSASTENODE);

    eNodeEClass = createEClass(ENODE);
    
  }
  
  private boolean isInitialized = false;
  
  /**
   * Complete the initialization of the package and its meta-model. This method
   * is guarded to have no affect on any invocation but its first.
   */
  public void initializePackageContents() {
    if (isInitialized) {
      return;
    }
    isInitialized = true;
    
    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);
    
    // Initialize classes and features; add operations and parameters
    
    // Initialize enums and add enum literals
    initEEnum(constantsASTENodeEEnum, ASTENodeLiterals.class, "ASTENodeLiterals");

    addEEnumLiteral(constantsASTENodeEEnum, ASTENodeLiterals.DEFAULT);
    
    initEClass(eNodeEClass, ASTENode.class, "ENode", IS_ABSTRACT, IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    
    // Create resource
    createResource(eNS_URI);
    
  }
  
  /**
   * @see de.monticore.emf._ast.ASTEPackage#getASTESuperPackages()
   */
  @Override
  public List<ASTEPackage> getASTESuperPackages() {
    return new ArrayList<>();
  }

  /**
   * @see de.monticore.emf._ast.ASTEPackage#getPackageName()
   */
  @Override
  public String getPackageName() {
    return "de.monticore.emf";
  }
  
}
