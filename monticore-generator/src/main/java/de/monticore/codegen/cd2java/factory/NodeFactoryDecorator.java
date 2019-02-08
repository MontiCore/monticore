package de.monticore.codegen.cd2java.factory;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.Decorator;
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

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class NodeFactoryDecorator implements Decorator<ASTCDCompilationUnit, ASTCDClass> {

  private final GlobalExtensionManagement glex;

  private static final String NODE_FACTORY_SUFFIX = "NodeFactory";

  private static final String FACTORY_INFIX = "factory";

  private static final String GET_FACTORY_METHOD = "getFactory";

  private static final String DOCREATE_INFIX = "doCreate";

  private static final String CREATE_INFIX = "create";

  private final CDTypeFactory cdTypeFacade;

  private final CDAttributeFactory cdAttributeFacade;

  private final CDConstructorFactory cdConstructorFacade;

  private final CDMethodFactory cdMethodFacade;

  private final CDParameterFactory cdParameterFacade;

  private ASTCDCompilationUnit compilationUnit;


  public NodeFactoryDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdTypeFacade = CDTypeFactory.getInstance();
    this.cdAttributeFacade = CDAttributeFactory.getInstance();
    this.cdConstructorFacade = CDConstructorFactory.getInstance();
    this.cdMethodFacade = CDMethodFactory.getInstance();
    this.cdParameterFacade = CDParameterFactory.getInstance();
  }

  public ASTCDClass decorate(ASTCDCompilationUnit astcdCompilationUnit) {
    this.compilationUnit = astcdCompilationUnit;
    ASTCDDefinition astcdDefinition = astcdCompilationUnit.getCDDefinition();
    String factoryClassName = astcdDefinition.getName() + NODE_FACTORY_SUFFIX;
    ASTType factoryType = this.cdTypeFacade.createSimpleReferenceType(factoryClassName);

    ASTCDConstructor constructor = this.cdConstructorFacade.createConstructor(PROTECTED, factoryClassName);

    ASTCDAttribute factoryAttribute = this.cdAttributeFacade.createAttribute(PROTECTED_STATIC, factoryType, FACTORY_INFIX);

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
        .setModifier(PUBLIC)
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
    ASTCDMethod getFactoryMethod = this.cdMethodFacade.createMethod(PRIVATE_STATIC, factoryType, GET_FACTORY_METHOD);
    this.glex.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("nodefactory.GetFactory", factoryClassName));
    return getFactoryMethod;
  }

  private String getParamCall(List<ASTCDParameter> parameterList) {
    String s = "";
    for (ASTCDParameter parameter : parameterList) {
      if (s.isEmpty()) {
        s += parameter.getName();
      } else {
        s += ", " + parameter.getName();
      }
    }
    return s;
  }

  private ASTCDAttribute addAttributes(ASTCDClass astcdClass, ASTType factoryType) {
    // create attribute for AST
    return this.cdAttributeFacade.createAttribute(PROTECTED_STATIC, factoryType, FACTORY_INFIX + astcdClass.getName());
  }

  private List<ASTCDMethod> addFactoryMethods(ASTCDClass astcdClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    String astName = astcdClass.getName();
    ASTType astType = this.cdTypeFacade.createSimpleReferenceType(astName);

    // add create Method for AST without parameters
    addCreateMethod(astName, astType, methodList);

    // add doCreate Method for AST without parameters
    addDoCreateMethod(astName, astType, methodList);

    if (!astcdClass.isEmptyCDAttributes()) {
      //create parameterList
      List<ASTCDParameter> params = this.cdParameterFacade.createParameters(astcdClass.getCDAttributeList());
      String paramCall = getParamCall(params);

      // add create Method for AST without parameters
      addCreateWithParamMethod(astName, astType, params, paramCall, methodList);

      // add doCreate Method for AST without parameters
      addDoCreateWithParamMethod(astName, astType, params, paramCall, methodList);
    }
    return methodList;
  }

  private void addCreateMethod(String astName, ASTType astType, List<ASTCDMethod> methodList) {
    ASTCDMethod createWithoutParameters = this.cdMethodFacade.createMethod(PUBLIC_STATIC, astType, CREATE_INFIX + astName);
    methodList.add(createWithoutParameters);
    this.glex.replaceTemplate(EMPTY_BODY, createWithoutParameters, new TemplateHookPoint("ast.factorymethods.Create", astName));
  }


  private void addDoCreateMethod(String astName, ASTType astType, List<ASTCDMethod> methodList) {
    ASTCDMethod doCreateWithoutParameters = this.cdMethodFacade.createMethod(PROTECTED, astType, DOCREATE_INFIX + astName);
    methodList.add(doCreateWithoutParameters);
    this.glex.replaceTemplate(EMPTY_BODY, doCreateWithoutParameters, new TemplateHookPoint("ast.factorymethods.DoCreate", astName));
  }

  private void addCreateWithParamMethod(String astName, ASTType astType, List<ASTCDParameter> params, String paramCall, List<ASTCDMethod> methodList) {
    ASTCDMethod createWithParameters = this.cdMethodFacade.createMethod(PUBLIC_STATIC, astType, CREATE_INFIX + astName, params);
    methodList.add(createWithParameters);
    this.glex.replaceTemplate(EMPTY_BODY, createWithParameters, new TemplateHookPoint("ast.factorymethods.CreateWithParams", astName, paramCall));
  }

  private void addDoCreateWithParamMethod(String astName, ASTType astType, List<ASTCDParameter> params, String paramCall, List<ASTCDMethod> methodList) {
    ASTCDMethod doCreateWithParameters = this.cdMethodFacade.createMethod(PROTECTED, astType, DOCREATE_INFIX + astName, params);
    methodList.add(doCreateWithParameters);
    this.glex.replaceTemplate(EMPTY_BODY, doCreateWithParameters, new TemplateHookPoint("ast.factorymethods.DoCreateWithParams", astName, paramCall));
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
          ASTType superAstType = this.cdTypeFacade.createSimpleReferenceType(packageName + superClass.getName());

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
    ASTCDMethod createDelegateMethod = this.cdMethodFacade.createMethod(PUBLIC_STATIC, superAstType, CREATE_INFIX + className);
    this.glex.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("nodefactory.CreateDelegateMethod", packageName + symbolName, className, ""));
    delegateMethodList.add(createDelegateMethod);
  }

  private void addCreateDelegateWithParamMethod(ASTType superAstType, ASTCDClass superClass, String packageName, String symbolName, List<ASTCDMethod> delegateMethodList) {
    List<ASTCDParameter> params = this.cdParameterFacade.createParameters(superClass.getCDAttributeList());
    String paramCall = getParamCall(params);

    ASTCDMethod createDelegateWithParamMethod = this.cdMethodFacade.createMethod(PUBLIC_STATIC, superAstType, CREATE_INFIX + superClass.getName(), params);
    this.glex.replaceTemplate(EMPTY_BODY, createDelegateWithParamMethod, new TemplateHookPoint("nodefactory.CreateDelegateMethod", packageName + symbolName, superClass.getName(), paramCall));
    delegateMethodList.add(createDelegateWithParamMethod);
  }
}


