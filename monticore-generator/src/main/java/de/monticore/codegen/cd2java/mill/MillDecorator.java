/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSER;
import static de.monticore.codegen.cd2java.mill.MillConstants.*;
import static de.monticore.codegen.cd2java.top.TopDecorator.TOP_SUFFIX;

/**
 * created mill class for a grammar
 */
public class MillDecorator extends AbstractCreator<List<ASTCDCompilationUnit>, ASTCDClass> {

  protected final SymbolTableService symbolTableService;
  protected final VisitorService visitorService;
  protected final ParserService parserService;

  public MillDecorator(final GlobalExtensionManagement glex,
                       final SymbolTableService symbolTableService,
                       final VisitorService visitorService,
                       final ParserService parserService) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
    this.parserService = parserService;
  }

  public ASTCDClass decorate(final List<ASTCDCompilationUnit> cdList) {
    String millClassName = symbolTableService.getMillSimpleName();
    ASTMCType millType = this.getMCTypeFacade().createQualifiedType(millClassName);

    String fullDefinitionName = symbolTableService.getCDSymbol().getFullName();

    List<CDDefinitionSymbol> superSymbolList = symbolTableService.getSuperCDsTransitive();

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED, millClassName);

    ASTCDAttribute millAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX);
    // add all standard methods
    ASTCDMethod getMillMethod = addGetMillMethods(millType);
    ASTCDMethod initMethod = addInitMethod(millType, superSymbolList, fullDefinitionName);

    ASTCDClass millClass = CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(millClassName)
        .addCDAttribute(millAttribute)
        .addCDConstructor(constructor)
        .addCDMethod(getMillMethod)
        .addCDMethod(initMethod)
        .build();

    // list of all classes needed for the reset and initMe method
    List<ASTCDClass> allClasses = new ArrayList<>();

    for (ASTCDCompilationUnit cd : cdList) {
      // filter out all classes that are abstract and only builder classes
      List<ASTCDClass> classList = cd.getCDDefinition().deepClone().getCDClassList()
          .stream()
          .filter(ASTCDClass::isPresentModifier)
          .filter(x -> !x.getModifier().isAbstract())
          .filter(cdClass -> checkIncludeInMill(cdClass))
          .collect(Collectors.toList());


      // filter out all classes that are abstract and end with the TOP suffix
      List<ASTCDClass> topClassList = cd.getCDDefinition().deepClone().getCDClassList()
          .stream()
          .filter(ASTCDClass::isPresentModifier)
          .filter(x -> x.getModifier().isAbstract())
          .filter(x -> x.getName().endsWith(TOP_SUFFIX))
          .collect(Collectors.toList());
      // remove TOP suffix
      topClassList.forEach(x -> x.setName(x.getName().substring(0, x.getName().length() - 3)));
      // check if builder classes
      topClassList = topClassList
          .stream()
          .filter(cdClass -> checkIncludeInMill(cdClass))
          .collect(Collectors.toList());
      // add to classes which need a builder method
      classList.addAll(topClassList);

      // add to all class list for reset and initMe method
      allClasses.addAll(classList);

      // add mill attribute for each class
      List<ASTCDAttribute> attributeList = new ArrayList<>();
      for (String attributeName : getAttributeNameList(classList)) {
        attributeList.add(this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX + attributeName));
      }

      List<ASTCDMethod> builderMethodsList = addBuilderMethods(classList, cd);

      millClass.addAllCDAttributes(attributeList);
      millClass.addAllCDMethods(builderMethodsList);
    }
    
    // decorate for traverser
    String traverserAttributeName = MILL_INFIX + visitorService.getTraverserSimpleName();
    ASTCDAttribute traverserAttribute = getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, traverserAttributeName);
    List<ASTCDMethod> traverserMethods = getAttributeMethods(visitorService.getTraverserSimpleName(),
        visitorService.getTraverserFullName(), TRAVERSER, visitorService.getTraverserInterfaceFullName());
    millClass.addCDAttribute(traverserAttribute);
    millClass.addAllCDMethods(traverserMethods);
    allClasses.add(CD4AnalysisMill.cDClassBuilder().setName(visitorService.getTraverserSimpleName()).build());

    // decorate for global scope
    //globalScope
    String millGlobalScopeAttributeName = MILL_INFIX + symbolTableService.getGlobalScopeSimpleName();
    ASTCDAttribute millGlobalScopeAttribute = getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, millGlobalScopeAttributeName);
    String globalScopeAttributeName = StringTransformations.uncapitalize(symbolTableService.getGlobalScopeSimpleName());
    ASTCDAttribute globalScopeAttribute = getCDAttributeFacade().createAttribute(PROTECTED, symbolTableService.getGlobalScopeInterfaceType(),globalScopeAttributeName);
    List<ASTCDMethod> globalScopeMethods = getGlobalScopeMethods(globalScopeAttribute);
    millClass.addCDAttribute(millGlobalScopeAttribute);
    millClass.addCDAttribute(globalScopeAttribute);
    millClass.addAllCDMethods(globalScopeMethods);

    //artifactScope
    String millArtifactScopeAttributeName = MILL_INFIX + symbolTableService.getArtifactScopeSimpleName();
    ASTCDAttribute millArtifactScopeAttribute = getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, millArtifactScopeAttributeName);
    millClass.addCDAttribute(millArtifactScopeAttribute);
    millClass.addAllCDMethods(getArtifactScopeMethods());


    if(!symbolTableService.hasComponentStereotype(symbolTableService.getCDSymbol().getAstNode())) {
      ASTCDAttribute parserAttribute = getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX + "Parser");
      List<ASTCDMethod> parserMethods = getParserMethods();
      millClass.addCDAttribute(parserAttribute);
      millClass.addAllCDMethods(parserMethods);
    }
    //scope
    String millScopeAttributeName = MILL_INFIX + symbolTableService.getScopeClassSimpleName();
    ASTCDAttribute millScopeAttribute = getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, millScopeAttributeName);
    millClass.addCDAttribute(millScopeAttribute);
    millClass.addAllCDMethods(getScopeMethods());

    // add builder methods for each class
    List<ASTCDMethod> superMethodsList = addSuperBuilderMethods(superSymbolList, allClasses);
    millClass.addAllCDMethods(superMethodsList);

    ASTCDMethod initMeMethod = addInitMeMethod(millType, allClasses);
    millClass.addCDMethod(initMeMethod);

    ASTCDMethod resetMethod = addResetMethod(allClasses, superSymbolList);
    millClass.addCDMethod(resetMethod);

    return millClass;
  }

  protected boolean checkIncludeInMill(ASTCDClass cdClass){
    String name = cdClass.getName();
    return name.endsWith(BUILDER_SUFFIX)
        || name.endsWith(SYMBOL_TABLE_CREATOR_SUFFIX)
        || name.endsWith(SYMBOL_TABLE_CREATOR_SUFFIX + DELEGATOR_SUFFIX)
        || name.endsWith(SCOPE_SKELETON_CREATOR_SUFFIX)
        || name.endsWith(SCOPE_SKELETON_CREATOR_SUFFIX + DELEGATOR_SUFFIX);
  }

  protected List<String> getAttributeNameList(List<ASTCDClass> astcdClasses) {
    List<String> attributeNames = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClasses) {
      attributeNames.add(astcdClass.getName());
    }
    return attributeNames;
  }

  protected ASTCDMethod addGetMillMethods(ASTMCType millType) {
    ASTCDMethod getMillMethod = this.getCDMethodFacade().createMethod(PROTECTED_STATIC, millType, GET_MILL);
    this.replaceTemplate(EMPTY_BODY, getMillMethod, new TemplateHookPoint("mill.GetMillMethod", millType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())));
    return getMillMethod;
  }

  protected ASTCDMethod addInitMeMethod(ASTMCType millType, List<ASTCDClass> astcdClassList) {
    ASTCDParameter astcdParameter = getCDParameterFacade().createParameter(millType, "a");
    ASTCDMethod initMeMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, INIT_ME, astcdParameter);
    this.replaceTemplate(EMPTY_BODY, initMeMethod, new TemplateHookPoint("mill.InitMeMethod", getAttributeNameList(astcdClassList)));
    return initMeMethod;
  }

  protected ASTCDMethod addInitMethod(ASTMCType millType, List<CDDefinitionSymbol> superSymbolList, String fullDefinitionName) {
    ASTCDMethod initMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, INIT);
    this.replaceTemplate(EMPTY_BODY, initMethod, new TemplateHookPoint("mill.InitMethod", millType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()), superSymbolList, fullDefinitionName+"."+AUXILIARY_PACKAGE));
    return initMethod;
  }

  protected ASTCDMethod addResetMethod(List<ASTCDClass> astcdClassList, List<CDDefinitionSymbol> superSymbolList) {
    ASTCDMethod resetMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, RESET);
    this.replaceTemplate(EMPTY_BODY, resetMethod, new TemplateHookPoint("mill.ResetMethod", getAttributeNameList(astcdClassList), superSymbolList));
    return resetMethod;
  }

  protected List<ASTCDMethod> addBuilderMethods(List<ASTCDClass> astcdClassList, ASTCDCompilationUnit cd) {
    List<ASTCDMethod> builderMethodsList = new ArrayList<>();

    for (ASTCDClass astcdClass : astcdClassList) {
      String astName = astcdClass.getName();
      String packageDef = String.join(".", cd.getPackageList());
      ASTMCQualifiedType builderType = this.getMCTypeFacade().createQualifiedType(packageDef + "." + astName);
      String methodName = astName.startsWith(AST_PREFIX) ?
          StringTransformations.uncapitalize(astName.replaceFirst(AST_PREFIX, ""))
          : StringTransformations.uncapitalize(astName);

      // add public static Method for Builder
      ASTModifier modifier = PUBLIC_STATIC.build();
      ASTCDMethod builderMethod = this.getCDMethodFacade().createMethod(modifier, builderType, methodName);
      builderMethodsList.add(builderMethod);
      this.replaceTemplate(EMPTY_BODY, builderMethod, new TemplateHookPoint("mill.BuilderMethod", astName, methodName));

      // add protected Method for Builder
      ASTModifier protectedModifier = PROTECTED.build();
      ASTCDMethod protectedMethod = this.getCDMethodFacade().createMethod(protectedModifier, builderType, "_" + methodName);
      builderMethodsList.add(protectedMethod);
      this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", builderType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())));
    }

    return builderMethodsList;
  }

  /**
   * adds builder methods for the delegation to builders of super grammars
   */
  protected List<ASTCDMethod> addSuperBuilderMethods(List<CDDefinitionSymbol> superSymbolList, List<ASTCDClass> classList) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    // get super symbols
    for (CDDefinitionSymbol superSymbol : superSymbolList) {
      if (superSymbol.isPresentAstNode()) {
        for (CDTypeSymbol type : superSymbol.getTypes()) {
          if (type.isPresentAstNode() && type.getAstNode().isPresentModifier()
              && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
            superMethods.addAll(getSuperSymbolMethods(superSymbol, type));
          }
          if (type.isIsClass() && !type.isIsAbstract() && type.isPresentAstNode() &&
              !symbolTableService.isClassOverwritten(type.getName() + BUILDER_SUFFIX, classList)) {
            superMethods.addAll(getSuperASTMethods(superSymbol, type, superMethods));
          }
        }
      }
    }
    return superMethods;
  }

  protected List<ASTCDMethod> getPhasedSymbolTableCreatorDelegatorMethods(){
    String phasedSymbolTableCreatorDelegatorName = symbolTableService.getPhasedSymbolTableCreatorDelegatorSimpleName();
    String phasedSymbolTableCreatorDelegatorFullName = symbolTableService.getPhasedSymbolTableCreatorDelegatorFullName();
    return getStaticAndProtectedMethods(phasedSymbolTableCreatorDelegatorName, phasedSymbolTableCreatorDelegatorFullName);
  }

  protected List<ASTCDMethod> getScopeSkeletonCreatorDelegatorMethods(){
    String scopeSkeletonCreatorDelegatorName = symbolTableService.getScopeSkeletonCreatorDelegatorSimpleName();
    String scopeSkeletonCreatorDelegatorFullName = symbolTableService.getScopeSkeletonCreatorDelegatorFullName();
    return getStaticAndProtectedMethods(scopeSkeletonCreatorDelegatorName, scopeSkeletonCreatorDelegatorFullName);
  }

  protected List<ASTCDMethod> getScopeSkeletonCreatorMethods(){
    String scopeSkeletonCreatorName = symbolTableService.getScopeSkeletonCreatorSimpleName();
    String scopeSkeletonCreatorFullName = symbolTableService.getScopeSkeletonCreatorFullName();
    return getStaticAndProtectedMethods(scopeSkeletonCreatorName, scopeSkeletonCreatorFullName);
  }

  protected List<ASTCDMethod> getStaticAndProtectedMethods(String name, String fullName){
    List<ASTCDMethod> methods = Lists.newArrayList();
    String staticMethodName = StringTransformations.uncapitalize(name);
    String protectedMethodName = "_"+staticMethodName;
    ASTMCType scopeSkeletonCreatorType = getMCTypeFacade().createQualifiedType(fullName);

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, scopeSkeletonCreatorType, staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", name, staticMethodName));
    methods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, scopeSkeletonCreatorType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", fullName));
    methods.add(protectedMethod);
    return methods;
  }

  protected List<ASTCDMethod> getParserMethods(){
    List<ASTCDMethod> parserMethods = Lists.newArrayList();

    String parserName = "Parser";
    String staticMethodName = "parser";
    String protectedMethodName = "_"+staticMethodName;
    ASTMCType parserType = getMCTypeFacade().createQualifiedType(parserService.getParserClassFullName());

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, parserType, staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", parserName, staticMethodName));
    parserMethods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, parserType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedParserMethod", parserService.getParserClassFullName()));
    parserMethods.add(protectedMethod);

    return parserMethods;
  }

  protected List<ASTCDMethod> getGlobalScopeMethods(ASTCDAttribute globalScopeAttribute){
    List<ASTCDMethod> globalScopeMethods = Lists.newArrayList();

    String attributeName = globalScopeAttribute.getName();
    String staticMethodName = StringTransformations.uncapitalize(symbolTableService.getGlobalScopeSimpleName());
    String protectedMethodName = "_"+staticMethodName;

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, globalScopeAttribute.getMCType(), staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", StringTransformations.capitalize(attributeName), staticMethodName));
    globalScopeMethods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, globalScopeAttribute.getMCType(), protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedGlobalScopeMethod", attributeName, symbolTableService.getGlobalScopeFullName()));
    globalScopeMethods.add(protectedMethod);

    return globalScopeMethods;
  }

  protected List<ASTCDMethod> getArtifactScopeMethods(){
    String artifactScopeName = symbolTableService.getArtifactScopeSimpleName();
    ASTMCType returnType = symbolTableService.getArtifactScopeInterfaceType();
    ASTMCType scopeType = symbolTableService.getArtifactScopeType();
    return getStaticAndProtectedScopeMethods(artifactScopeName, returnType, scopeType);
  }

  protected List<ASTCDMethod> getScopeMethods(){
    String scopeName = symbolTableService.getScopeClassSimpleName();
    ASTMCType returnType = symbolTableService.getScopeInterfaceType();
    ASTMCType scopeType = symbolTableService.getScopeType();
    return getStaticAndProtectedScopeMethods(scopeName, returnType, scopeType);
  }

  protected List<ASTCDMethod> getStaticAndProtectedScopeMethods(String scopeName, ASTMCType returnType, ASTMCType scopeType) {
    List<ASTCDMethod> scopeMethods = Lists.newArrayList();
    String staticMethodName = StringTransformations.uncapitalize(scopeName);
    String protectedMethodName = "_" + staticMethodName;

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", scopeName, staticMethodName));
    scopeMethods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, returnType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", scopeType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter())));
    scopeMethods.add(protectedMethod);

    return scopeMethods;
  }

  protected List<ASTCDMethod> getSuperSymbolMethods(CDDefinitionSymbol superSymbol, CDTypeSymbol type) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    // for prod with symbol property create delegate builder method
    String symbolBuilderFullName = symbolTableService.getSymbolBuilderFullName(type.getAstNode(), superSymbol);
    String millFullName = symbolTableService.getMillFullName(superSymbol);
    String symbolBuilderSimpleName = StringTransformations.uncapitalize(symbolTableService.getSymbolBuilderSimpleName(type.getAstNode()));
    ASTCDMethod builderMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC,
        getMCTypeFacade().createQualifiedType(symbolBuilderFullName), symbolBuilderSimpleName);

    this.replaceTemplate(EMPTY_BODY, builderMethod, new StringHookPoint("return " + millFullName + "." + symbolBuilderSimpleName + "();"));
    superMethods.add(builderMethod);

    // create corresponding builder for symbolSurrogate
    String symbolSurrogateBuilderFullName = symbolTableService.getSymbolSurrogateBuilderFullName(type.getAstNode(), superSymbol);
    String symbolSurrogateBuilderSimpleName = StringTransformations.uncapitalize(symbolTableService.getSymbolSurrogateBuilderSimpleName(type.getAstNode()));
    ASTCDMethod builderLoaderMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC,
        getMCTypeFacade().createQualifiedType(symbolSurrogateBuilderFullName), symbolSurrogateBuilderSimpleName);

    this.replaceTemplate(EMPTY_BODY, builderLoaderMethod, new StringHookPoint("return " + millFullName + "." + symbolSurrogateBuilderSimpleName + "();"));
    superMethods.add(builderLoaderMethod);
    return superMethods;
  }


  protected List<ASTCDMethod> getSuperASTMethods(CDDefinitionSymbol superSymbol, CDTypeSymbol type,
                                                 List<ASTCDMethod> alreadyDefinedMethods) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    String astPackageName = superSymbol.getFullName().toLowerCase() + "." + AST_PACKAGE + ".";
    ASTMCQualifiedType superAstType = this.getMCTypeFacade().createQualifiedType(astPackageName + type.getName() + BUILDER_SUFFIX);
    String methodName = StringTransformations.uncapitalize(type.getName().replaceFirst(AST_PREFIX, "")) + BUILDER_SUFFIX;

    // add builder method
    ASTCDMethod createDelegateMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, superAstType, methodName);
    if (!symbolTableService.isMethodAlreadyDefined(createDelegateMethod, alreadyDefinedMethods)) {
      String millPackageName = superSymbol.getFullName().toLowerCase() + ".";
      this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("mill.BuilderDelegatorMethod", millPackageName + superSymbol.getName(), methodName));
      superMethods.add(createDelegateMethod);
    }
    return superMethods;
  }
  
  /**
   * Creates the public accessor and protected internal method for a given
   * attribute. The attribute is specified by its simple name, its qualified
   * type, and the qualified return type of the methods. The return type of the
   * method may be equal to the attribute type or a corresponding super type.
   * 
   * @param attributeName The name of the attribute
   * @param attributeType The qualified type of the attribute
   * @param methodName The name of the method
   * @param methodType The return type of the methods
   * @return The accessor and corresponding internal method for the attribute
   */
  protected List<ASTCDMethod> getAttributeMethods(String attributeName, String attributeType, String methodName, String methodType) {
    List<ASTCDMethod> attributeMethods = Lists.newArrayList();
    
    // method names and return type
    String protectedMethodName = "_" + methodName;
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(methodType);
    
    // static accessor method
    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, methodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", StringTransformations.capitalize(attributeName), methodName));
    attributeMethods.add(staticMethod);
    
    // protected internal method
    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, returnType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new StringHookPoint("return new " + attributeType + "();"));
    attributeMethods.add(protectedMethod);
    
    return attributeMethods;
  }

}
