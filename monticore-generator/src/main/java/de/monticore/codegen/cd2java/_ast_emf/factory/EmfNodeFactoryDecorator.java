package de.monticore.codegen.cd2java._ast_emf.factory;

import com.google.common.collect.Lists;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.*;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class EmfNodeFactoryDecorator extends NodeFactoryDecorator {
  public EmfNodeFactoryDecorator(GlobalExtensionManagement glex, NodeFactoryService nodeFactoryService) {
    super(glex, nodeFactoryService);
  }

  public ASTCDClass decorate(final ASTCDCompilationUnit astcdCompilationUnit) {
    this.compilationUnit = astcdCompilationUnit;
    ASTCDDefinition astcdDefinition = astcdCompilationUnit.getCDDefinition();
    String factoryClassName = astcdDefinition.getName() + NODE_FACTORY_SUFFIX;
    ASTType factoryType = this.getCDTypeFacade().createSimpleReferenceType(factoryClassName);

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED, factoryClassName);

    ASTCDAttribute factoryAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, factoryType, FACTORY);

    ASTCDMethod getFactoryMethod = addGetFactoryMethod(factoryType, astcdDefinition.getName(), factoryClassName);

    ASTCDMethod emfCreateMethod = addEmfCreateMethod(astcdDefinition.getCDClassList(), astcdDefinition.getName(), factoryClassName);

    ASTCDMethod getPackageMethod = addGetPackageMethod(astcdDefinition.getName());

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
        .setSuperclass(getCDTypeFacade().createSimpleReferenceType(E_FACTORY_IMPL))
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

  protected ASTCDMethod addGetFactoryMethod(ASTType factoryType, String grammarName, String factoryClassName) {
    ASTCDMethod getFactoryMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, factoryType, GET_FACTORY_METHOD);
    this.replaceTemplate(EMPTY_BODY, getFactoryMethod,
        new TemplateHookPoint("_ast_emf.factory.GetFactory", factoryClassName, grammarName));
    return getFactoryMethod;
  }

  protected ASTCDMethod addEmfCreateMethod(List<ASTCDClass> astcdClassList, String grammarName, String factoryClassName) {
    ASTCDParameter eClassParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createSimpleReferenceType(E_CLASS_TYPE), "eClass");

    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC,
        getCDTypeFacade().createSimpleReferenceType(E_OBJECT_TYPE), CREATE_METHOD, eClassParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_ast_emf.factory.EmfCreate",
            factoryClassName, grammarName + PACKAGE_SUFFIX, astcdClassList));
    return method;
  }

  protected ASTCDMethod addGetPackageMethod(String grammarName) {
    String packageName = grammarName + PACKAGE_SUFFIX;
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PACKAGE_PRIVATE,
        getCDTypeFacade().createSimpleReferenceType(packageName), "get" + packageName);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return (" + packageName + ")getEPackage();"));
    return method;
  }
}
