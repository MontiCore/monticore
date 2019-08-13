/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.factory;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaVisitor;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class NodeFactoryDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final NodeFactoryService nodeFactoryService;

  public NodeFactoryDecorator(final GlobalExtensionManagement glex, final NodeFactoryService nodeFactoryService) {
    super(glex);
    this.nodeFactoryService = nodeFactoryService;
  }

  public ASTCDClass decorate(final ASTCDCompilationUnit astcdCompilationUnit) {
    ASTCDDefinition astcdDefinition = astcdCompilationUnit.getCDDefinition();
    String factoryClassName = astcdDefinition.getName() + NODE_FACTORY_SUFFIX;
    ASTMCType factoryType = this.getCDTypeFacade().createQualifiedType(factoryClassName);

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
    List<ASTCDMethod> delegateMethodList = addFactoryDelegateMethods(astcdClassList);


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


  protected ASTCDMethod addGetFactoryMethod(ASTMCType factoryType, String factoryClassName) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(factoryType).build();
    ASTCDMethod getFactoryMethod = this.getCDMethodFacade().createMethod(PRIVATE_STATIC, returnType, GET_FACTORY_METHOD);
    this.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("_ast.nodefactory.GetFactory", factoryClassName));
    return getFactoryMethod;
  }

  protected ASTCDAttribute addAttributes(ASTCDClass astcdClass, ASTMCType factoryType) {
    // create attribute for AST
    return this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, factoryType, FACTORY + astcdClass.getName());
  }

  protected List<ASTCDMethod> addFactoryMethods(ASTCDClass astcdClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    String astName = astcdClass.getName();
    ASTMCType astType = this.getCDTypeFacade().createQualifiedType(astName);

    // add create Method for AST without parameters
    methodList.add(addCreateMethod(astName, astType));

    // add doCreate Method for AST without parameters
    methodList.add(addDoCreateMethod(astName, astType));

    if (!astcdClass.isEmptyCDAttributes()) {
      //create parameterList
      List<ASTCDParameter> params = this.getCDParameterFacade().createParameters(astcdClass.getCDAttributeList());
      String paramCall = params.stream()
          .map(ASTCDParameter::getName)
          .collect(Collectors.joining(", "));

      // add create Method for AST without parameters
      methodList.add(addCreateWithParamMethod(astName, astType, params, paramCall));

      // add doCreate Method for AST without parameters
      methodList.add(addDoCreateWithParamMethod(astName, astType, params, paramCall));
    }
    return methodList;
  }

  protected ASTCDMethod addCreateMethod(String astName, ASTMCType astType) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(astType).build();
    ASTCDMethod createWithoutParameters = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, CREATE_METHOD + astName);
    this.replaceTemplate(EMPTY_BODY, createWithoutParameters, new TemplateHookPoint("_ast.nodefactory.Create", astName));
    return createWithoutParameters;
  }

  protected ASTCDMethod addDoCreateMethod(String astName, ASTMCType astType) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(astType).build();
    ASTCDMethod doCreateWithoutParameters = this.getCDMethodFacade().createMethod(PROTECTED, returnType, DO_CREATE_METHOD + astName);
    this.replaceTemplate(EMPTY_BODY, doCreateWithoutParameters, new TemplateHookPoint("_ast.nodefactory.DoCreate", astName));
    return doCreateWithoutParameters;
  }

  protected ASTCDMethod addCreateWithParamMethod(String astName, ASTMCType astType, List<ASTCDParameter> params, String paramCall) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(astType).build();
    ASTCDMethod createWithParameters = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, CREATE_METHOD + astName, params);
    this.replaceTemplate(EMPTY_BODY, createWithParameters, new TemplateHookPoint("_ast.nodefactory.CreateWithParams", astName, paramCall));
    return createWithParameters;
  }

  protected ASTCDMethod addDoCreateWithParamMethod(String astName, ASTMCType astType, List<ASTCDParameter> params, String paramCall) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(astType).build();
    ASTCDMethod doCreateWithParameters = this.getCDMethodFacade().createMethod(PROTECTED, returnType, DO_CREATE_METHOD + astName, params);
    this.replaceTemplate(EMPTY_BODY, doCreateWithParameters, new TemplateHookPoint("_ast.nodefactory.DoCreateWithParams", astName, paramCall));
    return doCreateWithParameters;
  }


  protected List<ASTCDMethod> addFactoryDelegateMethods(List<ASTCDClass> classList) {
    List<ASTCDMethod> delegateMethodList = new ArrayList<>();
    //get super symbols
    for (CDDefinitionSymbol superSymbol : nodeFactoryService.getSuperCDs()) {
      Optional<ASTCDDefinition> astNode = superSymbol.getAstNode();
      if (astNode.isPresent()) {
        //get super cddefinition
        ASTCDDefinition superDefinition = astNode.get();

        TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(superSymbol.getEnclosingScope());
        CD4AnalysisMill.cDCompilationUnitBuilder().setCDDefinition(superDefinition).build().accept(visitor);

        for (ASTCDClass superClass : superDefinition.getCDClassList()) {
          if (!nodeFactoryService.isClassOverwritten(superClass, classList)
              && !(superClass.isPresentModifier() && superClass.getModifier().isAbstract())
              && !nodeFactoryService.isMethodAlreadyDefined(CREATE_METHOD + superClass.getName(), delegateMethodList)) {
            String packageName = superSymbol.getFullName().toLowerCase() + "." + AST_PACKAGE + ".";
            ASTMCType superAstType = this.getCDTypeFacade().createQualifiedType(packageName + superClass.getName());

            //add create method without parameters
            delegateMethodList.add(addCreateDelegateMethod(superAstType, superClass.getName(), packageName, superSymbol.getName()));
            if (!superClass.isEmptyCDAttributes()) {
              //add create method with parameters
              delegateMethodList.add(addCreateDelegateWithParamMethod(superAstType, superClass, packageName, superSymbol.getName()));
            }
          }
        }
      }
    }
    return delegateMethodList;
  }

  protected ASTCDMethod addCreateDelegateMethod(ASTMCType superAstType, String className, String packageName, String symbolName) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(superAstType).build();
    ASTCDMethod createDelegateMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, CREATE_METHOD + className);
    this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("_ast.nodefactory.CreateDelegateMethod", packageName + symbolName, className, ""));
    return createDelegateMethod;
  }

  protected ASTCDMethod addCreateDelegateWithParamMethod(ASTMCType superAstType, ASTCDClass superClass, String packageName, String symbolName) {
    List<ASTCDParameter> params = this.getCDParameterFacade().createParameters(superClass.getCDAttributeList());
    String paramCall = params.stream()
        .map(ASTCDParameter::getName)
        .collect(Collectors.joining(", "));

    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(superAstType).build();
    ASTCDMethod createDelegateWithParamMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, CREATE_METHOD + superClass.getName(), params);
    this.replaceTemplate(EMPTY_BODY, createDelegateWithParamMethod, new TemplateHookPoint("_ast.nodefactory.CreateDelegateMethod", packageName + symbolName, superClass.getName(), paramCall));
    return createDelegateWithParamMethod;
  }

}


