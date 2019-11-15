/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.factory;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaVisitor;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.*;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * creates the nodeFactory class for a grammar
 */
public class NodeFactoryDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final NodeFactoryService nodeFactoryService;

  public NodeFactoryDecorator(final GlobalExtensionManagement glex, final NodeFactoryService nodeFactoryService) {
    super(glex);
    this.nodeFactoryService = nodeFactoryService;
  }

  public ASTCDClass decorate(final ASTCDCompilationUnit astcdCompilationUnit) {
    ASTCDDefinition astcdDefinition = astcdCompilationUnit.getCDDefinition();
    String factoryClassName = astcdDefinition.getName() + NODE_FACTORY_SUFFIX;
    ASTMCType factoryType = this.getMCTypeFacade().createQualifiedType(factoryClassName);

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
      factoryAttributeList.add(addAttribute(astcdClass, factoryType));
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
    ASTCDMethod getFactoryMethod = this.getCDMethodFacade().createMethod(PRIVATE_STATIC, factoryType, GET_FACTORY_METHOD);
    this.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("_ast.nodefactory.GetFactory", factoryClassName));
    return getFactoryMethod;
  }

  protected ASTCDAttribute addAttribute(ASTCDClass astcdClass, ASTMCType factoryType) {
    // create attribute for AST
    return this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, factoryType, FACTORY + astcdClass.getName());
  }

  protected List<ASTCDMethod> addFactoryMethods(ASTCDClass astcdClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    String astName = astcdClass.getName();
    ASTMCType astType = this.getMCTypeFacade().createQualifiedType(astName);

    // add create Method for AST without parameters
    methodList.add(addCreateMethod(astName, astType));

    // add doCreate Method for AST without parameters
    methodList.add(addDoCreateMethod(astName, astType));

    return methodList;
  }

  protected ASTCDMethod addCreateMethod(String astName, ASTMCType astType) {
    ASTCDMethod createWithoutParameters = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, astType, CREATE_METHOD + astName);
    this.replaceTemplate(EMPTY_BODY, createWithoutParameters, new TemplateHookPoint("_ast.nodefactory.Create", astName));
    return createWithoutParameters;
  }

  protected ASTCDMethod addDoCreateMethod(String astName, ASTMCType astType) {
    ASTCDMethod doCreateWithoutParameters = this.getCDMethodFacade().createMethod(PROTECTED, astType, DO_CREATE_METHOD + astName);
    this.replaceTemplate(EMPTY_BODY, doCreateWithoutParameters, new TemplateHookPoint("_ast.nodefactory.DoCreate", astName));
    return doCreateWithoutParameters;
  }

  /**
   * creates methods that delegate to factories of super grammars
   * has to go over all super grammars
   */
  protected List<ASTCDMethod> addFactoryDelegateMethods(List<ASTCDClass> classList) {
    List<ASTCDMethod> delegateMethodList = new ArrayList<>();
    //get super symbols
    for (CDDefinitionSymbol superSymbol : nodeFactoryService.getSuperCDsTransitive()) {
      Optional<ASTCDDefinition> astNode = superSymbol.getAstNodeOpt();
      if (astNode.isPresent()) {
        //get super cdDefinition
        ASTCDDefinition superDefinition = astNode.get();

        TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(superSymbol.getEnclosingScope());
        CD4AnalysisMill.cDCompilationUnitBuilder().setCDDefinition(superDefinition).build().accept(visitor);

        for (ASTCDClass superClass : superDefinition.getCDClassList()) {
          if (canAddDelegateMethod(superClass, classList, delegateMethodList)) {
            String packageName = superSymbol.getFullName().toLowerCase() + "." + AST_PACKAGE + ".";
            ASTMCType superAstType = this.getMCTypeFacade().createQualifiedType(packageName + superClass.getName());

            //add create method without parameters
            delegateMethodList.add(addCreateDelegateMethod(superAstType, superClass.getName(), packageName, superSymbol.getName()));
          }
        }
      }
    }
    return delegateMethodList;
  }

  /**
   * checks if a superClass is not overwritten and is not abstract
   * it is also checked if the method which will be created, does not already exist
   * only then the delegate method can be added
   */
  protected boolean canAddDelegateMethod(ASTCDClass superClass, List<ASTCDClass> classList, List<ASTCDMethod> delegateMethodList) {
    return !nodeFactoryService.isClassOverwritten(superClass, classList)
        && !(superClass.isPresentModifier() && superClass.getModifier().isAbstract())
        && !nodeFactoryService.isMethodAlreadyDefined(CREATE_METHOD + superClass.getName(), delegateMethodList);
  }

  protected ASTCDMethod addCreateDelegateMethod(ASTMCType superAstType, String className, String packageName, String symbolName) {
    ASTCDMethod createDelegateMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, superAstType, CREATE_METHOD + className);
    this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("_ast.nodefactory.CreateDelegateMethod", packageName + symbolName, className, ""));
    return createDelegateMethod;
  }

}


