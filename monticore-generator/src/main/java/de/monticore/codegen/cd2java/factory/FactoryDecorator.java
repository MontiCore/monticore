package de.monticore.codegen.cd2java.factory;

import com.google.common.collect.Lists;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Generator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;

public class FactoryDecorator implements Generator<ASTCDDefinition, ASTCDClass> {

  private final GlobalExtensionManagement glex;

  private static final String NODE_FACTORY_SUFFIX = "NodeFactory";

  private static final String FACTORY_INFIX = "factory";

  private static final String GET_FACTORY_METHOD = "getFactory";

  private static final String DOCREATE_INFIX = "doCreate";

  private static final String CREATE_INFIX = "create";

  private final CDTypeFactory cdTypeFassade;

  private final CDAttributeFactory cdAttributeFassade;

  private final CDConstructorFactory cdConstructorFassade;

  private final CDMethodFactory cdMethodFassade;

  private final CDParameterFactory cdParameterFassade;

  public FactoryDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdTypeFassade = CDTypeFactory.getInstance();
    this.cdAttributeFassade = CDAttributeFactory.getInstance();
    this.cdConstructorFassade = CDConstructorFactory.getInstance();
    this.cdMethodFassade = CDMethodFactory.getInstance();
    this.cdParameterFassade = CDParameterFactory.getInstance();
  }

  public ASTCDClass generate(ASTCDDefinition astcdDefinition) {
    String factoryClassName = astcdDefinition.getName() + NODE_FACTORY_SUFFIX;
    ASTType domainType = this.cdTypeFassade.createTypeByDefinition(astcdDefinition.getName());
    ASTType factoryType = this.cdTypeFassade.createTypeByDefinition(factoryClassName);

    ModifierBuilder modifierBuilder = ModifierBuilder.builder().Public();

    ASTCDAttribute factoryAttribute = this.cdAttributeFassade.createProtectedStaticAttribute(factoryType, FACTORY_INFIX);

    List<ASTCDClass> astcdClassList = Lists.newArrayList(astcdDefinition.getCDClassList());
    List<ASTCDAttribute> cdFactoryAttributeList = new ArrayList<>();
    List<ASTCDMethod> cdFactoryCreateMethodList = new ArrayList<>();
    List<ASTCDMethod> cdFactoryDoCreateMethodList = new ArrayList<>();

    for (ASTCDClass astcdClass : astcdClassList) {
      if (!astcdClass.isPresentModifier() || (astcdClass.getModifier().isAbstract() && !astcdClass.getName().endsWith("TOP"))) {
        continue;
      }
      // create attribute for AST
      cdFactoryAttributeList.add(this.cdAttributeFassade.createProtectedStaticAttribute(factoryType, FACTORY_INFIX + GeneratorHelper.AST_PREFIX + astcdClass.getName()));

      String astName = GeneratorHelper.AST_PREFIX + astcdClass.getName();
      ASTType astType = this.cdTypeFassade.createTypeByDefinition(astName);

      // add create Method for AST without parameters
      cdFactoryCreateMethodList.add(this.cdMethodFassade.createPublicStaticMethod(astType, CREATE_INFIX + astName));

      // add doCreate Method for AST without parameters
      cdFactoryDoCreateMethodList.add(this.cdMethodFassade.createProtectedMethod(astType, DOCREATE_INFIX + astName));

      if (!astcdClass.isEmptyCDAttributes()) {
        // add create Method for AST without parameters
        cdFactoryCreateMethodList.add(this.cdMethodFassade.createPublicStaticMethod(astType, CREATE_INFIX + astName, this.cdParameterFassade.createParameters(astcdClass.getCDAttributeList())));

        // add doCreate Method for AST without parameters
        cdFactoryDoCreateMethodList.add(this.cdMethodFassade.createProtectedMethod(astType, DOCREATE_INFIX + astName, this.cdParameterFassade.createParameters(astcdClass.getCDAttributeList())));
      }
    }

    ASTCDConstructor constructor = this.cdConstructorFassade.createProtectedDefaultConstructor(factoryClassName);

    ASTCDMethod getFactoryMethod = this.cdMethodFassade.createPublicStaticMethod(factoryType, GET_FACTORY_METHOD);


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
}
