package de.monticore.codegen.cd2java.factory;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.SuperSymbolHelper;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaVisitor;
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
import static de.monticore.codegen.cd2java.ast_new.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static de.monticore.codegen.cd2java.factory.NodeFactoryConstants.*;

public class NodeFactoryDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDClass> {

  private static final String FACTORY = "factory";

  private ASTCDCompilationUnit compilationUnit;

  public NodeFactoryDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public ASTCDClass decorate(ASTCDCompilationUnit astcdCompilationUnit) {
    this.compilationUnit = astcdCompilationUnit;
    ASTCDDefinition astcdDefinition = astcdCompilationUnit.getCDDefinition();
    String factoryClassName = astcdDefinition.getName() + NODE_FACTORY_SUFFIX;
    ASTType factoryType = this.getCDTypeFacade().createSimpleReferenceType(factoryClassName);

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED, factoryClassName);

    ASTCDAttribute factoryAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, factoryType, FACTORY);

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


  protected ASTCDMethod addGetFactoryMethod(ASTType factoryType, String factoryClassName) {
    ASTCDMethod getFactoryMethod = this.getCDMethodFacade().createMethod(PRIVATE_STATIC, factoryType, GET_FACTORY_METHOD);
    this.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("nodefactory.GetFactory", factoryClassName));
    return getFactoryMethod;
  }

  protected ASTCDAttribute addAttributes(ASTCDClass astcdClass, ASTType factoryType) {
    // create attribute for AST
    return this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, factoryType, FACTORY + astcdClass.getName());
  }

  protected List<ASTCDMethod> addFactoryMethods(ASTCDClass astcdClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    String astName = astcdClass.getName();
    ASTType astType = this.getCDTypeFacade().createSimpleReferenceType(astName);

    // add create Method for AST without parameters
    addCreateMethod(astName, astType, methodList);

    // add doCreate Method for AST without parameters
    addDoCreateMethod(astName, astType, methodList);

    if (!astcdClass.isEmptyCDAttributes()) {
      //create parameterList
      List<ASTCDParameter> params = this.getCDParameterFacade().createParameters(astcdClass.getCDAttributeList());
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

  protected void addCreateMethod(String astName, ASTType astType, List<ASTCDMethod> methodList) {
    ASTCDMethod createWithoutParameters = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, astType, CREATE_METHOD + astName);
    methodList.add(createWithoutParameters);
    this.replaceTemplate(EMPTY_BODY, createWithoutParameters, new TemplateHookPoint("ast.factorymethods.Create", astName));
  }

  protected void addDoCreateMethod(String astName, ASTType astType, List<ASTCDMethod> methodList) {
    ASTCDMethod doCreateWithoutParameters = this.getCDMethodFacade().createMethod(PROTECTED, astType, DO_CREATE_METHOD + astName);
    methodList.add(doCreateWithoutParameters);
    this.replaceTemplate(EMPTY_BODY, doCreateWithoutParameters, new TemplateHookPoint("ast.factorymethods.DoCreate", astName));
  }

  protected void addCreateWithParamMethod(String astName, ASTType astType, List<ASTCDParameter> params, String paramCall, List<ASTCDMethod> methodList) {
    ASTCDMethod createWithParameters = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, astType, CREATE_METHOD + astName, params);
    methodList.add(createWithParameters);
    this.replaceTemplate(EMPTY_BODY, createWithParameters, new TemplateHookPoint("ast.factorymethods.CreateWithParams", astName, paramCall));
  }

  protected void addDoCreateWithParamMethod(String astName, ASTType astType, List<ASTCDParameter> params, String paramCall, List<ASTCDMethod> methodList) {
    ASTCDMethod doCreateWithParameters = this.getCDMethodFacade().createMethod(PROTECTED, astType, DO_CREATE_METHOD + astName, params);
    methodList.add(doCreateWithParameters);
    this.replaceTemplate(EMPTY_BODY, doCreateWithParameters, new TemplateHookPoint("ast.factorymethods.DoCreateWithParams", astName, paramCall));
  }


  protected List<ASTCDMethod> addFactoryDelegateMethods() {
    List<ASTCDMethod> delegateMethodList = new ArrayList<>();
    //get super symbols
    for (CDSymbol superSymbol : SuperSymbolHelper.getSuperCDs(compilationUnit)) {
      Optional<ASTNode> astNode = superSymbol.getAstNode();
      if (astNode.isPresent() && astNode.get() instanceof ASTCDDefinition) {
        //get super cddefinition
        ASTCDDefinition superDefinition = (ASTCDDefinition) astNode.get();

        TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor();
        CD4AnalysisMill.cDCompilationUnitBuilder().setCDDefinition(superDefinition).build().accept(visitor);

        for (ASTCDClass superClass : superDefinition.getCDClassList()) {
          if (!isClassOverwritten(superClass) && !(superClass.isPresentModifier() && superClass.getModifier().isAbstract())) {
            String packageName = superSymbol.getFullName().toLowerCase() + "." + AST_PACKAGE + ".";
            ASTType superAstType = this.getCDTypeFacade().createSimpleReferenceType(packageName + superClass.getName());

            //add create method without parameters
            addCreateDelegateMethod(superAstType, superClass.getName(), packageName, superSymbol.getName(), delegateMethodList);
            if (!superClass.isEmptyCDAttributes()) {
              //add create method with parameters
              addCreateDelegateWithParamMethod(superAstType, superClass, packageName, superSymbol.getName(), delegateMethodList);
            }
          }
        }
      }
    }
    return delegateMethodList;
  }

  protected boolean isClassOverwritten(ASTCDClass astcdClass) {
    //if there is a Class with the same name in the current CompilationUnit, then the methods are only generated once
    return compilationUnit.getCDDefinition().getCDClassList().stream().anyMatch(x -> x.getName().endsWith(astcdClass.getName()));
  }

  protected void addCreateDelegateMethod(ASTType superAstType, String className, String packageName, String symbolName, List<ASTCDMethod> delegateMethodList) {
    ASTCDMethod createDelegateMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, superAstType, CREATE_METHOD + className);
    this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("nodefactory.CreateDelegateMethod", packageName + symbolName, className, ""));
    delegateMethodList.add(createDelegateMethod);
  }

  protected void addCreateDelegateWithParamMethod(ASTType superAstType, ASTCDClass superClass, String packageName, String symbolName, List<ASTCDMethod> delegateMethodList) {
    List<ASTCDParameter> params = this.getCDParameterFacade().createParameters(superClass.getCDAttributeList());
    String paramCall = params.stream()
        .map(ASTCDParameter::getName)
        .collect(Collectors.joining(", "));

    ASTCDMethod createDelegateWithParamMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, superAstType, CREATE_METHOD + superClass.getName(), params);
    this.replaceTemplate(EMPTY_BODY, createDelegateWithParamMethod, new TemplateHookPoint("nodefactory.CreateDelegateMethod", packageName + symbolName, superClass.getName(), paramCall));
    delegateMethodList.add(createDelegateWithParamMethod);
  }
}


