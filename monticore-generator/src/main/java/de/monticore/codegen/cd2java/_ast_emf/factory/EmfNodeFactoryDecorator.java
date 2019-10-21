/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.factory;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.*;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class EmfNodeFactoryDecorator extends NodeFactoryDecorator {
  public EmfNodeFactoryDecorator(GlobalExtensionManagement glex, NodeFactoryService nodeFactoryService) {
    super(glex, nodeFactoryService);
  }

  public ASTCDClass decorate(final ASTCDCompilationUnit astcdCompilationUnit) {
    ASTCDDefinition astcdDefinition = astcdCompilationUnit.getCDDefinition();
    String factoryClassName = astcdDefinition.getName() + NODE_FACTORY_SUFFIX;
    ASTMCType factoryType = this.getCDTypeFacade().createQualifiedType(factoryClassName);

    //remove abstract classes
    List<ASTCDClass> astcdClassList = astcdDefinition.deepClone().getCDClassList()
        .stream()
        .filter(ASTCDClass::isPresentModifier)
        .filter(x -> !x.getModifier().isAbstract())
        .collect(Collectors.toList());

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED, factoryClassName);

    ASTCDAttribute factoryAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, factoryType, FACTORY);

    ASTCDMethod getFactoryMethod = addGetFactoryMethod(factoryType, astcdDefinition.getName(), factoryClassName);

    ASTCDMethod emfCreateMethod = addEmfCreateMethod(astcdClassList, astcdDefinition.getName(), factoryClassName);

    ASTCDMethod getPackageMethod = addGetPackageMethod(astcdDefinition.getName());

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
        .setSuperclass(getCDTypeFacade().createQualifiedType(E_FACTORY_IMPL))
        .addCDAttribute(factoryAttribute)
        .addAllCDAttributes(factoryAttributeList)
        .addCDConstructor(constructor)
        .addCDMethod(getFactoryMethod)
        .addCDMethod(emfCreateMethod)
        .addCDMethod(getPackageMethod)
        .addAllCDMethods(createMethodList)
        .addAllCDMethods(delegateMethodList)
        .build();
  }

  protected ASTCDMethod addGetFactoryMethod(ASTMCType factoryType, String grammarName, String factoryClassName) {
    ASTCDMethod getFactoryMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, factoryType, GET_FACTORY_METHOD);
    this.replaceTemplate(EMPTY_BODY, getFactoryMethod,
        new TemplateHookPoint("_ast_emf.factory.GetFactory", factoryClassName, grammarName));
    return getFactoryMethod;
  }

  protected ASTCDMethod addEmfCreateMethod(List<ASTCDClass> astcdClassList, String grammarName, String factoryClassName) {
    ASTCDParameter eClassParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(E_CLASS_TYPE), "eClass");
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC,
        getCDTypeFacade().createQualifiedType(E_OBJECT_TYPE), CREATE_METHOD, eClassParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_ast_emf.factory.EmfCreate",
            factoryClassName, grammarName + PACKAGE_SUFFIX, astcdClassList));
    return method;
  }

  protected ASTCDMethod addGetPackageMethod(String grammarName) {
    String packageName = grammarName + PACKAGE_SUFFIX;
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PACKAGE_PRIVATE,
        getCDTypeFacade().createQualifiedType(packageName), "get" + packageName);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return (" + packageName + ")getEPackage();"));
    return method;
  }
}
