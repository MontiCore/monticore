/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.cd2java.ast;

import static de.se_rwth.commons.Names.getSimpleName;
import groovyjarjarantlr.ANTLRException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.antlr.v4.runtime.RecognitionException;

import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
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
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
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
    
    addEmfCode(cdCompilationUnit, nativeClasses, astHelper);
    
  }

  /**
   * TODO: Write me!
   * @param nativeClasses 
   */
  void addEmfCode(ASTCDCompilationUnit cdCompilationUnit, List<ASTCDClass> nativeClasses, AstEmfGeneratorHelper astHelper) {
    addEFactoryInterface(cdCompilationUnit, astHelper);
    addEFactoryImplementation(cdCompilationUnit, nativeClasses, astHelper);
    addEPackageInterface(cdCompilationUnit, astHelper);
    addEPackageImplementation(cdCompilationUnit, nativeClasses, astHelper);
  }

  /**
   * TODO: Write me!
   */
  void addEFactoryInterface(ASTCDCompilationUnit cdCompilationUnit, AstEmfGeneratorHelper astHelper) throws RecognitionException {
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
      List<ASTCDClass> nativeClasses, AstEmfGeneratorHelper astHelper) throws RecognitionException {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDClass nodeFactoryClass = CD4AnalysisNodeFactory.createASTCDClass();
    String nodeFactoryName = cdDef.getName() + EFACTORY_IMPL;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + nodeFactoryName)) {
      nodeFactoryName += TransformationHelper.GENERATED_CLASS_SUFFIX;
    }
    nodeFactoryClass.setName(nodeFactoryName);
    
    // Add factory-attributes for all ast classes
    Set<String> astClasses = new LinkedHashSet<>();
    nativeClasses.stream().filter(e -> e.getModifier().isPresent())
        .filter(e -> !e.getModifier().get().isAbstract())
        .forEach(e -> astClasses.add(GeneratorHelper.getPlainName(e)));
    
    for ( String clazz : astClasses) {
      String toParse = "protected static " + nodeFactoryName + " factory" + clazz + " = null;";
      cdTransformation.addCdAttributeUsingDefinition(nodeFactoryClass, toParse);
    }
    
    // Add ast-creating methods 
    for ( ASTCDClass clazz : nativeClasses) {
      if (!clazz.getModifier().isPresent() || clazz.getModifier().get().isAbstract()) {
        continue;
      }
      addMethodsToNodeFactory(clazz, nodeFactoryClass, astHelper);
    }
    
    // Add delegating methods for creating of the ast nodes of the super grammars
    List<String> superCds = astHelper.getSuperGrammarCds();
    List<String> imports = new ArrayList<>();
    if (superCds.size() != 0) {
      String testName = superCds.get(0);
      Optional<CDSymbol> superCd = astHelper.resolveCd(testName);
      if (superCd.isPresent()) {
        Log.debug(" CDSymbol for " + nodeFactoryName + " : " + superCd, "CdDecorator");
        nodeFactoryName = getSimpleName(superCd.get().getName()) + NODE_FACTORY;
        String superCdImport = superCd.get().getFullName().toLowerCase()
            + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT + "*";
        imports.add(superCdImport);
        for (CDTypeSymbol type : superCd.get().getTypes()) {
          Optional<ASTNode> node = type.getAstNode();
          if (node.isPresent() && node.get() instanceof ASTCDClass) {
            ASTCDClass cdClass = (ASTCDClass) node.get();
            if (astClasses.contains(cdClass.getName())) {
              continue;
            }
            astClasses.add(cdClass.getName());
            addDelegateMethodsToNodeFactory(cdClass, nodeFactoryClass, astHelper, superCd.get(),
                nodeFactoryName);
          }
        }
      }
    }
    
    cdDef.getCDClasses().add(nodeFactoryClass);
    glex.replaceTemplate("ast.ClassContent", nodeFactoryClass, new TemplateHookPoint(
        "ast_emf.EFactoryImpl", nodeFactoryClass, cdDef.getName(), "http://" + cdDef.getName() + "/1.0"));
    
  }
  
  /**
   * TODO: Write me!
   */
  void addEPackageInterface(ASTCDCompilationUnit cdCompilationUnit, AstEmfGeneratorHelper astHelper) throws RecognitionException {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDInterface packageIntarface = CD4AnalysisNodeFactory.createASTCDInterface();
    String inerfaceName = cdDef.getName() + EPACKAGE;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + inerfaceName)) {
      inerfaceName += TransformationHelper.GENERATED_CLASS_SUFFIX;
    }
    packageIntarface.setName(inerfaceName);
    cdDef.getCDInterfaces().add(packageIntarface);
    glex.replaceTemplate("ast.AstInterfaceContent", packageIntarface, new TemplateHookPoint(
        "ast_emf.EPackage", packageIntarface, cdDef.getName(), "http://" + cdDef.getName() + "/1.0"));
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
      List<ASTCDClass> nativeClasses, AstEmfGeneratorHelper astHelper) throws RecognitionException {
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
    
    // Add factory-attributes for all ast classes
    Set<String> astClasses = new LinkedHashSet<>();
    nativeClasses.stream().filter(e -> e.getModifier().isPresent())
        .filter(e -> !e.getModifier().get().isAbstract())
        .forEach(e -> astClasses.add(GeneratorHelper.getPlainName(e)));
    
    for ( String clazz : astClasses) {
      String toParse = "protected static " + className + " factory" + clazz + " = null;";
      cdTransformation.addCdAttributeUsingDefinition(packageImpl, toParse);
    }
    
    // Add ast-creating methods 
    for ( ASTCDClass clazz : nativeClasses) {
      if (!clazz.getModifier().isPresent() || clazz.getModifier().get().isAbstract()) {
        continue;
      }
      addMethodsToNodeFactory(clazz, packageImpl, astHelper);
    }
    
    // Add delegating methods for creating of the ast nodes of the super grammars
    List<String> superCds = astHelper.getSuperGrammarCds();
    List<String> imports = new ArrayList<>();
    if (superCds.size() != 0) {
      String testName = superCds.get(0);
      Optional<CDSymbol> superCd = astHelper.resolveCd(testName);
      if (superCd.isPresent()) {
        Log.debug(" CDSymbol for " + className + " : " + superCd, "CdDecorator");
        className = getSimpleName(superCd.get().getName()) + NODE_FACTORY;
        String superCdImport = superCd.get().getFullName().toLowerCase()
            + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT + "*";
        imports.add(superCdImport);
        for (CDTypeSymbol type : superCd.get().getTypes()) {
          Optional<ASTNode> node = type.getAstNode();
          if (node.isPresent() && node.get() instanceof ASTCDClass) {
            ASTCDClass cdClass = (ASTCDClass) node.get();
            if (astClasses.contains(cdClass.getName())) {
              continue;
            }
            astClasses.add(cdClass.getName());
            addDelegateMethodsToNodeFactory(cdClass, packageImpl, astHelper, superCd.get(),
                className);
          }
        }
      }
    }
    
    cdDef.getCDClasses().add(packageImpl);
    glex.replaceTemplate("ast.ClassContent", packageImpl, new TemplateHookPoint(
        "ast_emf.EPackageImpl", packageImpl, cdDef.getName()));
    
  }
  
}
