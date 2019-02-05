package de.monticore.codegen.cd2java.mill;

import com.google.common.collect.Lists;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class MillDecorator implements Decorator<ASTCDDefinition, ASTCDClass> {

  private final GlobalExtensionManagement glex;

  private static final String MILL_SUFFIX = "Mill";

  private static final String MILL_INFIX = "mill";

  private static final String INIT = "init";

  private static final String RESET = "reset";

  private static final String INIT_ME = "initMe";

  private static final String GET_MILL = "getMill";

  private static final String BUILDER = "Builder";

  private final CDTypeFactory cdTypeFacade;

  private final CDAttributeFactory cdAttributeFacade;

  private final CDConstructorFactory cdConstructorFacade;

  private final CDMethodFactory cdMethodFacade;

  private final CDParameterFactory cdParameterFacade;

  private List<ASTCDMethod> builderMethods;

  public MillDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdTypeFacade = CDTypeFactory.getInstance();
    this.cdAttributeFacade = CDAttributeFactory.getInstance();
    this.cdConstructorFacade = CDConstructorFactory.getInstance();
    this.cdMethodFacade = CDMethodFactory.getInstance();
    this.cdParameterFacade = CDParameterFactory.getInstance();
    this.builderMethods = new ArrayList<>();
  }

  @Override
  public ASTCDClass decorate(ASTCDDefinition astcdDefinition) {
    String millClassName = astcdDefinition.getName() + MILL_SUFFIX;
    ASTType millType = this.cdTypeFacade.createTypeByDefinition(millClassName);
    List<ASTCDClass> astcdClassList = Lists.newArrayList(astcdDefinition.getCDClassList());

    ASTModifier modifier = ModifierBuilder.builder().Public().build();

    ASTCDConstructor constructor = this.cdConstructorFacade.createProtectedDefaultConstructor(millClassName);

    ASTCDAttribute millAttribute = this.cdAttributeFacade.createProtectedStaticAttribute(millType, MILL_INFIX);

    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (String attributeName : getAttributeNameList(astcdClassList)) {
      attributeList.add(this.cdAttributeFacade.createProtectedStaticAttribute(millType, MILL_INFIX + attributeName));
    }

    ASTCDMethod getMillMethod = this.cdMethodFacade.createProtectedStaticMethod(millType, GET_MILL);
    this.glex.replaceTemplate(EMPTY_BODY, getMillMethod, new TemplateHookPoint("mill.GetMillMethod", TypesPrinter.printType(millType)));


    ASTCDParameter astcdParameter = cdParameterFacade.createParameter(millType, "mill");
    ASTCDMethod initMeMethod = this.cdMethodFacade.createPublicStaticVoidMethod(INIT_ME, astcdParameter);
    this.glex.replaceTemplate(EMPTY_BODY, initMeMethod, new TemplateHookPoint("mill.InitMeMethod", getAttributeNameList(astcdClassList)));

    ASTCDMethod initMethod = this.cdMethodFacade.createPublicStaticVoidMethod(INIT);
    this.glex.replaceTemplate(EMPTY_BODY, initMethod, new TemplateHookPoint("mill.InitMethod", TypesPrinter.printType(millType)));


    ASTCDMethod resetMethod = this.cdMethodFacade.createPublicStaticVoidMethod(RESET);
    this.glex.replaceTemplate(EMPTY_BODY, resetMethod, new TemplateHookPoint("mill.ResetMethod", getAttributeNameList(astcdClassList)));


    for (ASTCDClass astcdClass : astcdClassList) {
      if (!astcdClass.isPresentModifier() || (astcdClass.getModifier().isAbstract() && !astcdClass.getName().endsWith("TOP"))) {
        continue;
      }
      addBuilderMethods(astcdClass);
    }

    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(modifier)
        .setName(millClassName)
        .addCDAttribute(millAttribute)
        .addAllCDAttributes(attributeList)
        .addCDConstructor(constructor)
        .addCDMethod(getMillMethod)
        .addCDMethod(initMeMethod)
        .addCDMethod(initMethod)
        .addCDMethod(resetMethod)
        .addAllCDMethods(builderMethods)
        .build();
  }

  public List<String> getAttributeNameList(List<ASTCDClass> astcdClasses) {
    List<String> attributeNames = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClasses) {
      attributeNames.add(astcdClass.getName());
    }
    return attributeNames;
  }

  public void addBuilderMethods(ASTCDClass astcdClass) {
    String astName = astcdClass.getName(); //TODO understand when AST prefix exists and when not
    ASTType builderType = this.cdTypeFacade.createSimpleReferenceType(GeneratorHelper.AST_PREFIX + astName + BUILDER);

    // add public static Method for Builder
    ASTCDMethod builderMethod = this.cdMethodFacade.createPublicStaticMethod(builderType, StringTransformations.uncapitalize(astName) + BUILDER);
    builderMethods.add(builderMethod);
    this.glex.replaceTemplate(EMPTY_BODY, builderMethod, new TemplateHookPoint("mill.BuilderMethod", astName));

    // add protected Method for Builder
    ASTCDMethod protectedMethod = this.cdMethodFacade.createProtectedMethod(builderType, "_" + StringTransformations.uncapitalize(astName) + BUILDER);
    builderMethods.add(protectedMethod);
    this.glex.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", TypesPrinter.printType(builderType)));
  }
}
