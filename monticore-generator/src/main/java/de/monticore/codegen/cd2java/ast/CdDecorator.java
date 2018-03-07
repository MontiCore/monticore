/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.ast;

import static com.google.common.collect.Lists.newArrayList;
import static de.se_rwth.commons.Names.getQualifier;
import static de.se_rwth.commons.Names.getSimpleName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.manipul.BaseInterfaceAddingManipulation;
import de.monticore.codegen.mc2cd.transl.ConstantsTranslation;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.TypesMill;
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
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereoValue;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereotype;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.umlcd4a.prettyprint.AstPrinter;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import groovyjarjarantlr.ANTLRException;
import transformation.ast.ASTCDTransformation;

/**
 * Decorates class diagrams by adding of new classes and methods using in ast
 * files
 *
 */
public class CdDecorator {
  
  public static final String DIR_REPORTS = "_reports";
  
  public static final String THIS_PREFIX = String.format("%n%15cthis.", ' ');
  
  /**
   * The qualified name of the empty method body template
   */
  public static final String EMPTY_BODY_TEMPLATE = "ast.EmptyMethodBody";
  
  public static final String CLASS_CONTENT_TEMPLATE = "ast.ClassContent";
  
  public static final String ERROR_IFNULL_TEMPLATE = "ast.ErrorIfNull";
  
  public static final String NODE_FACTORY = "NodeFactory";
  
  public static final String MILL = "Mill";
  
  protected static final String DEL = ", ";
  
  protected ASTCDTransformation cdTransformation = new ASTCDTransformation();
  
  protected GlobalExtensionManagement glex;
  
  protected IterablePath targetPath;
  
  protected GlobalScope symbolTable;
  
  public CdDecorator(
      GlobalExtensionManagement glex,
      GlobalScope symbolTable,
      IterablePath targetPath) {
    this.glex = glex;
    this.symbolTable = symbolTable;
    this.targetPath = targetPath;
  }
  
  public void decorate(ASTCDCompilationUnit cdCompilationUnit) {
    AstGeneratorHelper astHelper = new AstGeneratorHelper(cdCompilationUnit, symbolTable);
    ASTCDDefinition cdDefinition = cdCompilationUnit.getCDDefinition();
    List<ASTCDClass> nativeClasses = Lists.newArrayList(cdDefinition.getCDClassList());
    
    // Run over classdiagramm and converts cd types to mc-java types
    astHelper.transformCdTypes2Java();
    
    // Interface for all ast nodes of the language
    decorateBaseInterface(cdDefinition);
    
    // Decorate with builder pattern
    addBuilders(cdDefinition, astHelper);
    
    addNodeFactoryClass(cdCompilationUnit, nativeClasses, astHelper);
    
    addMillClass(cdCompilationUnit, nativeClasses, astHelper);

    // Check if handwritten ast types exist
    transformCdTypeNamesForHWTypes(cdCompilationUnit);
    
    cdDefinition.getCDClassList().forEach(c -> addSuperInterfaces(c));
    
    // Decorate with additional methods and attributes
    for (ASTCDClass clazz : nativeClasses) {
      addConstructors(clazz, astHelper);
      addAdditionalMethods(clazz, astHelper);
      addListMethods(clazz, astHelper, cdDefinition);
      addGetter(clazz, astHelper);
      addSetter(clazz, astHelper, cdDefinition);
      addOptionalMethods(clazz, astHelper, cdDefinition);
      addSymbolGetter(clazz, astHelper);
      addNodeGetter(clazz, astHelper);

      Optional<ASTCDClass> builder = astHelper.getASTBuilder(clazz);
      builder.ifPresent(astcdClass -> decorateBuilderClass(astcdClass, astHelper, cdDefinition));
    }
    
    cdDefinition.getCDClassList().forEach(c -> makeAbstractIfHWC(c));
    
    for (ASTCDInterface interf : cdDefinition.getCDInterfaceList()) {
      addGetter(interf);
    }
    
    // Add ASTConstant class
    addConstantsClass(cdDefinition, astHelper);
    
    // Additional imports
    cdCompilationUnit.getImportStatementList().add(
        TypesMill.importStatementBuilder()
            .setImportList(
                Lists.newArrayList(VisitorGeneratorHelper.getQualifiedVisitorType(astHelper
                    .getPackageName(), cdDefinition.getName())))
            .build());
    
            
  }

  protected void decorateBuilderClass(ASTCDClass builder, AstGeneratorHelper astHelper, ASTCDDefinition cdDefinition) {
    addListMethods(builder, astHelper, cdDefinition);
    addGetter(builder, astHelper);
    addSetter(builder, astHelper, cdDefinition);
    addOptionalMethods(builder, astHelper, cdDefinition);
    addASTNodeBuilderMethods(builder, astHelper);
  }

  protected void addASTNodeBuilderMethods(ASTCDClass clazz, AstGeneratorHelper astHelper) {
    if(!clazz.printSuperClass().startsWith("de.monticore.ast.ASTNodeBuilder")) {
      for(AstBuilderMethods value : AstBuilderMethods.values()) {
        String methodSignatur = String.format(value.getMethodDeclaration(),
          clazz.getName());
        additionalNodeBuilderMethod(clazz, value.getMethodName(),
          methodSignatur);
      }
    }
  }

