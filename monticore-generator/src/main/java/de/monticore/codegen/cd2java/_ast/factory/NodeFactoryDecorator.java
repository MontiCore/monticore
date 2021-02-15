/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.factory;

import com.google.common.collect.Lists;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaVisitor;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.*;

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

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED.build(), factoryClassName);

    ASTCDAttribute factoryAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC.build(), factoryType, FACTORY);

    ASTCDMethod getFactoryMethod = addGetFactoryMethod(factoryType, factoryClassName);

    List<ASTCDClass> astcdClassList = Lists.newArrayList(astcdDefinition.getCDClassesList());

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
  
    // mark as deprecated (to be deleted, because Builders exist)
    ASTModifier modifier = PUBLIC.build();
    nodeFactoryService.addDeprecatedStereotype(modifier, Optional.empty());
    
    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(modifier)
        .setName(factoryClassName)
        .addCDMember(factoryAttribute)
        .addAllCDMembers(factoryAttributeList)
        .addCDMember(constructor)
        .addCDMember(getFactoryMethod)
        .addAllCDMembers(createMethodList)
        .addAllCDMembers(delegateMethodList)
        .build();
  }


  protected ASTCDMethod addGetFactoryMethod(ASTMCType factoryType, String factoryClassName) {
    ASTCDMethod getFactoryMethod = this.getCDMethodFacade().createMethod(PRIVATE_STATIC.build(), factoryType, GET_FACTORY_METHOD);
    this.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("_ast.nodefactory.GetFactory", factoryClassName));
    return getFactoryMethod;
  }

  protected ASTCDAttribute addAttribute(ASTCDClass astcdClass, ASTMCType factoryType) {
    // create attribute for AST
    return this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC.build(), factoryType, FACTORY + astcdClass.getName());
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
    ASTCDMethod createWithoutParameters = this.getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), astType, CREATE_METHOD + astName);
    this.replaceTemplate(EMPTY_BODY, createWithoutParameters, new TemplateHookPoint("_ast.nodefactory.Create", astName));
    return createWithoutParameters;
  }

  protected ASTCDMethod addDoCreateMethod(String astName, ASTMCType astType) {
    ASTCDMethod doCreateWithoutParameters = this.getCDMethodFacade().createMethod(PROTECTED.build(), astType, DO_CREATE_METHOD + astName);
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
    for (DiagramSymbol superSymbol : nodeFactoryService.getSuperCDsTransitive()) {
      if (superSymbol.isPresentAstNode()) {
        //get super cdDefinition
        ASTCDDefinition superDefinition = (ASTCDDefinition) superSymbol.getAstNode();

        TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor((ICDBasisScope) superSymbol.getEnclosingScope());
        ASTCDCompilationUnit a = CD4AnalysisMill.cDCompilationUnitBuilder().setCDDefinition(superDefinition).build();
        a.accept(visitor);

        for (ASTCDClass superClass : superDefinition.getCDClassesList()) {
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
    ASTCDMethod createDelegateMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), superAstType, CREATE_METHOD + className);
    this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("_ast.nodefactory.CreateDelegateMethod", packageName + symbolName, className, ""));
    return createDelegateMethod;
  }

}


