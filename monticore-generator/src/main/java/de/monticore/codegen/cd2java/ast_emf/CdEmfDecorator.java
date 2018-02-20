/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.ast_emf;

import static de.monticore.codegen.GeneratorHelper.getPlainName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstAdditionalAttributes;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.ast.CdDecorator;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.transl.ConstantsTranslation;
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
import de.monticore.types.types._ast.TypesMill;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisInheritanceVisitor;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import groovyjarjarantlr.ANTLRException;
import transformation.ast.ASTCDRawTransformation;

/**
 * Decorates class diagrams by adding of new classes and methods using in emf
 * compatible ast files
 *
 */
public class CdEmfDecorator extends CdDecorator {
  
  public static final String EFACTORY = "Factory";
  
  public static final String EFACTORY_IMPL = "FactoryImpl";
  
  public static final String EPACKAGE = "Package";
  
  public static final String EPACKAGE_IMPL = "PackageImpl";
  
  public static final String HTTP = "http://";
  
  private Map<ASTCDType, List<EmfAttribute>> emfAttributes = new LinkedHashMap<>();
  
  public CdEmfDecorator(
      GlobalExtensionManagement glex,
      GlobalScope symbolTable,
      IterablePath targetPath) {
    super(glex, symbolTable, targetPath);
  }
  
  public void decorate(ASTCDCompilationUnit cdCompilationUnit) {
    AstEmfGeneratorHelper astHelper = new AstEmfGeneratorHelper(cdCompilationUnit, symbolTable);
    
    // Run over classdiagramm and collects external emf types
    ETypeCollector emfCollector = new ETypeCollector(astHelper);
    emfCollector.handle(cdCompilationUnit.getCDDefinition());
    
    ASTCDDefinition cdDefinition = cdCompilationUnit.getCDDefinition();
    
    List<ASTCDClass> nativeClasses = Lists.newArrayList(cdDefinition.getCDClassList());
    List<ASTCDType> nativeTypes = astHelper.getNativeTypes(cdDefinition);
    
    List<ASTCDClass> astNotAbstractClasses = cdDefinition.getCDClassList().stream()
        .filter(e -> e.isPresentModifier())
        .filter(e -> !e.getModifier().isAbstract())
        .collect(Collectors.toList());
        
    // Run over classdiagramm and converts cd types to mc-java types
    astHelper.transformCdTypes2Java();
    
    createEmfAttributes(astHelper, emfCollector, nativeTypes);
    
    // Interface for all ast nodes of the language
    decorateBaseInterface(cdDefinition);
    
    // Decorate with builder pattern
    addBuilders(cdDefinition, astHelper);
    
    addNodeFactoryClass(cdCompilationUnit, astNotAbstractClasses, astHelper);
    
    addMillClass(cdCompilationUnit, nativeClasses, astHelper);
    
    // Check if handwritten ast types exist
    transformCdTypeNamesForHWTypes(cdCompilationUnit);
    
    cdDefinition.getCDClassList().stream()
        .forEach(c -> addSuperInterfaces(c));
        
    // Decorate with additional methods and attributes
    for (ASTCDClass clazz : nativeClasses) {
      addConstructors(clazz, astHelper);
      addAdditionalMethods(clazz, astHelper);
      addListMethods(clazz, astHelper, cdDefinition);
 //     addAdditionalAttributes(clazz, astHelper);
      addGetter(clazz, astHelper);
      addSetter(clazz, astHelper);
      addOptionalMethods(clazz, astHelper, cdDefinition);
      addSymbolGetter(clazz, astHelper);
      addNodeGetter(clazz, astHelper);

      Optional<ASTCDClass> builder = astHelper.getASTBuilder(clazz);
      builder.ifPresent(astcdClass -> decorateBuilderClass(astcdClass, astHelper, cdDefinition));
      
      glex.replaceTemplate("ast.AstImports", clazz, new TemplateHookPoint("ast_emf.AstEImports"));
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
            
    addEmfCode(cdCompilationUnit, nativeClasses, nativeTypes, astHelper,
        emfCollector.getExternalTypes());
        
  }
  
  /**
   *
   * @param astHelper
   * @param astNotListClasses
   */
  void createEmfAttributes(AstEmfGeneratorHelper astHelper, ETypeCollector emfCollector,
      List<ASTCDType> astTypes) {
    emfAttributes.clear();
    astTypes.stream().filter(t -> t instanceof ASTCDClass)
        .forEach(t -> ((ASTCDClass) t).getCDAttributeList().stream()
            .filter(a -> !(getAdditionaAttributeNames().anyMatch(ad -> ad.equals(a.getName()))))
            .forEach(a -> createEmfAttribute(t, a, astHelper, emfCollector)));
    astTypes.stream().filter(t -> t instanceof ASTCDInterface)
        .forEach(t -> ((ASTCDInterface) t).getCDAttributeList().stream()
            .filter(a -> !(getAdditionaAttributeNames().anyMatch(ad -> ad.equals(a.getName()))))
            .forEach(a -> createEmfAttribute(t, a, astHelper, emfCollector)));
    emfAttributes.keySet()
        .forEach(t -> AstEmfGeneratorHelper.sortEmfAttributes(emfAttributes.get(t)));
  }
  
  Stream<String> getAdditionaAttributeNames() {
    return Arrays.asList(AstAdditionalAttributes.values()).stream().map(a -> a.toString());
  }
  
  /**
   *
   * @param astClasses
   * @param map.
   */
  void addEmfCode(ASTCDCompilationUnit cdCompilationUnit, List<ASTCDClass> astClasses,
      List<ASTCDType> types, AstEmfGeneratorHelper astHelper, Map<String, String> map) {
      
    // addEFactoryInterface(cdCompilationUnit, types, astHelper);
    // addEFactoryImplementation(cdCompilationUnit, astClasses, astHelper);
    addEPackageInterface(cdCompilationUnit, types, map.values(), astHelper);
    addEPackageImplementation(cdCompilationUnit, types, map, astHelper);
    addLiteralsEnum(cdCompilationUnit, astHelper);
    
    // Decorate with additional EMF methods and attributes
    for (ASTCDClass clazz : astClasses) {
      addEGetter(clazz, astHelper);
      addESetter(clazz, astHelper);
      addEUnset(clazz, astHelper);
      addEIsSet(clazz, astHelper);
      addStructuralFeatureMethods(clazz, astHelper);
      // addValuesForEListAttributes(clazz, astHelper);
      addToString(clazz, astHelper);
      addEStaticClass(clazz, astHelper);
    }
  }
  
  void addEFactoryInterface(ASTCDCompilationUnit cdCompilationUnit, List<ASTCDType> astClasses,
      AstEmfGeneratorHelper astHelper) {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDInterface factory = CD4AnalysisNodeFactory.createASTCDInterface();
    String factoryName = cdDef.getName() + EFACTORY;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + factoryName)) {
      factoryName += GeneratorSetup.GENERATED_CLASS_SUFFIX;
    }
    factory.setName(factoryName);
    cdDef.getCDInterfaceList().add(factory);
    