  protected void addSymbolGetter(ASTCDClass clazz, AstGeneratorHelper astHelper) {
    List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributeList());
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
        referencedSymbol = SymbolTableGeneratorHelper
            .getQualifiedSymbolType(getQualifier(referencedSymbol)
                .toLowerCase(), getSimpleName(referencedSymbol));
      }
      
      String returnType;
      String nameSuffix;
      if (astHelper.isListAstNode(attribute)) {
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
          "ast.additionalmethods.GetReferencedSymbol",
          attribute.getName(), referencedSymbol);
      replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
    }
  }

  protected void addNodeGetter(ASTCDClass clazz, AstGeneratorHelper astHelper) {
    List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributeList());
    for (ASTCDAttribute attribute : attributes) {
      if (GeneratorHelper.isInherited(attribute)
        || !CD4AnalysisHelper.hasStereotype(attribute,
        MC2CDStereotypes.REFERENCED_SYMBOL.toString())) {
        continue;
      }

      String referencedSymbol = CD4AnalysisHelper.getStereotypeValues(attribute,
        MC2CDStereotypes.REFERENCED_SYMBOL.toString()).get(0);
      String symbolName = getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf("Symbol"));
      String referencedNode = GeneratorHelper.AST_PREFIX + symbolName;
      referencedNode = GeneratorHelper.getPackageName(astHelper.getAstPackage(), referencedNode);

      if (!getQualifier(referencedSymbol).isEmpty()) {
        referencedSymbol = SymbolTableGeneratorHelper
          .getQualifiedSymbolType(getQualifier(referencedSymbol)
            .toLowerCase(), getSimpleName(referencedSymbol));
      }

      String returnType = "Optional<" + referencedNode + ">";
      String nameSuffix = "Node";

      String toParse = "public " + returnType + " "
        + GeneratorHelper.getPlainGetter(attribute) + nameSuffix + "() ;";
      HookPoint getMethodBody = new TemplateHookPoint(
        "ast.additionalmethods.GetReferencedNode",
        attribute.getName(), referencedSymbol, symbolName);
      replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
    }
  }
  
  /**
   * Adds common ast methods to the all classes in the class diagram
   * 
   * @param clazz - each entry contains a class diagram class and a respective
   * builder class
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addAdditionalMethods(ASTCDClass clazz,
      AstGeneratorHelper astHelper) {
    if (astHelper.isAstClass(clazz)) {
      AstAdditionalMethods additionalMethod = AstAdditionalMethods.accept;
      String visitorTypeFQN = VisitorGeneratorHelper.getQualifiedVisitorType(
          astHelper.getPackageName(), astHelper.getCdName());
      String methodSignatur = String.format(additionalMethod.getDeclaration(), visitorTypeFQN);
      replaceMethodBodyTemplate(clazz, methodSignatur, new TemplateHookPoint(
          "ast.additionalmethods.Accept"));
          
      // node needs to accept visitors from all super languages
      for (CDSymbol cdSym : astHelper.getAllSuperCds(astHelper.getCd())) {
        String superGrammarName = Names.getSimpleName(cdSym.getFullName());
        String visitorType = superGrammarName + "Visitor";
        String visitorPackage = VisitorGeneratorHelper.getVisitorPackage(cdSym.getFullName());
        
        additionalMethod = AstAdditionalMethods.accept;
        String superVisitorTypeFQN = visitorPackage + "." + visitorType;
        methodSignatur = String.format(additionalMethod.getDeclaration(), superVisitorTypeFQN);
        replaceMethodBodyTemplate(clazz, methodSignatur, new TemplateHookPoint(
            "ast.additionalmethods.AcceptSuper", astHelper.getQualifiedCdName(),
            visitorTypeFQN, superVisitorTypeFQN));
      }
    }
    
    Optional<ASTModifier> modifier = clazz.getModifierOpt();
    String plainClassName = GeneratorHelper.getPlainName(clazz);
    Optional<CDTypeSymbol> symbol = astHelper.getCd().getType(plainClassName);
    if (!symbol.isPresent()) {
      Log.error("0xA1062 CdDecorator error: Can't find symbol for class " + plainClassName);
    }
    
    ASTCDMethod meth = replaceMethodBodyTemplate(clazz, AstAdditionalMethods.get_Children.getDeclaration(),
        new TemplateHookPoint("ast.additionalmethods.GetChildren", symbol.get()));
    
    addDeprecatedStereotype(meth);
        
    replaceMethodBodyTemplate(clazz, AstAdditionalMethods.deepEqualsWithOrder.getDeclaration(),
        new TemplateHookPoint("ast.additionalmethods.DeepEqualsWithOrder"));
        
    replaceMethodBodyTemplate(clazz, AstAdditionalMethods.deepEquals.getDeclaration(),
        new StringHookPoint("return deepEquals(o, true);\n"));
        
    replaceMethodBodyTemplate(clazz,
        AstAdditionalMethods.deepEqualsWithCommentsWithOrder.getDeclaration(),
        new TemplateHookPoint("ast.additionalmethods.DeepEqualsWithComments"));
        
    replaceMethodBodyTemplate(clazz,
        AstAdditionalMethods.deepEqualsWithComments.getDeclaration(),
        new StringHookPoint("return deepEqualsWithComments(o, true);\n"));
        
    replaceMethodBodyTemplate(clazz, AstAdditionalMethods.equalAttributes.getDeclaration(),
        new TemplateHookPoint("ast.additionalmethods.EqualAttributes"));
        
    replaceMethodBodyTemplate(clazz, AstAdditionalMethods.equalsWithComments.getDeclaration(),
        new TemplateHookPoint("ast.additionalmethods.EqualsWithComments"));
        
    String stringToParse = String.format(AstAdditionalMethods.deepClone.getDeclaration(),
        plainClassName);
    replaceMethodBodyTemplate(clazz, stringToParse,
        new StringHookPoint("return deepClone(_construct());\n"));
        
    stringToParse = String.format(AstAdditionalMethods.deepCloneWithOrder.getDeclaration(),
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
          new StringHookPoint(
              "return " + astHelper.getCdName() + "NodeFactory.create" + plainClassName + "();\n"));
    }
  }

  protected void addDeprecatedStereotype(ASTCDMethod meth) {
    ASTStereotype stereo;
    if (meth.getModifier().isPresentStereotype()) {
      stereo = meth.getModifier().getStereotype();
    } else {
      stereo = CD4AnalysisMill.stereotypeBuilder().build();
      meth.getModifier().setStereotype(stereo);
    }
    ASTStereoValue value = CD4AnalysisMill.stereoValueBuilder().setName("@Deprecated").build();
    stereo.getValueList().add(value);
  }

  /**
   * Adds common ast methods to the all classes in the class diagram
   * 
   * @param clazz - each entry contains a class diagram class and a respective
   * builder class
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addListMethods(ASTCDClass clazz,
      AstGeneratorHelper astHelper, ASTCDDefinition cdDefinition) {
    boolean isBuilderClass = AstGeneratorHelper.isBuilderClass(cdDefinition, clazz);
    if (astHelper.isAstClass(clazz) || isBuilderClass) {
      List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributeList());
      for (ASTCDAttribute attribute : attributes) {
        if (!astHelper.isListType(TypesPrinter.printType(attribute.getType()))) {
          continue;
        }
        Optional<ASTSimpleReferenceType> type = TypesHelper
          .getFirstTypeArgumentOfGenericType(attribute.getType(), GeneratorHelper.JAVA_LIST);
        if (!type.isPresent()) {
          continue;
        }
        String typeName = new AstPrinter().printType(type.get());
        String attrName = GeneratorHelper.getSimpleListName(attribute);
        String listName = StringTransformations.capitalize(attribute.getName());
        if (!GeneratorHelper.isInherited(attribute)) {
          addListSetterMethods(clazz, attribute, typeName, listName, attrName, isBuilderClass, false);
          addListGetterMethods(clazz, attribute, typeName, listName, attrName);
        }
        else if (isBuilderClass) {
          addListSetterMethods(clazz, attribute, typeName, listName, attrName, isBuilderClass, true);
        }
      }
    }
  }

  protected void addListSetterMethods(ASTCDClass clazz, ASTCDAttribute attribute, String typeName,
      String listName, String attrName, boolean isBuilderClass, boolean isInherited) {
    String methodSignatur = String.format(AstListMethods.clear.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "void", listName);

    additionalMethodForListAttribute(clazz, AstListMethods.clear.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.add.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "boolean", attrName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.add.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.addAll.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "boolean", listName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.addAll.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.remove.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "boolean", attrName);
    additionalMethodForListAttribute(clazz, AstListMethods.remove.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.removeAll.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "boolean", listName);
    additionalMethodForListAttribute(clazz, AstListMethods.removeAll.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.retainAll.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "boolean", listName);
    additionalMethodForListAttribute(clazz, AstListMethods.retainAll.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.removeIf.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "boolean", attrName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.removeIf.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.forEach.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "void", listName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.forEach.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.add_.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "void", attrName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.add_.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.addAll_.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "boolean", listName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.addAll_.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.replaceAll.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "void", listName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.replaceAll.getMethodName(),
      attribute, methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.sort.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : "void", listName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.sort.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.remove_.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : typeName, attrName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.remove_.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);

    methodSignatur = String.format(AstListMethods.set_.getMethodDeclaration(),
      isBuilderClass ? clazz.getName() : typeName, attrName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.set_.getMethodName(), attribute,
      methodSignatur, isBuilderClass, isInherited);
  }

  protected void addListGetterMethods(ASTCDClass clazz, ASTCDAttribute attribute, String typeName,
      String listName, String attrName) {
    String methodSignatur = String.format(AstListMethods.contains.getMethodDeclaration(),
      attrName);
    additionalMethodForListAttribute(clazz, AstListMethods.contains.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.containsAll.getMethodDeclaration(),
      listName);
    additionalMethodForListAttribute(clazz, AstListMethods.containsAll.getMethodName(),
      attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.isEmpty.getMethodDeclaration(),
      listName);
    additionalMethodForListAttribute(clazz, AstListMethods.isEmpty.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.iterator.getMethodDeclaration(),
      typeName, listName);
    additionalMethodForListAttribute(clazz, AstListMethods.iterator.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.size.getMethodDeclaration(),
      listName);
    additionalMethodForListAttribute(clazz, AstListMethods.size.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.toArray.getMethodDeclaration(),
      typeName, listName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.toArray.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.toArray_.getMethodDeclaration(),
      listName);
    additionalMethodForListAttribute(clazz, AstListMethods.toArray_.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.spliterator.getMethodDeclaration(),
      typeName, listName);
    additionalMethodForListAttribute(clazz, AstListMethods.spliterator.getMethodName(),
      attribute, methodSignatur);

    methodSignatur = String.format(AstListMethods.stream.getMethodDeclaration(),
      typeName, listName);
    additionalMethodForListAttribute(clazz, AstListMethods.stream.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.parallelStream.getMethodDeclaration(),
      typeName, listName);
    additionalMethodForListAttribute(clazz, AstListMethods.parallelStream.getMethodName(),
      attribute, methodSignatur);

    methodSignatur = String.format(AstListMethods.get.getMethodDeclaration(),
      typeName, attrName);
    additionalMethodForListAttribute(clazz, AstListMethods.get.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.indexOf.getMethodDeclaration(),
      attrName, typeName);
    additionalMethodForListAttribute(clazz, AstListMethods.indexOf.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.lastIndexOf.getMethodDeclaration(),
      attrName);
    additionalMethodForListAttribute(clazz, AstListMethods.lastIndexOf.getMethodName(),
      attribute, methodSignatur);

    methodSignatur = String.format(AstListMethods.equals.getMethodDeclaration(),
      listName);
    additionalMethodForListAttribute(clazz, AstListMethods.equals.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.hashCode.getMethodDeclaration(),
      listName);
    additionalMethodForListAttribute(clazz, AstListMethods.hashCode.getMethodName(), attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.listIterator.getMethodDeclaration(),
      typeName, listName);
    additionalMethodForListAttribute(clazz, AstListMethods.listIterator.getMethodName(),
      attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.listIterator_.getMethodDeclaration(),
      typeName, listName);
    additionalMethodForListAttribute(clazz, AstListMethods.listIterator_.getMethodName(),
      attribute,
      methodSignatur);

    methodSignatur = String.format(AstListMethods.subList.getMethodDeclaration(),
      typeName, listName);
    additionalMethodForListAttribute(clazz, AstListMethods.subList.getMethodName(), attribute,
      methodSignatur);
  }
  
  /**
   * Adds common ast methods to the all classes in the class diagram
   * 
   * @param clazz - each entry contains a class diagram class and a respective
   * builder class
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addOptionalMethods(ASTCDClass clazz,
      AstGeneratorHelper astHelper, ASTCDDefinition cdDefinition) {
    for (ASTCDAttribute attribute: clazz.getCDAttributeList()) {
      boolean isBuilderClass = AstGeneratorHelper.isBuilderClass(cdDefinition, clazz);
      String nativeName = StringTransformations.capitalize(GeneratorHelper.getNativeAttributeName(attribute.getName()));
      if(!astHelper.isOptional(attribute)) {
        continue;
      }
      if (!astHelper.isInherited(attribute)) {
        addOptionalGetMethods(clazz, attribute, nativeName);
        addOptionalSetMethods(clazz, attribute, nativeName, isBuilderClass, false);
      }
      else if (isBuilderClass) {
        addOptionalSetMethods(clazz, attribute, nativeName, isBuilderClass, true);
      }
    }
  }

  protected void addOptionalSetMethods(ASTCDClass clazz, ASTCDAttribute attribute, String nativeName,
      boolean isBuilderClass, boolean isInherited) {
    String returnType = isBuilderClass ? clazz.getName() : "void";
    String methodSetAbsent = "set" + nativeName + "Absent";
    String toParse = "public " + returnType + " " + methodSetAbsent + "() ;";
    HookPoint getMethodBody = new TemplateHookPoint("ast.additionalmethods.SetAbsent",
      GeneratorHelper.getJavaAndCdConformName(attribute.getName()), isBuilderClass, isInherited);
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);

    String methodSetOpt = "set" + nativeName + "Opt";
    toParse = "public " + returnType + " " + methodSetOpt + "(" + TypesPrinter.printType(attribute.getType()) + " value) ;";
    getMethodBody = new TemplateHookPoint("ast.additionalmethods.SetOpt",
      GeneratorHelper.getJavaAndCdConformName(attribute.getName()), isBuilderClass, isInherited);
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
  }

  protected void addOptionalGetMethods(ASTCDClass clazz, ASTCDAttribute attribute, String nativeName) {
    String methodName = GeneratorHelper.getPlainGetter(attribute);
    String methodNameOpt = methodName.substring(0, methodName.length()-GeneratorHelper.GET_SUFFIX_OPTINAL.length());
    String returnType = TypesPrinter.printType(TypesHelper.getFirstTypeArgumentOfOptional(attribute.getType()).get());
    String toParse = "public " + returnType + " " + methodNameOpt + "() ;";
    HookPoint getMethodBody = new TemplateHookPoint("ast.additionalmethods.GetOpt", methodName);
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
    String methodIsPresent = "isPresent" + nativeName;
    toParse = "public boolean " + methodIsPresent + "() ;";
    getMethodBody = new TemplateHookPoint("ast.additionalmethods.IsPresent", methodName);
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
  }

  /**
   * Decorates class diagram with builder pattern for all classes excepting
   * lists
   * 
   * @param cdDefinition
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addBuilders(ASTCDDefinition cdDefinition, AstGeneratorHelper astHelper) {
    newArrayList(cdDefinition.getCDClassList()).stream()
        .forEach(c -> {
          Optional<ASTCDClass> clazz = cdTransformation.addCdClassUsingDefinition(
            cdDefinition,
            "public class " + AstGeneratorHelper.getNameOfBuilderClass(c) + " ;");
          if(clazz.isPresent()) {
            c.getCDAttributeList().stream().forEach(a -> cdTransformation.addCdAttribute(clazz.get(), a));
            ASTSimpleReferenceType superClass;
            if(c.isPresentSuperclass()) {
              superClass = TransformationHelper.createSimpleReference(c.printSuperClass() + "Builder");
            }
            else {
              superClass = TransformationHelper.createSimpleReference("de.monticore.ast.ASTNodeBuilder", clazz.get().getName());
            }
            clazz.get().setSuperclass(superClass);
          }
        });
  }
  
  protected void addSuperInterfaces(ASTCDClass clazz) {
    String interfaces = clazz.printInterfaces();
    if (!interfaces.isEmpty()) {
      glex.replaceTemplate("ast.AstSuperInterfaces", clazz,
          new StringHookPoint(interfaces + DEL));
    }
  }
  
  /**
   * Makes the AST class abstract if it's a super class of an HW type
   * 
   * @param clazz
   */
  protected void makeAbstractIfHWC(ASTCDClass clazz) {
    if (AstGeneratorHelper.isSupertypeOfHWType(clazz.getName())) {
      Optional<ASTModifier> previousModifier = clazz.getModifierOpt();
      ASTModifier newModifier = previousModifier.isPresent()
          ? previousModifier.get()
          : CD4AnalysisNodeFactory
              .createASTModifier();
      newModifier.setAbstract(true);
      clazz.setModifier(newModifier);
    }
  }
  
  /**
   * 
   * @param clazz
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addAdditionalAttributes(
      ASTCDClass clazz, AstGeneratorHelper astHelper) {
    // Add Symbol attribute
    cdTransformation.addCdAttributeUsingDefinition(clazz,
        AstAdditionalAttributes.symbol.getDeclaration());
    // Add Scope attribute
    cdTransformation.addCdAttributeUsingDefinition(clazz,
        AstAdditionalAttributes.enclosingScope.getDeclaration());
        
  }
  
  /**
   * Adds getter for all attributes of ast classes
   * 
   * @param interf
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addGetter(ASTCDClass clazz, AstGeneratorHelper astHelper) {
    List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributeList());
    // attributes.addAll(astHelper.getAttributesOfExtendedInterfaces(clazz));
    for (ASTCDAttribute attribute : attributes) {
      if (GeneratorHelper.isInherited(attribute)) {
        continue;
      }
      String methodName = GeneratorHelper.getPlainGetter(attribute);
      if (!clazz.getCDMethodList().stream()
          .filter(m -> methodName.equals(m.getName()) && m.getCDParameterList().isEmpty()).findAny()
          .isPresent()) {     
        String toParse = "public " + TypesPrinter.printType(attribute.getType()) + " "
            + methodName + "() ;";
        HookPoint getMethodBody = new TemplateHookPoint("ast.additionalmethods.Get",
            attribute.getName());
        replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
      }
    }
  }
  
  /**
   *
   * @param interf
   * @param astHelper
   */
  protected void addGetter(ASTCDInterface interf) {
    for (ASTCDAttribute attribute : interf.getCDAttributeList()) {
      if (GeneratorHelper.isInherited(attribute)) {
        continue;
      }
      String methodName = GeneratorHelper.getPlainGetter(attribute);
      if (interf.getCDMethodList().stream()
          .filter(m -> methodName.equals(m.getName()) && m.getCDParameterList().isEmpty()).findAny()
          .isPresent()) {
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
  protected void addSetter(ASTCDClass clazz, AstGeneratorHelper astHelper, ASTCDDefinition cdDefinition) {
    for (ASTCDAttribute attribute : clazz.getCDAttributeList()) {
      String typeName = TypesHelper.printSimpleRefType(attribute.getType());
      boolean isBuilderClass = AstGeneratorHelper.isBuilderClass(cdDefinition, clazz);
      if (!AstGeneratorHelper.generateSetter(clazz, attribute, typeName) && !isBuilderClass) {
        continue;
      }
      boolean isInherited = GeneratorHelper.isInherited(attribute);
      String attributeName = attribute.getName();
      String methodName = GeneratorHelper.getPlainSetter(attribute);
      boolean isOptional = GeneratorHelper.isOptional(attribute);
      String returnType =  isBuilderClass ? clazz.getName() : "void";
      
      String toParse = "public " + returnType + " " + methodName + "("
          + typeName + " " + attributeName + ") ;";
      HookPoint methodBody = new TemplateHookPoint("ast.additionalmethods.Set", 
          attribute, attributeName, isBuilderClass, isInherited, methodName);
      ASTCDMethod setMethod = replaceMethodBodyTemplate(clazz, toParse, methodBody);
      
      if (isOptional) {
        glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, setMethod, new StringHookPoint(""));
      }
      
    }
  }
  
  /**
   *
   * @param cdCompilationUnit
   * @param nativeClasses
   * @param astHelper
   */
  protected void addNodeFactoryClass(ASTCDCompilationUnit cdCompilationUnit,
      List<ASTCDClass> nativeClasses, AstGeneratorHelper astHelper) {
      
    // Add factory-attributes for all ast classes
    Set<String> astClasses = new LinkedHashSet<>();
    nativeClasses.stream()
        .forEach(e -> astClasses.add(GeneratorHelper.getPlainName(e)));
        
    ASTCDClass nodeFactoryClass = createNodeFactoryClass(cdCompilationUnit, nativeClasses,
        astHelper, astClasses);
        
    List<String> imports = getImportsForNodeFactory(nodeFactoryClass, astClasses, astHelper);
    
    glex.replaceTemplate(CLASS_CONTENT_TEMPLATE, nodeFactoryClass, new TemplateHookPoint(
        "ast.AstNodeFactory", nodeFactoryClass, imports));
    
  }
  
  /**
   * Generate Mill class
   * 
   * @param cdCompilationUnit
   * @param nativeClasses
   * @param astHelper
   */
  protected void addMillClass(ASTCDCompilationUnit cdCompilationUnit,
      List<ASTCDClass> nativeClasses, AstGeneratorHelper astHelper) {
    
    Set<String> astClasses = new LinkedHashSet<>();
    nativeClasses.stream()
        .forEach(e -> astClasses.add(GeneratorHelper.getPlainName(e)));
    
    // CreateMill 
    ASTCDClass millClass = createMillClass(cdCompilationUnit, nativeClasses,
        astHelper);
    
    // Create Import
    String packageName = Names.getQualifiedName(cdCompilationUnit.getPackageList());
    String importPrefix = (packageName.isEmpty() ? "" : packageName + ".")
        + cdCompilationUnit.getCDDefinition().getName().toLowerCase()
        + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;   
    List<String> imports = nativeClasses.stream().filter(c -> c.isPresentModifier())
        .filter(c -> !c.getModifier().isAbstract())
        .filter(c -> GeneratorHelper.getPlainName(c).startsWith(GeneratorHelper.AST_PREFIX))
        .map(c -> importPrefix  + AstGeneratorHelper.getNameOfBuilderClass(c))
        .collect(Collectors.toList());
    
    glex.replaceTemplate(CLASS_CONTENT_TEMPLATE, millClass, new TemplateHookPoint(
        "ast.AstMill", millClass,
        millClass.isPresentModifier() && millClass.getModifier().isAbstract(),
        imports));
    
    // Create delegate methods for inherited classes
    ArrayList<CDSymbol> overridden = Lists.newArrayList();
    List<String> delegateList = Lists.newArrayList(astClasses);
    for (CDSymbol superCd : astHelper.getAllSuperCds(astHelper.getCdSymbol())) {
      for (CDTypeSymbol cdType : superCd.getTypes()) {
        Optional<ASTNode> node = cdType.getAstNode();
        if (node.isPresent() && node.get() instanceof ASTCDClass) {
          if (!cdType.isAbstract() && !delegateList.contains(cdType.getName())) {
            delegateList.add(cdType.getName());
            String millName = getSimpleName(superCd.getName()) + MILL;
            String millPackage = superCd.getFullName().toLowerCase()
                + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
            addDelegateMethodToMill(cdType.getName(), millClass, astHelper, superCd,
                millPackage + millName);
          }
          
        }
      }
    }
    
    // Compute diagrams with overridden classes
    calculateOverriddenCds(astHelper.getCdSymbol(), astClasses, overridden);
    
    // Create init for super class diagrams
    String toParse = "public static void init();" ;
    HookPoint methodBody = new TemplateHookPoint("ast.ASTMillInitMethod", cdCompilationUnit.getCDDefinition().getName(),
        GeneratorHelper.getPlainName(millClass, ""), overridden);
    replaceMethodBodyTemplate(millClass, toParse, methodBody);    

    // Create reset for super class diagrams
    toParse = "public static void reset();" ;
    methodBody = new TemplateHookPoint("ast.ASTMillResetMethod",  astHelper.getCdSymbol().getImports());
    replaceMethodBodyTemplate(millClass, toParse, methodBody);    

    // Create Mill for overridden Rules
    for (CDSymbol symbol: overridden) {
      List<ASTCDClass> classes = Lists.newArrayList();
      for (CDTypeSymbol overriddenType: symbol.getTypes()) {
        if (astClasses.contains(overriddenType.getName())) {
          classes.add((ASTCDClass) overriddenType.getAstNode().get());
        }        
      }
      String millForName = symbol.getName()+ "MillFor" +cdCompilationUnit.getCDDefinition().getName();
      String millSuperName = getSimpleName(symbol.getName()) + MILL;
      String millSuperPackage = symbol.getFullName().toLowerCase()
            + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
      millClass = createMillForSuperClass(cdCompilationUnit, millForName, symbol.getName(), classes, astHelper);
      glex.replaceTemplate(CLASS_CONTENT_TEMPLATE, millClass, new TemplateHookPoint(
          "ast.AstMillForSuper", millClass,
          millClass.isPresentModifier() && millClass.getModifier().isAbstract(),
          imports, millSuperPackage + millSuperName));
    }
  }
  

  protected void calculateOverriddenCds(CDSymbol cd,
      Collection<String> nativeClasses, ArrayList<CDSymbol> overridden) {
    // imported cds
    for (String importedCdName : cd.getImports()) {
      Optional<CDSymbol> importedCd = symbolTable.resolve(importedCdName, CDSymbol.KIND);
      if (importedCd.isPresent()) {
        CDSymbol superCd = importedCd.get();
        for (String className: nativeClasses) {
          if (superCd.getType(className).isPresent()) {
            overridden.add(superCd);
            calculateOverriddenCds(superCd, nativeClasses, overridden);
            return;
          }
        }
        calculateOverriddenCds(superCd, nativeClasses, overridden);
      }
    }
    return;  
  }

  protected ASTCDClass createMillClass(ASTCDCompilationUnit cdCompilationUnit,
      List<ASTCDClass> nativeClasses,  AstGeneratorHelper astHelper) {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    
    ASTCDClass millClass = CD4AnalysisNodeFactory.createASTCDClass();
    String millClassName = cdDef.getName() + MILL;
    String plainName = millClassName;
    
    // Check if a handwritten mill class exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + millClassName)) {
      millClassName += GeneratorSetup.GENERATED_CLASS_SUFFIX;
      millClass.setModifier(TransformationHelper.createAbstractModifier());
    }
    millClass.setName(millClassName);
                
    for (ASTCDClass clazz : nativeClasses) {
      String toParse = "protected static " + plainName + " mill"
          + AstGeneratorHelper.getASTClassNameWithoutPrefix(clazz) + " = null;";
      cdTransformation.addCdAttributeUsingDefinition(millClass, toParse);
    }
    
    // Add builder-creating methods
    for (ASTCDClass clazz : nativeClasses) {
      // TODO MB,BS: Check Builder and Mill for abstract classes
      if (AstGeneratorHelper.isBuilderClassAbstract(clazz)
          || !GeneratorHelper.getPlainName(clazz).startsWith(GeneratorHelper.AST_PREFIX)) {
        continue;
      }
      String className = AstGeneratorHelper.getASTClassNameWithoutPrefix(clazz);
      String methodName = StringTransformations.uncapitalize(className);
      String toParse = "public static " + clazz.getName() + "Builder "
          + methodName
          + AstGeneratorHelper.AST_BUILDER + "() ;";
      
      HookPoint methodBody = new TemplateHookPoint("ast.AstMillBuilderMethod", className, methodName);
      replaceMethodBodyTemplate(millClass, toParse, methodBody);
      
      toParse = "protected " + clazz.getName() + "Builder _"
          + methodName + AstGeneratorHelper.AST_BUILDER + "() ;";
      replaceMethodBodyTemplate(millClass, toParse,
          new StringHookPoint("return new " + clazz.getName() + "Builder();\n"));
    }
    
    cdDef.getCDClassList().add(millClass);
    return millClass;
  }
  
  protected ASTCDClass createMillForSuperClass(ASTCDCompilationUnit cdCompilationUnit,
      String millClassName,
      String superName,
      List<ASTCDClass> nativeClasses,
      AstGeneratorHelper astHelper) {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    
    ASTCDClass millClass = CD4AnalysisNodeFactory.createASTCDClass();
    
    // Check if a handwritten mill class exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + millClassName)) {
      millClassName += GeneratorSetup.GENERATED_CLASS_SUFFIX;
      millClass.setModifier(TransformationHelper.createAbstractModifier());
    }
    millClass.setName(millClassName);
    millClass.setSuperclass(TransformationHelper.createSimpleReference(superName));
 
    // Add builder-creating methods
    for (ASTCDClass clazz : nativeClasses) {
      if (AstGeneratorHelper.isBuilderClassAbstract(clazz)
          || !GeneratorHelper.getPlainName(clazz).startsWith(GeneratorHelper.AST_PREFIX)) {
        continue;
      }

      String className = AstGeneratorHelper.getASTClassNameWithoutPrefix(clazz);
      String methodName = StringTransformations.uncapitalize(className) + AstGeneratorHelper.AST_BUILDER;
      String toParse = "protected " + astHelper.getPlainName(clazz) + AstGeneratorHelper.AST_BUILDER + " _"
          + methodName  + "() ;";
      replaceMethodBodyTemplate(millClass, toParse,
          new StringHookPoint("return " + cdCompilationUnit.getCDDefinition().getName() + "Mill." + methodName + "();\n"));
    }
    
    cdDef.getCDClassList().add(millClass);
    return millClass;
  }

  protected ASTCDClass createNodeFactoryClass(ASTCDCompilationUnit cdCompilationUnit,
      List<ASTCDClass> nativeClasses, AstGeneratorHelper astHelper, Set<String> astClasses) {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDClass nodeFactoryClass = CD4AnalysisNodeFactory.createASTCDClass();
    String nodeFactoryName = cdDef.getName() + NODE_FACTORY;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + nodeFactoryName)) {
      nodeFactoryName += GeneratorSetup.GENERATED_CLASS_SUFFIX;
    }
    nodeFactoryClass.setName(nodeFactoryName);
    
    for (String clazz : astClasses) {
      String toParse = "protected static " + nodeFactoryName + " factory" + clazz + " = null;";
      cdTransformation.addCdAttributeUsingDefinition(nodeFactoryClass, toParse);
    }
    
    // Add ast-creating methods
    for (ASTCDClass clazz : nativeClasses) {
      addMethodsToNodeFactory(clazz, nodeFactoryClass, astHelper);
    }
    
    cdDef.getCDClassList().add(nodeFactoryClass);
    
    return nodeFactoryClass;
  }
  
  protected List<String> getImportsForNodeFactory(ASTCDClass nodeFactoryClass,
      Set<String> astClasses, AstGeneratorHelper astHelper) {
    String nodeFactoryName = nodeFactoryClass.getName();
    // Add delegating methods for creating of the ast nodes of the super
    // grammars
    List<String> imports = new ArrayList<>();
    if (!astHelper.getSuperGrammarCds().isEmpty()) {
      for (CDSymbol superCd : astHelper.getAllCds(astHelper.getCdSymbol())) {
        Log.debug(" CDSymbol for " + nodeFactoryName + " : " + superCd, "CdDecorator");
        nodeFactoryName = getSimpleName(superCd.getName()) + NODE_FACTORY;
        String factoryPackage = superCd.getFullName().toLowerCase()
            + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
        imports.add(factoryPackage + "*");
        for (CDTypeSymbol type : superCd.getTypes()) {
          Optional<ASTNode> node = type.getAstNode();
          if (node.isPresent() && node.get() instanceof ASTCDClass) {
            ASTCDClass cdClass = (ASTCDClass) node.get();
            if (astClasses.contains(cdClass.getName())) {
              continue;
            }
            astClasses.add(cdClass.getName());
            addDelegateMethodsToNodeFactory(cdClass, nodeFactoryClass, astHelper, superCd,
                factoryPackage + nodeFactoryName);
          }
        }
      }
    }
    return imports;
  }
  
 
  protected void addMethodsToNodeFactory(ASTCDClass clazz, ASTCDClass nodeFactoryClass,
      AstGeneratorHelper astHelper) {
    if (!clazz.isPresentModifier() || clazz.getModifier().isAbstract()) {
      return;
    }
    String className = GeneratorHelper.getPlainName(clazz);
    String toParse = "public static " + className + " create" + className + "() ;";
    HookPoint methodBody = new TemplateHookPoint("ast.factorymethods.Create", className);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);
    
    toParse = "protected " + className + " doCreate" + className + "() ;";
    methodBody = new TemplateHookPoint("ast.factorymethods.DoCreate", className);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);
    
    // No create methods with parameters
    if (clazz.getCDAttributeList().isEmpty()) {
      return;
    }
    
    toParse = "public static " + className + " create" + className + "() ;";
    
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(
        nodeFactoryClass, toParse);
    Preconditions.checkArgument(astMethod.isPresent());
    ASTCDMethod createMethod = astMethod.get();
    
    toParse = "protected " + className + " doCreate" + className + "() ;";
    astMethod = cdTransformation.addCdMethodUsingDefinition(
        nodeFactoryClass, toParse);
    Preconditions.checkArgument(astMethod.isPresent());
    ASTCDMethod doCreateMethod = astMethod.get();
    
    StringBuilder paramCall = new StringBuilder();
    List<ASTCDAttribute> parameters = Lists.newArrayList();
    String del = "";
    List<ASTCDAttribute> inheritedAttributes = Lists.newArrayList();
    for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
      if (GeneratorHelper.isInherited(attr)) {
        inheritedAttributes.add(attr);
        continue;
      }
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      ASTType type = attr.getType();
      parameters.add(attr);
      param.setType(type);
      String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
      param.setName(javaAttrName);
      ASTCDParameter doParam = param.deepClone();
      createMethod.getCDParameterList().add(param);
      doCreateMethod.getCDParameterList().add(doParam);
      paramCall.append(del + javaAttrName);
      del = DEL;
    }
    
    for (ASTCDAttribute attr : inheritedAttributes) {
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      ASTType type = attr.getType();
      parameters.add(attr);
      param.setType(type);
      String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
      param.setName(javaAttrName);
      ASTCDParameter doParam = param.deepClone();
      createMethod.getCDParameterList().add(param);
      doCreateMethod.getCDParameterList().add(doParam);
      paramCall.append(del + javaAttrName);
      del = DEL;
    }
    
    // create() method
    glex.replaceTemplate("ast.ParametersDeclaration", createMethod, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration"));
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, createMethod, new TemplateHookPoint(
        "ast.factorymethods.CreateWithParams", className, paramCall.toString()));
        
    // doCreate() method
    glex.replaceTemplate("ast.ParametersDeclaration", doCreateMethod, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration"));
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, doCreateMethod, new TemplateHookPoint(
        "ast.factorymethods.DoCreateWithParams", className, paramCall.toString()));
        
    if (parameters.size() != createMethod.getCDParameterList().size()) {
      glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, createMethod, new TemplateHookPoint(
          "ast.factorymethods.ErrorIfNull", parameters));
    }
    if (parameters.size() != doCreateMethod.getCDParameterList().size()) {
      glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, doCreateMethod, new TemplateHookPoint(
          "ast.factorymethods.ErrorIfNull", parameters));
    }
    
  }
  
  
  /**
   *
   * @param astHelper
   * @param cdSymbol
   * @param delegateFactoryName
   * @param cdClass
   * @param nodeFactoryForSuperCd
   */
  protected void addDelegateMethodsToNodeFactory(ASTCDClass clazz, ASTCDClass nodeFactoryClass,
      AstGeneratorHelper astHelper, CDSymbol cdSymbol, String delegateFactoryName) {
    if (!clazz.isPresentModifier() || clazz.getModifier().isAbstract()) {
      return;
    }
    String className = GeneratorHelper.getPlainName(clazz);
    String toParse = "public static " + cdSymbol.getFullName().toLowerCase()
        + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT + className + " create" + className
        + "() ;";
    HookPoint methodBody = new TemplateHookPoint("ast.factorymethods.CreateDelegate",
        delegateFactoryName, className);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);
    
    // No create methods with parameters (only additional attributes {@link
    // AstAdditionalAttributes}
    if (clazz.getCDAttributeList().size() <= 2) {
      return;
    }
    
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(
        nodeFactoryClass, toParse);
    Preconditions.checkArgument(astMethod.isPresent());
    ASTCDMethod createMethod = astMethod.get();
    
    StringBuilder paramCall = new StringBuilder();
    String del = "";
    List<ASTCDAttribute> inheritedAttributes = Lists.newArrayList();
    for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
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
      if (type instanceof ASTSimpleReferenceType) {
        type = astHelper.convertTypeCd2Java((ASTSimpleReferenceType) type,
            AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT);
      }
      param.setType(type);
      String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
      param.setName(javaAttrName);
      createMethod.getCDParameterList().add(param);
      paramCall.append(del + javaAttrName);
      del = DEL;
    }
    for (ASTCDAttribute attr : inheritedAttributes) {
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      ASTType type = attr.getType();
      if (type instanceof ASTSimpleReferenceType) {
        type = astHelper.convertTypeCd2Java((ASTSimpleReferenceType) type,
            AstGeneratorHelper.AST_PACKAGE_SUFFIX);
      }
      param.setType(type);
      String javaAttrName = GeneratorHelper.getJavaAndCdConformName(attr.getName());
      param.setName(javaAttrName);
      createMethod.getCDParameterList().add(param);
      paramCall.append(del + javaAttrName);
      del = DEL;
    }
    
    // create() method
    glex.replaceTemplate("ast.ParametersDeclaration", createMethod, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration"));
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, createMethod,
        new TemplateHookPoint(
            "ast.factorymethods.CreateWithParamsDelegate", delegateFactoryName, className,
            paramCall.toString()));
    glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, createMethod, new StringHookPoint(""));
    
  }
  
  /**
   *
   * @param astHelper
   * @param cdSymbol
   * @param delegateFactoryName
   * @param cdClass
   * @param nodeFactoryForSuperCd
   */
  protected void addDelegateMethodToMill(String name, ASTCDClass nodeMill,
      AstGeneratorHelper astHelper, CDSymbol cdSymbol, String delegateMillName) {
    String className = StringTransformations.uncapitalize(AstGeneratorHelper.getASTClassNameWithoutPrefix(name) + "Builder");
    String toParse = "public static " + cdSymbol.getFullName().toLowerCase()
        + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT + name + "Builder " + className
        + "() ;";
    HookPoint methodBody = new TemplateHookPoint("ast.ASTMillDelegateMethod",
        delegateMillName, className);
    replaceMethodBodyTemplate(nodeMill, toParse, methodBody);    
  }
  
  /**
   *
   * @param clazz
   * @param astHelper
   */
  protected void addConstructors(ASTCDClass clazz,
      AstGeneratorHelper astHelper) {
    ASTCDConstructor emptyConstructor = CD4AnalysisNodeFactory.createASTCDConstructor();
    emptyConstructor.setName(clazz.getName());
    emptyConstructor.setModifier(TransformationHelper.createProtectedModifier());
    clazz.getCDConstructorList().add(emptyConstructor);
    
    // Only one constructor
    if (clazz.getCDAttributeList().isEmpty()) {
      return;
    }
    
    ASTCDConstructor fullConstructor = emptyConstructor.deepClone();
    // TODO: Collect parameters of the super class (Symbol table)
    List<ASTCDAttribute> inheritedAttributes = Lists.newArrayList();
    for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
      if (GeneratorHelper.isInherited(attr)) {
        inheritedAttributes.add(attr);
        continue;
      }
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      param.setType((ASTType) attr.getType().deepClone());
      param.setName(attr.getName());
      fullConstructor.getCDParameterList().add(param);
    }
    for (ASTCDAttribute attr : inheritedAttributes) {
      ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
      param.setType((ASTType) attr.getType().deepClone());
      param.setName(attr.getName());
      fullConstructor.getCDParameterList().add(param);
    }
    clazz.getCDConstructorList().add(fullConstructor);
    
    glex.replaceTemplate("ast.ParametersDeclaration", fullConstructor, new TemplateHookPoint(
        "ast.ConstructorParametersDeclaration", Lists.newArrayList()));
    glex.replaceTemplate("ast.EmptyMethodBody", fullConstructor, new TemplateHookPoint(
        "ast.ConstructorAttributesSetter"));
    Optional<ASTCDClass> clazzBuilder = astHelper.getASTBuilder(clazz);
    if (clazzBuilder.isPresent()) {
      glex.replaceTemplate(
          "ast.ParametersDeclaration",
          clazzBuilder.get(),
          new TemplateHookPoint(
              "ast.BuilderConstructorParametersDeclaration", fullConstructor.getCDParameterList()));
    }
  }
  
  /**
   *
   * @param cdDefinition
   * @param astHelper
   * @throws ANTLRException
   */
  protected void addConstantsClass(ASTCDDefinition cdDefinition, AstGeneratorHelper astHelper) {
    String enumLiterals = cdDefinition.getName() + ConstantsTranslation.CONSTANTS_ENUM;
    Optional<ASTCDEnum> enumConstans = cdDefinition.getCDEnumList().stream()
        .filter(e -> e.getName().equals(enumLiterals)).findAny();
    if (!enumConstans.isPresent()) {
      Log.error("0xA1004 CdDecorator error: " + enumLiterals
          + " class can't be created for the class diagramm "
          + cdDefinition.getName());
      return;
    }
    
    String constantsClassName = "ASTConstants" + cdDefinition.getName();
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
        CLASS_CONTENT_TEMPLATE,
        astConstantsClass,
        new TemplateHookPoint(
            "ast.ASTConstantsClass", astConstantsClass, astHelper.getQualifiedCdName(), astHelper
                .getSuperGrammarCds()));
    for (ASTCDEnumConstant astConstant : enumConstans.get().getCDEnumConstantList()) {
      ASTCDAttribute constAttr = CD4AnalysisNodeFactory.createASTCDAttribute();
      constAttr.setName(astConstant.getName());
      astConstantsClass.getCDAttributeList().add(constAttr);
    }
    // cdDefinition.getCDEnumList().remove(enumConstans.get());
  }
  
  /**
   *
   * @param cdDefinition
   * @param astHelper
   */
  protected void decorateBaseInterface(ASTCDDefinition cdDefinition) {
    List<ASTCDInterface> stream = cdDefinition
        .getCDInterfaceList()
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
    baseInterface.getInterfaceList().add(
        TransformationHelper.createSimpleReference(GeneratorHelper.AST_NODE));
    glex.replaceTemplate(
        "ast.AstInterfaceContent",
        baseInterface,
        new TemplateHookPoint(
            "ast.ASTNodeBase", baseInterface));
            
  }
  
  /**
   *
   * @param cdCompilationUnit
   */
  protected void transformCdTypeNamesForHWTypes(ASTCDCompilationUnit cdCompilationUnit) {
    String packageName = TransformationHelper.getAstPackageName(cdCompilationUnit);
    
    cdCompilationUnit
        .getCDDefinition()
        .getCDClassList()
        .forEach(
            c -> c.setName(GeneratorHelper.getSimpleTypeNameToGenerate(c.getName(), packageName,
                targetPath)));
    cdCompilationUnit
        .getCDDefinition()
        .getCDInterfaceList()
        .forEach(
            c -> c.setName(GeneratorHelper.getSimpleTypeNameToGenerate(c.getName(), packageName,
                targetPath)));
    cdCompilationUnit
        .getCDDefinition()
        .getCDEnumList()
        .forEach(
            c -> c.setName(GeneratorHelper.getSimpleTypeNameToGenerate(c.getName(), packageName,
                targetPath)));
  }
  
  /**
   * Performs ast specific template replacements using {@link HookPoint}
   * 
   * @param ast
   * @param replacedTemplateName qualified name of template to be replaced
   */
  protected ASTCDMethod replaceMethodBodyTemplate(ASTCDType clazz, String methodSignatur,
      HookPoint hookPoint) {
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(clazz,
        methodSignatur);
    Preconditions.checkArgument(astMethod.isPresent());
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, astMethod.get(), hookPoint);
    return astMethod.get();
  }
  
  /**
   * Performs list-valued attribute specific template replacements using
   * {@link HookPoint}
   * 
   * @param ast
   * @param replacedTemplateName qualified name of template to be replaced
   */
  protected ASTCDMethod additionalMethodForListAttribute(ASTCDClass clazz, String callMethod,
      ASTCDAttribute attribute, String methodSignatur, boolean returnBuilder, boolean isInherited) {
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(clazz,
        methodSignatur);
    Preconditions.checkArgument(astMethod.isPresent());
    List<ASTCDParameter> parameters = astMethod.get().getCDParameterList();
    String callParameters = Joiners.COMMA
        .join(parameters.stream().map(ASTCDParameter::getName).collect(Collectors.toList()));
    String call;
    if(isInherited) {
      call = "super." + astMethod.get().getName();
    }
    else {
      call = "this." + attribute.getName() + "." + callMethod;
    }
    HookPoint hookPoint = new TemplateHookPoint(
        "ast.additionalmethods.ListAttributeMethod", call,
        !AstGeneratorHelper.hasReturnTypeVoid(astMethod.get()), callParameters, returnBuilder);
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, astMethod.get(), hookPoint);
    glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, astMethod.get(), new StringHookPoint(""));
    return astMethod.get();
  }

  protected ASTCDMethod additionalMethodForListAttribute(ASTCDClass clazz, String callMethod,
      ASTCDAttribute attribute, String methodSignatur) {
    return additionalMethodForListAttribute(clazz, callMethod, attribute, methodSignatur, false, false);
  }

  protected ASTCDMethod additionalNodeBuilderMethod(ASTCDClass clazz, String callMethod,
      String methodSignatur) {
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(clazz,
      methodSignatur);
      Preconditions.checkArgument(astMethod.isPresent());
    List<ASTCDParameter> parameters = astMethod.get().getCDParameterList();
    String callParameters = Joiners.COMMA
      .join(parameters.stream().map(ASTCDParameter::getName).collect(Collectors.toList()));
    HookPoint hookPoint = new TemplateHookPoint(
      "ast.additionalmethods.NodeBuilderMethod", callMethod, callParameters);
      glex.replaceTemplate(EMPTY_BODY_TEMPLATE, astMethod.get(), hookPoint);
      glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, astMethod.get(), new StringHookPoint(""));
      return astMethod.get();
  }
}
