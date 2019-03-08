package de.monticore.codegen.cd2java.factory;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class NodeFactoryDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDClass> {

  private static final String NODE_FACTORY_SUFFIX = "NodeFactory";

  private static final String FACTORY_INFIX = "factory";

  private static final String GET_FACTORY_METHOD = "getFactory";

  private static final String DOCREATE_INFIX = "doCreate";

  private static final String CREATE_INFIX = "create";

  private ASTCDCompilationUnit compilationUnit;

  public NodeFactoryDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public ASTCDClass decorate(ASTCDCompilationUnit astcdCompilationUnit) {
    this.compilationUnit = astcdCompilationUnit;
    ASTCDDefinition astcdDefinition = astcdCompilationUnit.getCDDefinition();
    String factoryClassName = astcdDefinition.getName() + NODE_FACTORY_SUFFIX;
    ASTType factoryType = this.getCDTypeFactory().createSimpleReferenceType(factoryClassName);

    ASTCDConstructor constructor = this.getCDConstructorFactory().createConstructor(PROTECTED, factoryClassName);

    ASTCDAttribute factoryAttribute = this.getCDAttributeFactory().createAttribute(PROTECTED_STATIC, factoryType, FACTORY_INFIX);

    ASTCDMethod getFactoryMethod = addGetFactoryMethod(factoryType, factoryClassName);

    List<ASTCDClass> astcdClassList = Lists.newArrayList(astcdDefinition.getCDClassList());

    List<ASTCDMethod> createMethodList = new ArrayList<>();

    List<ASTCDAttribute> factoryAttributeList = new ArrayList<>();


    for (ASTCDClass astcdClass : astcdClassList) {
      if (!astcdClass.isPresentModifier() || (astcdClass.getModifier().isAbstract() && !astcdClass.getName().endsWith("TOP"))) {
        continue;
      }
      //add factory attributes for all classes
      factoryAttributeList.add(addAttributes(astcdClass, factoryType));
      //add create and doCreate Methods for all classes
      createMethodList.addAll(addFactoryMethods(astcdClass));
    }

    //add factory delegate Methods form Super Classes
    List<ASTCDMethod> delegateMethodList = addFactoryDelegateMethods();


    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(factoryClassName)
        .addCDAttribute(factoryAttribute)
        .addAllCDAttributes(factoryAttributeList)
        .addCDConstructor(constructor)
        .addCDMethod(getFactoryMethod)
        .addAllCDMethods(createMethodList)
        .addAllCDMethods(delegateMethodList)
        .build();
  }


  private ASTCDMethod addGetFactoryMethod(ASTType factoryType, String factoryClassName) {
    ASTCDMethod getFactoryMethod = this.getCDMethodFactory().createMethod(PRIVATE_STATIC, factoryType, GET_FACTORY_METHOD);
    this.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("nodefactory.GetFactory", factoryClassName));
    return getFactoryMethod;
  }

  private ASTCDAttribute addAttributes(ASTCDClass astcdClass, ASTType factoryType) {
    // create attribute for AST
    return this.getCDAttributeFactory().createAttribute(PROTECTED_STATIC, factoryType, FACTORY_INFIX + astcdClass.getName());
  }

  private List<ASTCDMethod> addFactoryMethods(ASTCDClass astcdClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    String astName = astcdClass.getName();
    ASTType astType = this.getCDTypeFactory().createSimpleReferenceType(astName);

    // add create Method for AST without parameters
    addCreateMethod(astName, astType, methodList);

    // add doCreate Method for AST without parameters
    addDoCreateMethod(astName, astType, methodList);

    if (!astcdClass.isEmptyCDAttributes()) {
      //create parameterList
      List<ASTCDParameter> params = this.getCDParameterFactory().createParameters(astcdClass.getCDAttributeList());
      String paramCall = params.stream()
          .map(ASTCDParameter::getName)
          .collect(Collectors.joining(", "));

      // add create Method for AST without parameters
      addCreateWithParamMethod(astName, astType, params, paramCall, methodList);

      // add doCreate Method for AST without parameters
      addDoCreateWithParamMethod(astName, astType, params, paramCall, methodList);
    }
    return methodList;
  }

  private void addCreateMethod(String astName, ASTType astType, List<ASTCDMethod> methodList) {
    ASTCDMethod createWithoutParameters = this.getCDMethodFactory().createMethod(PUBLIC_STATIC, astType, CREATE_INFIX + astName);
    methodList.add(createWithoutParameters);
    this.replaceTemplate(EMPTY_BODY, createWithoutParameters, new TemplateHookPoint("ast.factorymethods.Create", astName));
  }

  private void addDoCreateMethod(String astName, ASTType astType, List<ASTCDMethod> methodList) {
    ASTCDMethod doCreateWithoutParameters = this.getCDMethodFactory().createMethod(PROTECTED, astType, DOCREATE_INFIX + astName);
    methodList.add(doCreateWithoutParameters);
    this.replaceTemplate(EMPTY_BODY, doCreateWithoutParameters, new TemplateHookPoint("ast.factorymethods.DoCreate", astName));
  }

  private void addCreateWithParamMethod(String astName, ASTType astType, List<ASTCDParameter> params, String paramCall, List<ASTCDMethod> methodList) {
    ASTCDMethod createWithParameters = this.getCDMethodFactory().createMethod(PUBLIC_STATIC, astType, CREATE_INFIX + astName, params);
    methodList.add(createWithParameters);
    this.replaceTemplate(EMPTY_BODY, createWithParameters, new TemplateHookPoint("ast.factorymethods.CreateWithParams", astName, paramCall));
  }

  private void addDoCreateWithParamMethod(String astName, ASTType astType, List<ASTCDParameter> params, String paramCall, List<ASTCDMethod> methodList) {
    ASTCDMethod doCreateWithParameters = this.getCDMethodFactory().createMethod(PROTECTED, astType, DOCREATE_INFIX + astName, params);
    methodList.add(doCreateWithParameters);
    this.replaceTemplate(EMPTY_BODY, doCreateWithParameters, new TemplateHookPoint("ast.factorymethods.DoCreateWithParams", astName, paramCall));
  }


  private List<ASTCDMethod> addFactoryDelegateMethods() {
    List<ASTCDMethod> delegateMethodList = new ArrayList<>();
    //get super symbols
    for (CDSymbol superSymbol : SuperSymbolHelper.getSuperCDs(compilationUnit)) {
      Optional<ASTNode> astNode = superSymbol.getAstNode();
      if (astNode.isPresent() && astNode.get() instanceof ASTCDDefinition) {
        //get super cddefinition
        ASTCDDefinition superDefinition = (ASTCDDefinition) astNode.get();
        for (ASTCDClass superClass : superDefinition.getCDClassList()) {
          String packageName = superSymbol.getFullName().toLowerCase() + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
          ASTType superAstType = this.getCDTypeFactory().createSimpleReferenceType(packageName + superClass.getName());

          //add create method without parameters
          addCreateDelegateMethod(superAstType, superClass.getName(), packageName, superSymbol.getName(), delegateMethodList);
          if (!superClass.isEmptyCDAttributes()) {
            //add create method with parameters
            addCreateDelegateWithParamMethod(superAstType, superClass, packageName, superSymbol.getName(), delegateMethodList);
          }
        }
      }
    }
    return delegateMethodList;
  }

  private void addCreateDelegateMethod(ASTType superAstType, String className, String packageName, String symbolName, List<ASTCDMethod> delegateMethodList) {
    ASTCDMethod createDelegateMethod = this.getCDMethodFactory().createMethod(PUBLIC_STATIC, superAstType, CREATE_INFIX + className);
    this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("nodefactory.CreateDelegateMethod", packageName + symbolName, className, ""));
    delegateMethodList.add(createDelegateMethod);
  }

  private void addCreateDelegateWithParamMethod(ASTType superAstType, ASTCDClass superClass, String packageName, String symbolName, List<ASTCDMethod> delegateMethodList) {
    List<ASTCDParameter> params = this.getCDParameterFactory().createParameters(superClass.getCDAttributeList());
    String paramCall = params.stream()
        .map(ASTCDParameter::getName)
        .collect(Collectors.joining(", "));

    ASTCDMethod createDelegateWithParamMethod = this.getCDMethodFactory().createMethod(PUBLIC_STATIC, superAstType, CREATE_INFIX + superClass.getName(), params);
    this.replaceTemplate(EMPTY_BODY, createDelegateWithParamMethod, new TemplateHookPoint("nodefactory.CreateDelegateMethod", packageName + symbolName, superClass.getName(), paramCall));
    delegateMethodList.add(createDelegateWithParamMethod);
  }
}