    for (ASTCDType clazz : astClasses) {
      if (!clazz.getModifierOpt().isPresent() || clazz.getModifierOpt().get().isAbstract()) {
        return;
      }
      String className = GeneratorHelper.getPlainName(clazz);
      String toParse = "public static " + className + " create" + className + "() ;";
      HookPoint methodBody = new TemplateHookPoint("ast.factorymethods.Create", className);
      replaceMethodBodyTemplate(factory, toParse, methodBody);
    }
    List<String> classNames = astClasses.stream().map(e -> getPlainName(e))
        .collect(Collectors.toList());
        
    glex.replaceTemplate("ast.AstInterfaceContent", factory, new TemplateHookPoint(
        "ast_emf.EFactory", factory, cdDef.getName(), HTTP + cdDef.getName()
            + "/1.0",
        classNames));
  }
  
  void addEFactoryImplementation(ASTCDCompilationUnit cdCompilationUnit,
      List<ASTCDClass> astClasses, AstEmfGeneratorHelper astHelper) {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDClass factoryClass = CD4AnalysisNodeFactory.createASTCDClass();
    String factoryClassName = cdDef.getName() + EFACTORY_IMPL;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + factoryClassName)) {
      factoryClassName += GeneratorSetup.GENERATED_CLASS_SUFFIX;
    }
    factoryClass.setName(factoryClassName);
    
    List<String> classNames = astClasses.stream()
        .filter(e -> e.getModifierOpt().isPresent())
        .filter(e -> !e.getModifier().isAbstract())
        .map(e -> getPlainName(e))
        .collect(Collectors.toList());
        
    cdDef.getCDClassList().add(factoryClass);
    glex.replaceTemplate(CLASS_CONTENT_TEMPLATE, factoryClass, new TemplateHookPoint(
        "ast_emf.EFactoryImpl", factoryClass, cdDef.getName(), HTTP + cdDef.getName()
            + "/1.0",
        classNames));
        
  }
  
  void addEPackageInterface(ASTCDCompilationUnit cdCompilationUnit, List<ASTCDType> astTypes,
      Collection<String> externaltypes, AstEmfGeneratorHelper astHelper) {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDInterface packageInterface = CD4AnalysisNodeFactory.createASTCDInterface();
    String interfaceName = cdDef.getName() + EPACKAGE;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + interfaceName)) {
      interfaceName += GeneratorSetup.GENERATED_CLASS_SUFFIX;
    }
    packageInterface.setName(interfaceName);
    cdDef.getCDInterfaceList().add(packageInterface);
    
    for (ASTCDType type : astTypes) {
      List<CDTypeSymbol> superTypes = astHelper.getAllSuperTypesEmfOrder(type);
      int count = 0;
      for (CDTypeSymbol interf : superTypes) {
        List<CDFieldSymbol> attributes = GeneratorHelper.getVisibleFields(interf).stream()
            .filter(e -> !astHelper.isAttributeOfSuperType(e, interf))
            .collect(Collectors.toList());
        for (int i = count; i < count + attributes.size(); i++) {
          String toParseAttr = "int " + getPlainName(type) + "_"
              + StringTransformations.capitalize(attributes.get(i - count).getName()) + " = " + i
              + ";";
          cdTransformation.addCdAttributeUsingDefinition(packageInterface, toParseAttr);
        }
        count += attributes.size();
      }
      List<EmfAttribute> attributes = getEmfAttributes(type).stream()
          .filter(e -> !astHelper.isAttributeOfSuperType(e.getCdAttribute(), type))
          .collect(Collectors.toList());
      for (int i = count; i < count + attributes.size(); i++) {
        EmfAttribute emfAttribute = attributes.get(i - count);
        String toParseAttr = "int " + emfAttribute.getFullName() + " = " + i + ";";
        cdTransformation.addCdAttributeUsingDefinition(packageInterface, toParseAttr);
        String toParse = emfAttribute.getEmfType() + " get" + emfAttribute.getFullName() + "();";
        cdTransformation.addCdMethodUsingDefinition(packageInterface,
            toParse);
      }
    }
    
    int i = astTypes.size() + 1;
    for (String typeName : externaltypes) {
      String toParseAttr = "int " + typeName + " = " + i + ";";
      cdTransformation.addCdAttributeUsingDefinition(packageInterface, toParseAttr);
      String toParse = "EDataType get" + typeName + "();";
      cdTransformation.addCdMethodUsingDefinition(packageInterface,
          toParse);
      i++;
    }
    
    List<String> classNames = astTypes.stream().map(e -> getPlainName(e))
        .collect(Collectors.toList());
        
    glex.replaceTemplate("ast.AstInterfaceContent", packageInterface,
        new TemplateHookPoint(
            "ast_emf.EPackage", packageInterface, cdDef.getName(), HTTP + cdDef.getName()
                + "/1.0",
            classNames));
  }
  
  void addEPackageImplementation(ASTCDCompilationUnit cdCompilationUnit,
      List<ASTCDType> astClasses, Map<String, String> externaltypes,
      AstEmfGeneratorHelper astHelper) {
    ASTCDDefinition cdDef = cdCompilationUnit.getCDDefinition();
    ASTCDClass packageImpl = CD4AnalysisNodeFactory.createASTCDClass();
    String className = cdDef.getName() + EPACKAGE_IMPL;
    
    // Check if a handwritten node factory exists
    if (TransformationHelper.existsHandwrittenClass(targetPath,
        TransformationHelper.getAstPackageName(cdCompilationUnit)
            + className)) {
      className += GeneratorSetup.GENERATED_CLASS_SUFFIX;
    }
    packageImpl.setName(className);
    
    List<String> classNames = astClasses.stream().map(e -> getPlainName(e))
        .collect(Collectors.toList());
        
    for (String clazz : classNames) {
      String toParse = "protected static " + className + " factory" + clazz + " = null;";
      cdTransformation.addCdAttributeUsingDefinition(packageImpl, toParse);
    }
    
    for (ASTCDType type : emfAttributes.keySet()) {
      List<EmfAttribute> allEmfAttrbutes = getNotInheritedEmfAttributes(type, astHelper);
      for (int i = 0; i < allEmfAttrbutes.size(); i++) {
        EmfAttribute emfAttribute = allEmfAttrbutes.get(i);
        String toParse = "public " + emfAttribute.getEmfType() + " get" + emfAttribute.getFullName()
            + "();";
        HookPoint getMethodBody = new StringHookPoint("return (" + emfAttribute.getEmfType() + ")"
            + StringTransformations
                .uncapitalize(getPlainName(emfAttribute.getCdType()).substring(3))
            + "EClass.getEStructuralFeatures().get(" + i + ");");
        replaceMethodBodyTemplate(packageImpl, toParse, getMethodBody);
      }
    }
    
    for (String typeName : externaltypes.values()) {
      String toParse = "public EDataType get" + typeName + "();";
      HookPoint getMethodBody = new StringHookPoint(
          "return " + StringTransformations.uncapitalize(typeName) + "EDataType;");
      replaceMethodBodyTemplate(packageImpl, toParse, getMethodBody);
    }
    
    List<EmfAttribute> allEmfAttrbutes = getAllNotInheritedEmfAttributes(astHelper);
    
    String toParse = "public void createPackageContents();";
    HookPoint getMethodBody = new TemplateHookPoint(
        "ast_emf.epackagemethods.CreatePackageContents", cdDef.getName(), astClasses,
        allEmfAttrbutes, externaltypes.values());
    replaceMethodBodyTemplate(packageImpl, toParse, getMethodBody);
    
    List<String> superCDs = astHelper.getAllSuperCds(astHelper.getCdSymbol()).stream()
        .map(CDSymbol::getFullName).collect(Collectors.toList());
    toParse = "public void initializePackageContents();";
    getMethodBody = new TemplateHookPoint(
        "ast_emf.epackagemethods.InitializePackageContents", cdDef.getName(),
        superCDs, astClasses,
        allEmfAttrbutes, externaltypes,
        GeneratorHelper.getValuesOfConstantEnum(astHelper.getCdDefinition()));
    replaceMethodBodyTemplate(packageImpl, toParse, getMethodBody);
    
    cdDef.getCDClassList().add(packageImpl);
    
    glex.replaceTemplate(CLASS_CONTENT_TEMPLATE, packageImpl, new TemplateHookPoint(
        "ast_emf.EPackageImpl", packageImpl, cdDef.getName(), classNames, externaltypes.values()));
  }
  
  void addSetter(ASTCDClass clazz, AstEmfGeneratorHelper astHelper) {
    for (EmfAttribute attribute : getEmfAttributes(clazz)) {
      ASTCDAttribute cdAttribute = attribute.getCdAttribute();
      String typeName = TypesHelper.printSimpleRefType(cdAttribute.getType());
      if (!AstGeneratorHelper.generateSetter(clazz, cdAttribute, typeName)) {
        continue;
      }
      String methodName = GeneratorHelper.getPlainSetter(cdAttribute);
      String attributeName = cdAttribute.getName();
      boolean isOptional = GeneratorHelper.isOptional(cdAttribute);
      String toParse = "public void " + methodName + "("
          + typeName + " " + attributeName + ") ;";
      HookPoint methodBody = new TemplateHookPoint("ast_emf.additionalmethods.Set",
          astHelper.getCdName(),
          attribute, attributeName);
      ASTCDMethod setMethod = replaceMethodBodyTemplate(clazz, toParse, methodBody);
      
      if (isOptional) {
        glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, setMethod, new StringHookPoint(""));
      }
      
    }
  }
  
  /**
   * Adds getter for all attributes of ast classes
   * 
   * @param clazz
   * @param astHelper
   * @throws ANTLRException
   */
  void addEGetter(ASTCDClass clazz, AstEmfGeneratorHelper astHelper) {
    String toParse = "public Object eGet(int featureID, boolean resolve, boolean coreType);";
    HookPoint getMethodBody = new TemplateHookPoint("ast_emf.additionalmethods.EGet",
        astHelper.getCdName(), astHelper.getAllVisibleFields(clazz));
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
  }
  
  /**
   * Adds setter for all attributes of ast classes
   * 
   * @param clazz
   * @param astHelper
   * @throws ANTLRException
   */
  void addESetter(ASTCDClass clazz, AstEmfGeneratorHelper astHelper) {
    String toParse = "public void eSet(int featureID, Object newValue);";
    HookPoint getMethodBody = new TemplateHookPoint("ast_emf.additionalmethods.ESet",
        astHelper.getCdName(), astHelper.getAllVisibleFields(clazz));
    Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(clazz,
        toParse);
    Preconditions.checkArgument(astMethod.isPresent());
    glex.replaceTemplate(EMPTY_BODY_TEMPLATE, astMethod.get(), getMethodBody);
    glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, astMethod.get(), new StringHookPoint(""));
  }
  
  /**
   *
   * @param clazz
   * @param astHelper
   */
  @Override
  protected void addAdditionalAttributes(ASTCDClass clazz, AstGeneratorHelper astHelper) {
    // Add Symbol attribute
    Optional<ASTCDAttribute> attribute = cdTransformation.addCdAttributeUsingDefinition(clazz,
        AstAdditionalAttributes.symbol.getDeclaration());
    addSetterForAdditionalAttribute(clazz, attribute.get(), "Symbol",
        AstAdditionalAttributes.symbol.toString(), true);
    // Add Scope attribute
    attribute = cdTransformation.addCdAttributeUsingDefinition(clazz,
        AstAdditionalAttributes.enclosingScope.getDeclaration());
    addSetterForAdditionalAttribute(clazz, attribute.get(), "Scope",
        AstAdditionalAttributes.enclosingScope.toString(), true);
  }
  
  private void addSetterForAdditionalAttribute(ASTCDClass clazz, ASTCDAttribute attribute,
      String typeName, String attributeName, boolean isOptional) {
    String toParse = "public void set" + StringTransformations.capitalize(attributeName) + "("
        + typeName + " " + attributeName + ") ;";
    HookPoint methodBody = new TemplateHookPoint("ast.additionalmethods.Set",
        attribute, attributeName);
    ASTCDMethod setMethod = replaceMethodBodyTemplate(clazz, toParse, methodBody);
    
    if (isOptional) {
      glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, setMethod, new StringHookPoint(""));
    }
    
    if (isOptional) {
      toParse = "public boolean " + attributeName + "IsPresent() ;";
      methodBody = new StringHookPoint("  return " + attributeName + ".isPresent(); \n");
      replaceMethodBodyTemplate(clazz, toParse, methodBody);
    }
  }
  
  /**
   * Adds setter for all attributes of ast classes
   * 
   * @param clazz
   * @param astHelper
   * @throws ANTLRException
   */
  void addEUnset(ASTCDClass clazz, AstEmfGeneratorHelper astHelper) {
    String toParse = "public void eUnset(int featureID);";
    HookPoint getMethodBody = new TemplateHookPoint("ast_emf.additionalmethods.EUnset",
        astHelper.getCdName(), astHelper.getAllVisibleFields(clazz));
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
  }
  
  /**
   * Adds setter for all attributes of ast classes
   * 
   * @param clazz
   * @param astHelper
   * @throws ANTLRException
   */
  void addEIsSet(ASTCDClass clazz, AstEmfGeneratorHelper astHelper) {
    String toParse = "public boolean eIsSet(int featureID);";
    HookPoint getMethodBody = new TemplateHookPoint("ast_emf.additionalmethods.EIsSet",
        astHelper.getCdName(), astHelper.getAllVisibleFields(clazz));
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
  }
  
  /**
   * Adds overriding for the eBaseStructuralFeatureID method i this class has an
   * interfaces
   * 
   * @param clazz
   * @param astHelper
   */
  void addStructuralFeatureMethods(ASTCDClass clazz, AstEmfGeneratorHelper astHelper) {
    String methodName = "eBaseStructuralFeatureID";
    String toParse = "public int " + methodName + "(int featureID, Class<?> baseClass);";
    HookPoint getMethodBody = new TemplateHookPoint("ast_emf.additionalmethods.EStructuralFeature",
        methodName, astHelper.getAllSuperInterfaces(clazz));
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
    
    methodName = "eDerivedStructuralFeatureID";
    toParse = "public int " + methodName + "(int featureID, Class<?> baseClass);";
    getMethodBody = new TemplateHookPoint("ast_emf.additionalmethods.EStructuralFeature",
        methodName, astHelper.getAllSuperInterfaces(clazz));
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
  }
  
  /**
   *
   * @param clazz
   * @param astHelper
   */
  void addToString(ASTCDClass clazz, AstEmfGeneratorHelper astHelper) {
    if (clazz.getCDMethodList().stream().anyMatch(m -> "toString".equals(m.getName()))) {
      return;
    }
    String toParse = "public String toString();";
    HookPoint getMethodBody = new TemplateHookPoint("ast_emf.additionalmethods.EToString",
        astHelper.getCdName(), getEmfAttributes(clazz));
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
  }
  
  /**
   *
   * @param clazz
   * @param astHelper
   */
  void addEStaticClass(ASTCDClass clazz, AstEmfGeneratorHelper astHelper) {
    String toParse = "protected EClass eStaticClass();";
    HookPoint getMethodBody = new StringHookPoint("return " + astHelper.getCdName()
        + "Package.Literals." + GeneratorHelper.getPlainName(clazz) + ";");
    replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
  }
  
  void addEmfAttribute(ASTCDType type, EmfAttribute attribute) {
    List<EmfAttribute> attributes = emfAttributes.get(type);
    if (attributes == null) {
      attributes = new ArrayList<>();
      emfAttributes.put(type, attributes);
    }
    attributes.add(attribute);
  }
  
  void createEmfAttribute(ASTCDType ast, ASTCDAttribute cdAttribute,
      AstEmfGeneratorHelper astHelper, ETypeCollector eTypeCollector) {
    String attributeName = getPlainName(ast) + "_"
        + StringTransformations.capitalize(GeneratorHelper.getNativeAttributeName(cdAttribute
            .getName()));
    boolean isAstNode = astHelper.isAstNode(cdAttribute)
        || astHelper.isOptionalAstNode(cdAttribute);
    boolean isAstList = astHelper.isListAstNode(cdAttribute);
    boolean isOptional = AstGeneratorHelper.isOptional(cdAttribute);
    boolean isInherited = astHelper.attributeDefinedInOtherCd(cdAttribute);
    boolean isEnum = !isAstNode && astHelper.isAttributeOfTypeEnum(cdAttribute);
    boolean hasExternalType = eTypeCollector
        .isExternalType(astHelper.getNativeTypeName(cdAttribute));
    String eDataType = createEDataType(cdAttribute, isAstList, astHelper, eTypeCollector);
    addEmfAttribute(ast,
        new EmfAttribute(cdAttribute, ast, attributeName, eDataType,
            astHelper.getDefinedGrammarName(cdAttribute),
            isAstNode, isAstList, isOptional, isInherited, astHelper.isExternal(cdAttribute),
            isEnum, hasExternalType));
  }
  
  List<EmfAttribute> getEmfAttributes(ASTCDType type) {
    List<EmfAttribute> attributes = new ArrayList<>();
    if (emfAttributes.containsKey(type)) {
      attributes.addAll(emfAttributes.get(type));
    }
    return attributes;
  }
  
  List<EmfAttribute> getNotInheritedEmfAttributes(ASTCDType type, AstEmfGeneratorHelper astHelper) {
    List<EmfAttribute> attributes = new ArrayList<>();
    if (emfAttributes.containsKey(type)) {
      emfAttributes.get(type).stream()
          .filter(e -> !astHelper.isAttributeOfSuperType(e.getCdAttribute(), type))
          .forEach(attributes::add);
    }
    return attributes;
  }
  
  List<EmfAttribute> getEAttributes(ASTCDType type) {
    if (!emfAttributes.containsKey(type)) {
      return new ArrayList<>();
    }
    return emfAttributes.get(type).stream().filter(e -> e.isEAttribute())
        .collect(Collectors.toList());
  }
  
  List<EmfAttribute> getEReferences(ASTCDType type) {
    if (!emfAttributes.containsKey(type)) {
      return new ArrayList<>();
    }
    return emfAttributes.get(type).stream().filter(e -> e.isEReference())
        .collect(Collectors.toList());
  }
  
  List<EmfAttribute> getAllEmfAttributes() {
    List<EmfAttribute> attributes = new ArrayList<>();
    emfAttributes.keySet().stream().forEach(t -> attributes.addAll(getEmfAttributes(t)));
    return attributes;
  }
  
  List<EmfAttribute> getAllNotInheritedEmfAttributes(AstEmfGeneratorHelper astHelper) {
    List<EmfAttribute> attributes = new ArrayList<>();
    emfAttributes.keySet().stream()
        .forEach(t -> attributes.addAll(getNotInheritedEmfAttributes(t, astHelper)));
    return attributes;
  }
  
  String createEDataType(ASTCDAttribute cdAttribute, boolean isAstList,
      AstEmfGeneratorHelper astHelper, ETypeCollector eTypeCollector) {
    if (isAstList || AstEmfGeneratorHelper.istJavaList(cdAttribute)) {
      Optional<ASTSimpleReferenceType> typeArg = TypesHelper
          .getFirstTypeArgumentOfGenericType(cdAttribute.getType(), GeneratorHelper.JAVA_LIST);
      if (typeArg.isPresent()) {
        return Names.getSimpleName(TypesHelper
            .printType(typeArg.get()));
      }
    }
    String nativeType = astHelper.getNativeTypeName(cdAttribute);
    Optional<String> externalType = eTypeCollector.getExternalType(nativeType);
    if (externalType.isPresent()) {
      return externalType.get();
    }
    return Names.getSimpleName(nativeType);
  }
  
  // TODO GV: not used now
  protected void addAdditionalCreateMethods(ASTCDClass nodeFactoryClass, ASTCDClass clazz) {
    String className = GeneratorHelper.getPlainName(clazz);
    String params = "owner, featureID";
    String toParse = "public static " + className + " create" + className
        + "(InternalEObject owner, int featureID) ;";
    HookPoint methodBody = new TemplateHookPoint("ast.factorymethods.Create", className,
        params);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);
    
    toParse = "protected " + className + " doCreate" + className
        + "(InternalEObject owner, int featureID) ;";
    methodBody = new TemplateHookPoint("ast.factorymethods.DoCreate", className, params);
    replaceMethodBodyTemplate(nodeFactoryClass, toParse, methodBody);
  }
  
  /**
   * @param cdCompilationUnit
   * @param nativeClasses
   * @param astHelper
   * @throws ANTLRException
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
    
    List<String> classNames = nativeClasses.stream()
        .map(e -> GeneratorHelper.getPlainName(e))
        .collect(Collectors.toList());
        
    glex.replaceTemplate(CLASS_CONTENT_TEMPLATE, nodeFactoryClass, new TemplateHookPoint(
        "ast_emf.AstNodeFactory", nodeFactoryClass, imports, classNames));
        
  }
  
  void addLiteralsEnum(ASTCDCompilationUnit ast, AstGeneratorHelper astHelper) {
    ASTCDDefinition cdDefinition = ast.getCDDefinition();
    String constantsEnumName = cdDefinition.getName() + ConstantsTranslation.CONSTANTS_ENUM;
    Optional<ASTCDEnum> enumConstants = cdDefinition.getCDEnumList().stream()
        .filter(e -> e.getName().equals(constantsEnumName)).findAny();
    if (!enumConstants.isPresent()) {
      Log.error("0xA5000 CdDecorator error: " + constantsEnumName
          + " class can't be created for the class diagramm "
          + cdDefinition.getName());
      return;
    }
    
    ASTCDEnum astEnum = enumConstants.get();
    astEnum.getCDEnumConstantList().add(0, CD4AnalysisMill.cDEnumConstantBuilder().setName("DEFAULT").build());
    astEnum.getInterfaceList()
        .add(new ASTCDRawTransformation().createType("org.eclipse.emf.common.util.Enumerator"));
        
    // Add methods of the implemented interface {@link Enumerator}
    String toParse = "public String getName();";
    StringHookPoint methodBody = new StringHookPoint("  return toString(); \n");
    replaceMethodBodyTemplate(astEnum, toParse, methodBody);
    
    toParse = "public String getLiteral();";
    methodBody = new StringHookPoint("  return toString(); \n");
    replaceMethodBodyTemplate(astEnum, toParse, methodBody);
    
    toParse = "public int getValue();";
    methodBody = new StringHookPoint("  return intValue; \n");
    replaceMethodBodyTemplate(astEnum, toParse, methodBody);
  }
  
  /**
   * Collects all external types used in the given class diagram
   */
  public class ETypeCollector implements CD4AnalysisInheritanceVisitor {
    
    private AstEmfGeneratorHelper astHelper;
    
    private Map<String, String> externalTypes = Maps.newHashMap();
    
    public Map<String, String> getExternalTypes() {
      return this.externalTypes;
    }
    
    public boolean isExternalType(String nativeType) {
      return externalTypes.containsKey(nativeType);
    }
    
    public Optional<String> getExternalType(String nativeType) {
      return Optional.ofNullable(externalTypes.get(nativeType));
    }
    
    private void addExternalType(String extType, String simpleType) {
      if (externalTypes.containsKey(extType)) {
        return;
      }
      int i = 0;
      String typeName = "E" + simpleType;
      // String typeName = AstEmfGeneratorHelper.getEDataType(simpleType);
      while (externalTypes.values().contains(typeName)) {
        typeName = typeName + i;
        i++;
      }
      externalTypes.put(extType, typeName);
    }
    
    public ETypeCollector(AstEmfGeneratorHelper astHelper) {
      this.astHelper = astHelper;
    }
    
    @Override
    public void visit(ASTSimpleReferenceType ast) {
      collectExternalTypes(ast);
    }
    
    private void collectExternalTypes(ASTSimpleReferenceType astType) {
      String genericType = "";
      ASTSimpleReferenceType convertedType = astType;
      if (AstGeneratorHelper.isOptional(astType)) {
        Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
            .getFirstTypeArgumentOfOptional(astType);
        if (!typeArgument.isPresent()) {
          return;
        }
        convertedType = typeArgument.get();
        genericType = AstGeneratorHelper.OPTIONAL;
      }
      else if (TypesHelper.isGenericTypeWithOneTypeArgument(astType,
          AstGeneratorHelper.JAVA_LIST)) {
        Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
            .getFirstTypeArgumentOfGenericType(astType, AstGeneratorHelper.JAVA_LIST);
        if (!typeArgument.isPresent()) {
          return;
        }
        convertedType = typeArgument.get();
        genericType = AstGeneratorHelper.JAVA_LIST;
      }
      String convertedTypeName = TypesPrinter.printType(convertedType);
      /* TODO GV
      if (!genericType.isEmpty() && !convertedTypeName.contains("<")) {
        String newType = "";
        Optional<CDTypeSymbol> symbol = astHelper.resolveCdType(convertedTypeName);
        if (!symbol.isPresent()) {
          if (!genericType.isEmpty()) {
            newType = genericType + "<" + convertedTypeName + ">";
          }
          else {
            newType = convertedTypeName;
          }
          addExternalType(newType, Names.getSimpleName(convertedTypeName));
        }
        else if (symbol.get().isEnum()) {
          String simpleName = Names.getSimpleName(convertedTypeName);
          convertedTypeName = symbol.get().getModelName().toLowerCase()
              + GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT + simpleName;
          addExternalType(convertedTypeName, simpleName);
        }
      }
      else {
        String typeName = Names.getQualifiedName(astType.getNames());
        addExternalType(typeName, Names.getSimpleName(convertedTypeName));
      } */
      if (!convertedTypeName.contains(".")) {
        return;
      }
      
      // TODO: GV, PN: path converter by resolving
      // TODO GV: typeArgs
      if (convertedTypeName.contains("<")) {
        return;
      }
      String newType = "";
      Optional<CDTypeSymbol> symbol = astHelper.resolveCdType(convertedTypeName);
      if (!symbol.isPresent()) {
        if (!genericType.isEmpty()) {
          newType = genericType + "<" + convertedTypeName + ">";
        }
        else {
          newType = convertedTypeName;
        }
        addExternalType(newType, Names.getSimpleName(convertedTypeName));
      }
      else if (symbol.get().isEnum()) {
        String simpleName = Names.getSimpleName(convertedTypeName);
        convertedTypeName = symbol.get().getModelName().toLowerCase()
            + GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT + simpleName;
        addExternalType(convertedTypeName, simpleName);
      }
    }
    
  }
  
}
