/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.cd2java.ast;

import static de.monticore.codegen.GeneratorHelper.getPlainName;
import groovyjarjarantlr.ANTLRException;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.RecognitionException;

import com.google.common.collect.Lists;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast_emf.AstEmfGeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class CdEmfDecorator extends CdDecorator {
  
  public static final String EFACTORY = "Factory";
  
  public static final String EFACTORY_IMPL = "FactoryImpl";
  
  public static final String EPACKAGE = "Package";
  
  public static final String EPACKAGE_IMPL = "PackageImpl";
  
  public CdEmfDecorator(
      GlobalExtensionManagement glex,
      GlobalScope symbolTable,
      IterablePath targetPath) {
    super(glex, symbolTable, targetPath);
  }
  
  public void decorate(ASTCDCompilationUnit cdCompilationUnit) {
    AstEmfGeneratorHelper astHelper = new AstEmfGeneratorHelper(cdCompilationUnit, symbolTable);
    ASTCDDefinition cdDefinition = cdCompilationUnit.getCDDefinition();
    
    List<ASTCDClass> nativeClasses = Lists.newArrayList(cdDefinition.getCDClasses());
    
    List<ASTCDClass> astNotAbstractClasses =
        cdDefinition.getCDClasses().stream().filter(e -> e.getModifier().isPresent())
            .filter(e -> !e.getModifier().get().isAbstract())
            .collect(Collectors.toList());
    // .forEach(e ->
    // astNotAbstractClasses.add(GeneratorHelper.getPlainName(e)));
    
    List<ASTCDClass> astNotListClasses = astNotAbstractClasses.stream()
        .filter(e -> !astHelper.isAstListClass(e))
        .collect(Collectors.toList());
    // .forEach(e -> astNoListsClasses.add(GeneratorHelper.getPlainName(e)));
    
    // Run over classdiagramm and converts cd types to mc-java types
    new Cd2JavaTypeConverter(astHelper).handle(cdDefinition);
    
    // Interface for all ast nodes of the language
    decorateBaseInterface(cdDefinition);
    
    // Decorate with builder pattern
    addBuilders(cdDefinition, astHelper);
    
    addNodeFactoryClass(cdCompilationUnit, nativeClasses, astHelper);
    
    // Check if handwritten ast types exist
    transformCdTypeNamesForHWTypes(cdCompilationUnit);
    
    cdDefinition.getCDClasses().stream().filter(c -> !astHelper.isAstListClass(c))
        .forEach(c -> addSuperInterfaces(c));
    
    // Decorate with additional methods and attributes
    for (ASTCDClass clazz : nativeClasses) {
      addConstructors(clazz, astHelper);
      addAdditionalMethods(clazz, astHelper);
      addAdditionalAttributes(clazz, astHelper);
      addGetter(clazz, astHelper);
      addSetter(clazz, astHelper);
      addSymbolGetter(clazz, astHelper);
    }
    
    for (ASTCDInterface interf : cdDefinition.getCDInterfaces()) {
      addGetter(interf);
    }
    
    // Decorate list classes
    cdDefinition.getCDClasses().stream().filter(c -> astHelper.isAstListClass(c))
        .forEach(c -> decorateAstListClass(c));
    
    // Add ASTConstant class
    addConstantsClass(cdDefinition, astHelper);
    
    // Additional imports
    cdCompilationUnit.getImportStatements().add(
        ASTImportStatement
            .getBuilder()
            .importList(
                Lists.newArrayList(VisitorGeneratorHelper.getQualifiedVisitorType(astHelper
                    .getPackageName(), cdDefinition.getName()))).build());
    
    addEmfCode(cdCompilationUnit, astNotListClasses, astHelper);
    
  }
  
  /**
   * TODO: Write me!
   * 
   * @param astClasses
   */
  void addEmfCode(ASTCDCompilationUnit cdCompilationUnit, List<ASTCDClass> astClasses,
      AstEmfGeneratorHelper astHelper) {
    addEFactoryInterface(cdCompilationUnit, astHelper);
    addEFactoryImplementation(cdCompilationUnit, astClasses, astHelper);
    addEPackageInterface(cdCompilationUnit, astClasses, astHelper);
    addEPackageImplementation(cdCompilationUnit, astClasses, astHelper);
  }
  
  /**
   * TODO: Write me!
   */
  void addEFactoryInterface(ASTCDCompilationUnit cdCompilationUnit, AstEmfGeneratorHelper astHelper)
      throws RecognitionException {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDInterface factory = CD4AnalysisNodeFactory.createASTCDInterface();
    String factoryName = cdDef.getName() + EFACTORY;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + factoryName)) {
      factoryName += TransformationHelper.GENERATED_CLASS_SUFFIX;
    }
    factory.setName(factoryName);
    cdDef.getCDInterfaces().add(factory);
    
    glex.replaceTemplate("ast.AstInterfaceContent", factory, new TemplateHookPoint(
        "ast_emf.EFactory", factory, cdDef.getName()));
  }
  
  /**
   * TODO: Write me!
   * 
   * @param cdCompilationUnit
   * @param nativeClasses
   * @param astHelper
   * @throws ANTLRException
   */
  void addEFactoryImplementation(ASTCDCompilationUnit cdCompilationUnit,
      List<ASTCDClass> astClasses, AstEmfGeneratorHelper astHelper) throws RecognitionException {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDClass factoryClass = CD4AnalysisNodeFactory.createASTCDClass();
    String factoryClassName = cdDef.getName() + EFACTORY_IMPL;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + factoryClassName)) {
      factoryClassName += TransformationHelper.GENERATED_CLASS_SUFFIX;
    }
    factoryClass.setName(factoryClassName);
    
    List<String> classNames = astClasses.stream().map(e -> getPlainName(e))
        .collect(Collectors.toList());
    
    cdDef.getCDClasses().add(factoryClass);
    glex.replaceTemplate("ast.ClassContent", factoryClass, new TemplateHookPoint(
        "ast_emf.EFactoryImpl", factoryClass, cdDef.getName(), "http://" + cdDef.getName()
            + "/1.0", classNames));
    
  }
  
  /**
   * TODO: Write me!
   */
  void addEPackageInterface(ASTCDCompilationUnit cdCompilationUnit, List<ASTCDClass> astClasses,
      AstEmfGeneratorHelper astHelper)
      throws RecognitionException {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDInterface packageInterface = CD4AnalysisNodeFactory.createASTCDInterface();
    String interfaceName = cdDef.getName() + EPACKAGE;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + interfaceName)) {
      interfaceName += TransformationHelper.GENERATED_CLASS_SUFFIX;
    }
    packageInterface.setName(interfaceName);
    cdDef.getCDInterfaces().add(packageInterface);
    
    for (ASTCDClass clazz : astClasses) {
      for (int i = 0; i < clazz.getCDAttributes().size(); i++) {
        String toParseAttr = "int " + getPlainName(clazz).toUpperCase() + "__"
            + clazz.getCDAttributes().get(i).getName().toUpperCase() + " = " + i + ";";
        cdTransformation.addCdAttributeUsingDefinition(packageInterface, toParseAttr);
      }
    }
    
    List<String> classNames = astClasses.stream().map(e -> getPlainName(e))
        .collect(Collectors.toList());
    
    glex.replaceTemplate("ast.AstInterfaceContent", packageInterface,
        new TemplateHookPoint(
            "ast_emf.EPackage", packageInterface, cdDef.getName(), "http://" + cdDef.getName()
                + "/1.0", classNames));
  }
  
  /**
   * TODO: Write me!
   * 
   * @param cdCompilationUnit
   * @param nativeClasses
   * @param astHelper
   * @throws ANTLRException
   */
  void addEPackageImplementation(ASTCDCompilationUnit cdCompilationUnit,
      List<ASTCDClass> astClasses, AstEmfGeneratorHelper astHelper) throws RecognitionException {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDClass packageImpl = CD4AnalysisNodeFactory.createASTCDClass();
    String className = cdDef.getName() + EPACKAGE_IMPL;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + className)) {
      className += TransformationHelper.GENERATED_CLASS_SUFFIX;
    }
    packageImpl.setName(className);
    
    List<String> classNames = astClasses.stream().map(e -> getPlainName(e))
        .collect(Collectors.toList());
    
    for (String clazz : classNames) {
      String toParse = "protected static " + className + " factory" + clazz + " = null;";
      cdTransformation.addCdAttributeUsingDefinition(packageImpl, toParse);
    }
    
    cdDef.getCDClasses().add(packageImpl);
    glex.replaceTemplate("ast.ClassContent", packageImpl, new TemplateHookPoint(
        "ast_emf.EPackageImpl", packageImpl, cdDef.getName(), classNames));
    
  }
  
}
