package de.monticore.codegen.cd2java.factory;

import com.google.common.collect.Lists;
import de.monticore.codegen.cd2java.Generator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class NodeFactoryDecorator implements Generator<ASTCDDefinition, ASTCDClass> {

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


  public NodeFactoryDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdTypeFacade = CDTypeFactory.getInstance();
    this.cdAttributeFacade = CDAttributeFactory.getInstance();
    this.cdConstructorFacade = CDConstructorFactory.getInstance();
    this.cdMethodFacade = CDMethodFactory.getInstance();
    this.cdParameterFacade = CDParameterFactory.getInstance();
  }

  public ASTCDClass generate(ASTCDDefinition astcdDefinition) {
    String factoryClassName = astcdDefinition.getName() + NODE_FACTORY_SUFFIX;
    ASTType factoryType = this.cdTypeFacade.createTypeByDefinition(factoryClassName);

    ModifierBuilder modifierBuilder = ModifierBuilder.builder().Public();

    ASTCDConstructor constructor = this.cdConstructorFacade.createProtectedDefaultConstructor(factoryClassName);

    ASTCDAttribute factoryAttribute = this.cdAttributeFacade.createProtectedStaticAttribute(factoryType, FACTORY_INFIX);

    ASTCDMethod getFactoryMethod = this.cdMethodFacade.createPrivateStaticMethod(factoryType, GET_FACTORY_METHOD);
    this.glex.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("ast.factorymethods.Create", ""));

    List<ASTCDClass> astcdClassList = Lists.newArrayList(astcdDefinition.getCDClassList());
    List<ASTCDAttribute> cdFactoryAttributeList = new ArrayList<>();
    List<ASTCDMethod> cdFactoryCreateMethodList = new ArrayList<>();
    List<ASTCDMethod> cdFactoryDoCreateMethodList = new ArrayList<>();

    for (ASTCDClass astcdClass : astcdClassList) {
      if (!astcdClass.isPresentModifier() || (astcdClass.getModifier().isAbstract() && !astcdClass.getName().endsWith("TOP"))) {
        continue;
      }
      // create attribute for AST
      cdFactoryAttributeList.add(this.cdAttributeFacade.createProtectedStaticAttribute(factoryType, FACTORY_INFIX + astcdClass.getName()));

      String astName = astcdClass.getName();
      ASTType astType = this.cdTypeFacade.createTypeByDefinition(astName);

      // add create Method for AST without parameters
      ASTCDMethod createWithoutParameters = this.cdMethodFacade.createPublicStaticMethod(astType, CREATE_INFIX + astName);
      cdFactoryCreateMethodList.add(createWithoutParameters);
      this.glex.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("ast.factorymethods.Create", astName));

      // add doCreate Method for AST without parameters
      ASTCDMethod doCreateWithoutParameters = this.cdMethodFacade.createProtectedMethod(astType, DOCREATE_INFIX + astName);
      cdFactoryDoCreateMethodList.add(doCreateWithoutParameters);
      this.glex.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("ast.factorymethods.DoCreate", astName));

      if (!astcdClass.isEmptyCDAttributes()) {
        //create parameterList
        List<ASTCDParameter> params = this.cdParameterFacade.createParameters(astcdClass.getCDAttributeList());
        String paramCall = getParamCall(params);

        // add create Method for AST without parameters
        ASTCDMethod createWithParameters = this.cdMethodFacade.createPublicStaticMethod(astType, CREATE_INFIX + astName, params);
        cdFactoryCreateMethodList.add(createWithParameters);
        this.glex.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("ast.factorymethods.CreateWithParams", astName, paramCall));


        // add doCreate Method for AST without parameters
        ASTCDMethod doCreateWithParameters = this.cdMethodFacade.createProtectedMethod(astType, DOCREATE_INFIX + astName, params);
        cdFactoryDoCreateMethodList.add(doCreateWithParameters);
        this.glex.replaceTemplate(EMPTY_BODY, getFactoryMethod, new TemplateHookPoint("ast.factorymethods.DoCreateWithParams", astName, paramCall));
      }
    }

    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(modifierBuilder.build())
        .setName(factoryClassName)
        .addCDAttribute(factoryAttribute)
        .addAllCDAttributes(cdFactoryAttributeList)
        .addCDConstructor(constructor)
        .addCDMethod(getFactoryMethod)
        .addAllCDMethods(cdFactoryCreateMethodList)
        .addAllCDMethods(cdFactoryDoCreateMethodList)
        .build();
  }

  private String getParamCall(List<ASTCDParameter> parameterList) {
    String s = "";
    for (ASTCDParameter parameter : parameterList) {
      if (s.isEmpty()) {
        s += parameter.getType().toString() + " " + parameter.getName();
      } else {
        s += ", " + parameter.getType().toString() + " " + parameter.getName();
      }
    }
    return s;
  }

}


