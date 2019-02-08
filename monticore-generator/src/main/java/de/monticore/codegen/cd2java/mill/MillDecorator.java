package de.monticore.codegen.cd2java.mill;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

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

  private List<ASTCDMethod> superBuilderMethods;

  private GeneratorHelper genHelper;

  private List<CDSymbol> superSymbolList;


  public MillDecorator(final GlobalExtensionManagement glex, GeneratorHelper genHelper) {
    this.glex = glex;
    this.genHelper = genHelper;
    this.cdTypeFacade = CDTypeFactory.getInstance();
    this.cdAttributeFacade = CDAttributeFactory.getInstance();
    this.cdConstructorFacade = CDConstructorFactory.getInstance();
    this.cdMethodFacade = CDMethodFactory.getInstance();
    this.cdParameterFacade = CDParameterFactory.getInstance();
    this.builderMethods = new ArrayList<>();
    this.superBuilderMethods = new ArrayList<>();
    this.superSymbolList = new ArrayList<>();
  }

  public ASTCDClass decorate(ASTCDDefinition astcdDefinition) {
    String millClassName = astcdDefinition.getName() + MILL_SUFFIX;
    ASTType millType = this.cdTypeFacade.createTypeByDefinition(millClassName);
    List<ASTCDClass> astcdClassList = Lists.newArrayList(astcdDefinition.getCDClassList());

    superSymbolList = genHelper.getAllSuperCds(genHelper.getCd());

    ASTCDConstructor constructor = this.cdConstructorFacade.createConstructor(PROTECTED, millClassName);

    ASTCDAttribute millAttribute = this.cdAttributeFacade.createAttribute(PROTECTED_STATIC, millType, MILL_INFIX);

    //add mill attribute for each class
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (String attributeName : getAttributeNameList(astcdClassList)) {
      attributeList.add(this.cdAttributeFacade.createAttribute(PROTECTED_STATIC, millType, MILL_INFIX + attributeName));
    }

    //add all standard methods
    ASTCDMethod getMillMethod = addGetMillMethods(millType);

    ASTCDMethod initMeMethod = addInitMeMethod(millType, astcdClassList);

    ASTCDMethod initMethod = addInitMethod(millType);

    ASTCDMethod resetMethod = addResetMethod(astcdClassList);

    //add builder methods for each class
    for (ASTCDClass astcdClass : astcdClassList) {
      if (astcdClass.isEmptyCDAttributes() || !astcdClass.isPresentModifier() || (astcdClass.getModifier().isAbstract() && !astcdClass.getName().endsWith("TOP"))) {
        continue;
      }
      addBuilderMethods(astcdClass);
    }

    addSuperBuilderMethods();

    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC)
        .setName(millClassName)
        .addCDAttribute(millAttribute)
        .addAllCDAttributes(attributeList)
        .addCDConstructor(constructor)
        .addCDMethod(getMillMethod)
        .addCDMethod(initMeMethod)
        .addCDMethod(initMethod)
        .addCDMethod(resetMethod)
        .addAllCDMethods(builderMethods)
        .addAllCDMethods(superBuilderMethods)
        .build();
  }

  private List<String> getAttributeNameList(List<ASTCDClass> astcdClasses) {
    List<String> attributeNames = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClasses) {
      attributeNames.add(astcdClass.getName());
    }
    return attributeNames;
  }

  private ASTCDMethod addGetMillMethods(ASTType millType) {
    ASTCDMethod getMillMethod = this.cdMethodFacade.createMethod(PROTECTED_STATIC, millType, GET_MILL);
    this.glex.replaceTemplate(EMPTY_BODY, getMillMethod, new TemplateHookPoint("mill.GetMillMethod", TypesPrinter.printType(millType)));
    return getMillMethod;
  }

  private ASTCDMethod addInitMeMethod(ASTType millType, List<ASTCDClass> astcdClassList) {
    ASTCDParameter astcdParameter = cdParameterFacade.createParameter(millType, "a");
    ASTCDMethod initMeMethod = this.cdMethodFacade.createMethod(PUBLIC_STATIC, INIT_ME, astcdParameter);
    this.glex.replaceTemplate(EMPTY_BODY, initMeMethod, new TemplateHookPoint("mill.InitMeMethod", getAttributeNameList(astcdClassList)));
    return initMeMethod;
  }

  private ASTCDMethod addInitMethod(ASTType millType) {
    ASTCDMethod initMethod = this.cdMethodFacade.createMethod(PUBLIC_STATIC, INIT);
    this.glex.replaceTemplate(EMPTY_BODY, initMethod, new TemplateHookPoint("mill.InitMethod", TypesPrinter.printType(millType)));
    return initMethod;
  }

  private ASTCDMethod addResetMethod(List<ASTCDClass> astcdClassList) {
    ASTCDMethod resetMethod = this.cdMethodFacade.createMethod(PUBLIC_STATIC, RESET);
    this.glex.replaceTemplate(EMPTY_BODY, resetMethod, new TemplateHookPoint("mill.ResetMethod", getAttributeNameList(astcdClassList), this.superSymbolList));
    return resetMethod;
  }

  private void addBuilderMethods(ASTCDClass astcdClass) {
    String astName = astcdClass.getName();
    ASTType builderType = this.cdTypeFacade.createSimpleReferenceType(astName + BUILDER);

    // add public static Method for Builder
    ASTCDMethod builderMethod = this.cdMethodFacade.createMethod(PUBLIC_STATIC, builderType, StringTransformations.uncapitalize(astName) + BUILDER);
    builderMethods.add(builderMethod);
    this.glex.replaceTemplate(EMPTY_BODY, builderMethod, new TemplateHookPoint("mill.BuilderMethod", astName));

    // add protected Method for Builder
    ASTCDMethod protectedMethod = this.cdMethodFacade.createMethod(PROTECTED, builderType, "_" + StringTransformations.uncapitalize(astName) + BUILDER);
    builderMethods.add(protectedMethod);
    this.glex.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", TypesPrinter.printType(builderType)));
  }

  private void addSuperBuilderMethods() {
    //get super symbols
    for (CDSymbol superSymbol : this.superSymbolList) {
      Optional<ASTNode> astNode = superSymbol.getAstNode();
      if (astNode.isPresent() && astNode.get() instanceof ASTCDDefinition) {
        //get super cddefinition
        ASTCDDefinition superDefinition = (ASTCDDefinition) astNode.get();
        for (ASTCDClass superClass : superDefinition.getCDClassList()) {
          String packageName = superSymbol.getFullName().toLowerCase() + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
          ASTType superAstType = this.cdTypeFacade.createSimpleReferenceType(packageName + superClass.getName());

          //add builder method
          addSuperBuilderMethod(superAstType, superClass.getName(), packageName, superSymbol.getName());
        }
      }
    }
  }

  private void addSuperBuilderMethod(ASTType superAstType, String className, String packageName, String symbolName) {
    ASTCDMethod createDelegateMethod = this.cdMethodFacade.createMethod(PUBLIC_STATIC, superAstType, StringTransformations.uncapitalize(className) + BUILDER);
    this.glex.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("mill.BuilderDelegatorMethod", packageName + symbolName, className));
    this.superBuilderMethods.add(createDelegateMethod);
  }

}
