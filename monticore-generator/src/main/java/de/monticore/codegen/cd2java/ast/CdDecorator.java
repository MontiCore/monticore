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

package de.monticore.codegen.cd2java.ast;

import static com.google.common.collect.Lists.newArrayList;
import static de.se_rwth.commons.Names.getQualifier;
import static de.se_rwth.commons.Names.getSimpleName;
import groovyjarjarantlr.ANTLRException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.RecognitionException;

import transformation.ast.ASTCDTransformation;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.manipul.BaseInterfaceAddingManipulation;
import de.monticore.codegen.mc2cd.transl.ConstantsTranslation;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.umlcd4a.CD4AnalysisHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisInheritanceVisitor;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Decorates class diagrams by adding of new classes and methods using in ast
 * files
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class CdDecorator {
  
  public static final String DIR_REPORTS = "_reports";
  
  public static final String THIS_PREFIX = String.format("%n%15cthis.", ' ');
  
  /**
   * The qualified name of the empty method body template
   */
  public static final String EMPTY_BODY_TEMPLATE = "ast.EmptyMethodBody";
  
  public static final String NODE_FACTORY = "NodeFactory";
  
  private static final String DEL = ", ";
  
  private ASTCDTransformation cdTransformation = new ASTCDTransformation();
  
  private GlobalExtensionManagement glex;
  
  private IterablePath targetPath;
  
  private GlobalScope symbolTable;
  
  public CdDecorator(
      GlobalExtensionManagement glex,
      GlobalScope symbolTable,
      IterablePath targetPath) {
    this.glex = glex;
    this.symbolTable = symbolTable;
    this.targetPath = targetPath;
  }
  
  public void decorate(ASTCDCompilationUnit cdCompilationUnit) {
    Preconditions.checkArgument(cdCompilationUnit.getCDDefinition() != null);
    
    AstGeneratorHelper astHelper = new AstGeneratorHelper(cdCompilationUnit, symbolTable);
    
    ASTCDDefinition cdDefinition = cdCompilationUnit.getCDDefinition();
    
    cdCompilationUnit.getImportStatements().add(
        ASTImportStatement
            .getBuilder()
            .importList(
                Lists.newArrayList(VisitorGeneratorHelper.getQualifiedVisitorType(astHelper
                    .getPackageName(), cdDefinition.getName()))).build());
    
    try {
      
      List<ASTCDClass> nativeClasses = Lists.newArrayList(cdDefinition.getCDClasses());

      // Run over classdiagramm and converts cd types to mc-java types
      Cd2JavaTypeConverter visitor = new Cd2JavaTypeConverter(astHelper);
      visitor.handle(cdDefinition);
      
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
        addAdditionalMethods(clazz, astHelper);
        addConstructors(clazz, astHelper);
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
      
    }
    catch (IOException e) {
      Throwables.propagate(e);
    }
    
  }
  
  void addSymbolGetter(ASTCDClass clazz, AstGeneratorHelper astHelper) {
    if (!astHelper.isAstListClass(clazz)) {
      List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributes());
      for (ASTCDAttribute attribute : attributes) {
        if (GeneratorHelper.isInherited(attribute)
            || !CD4AnalysisHelper.hasStereotype(attribute,
                MC2CDStereotypes.REFERENCED_SYMBOL.toString())) {
          continue;
        }
        // TODO PN use a general helper function
        // TODO PN use import statements instead of qualified names
        String referencedSymbol = CD4AnalysisHelper.getStereotypeValues(attribute,
            MC2CDStereotypes.REFERENCED_SYMBOL.toString()).get(0);
        
        if (!getQualifier(referencedSymbol).isEmpty()) {
          referencedSymbol =
              SymbolTableGeneratorHelper.getQualifiedSymbolType(getQualifier(referencedSymbol)
                  .toLowerCase(), getSimpleName(referencedSymbol));
        }
        
        String returnType;
        String nameSuffix;
        if (astHelper.isAstList(attribute)) {
          returnType = "java.util.Collection<" + referencedSymbol + ">";
          nameSuffix = "Symbols";
        }
        else {
          returnType = "Optional<" + referencedSymbol + ">";
          nameSuffix = "Symbol";
        }
        
        // TODO PN handle both from:Name@Foo and from:QualifiedName@Foo
        
        String toParse = "public " + returnType + " "
            + GeneratorHelper.getPlainGetter(attribute) + nameSuffix + "() ;";
        HookPoint getMethodBody = new TemplateHookPoint(
            "ast.additionalmethods.GetReferencedSymbol", clazz,
            attribute.getName(), referencedSymbol);
        replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
      }
    }
  }
  
  /**
   * Adds common ast methods to the all classes in the class diagram
   * 
   * @param clazz2 - each entry contains a class diagram class and a respective
   * builder class
   * @param astHelper
   * @throws ANTLRException
   */
  void addAdditionalMethods(ASTCDClass clazz,
      AstGeneratorHelper astHelper) throws RecognitionException, IOException {
    if (astHelper.isAstClass(clazz)) {
      AstAdditionalMethods additionalMethod = AstAdditionalMethods.accept;
      String visitorTypeFQN = VisitorGeneratorHelper.getQualifiedVisitorType(
          astHelper.getPackageName(), astHelper.getCdName());
      String methodSignatur = String.format(additionalMethod.getDeclaration(), visitorTypeFQN);
      replaceMethodBodyTemplate(clazz, methodSignatur, new TemplateHookPoint(
          "ast.additionalmethods.Accept"));
      
      // node needs to accept visitors from all languages that it uses nodes of.
      for (String usedSupergrammarFQN : astHelper.getSuperGrammarCdsUsed(clazz)) {
        String superGrammarName = Names.getSimpleName(usedSupergrammarFQN);
        String visitorType = superGrammarName + "Visitor";
        String visitorPackage = VisitorGeneratorHelper.getVisitorPackage(usedSupergrammarFQN);
        
        additionalMethod = AstAdditionalMethods.accept;
        String superVisitorTypeFQN = visitorPackage + "." + visitorType;
        methodSignatur = String.format(additionalMethod.getDeclaration(), superVisitorTypeFQN);
        replaceMethodBodyTemplate(clazz, methodSignatur, new TemplateHookPoint(
            "ast.additionalmethods.AcceptSuper", clazz, astHelper.getQualifiedCdName(),
            visitorTypeFQN, superVisitorTypeFQN));
      }
    }
    if (!astHelper.isAstListClass(clazz)) {
      
      Optional<ASTModifier> modifier = clazz.getModifier();
      String plainClassName = GeneratorHelper.getPlainName(clazz);
      
      replaceMethodBodyTemplate(clazz, AstAdditionalMethods.deepEquals.getDeclaration(),
          new TemplateHookPoint("ast.additionalmethods.DeepEquals"));
      
      replaceMethodBodyTemplate(clazz, AstAdditionalMethods.deepEqualsWithOrder.getDeclaration(),
          new StringHookPoint("return deepEquals(o);\n"));
      
      replaceMethodBodyTemplate(clazz,
          AstAdditionalMethods.deepEqualsWithComments.getDeclaration(),
          new TemplateHookPoint("ast.additionalmethods.DeepEqualsWithComments"));
      
      replaceMethodBodyTemplate(clazz,
          AstAdditionalMethods.deepEqualsWithCommentsWithOrder.getDeclaration(),
          new StringHookPoint("return deepEqualsWithComments(o);\n"));
      
      replaceMethodBodyTemplate(clazz, AstAdditionalMethods.equalAttributes.getDeclaration(),
          new TemplateHookPoint("ast.additionalmethods.EqualAttributes"));
      
      replaceMethodBodyTemplate(clazz, AstAdditionalMethods.equalsWithComments.getDeclaration(),
          new TemplateHookPoint("ast.additionalmethods.EqualsWithComments"));
      
      replaceMethodBodyTemplate(clazz, AstAdditionalMethods.get_Children.getDeclaration(),
          new TemplateHookPoint("ast.additionalmethods.GetChildren"));
      
      replaceMethodBodyTemplate(clazz, AstAdditionalMethods.remove_Child.getDeclaration(),
          new TemplateHookPoint("ast.additionalmethods.RemoveChild"));
      
      replaceMethodBodyTemplate(clazz, AstAdditionalMethods.getBuilder.getDeclaration(),
          new StringHookPoint("return new Builder();\n"));
      
      String stringToParse = String.format(AstAdditionalMethods.deepClone.getDeclaration(),
          plainClassName);
      replaceMethodBodyTemplate(clazz, stringToParse,
          new StringHookPoint("return deepClone(_construct());\n"));
      
      stringToParse = String.format(AstAdditionalMethods.deepCloneWithParameters.getDeclaration(),
          plainClassName, plainClassName);
      replaceMethodBodyTemplate(clazz, stringToParse,
          new TemplateHookPoint("ast.additionalmethods.DeepCloneWithParameters"));
      
      if (modifier.isPresent() && modifier.get().isAbstract()) {
        stringToParse = String.format(AstAdditionalMethods._construct.getDeclaration(), "abstract "
            + plainClassName);
        cdTransformation.addCdMethodUsingDefinition(clazz, stringToParse);
      }
      else {
        stringToParse = String.format(AstAdditionalMethods._construct.getDeclaration(),
            plainClassName);
        replaceMethodBodyTemplate(clazz, stringToParse,
            new StringHookPoint("return new " + plainClassName + "();\n"));
      }
    }
  }
  
  /**
   * Decorates class diagram with builder pattern for all classes excepting
   * lists
   * 
   * @param cdDefinition
   * @param astHelper
   * @throws ANTLRException
   */
  void addBuilders(ASTCDDefinition cdDefinition, AstGeneratorHelper astHelper)
      throws RecognitionException, IOException {
    newArrayList(cdDefinition.getCDClasses()).stream()
        .filter(c -> !astHelper.isAstListClass(c))
        .forEach(c -> cdTransformation.addCdClassUsingDefinition(cdDefinition,
            "public static class " + AstGeneratorHelper.getNameOfBuilderClass(c) + " ;"));
  }
  
  
  void addSuperInterfaces(ASTCDClass clazz) {
    String interfaces = clazz.printInterfaces();
    if (!interfaces.isEmpty()) {
      glex.replaceTemplate("ast.AstSuperInterfaces", clazz,
          new StringHookPoint(interfaces + DEL));
    }
  }
  
  /**
   * TODO: Write me!
   * 
   * @param clazz
   * @param astHelper
   * @throws ANTLRException
   */
  void addAdditionalAttributes(
      ASTCDClass clazz, AstGeneratorHelper astHelper) {
    if (!astHelper.isAstListClass(clazz)) {
      // Add Symbol attribute
      cdTransformation.addCdAttributeUsingDefinition(clazz,
          AstAdditionalAttributes.symbol.getDeclaration());
      // Add Scope attribute
      cdTransformation.addCdAttributeUsingDefinition(clazz,
          AstAdditionalAttributes.enclosingScope.getDeclaration());
    }
    
  }
  
  /**
   * Adds getter for all attributes of ast classes
   * 
   * @param interf
   * @param astHelper
   * @throws ANTLRException
   */
  void addGetter(ASTCDClass clazz, AstGeneratorHelper astHelper)
      throws RecognitionException, IOException {
    if (!astHelper.isAstListClass(clazz)) {
      List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributes());
      // attributes.addAll(astHelper.getAttributesOfExtendedInterfaces(clazz));
      for (ASTCDAttribute attribute : attributes) {
        if (GeneratorHelper.isInherited(attribute)) {
          continue;
        }
        String toParse = "public " + TypesPrinter.printType(attribute.getType()) + " "
            + GeneratorHelper.getPlainGetter(attribute) + "() ;";
        HookPoint getMethodBody = new TemplateHookPoint("ast.additionalmethods.Get", clazz,
            attribute.getName());
        replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
      }
    }
  }
  
  /**
   * TODO: Write me!
   * 
   * @param interf
   * @param astHelper
   * @param glex
   */
  void addGetter(ASTCDInterface interf) throws RecognitionException,
      IOException {
    for (ASTCDAttribute attribute : interf.getCDAttributes()) {
      if (GeneratorHelper.isInherited(attribute)) {
        continue;
      }
      String toParse = "public " + TypesPrinter.printType(attribute.getType()) + " "
          + GeneratorHelper.getPlainGetter(attribute) + "();";
      cdTransformation.addCdMethodUsingDefinition(interf, toParse);
    }
  }
  
  /**
   * Adds getter for all attributes of ast classes
   * 
   * @param clazz
   * @param astHelper
   * @throws ANTLRException
   */
  void addSetter(ASTCDClass clazz, AstGeneratorHelper astHelper)
      throws RecognitionException, IOException {
    if (!astHelper.isAstListClass(clazz)) {
      for (ASTCDAttribute attribute : clazz.getCDAttributes()) {
        if (GeneratorHelper.isInherited(attribute)) {
          continue;
        }
        String attributeName = attribute.getName();
        boolean isOptional = GeneratorHelper.isOptional(attribute);
        String typeName = TypesHelper.printSimpleRefType(attribute.getType());
        String toParse = "public void " + GeneratorHelper.getPlainSetter(attribute) + "("
            + typeName + " " + attributeName + ") ;";
        HookPoint methodBody = new TemplateHookPoint("ast.additionalmethods.Set", clazz,
            attribute, attributeName);
        ASTCDMethod setMethod = replaceMethodBodyTemplate(clazz, toParse, methodBody);
        
        if (isOptional) {
          glex.replaceTemplate("ast.ErrorIfNull", setMethod, new StringHookPoint(""));
        }
        
        if (isOptional) {
          toParse = "public boolean " + attributeName + "IsPresent() ;";
          methodBody = new StringHookPoint("  return " + attributeName + ".isPresent(); \n");
          replaceMethodBodyTemplate(clazz, toParse, methodBody);
        }
      }
    }
  }
  
  /**
   * TODO: Write me!
   * 
   * @param cdCompilationUnit
   * @param nativeClasses
   * @param astHelper
   * @throws ANTLRException
   */
  void addNodeFactoryClass(ASTCDCompilationUnit cdCompilationUnit,
      List<ASTCDClass> nativeClasses, AstGeneratorHelper astHelper) throws RecognitionException, IOException {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDClass nodeFactoryClass = CD4AnalysisNodeFactory.createASTCDClass();
    String nodeFactoryName = cdDef.getName() + NODE_FACTORY;
    
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
        "ast.AstNodeFactory", nodeFactoryClass, imports));
    
  }
  
  /**
   * TODO: Write me!
   * 
   * @param astHelper
   * @param cdClass
   * @param nodeFactoryForSuperCd
   */
  void addMethodsToNodeFactory(ASTCDClass clazz, ASTCDClass nodeFactoryClass, AstGeneratorHelper astHelper) {
    if (!clazz.getModifier().isPresent() || clazz.getModifier().get().isAbstract()) {
      return;
    }
    String className = GeneratorHelper.getPlainName(clazz);
    String toParse = "public static " + className + " create" + className + "() ;";
    HookPoint methodBody = new TemplateHookPoint("ast.factorymethods.Create", clazz, className);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);
    
    toParse = "protected " + className + " doCreate" + className + "() ;";
    methodBody = new TemplateHookPoint("ast.factorymethods.DoCreate", clazz, className);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);
    
    String ordered = "strictlyOrdered";
    boolean isListClass = astHelper.isAstListClass(clazz);
    toParse = "public static " + className + " create" + className + "() ;";
    
    // No create methods with parameters
    if (clazz.getCDAttributes().isEmpty()) {
      return;
    }
    
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(
        nodeFactoryClass, toParse);
    Preconditions.checkArgument(astMethod.isPresent());
    ASTCDMethod createMethod = astMethod.get();
    if (astHelper.isAstListClass(clazz)) {
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      param.setName(ordered);
      param.setType(TypesNodeFactory.createASTPrimitiveType(ASTConstantsTypes.BOOLEAN));
      createMethod.getCDParameters().add(param);
    }
    
    toParse = "protected " + className + " doCreate" + className + "() ;";
    astMethod = cdTransformation.addCdMethodUsingDefinition(
        nodeFactoryClass, toParse);
    Preconditions.checkArgument(astMethod.isPresent());
    ASTCDMethod doCreateMethod = astMethod.get();
    
    StringBuilder paramCall = new StringBuilder();
    List<ASTCDAttribute> parameters = Lists.newArrayList();
    String del = "";
    if (isListClass) {
      paramCall.append(ordered);
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      param.setName(ordered);
      param.setType(TypesNodeFactory.createASTPrimitiveType(ASTConstantsTypes.BOOLEAN));
      doCreateMethod.getCDParameters().add(param);
    }
    else {
      List<ASTCDAttribute> inheritedAttributes = Lists.newArrayList();
      for (ASTCDAttribute attr : clazz.getCDAttributes()) {
        if (GeneratorHelper.isInherited(attr)) {
          inheritedAttributes.add(attr);
          continue;
        }
        ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
        ASTType type = attr.getType();
        if (TypesHelper.isOptional(type)) {
          type = TypesHelper.getSimpleReferenceTypeFromOptional(type);
        }
        else {
          parameters.add(attr);
        }
        param.setType(type);
        String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
        param.setName(javaAttrName);
        ASTCDParameter doParam = param.deepClone();
        createMethod.getCDParameters().add(param);
        doCreateMethod.getCDParameters().add(doParam);
        paramCall.append(del + javaAttrName);
        del = DEL;
      }
      for (ASTCDAttribute attr : inheritedAttributes) {
        ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
        ASTType type = attr.getType();
        if (TypesHelper.isOptional(type)) {
          type = TypesHelper.getSimpleReferenceTypeFromOptional(type);
        }
        else {
          parameters.add(attr);
        }
        param.setType(type);
        String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
        param.setName(javaAttrName);
        ASTCDParameter doParam = param.deepClone();
        createMethod.getCDParameters().add(param);
        doCreateMethod.getCDParameters().add(doParam);
        paramCall.append(del + javaAttrName);
        del = DEL;
      }
    }
    
    // create() method
    glex.replaceTemplate("ast.ParametersDeclaration", createMethod, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration"));
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, createMethod, new TemplateHookPoint(
        "ast.factorymethods.CreateWithParams", clazz, className, paramCall.toString()));
    
    // doCreate() method
    glex.replaceTemplate("ast.ParametersDeclaration", doCreateMethod, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration"));
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, doCreateMethod, new TemplateHookPoint(
        "ast.factorymethods.DoCreateWithParams", clazz, className, paramCall.toString()));
    
    if (parameters.size() != createMethod.getCDParameters().size()) {
      glex.replaceTemplate("ast.ErrorIfNull", createMethod, new TemplateHookPoint(
          "ast.factorymethods.ErrorIfNull", parameters));
    }
    if (parameters.size() != doCreateMethod.getCDParameters().size()) {
      glex.replaceTemplate("ast.ErrorIfNull", doCreateMethod, new TemplateHookPoint(
          "ast.factorymethods.ErrorIfNull", parameters));
    }
    
  }
  
  /**
   * TODO: Write me!
   * 
   * @param astHelper
   * @param cdSymbol
   * @param delegateFactoryName
   * @param cdClass
   * @param nodeFactoryForSuperCd
   */
  void addDelegateMethodsToNodeFactory(ASTCDClass clazz, ASTCDClass nodeFactoryClass,
      AstGeneratorHelper astHelper, CDSymbol cdSymbol, String delegateFactoryName) {
    if (!clazz.getModifier().isPresent() || clazz.getModifier().get().isAbstract()) {
      return;
    }
    String className = GeneratorHelper.getPlainName(clazz);
    String toParse = "public static " + className + " create" + className + "() ;";
    HookPoint methodBody = new TemplateHookPoint("ast.factorymethods.CreateDelegate",
        delegateFactoryName, className);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);
    
    String ordered = "strictlyOrdered";
    boolean isListClass = AstGeneratorHelper.isAstListClass(clazz.getName(), cdSymbol);
    toParse = "public static " + className + " create" + className + "() ;";
    
    // No create methods with parameters (only additional attributes {@link
    // AstAdditionalAttributes}
    if (clazz.getCDAttributes().size() <= 2) {
      return;
    }
    
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(
        nodeFactoryClass, toParse);
    Preconditions.checkArgument(astMethod.isPresent());
    ASTCDMethod createMethod = astMethod.get();
    if (AstGeneratorHelper.isAstListClass(clazz.getName(), cdSymbol)) {
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      param.setName(ordered);
      param.setType(TypesNodeFactory.createASTPrimitiveType(ASTConstantsTypes.BOOLEAN));
      createMethod.getCDParameters().add(param);
    }
    
    StringBuilder paramCall = new StringBuilder();
    String del = "";
    if (isListClass) {
      paramCall.append(ordered);
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      param.setName(ordered);
      param.setType(TypesNodeFactory.createASTPrimitiveType(ASTConstantsTypes.BOOLEAN));
    }
    else {
      List<ASTCDAttribute> inheritedAttributes = Lists.newArrayList();
      for (ASTCDAttribute attr : clazz.getCDAttributes()) {
        if (GeneratorHelper.isInherited(attr)) {
          inheritedAttributes.add(attr);
          continue;
        }
        if (Arrays.asList(AstAdditionalAttributes.values()).stream().map(a -> a.toString())
            .anyMatch(a -> a.equals(attr.getName()))) {
          continue;
        }
        ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
        ASTType type = attr.getType();
        if (TypesHelper.isOptional(type)) {
          type = TypesHelper.getSimpleReferenceTypeFromOptional(type);
        }
        if (type instanceof ASTSimpleReferenceType) {
          type = astHelper.convertTypeCd2Java((ASTSimpleReferenceType) type,
              AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT);
        }
        param.setType(type);
        String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
        param.setName(javaAttrName);
        createMethod.getCDParameters().add(param);
        paramCall.append(del + javaAttrName);
        del = DEL;
      }
      for (ASTCDAttribute attr : inheritedAttributes) {
        ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
        ASTType type = attr.getType();
        if (TypesHelper.isOptional(type)) {
          type = TypesHelper.getSimpleReferenceTypeFromOptional(type);
        }
        if (type instanceof ASTSimpleReferenceType) {
          type = astHelper.convertTypeCd2Java((ASTSimpleReferenceType) type,
              AstGeneratorHelper.AST_PACKAGE_SUFFIX);
        }
        param.setType(type);
        String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
        param.setName(javaAttrName);
        createMethod.getCDParameters().add(param);
        paramCall.append(del + javaAttrName);
        del = DEL;
      }
    }
    
    // create() method
    glex.replaceTemplate("ast.ParametersDeclaration", createMethod, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration"));
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, createMethod,
        new TemplateHookPoint(
            "ast.factorymethods.CreateWithParamsDelegate", delegateFactoryName, className,
            paramCall.toString()));
    glex.replaceTemplate("ast.ErrorIfNull", createMethod, new StringHookPoint(""));
    
  }
  
  /**
   * TODO: Write me!
   * 
   * @param clazz
   * @param astHelper
   */
  void addConstructors(ASTCDClass clazz,
      AstGeneratorHelper astHelper) {
    if (astHelper.isAstListClass(clazz)) {
      return;
    }
    ASTCDConstructor emptyConstructor = CD4AnalysisNodeFactory.createASTCDConstructor();
    emptyConstructor.setName(clazz.getName());
    emptyConstructor.setModifier(TransformationHelper.createProtectedModifier());
    clazz.getCDConstructors().add(emptyConstructor);
    
    // Only one constructor
    if (clazz.getCDAttributes().isEmpty()) {
      return;
    }
    
    ASTCDConstructor fullConstructor = emptyConstructor.deepClone();
    // TODO: Collect parameters of the super class (Symbol table)
    List<ASTCDAttribute> inheritedAttributes = Lists.newArrayList();
    for (ASTCDAttribute attr : clazz.getCDAttributes()) {
      if (GeneratorHelper.isInherited(attr)) {
        inheritedAttributes.add(attr);
        continue;
      }
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      param.setType((ASTType) attr.getType().deepClone());
      param.setName(attr.getName());
      fullConstructor.getCDParameters().add(param);
    }
    for (ASTCDAttribute attr : inheritedAttributes) {
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      param.setType((ASTType) attr.getType().deepClone());
      param.setName(attr.getName());
      fullConstructor.getCDParameters().add(param);
    }
    clazz.getCDConstructors().add(fullConstructor);
    
    glex.replaceTemplate("ast.ParametersDeclaration", fullConstructor, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration"));
    glex.replaceTemplate("ast.EmptyMethodBody", fullConstructor, new TemplateHookPoint(
        "ast.ConstructorAttributesSetter"));
    Optional<ASTCDClass> clazzBuilder = astHelper.getASTBuilder(clazz);
    if (clazzBuilder.isPresent()) {
      glex.replaceTemplate(
          "ast.ParametersDeclaration",
          clazzBuilder.get(),
          new TemplateHookPoint(
              "ast.BuilderConstructorParametersDeclaration", fullConstructor.getCDParameters()));
    }
  }
  
  /**
   * TODO: Write me!
   */
  void decorateAstListClass(ASTCDClass clazz) {
    glex.replaceTemplate("ast.ClassContent", clazz, new TemplateHookPoint("ast.AstListMethods"));
    glex.replaceTemplate("ast.AstImports", clazz, new TemplateHookPoint("ast.ListImports"));
    glex.replaceTemplate("ast.AstSuperTypes", clazz, new TemplateHookPoint(
        "ast.AstSuperTypesList"));
  }
  
  /**
   * TODO: Write me!
   * 
   * @param cdDefinition
   * @param astHelper
   * @throws ANTLRException
   */
  void addConstantsClass(ASTCDDefinition cdDefinition, AstGeneratorHelper astHelper)
      throws RecognitionException, IOException {
    
    String constantsClassName = ConstantsTranslation.AST_CONSTANTS_ENUM + cdDefinition.getName();
    Optional<ASTCDEnum> enumConstans = cdDefinition.getCDEnums().stream()
        .filter(e -> e.getName().equals(constantsClassName)).findAny();
    if (!enumConstans.isPresent()) {
      Log.error("0xA1004 CdDecorator error: " + constantsClassName
          + " class can't be created for the class diagramm "
          + cdDefinition.getName());
      return;
    }
    
    Optional<ASTCDClass> ast = cdTransformation.addCdClassUsingDefinition(cdDefinition,
        "public class " + constantsClassName + ";");
    if (!ast.isPresent()) {
      Log.error("0xA1028 CdDecorator error:" + constantsClassName
          + " class can't be created for the class diagramm "
          + cdDefinition.getName());
      return;
    }
    
    ASTCDClass astConstantsClass = ast.get();
    glex.replaceTemplate(
        "ast.ClassContent",
        astConstantsClass,
        new TemplateHookPoint(
            "ast.ASTConstantsClass", astConstantsClass, astHelper.getQualifiedCdName(), astHelper
                .getSuperGrammarCds()));
    for (ASTCDEnumConstant astConstant : enumConstans.get().getCDEnumConstants()) {
      ASTCDAttribute constAttr = CD4AnalysisNodeFactory.createASTCDAttribute();
      constAttr.setName(astConstant.getName());
      astConstantsClass.getCDAttributes().add(constAttr);
    }
    cdDefinition.getCDEnums().remove(enumConstans.get());
  }
  
  /**
   * TODO: Write me!
   * 
   * @param cdDefinition
   * @param astHelper
   */
  private void decorateBaseInterface(ASTCDDefinition cdDefinition) {
    List<ASTCDInterface> stream = cdDefinition
        .getCDInterfaces()
        .stream()
        .filter(
            i -> i.getName().equals(
                BaseInterfaceAddingManipulation.getBaseInterfaceName(cdDefinition)))
        .collect(Collectors.toList());
    if (stream.size() != 1) {
      Log.error("0xA3002 error by generation of the base node interface for the grammar "
          + cdDefinition.getName());
      return;
    }
    ASTCDInterface baseInterface = stream.get(0);
    baseInterface.getInterfaces().add(
        TransformationHelper.createSimpleReference(GeneratorHelper.AST_NODE));
    glex.replaceTemplate(
        "ast.AstInterfaceContent",
        baseInterface,
        new TemplateHookPoint(
            "ast.ASTNodeBase", baseInterface));
    
  }
  
  /**
   * TODO: Write me!
   * 
   * @param cdCompilationUnit
   */
  void transformCdTypeNamesForHWTypes(ASTCDCompilationUnit cdCompilationUnit) {
    String packageName = TransformationHelper.getAstPackageName(cdCompilationUnit);
    
    String modelName = Names.getQualifiedName(cdCompilationUnit.getPackage(), cdCompilationUnit
        .getCDDefinition().getName());
    
    cdCompilationUnit
        .getCDDefinition()
        .getCDClasses()
        .forEach(
            c -> c.setName(GeneratorHelper.getSimpleTypeNameToGenerate(c.getName(), packageName,
                modelName, targetPath)));
    cdCompilationUnit
        .getCDDefinition()
        .getCDInterfaces()
        .forEach(
            c -> c.setName(GeneratorHelper.getSimpleTypeNameToGenerate(c.getName(), packageName,
                modelName, targetPath)));
    cdCompilationUnit
        .getCDDefinition()
        .getCDEnums()
        .forEach(
            c -> c.setName(GeneratorHelper.getSimpleTypeNameToGenerate(c.getName(), packageName,
                modelName, targetPath)));
  }
  
  /**
   * Performs ast specific template replacements using {@link HookPoint}
   * 
   * @param ast
   * @param replacedTemplateName qualified name of template to be replaced
   * @param glex
   */
  public ASTCDMethod replaceMethodBodyTemplate(ASTCDClass clazz, String methodSignatur,
      HookPoint hookPoint) {
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(clazz,
        methodSignatur);
    Preconditions.checkArgument(astMethod.isPresent());
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, astMethod.get(), hookPoint);
    return astMethod.get();
  }
  
  /**
   * Converts all CD types [(qualified)Classdiagramm.Type] to MontiCore-Java
   * types [qualified AST-Type] e.g. Literals.ASTIntLiteral (oder
   * de.monticore.literals.Literals.ASTIntLiteral) will be converted to
   * de.monticore.literals.literals._ast.ASTIntLiteral
   */
  public class Cd2JavaTypeConverter implements CD4AnalysisInheritanceVisitor {
    
    private AstGeneratorHelper astHelper;
    
    public Cd2JavaTypeConverter(AstGeneratorHelper astHelper) {
      this.astHelper = astHelper;
    }
    
    @Override
    public void visit(ASTSimpleReferenceType ast) {
      astHelper.transformTypeCd2Java(ast, GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT);
    }
    
  }
  
}
