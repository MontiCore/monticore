package de.monticore.codegen.cd2java._ast.mill;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.factories.SuperSymbolHelper;
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
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class MillDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDClass> {

  private static final String MILL_SUFFIX = "Mill";

  private static final String MILL_INFIX = "mill";

  private static final String INIT = "init";

  private static final String RESET = "reset";

  private static final String INIT_ME = "initMe";

  private static final String GET_MILL = "getMill";

  private static final String BUILDER = "Builder";

  private ASTCDDefinition astcdDefinition;

  private final AbstractService service;

  public MillDecorator(final GlobalExtensionManagement glex, final AbstractService service) {
    super(glex);
    this.service = service;
  }

  public ASTCDClass decorate(ASTCDCompilationUnit compilationUnit) {
    astcdDefinition = compilationUnit.getCDDefinition().deepClone();
    //filter out all classes that are abstract and remove AST prefix
    astcdDefinition.setCDClassList(astcdDefinition.getCDClassList()
        .stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(x -> !x.getModifier().isAbstract())
        .collect(Collectors.toList()));

    String millClassName = astcdDefinition.getName() + MILL_SUFFIX;
    ASTType millType = this.getCDTypeFacade().createTypeByDefinition(millClassName);
    List<ASTCDClass> astcdClassList = Lists.newArrayList(astcdDefinition.getCDClassList());

    List<CDSymbol> superSymbolList = SuperSymbolHelper.getSuperCDs(compilationUnit);

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED, millClassName);

    ASTCDAttribute millAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX);

    //add mill attribute for each class
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (String attributeName : getAttributeNameList(astcdClassList)) {
      attributeList.add(this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX + attributeName));
    }

    //add all standard methods
    ASTCDMethod getMillMethod = addGetMillMethods(millType);

    ASTCDMethod initMeMethod = addInitMeMethod(millType, astcdClassList);

    ASTCDMethod initMethod = addInitMethod(millType);

    ASTCDMethod resetMethod = addResetMethod(astcdClassList, superSymbolList);

    List<ASTCDMethod> builderMethodsList = addBuilderMethods(astcdClassList);

    //add builder methods for each class
    List<ASTCDMethod> superMethodsList = addSuperBuilderMethods(superSymbolList);

    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(millClassName)
        .addCDAttribute(millAttribute)
        .addAllCDAttributes(attributeList)
        .addCDConstructor(constructor)
        .addCDMethod(getMillMethod)
        .addCDMethod(initMeMethod)
        .addCDMethod(initMethod)
        .addCDMethod(resetMethod)
        .addAllCDMethods(builderMethodsList)
        .addAllCDMethods(superMethodsList)
        .build();
  }

  protected List<String> getAttributeNameList(List<ASTCDClass> astcdClasses) {
    List<String> attributeNames = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClasses) {
      attributeNames.add(astcdClass.getName());
    }
    return attributeNames;
  }

  protected ASTCDMethod addGetMillMethods(ASTType millType) {
    ASTCDMethod getMillMethod = this.getCDMethodFacade().createMethod(PROTECTED_STATIC, millType, GET_MILL);
    this.replaceTemplate(EMPTY_BODY, getMillMethod, new TemplateHookPoint("mill.GetMillMethod", TypesPrinter.printType(millType)));
    return getMillMethod;
  }

  protected ASTCDMethod addInitMeMethod(ASTType millType, List<ASTCDClass> astcdClassList) {
    ASTCDParameter astcdParameter = getCDParameterFacade().createParameter(millType, "a");
    ASTCDMethod initMeMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, INIT_ME, astcdParameter);
    this.replaceTemplate(EMPTY_BODY, initMeMethod, new TemplateHookPoint("mill.InitMeMethod", getAttributeNameList(astcdClassList)));
    return initMeMethod;
  }

  protected ASTCDMethod addInitMethod(ASTType millType) {
    ASTCDMethod initMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, INIT);
    this.replaceTemplate(EMPTY_BODY, initMethod, new TemplateHookPoint("mill.InitMethod", TypesPrinter.printType(millType)));
    return initMethod;
  }

  protected ASTCDMethod addResetMethod(List<ASTCDClass> astcdClassList, List<CDSymbol> superSymbolList) {
    ASTCDMethod resetMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, RESET);
    this.replaceTemplate(EMPTY_BODY, resetMethod, new TemplateHookPoint("mill.ResetMethod", getAttributeNameList(astcdClassList), superSymbolList));
    return resetMethod;
  }

  protected List<ASTCDMethod> addBuilderMethods(List<ASTCDClass> astcdClassList) {
    List<ASTCDMethod> builderMethodsList = new ArrayList<>();

    for (ASTCDClass astcdClass : astcdClassList) {
      String astName = astcdClass.getName();
      ASTType builderType = this.getCDTypeFacade().createSimpleReferenceType(astName + BUILDER);
      String methodName = StringTransformations.uncapitalize(astName.replaceFirst("AST", "")) + BUILDER;

      // add public static Method for Builder
      ASTCDMethod builderMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, builderType, methodName);
      builderMethodsList.add(builderMethod);
      this.replaceTemplate(EMPTY_BODY, builderMethod, new TemplateHookPoint("mill.BuilderMethod", astName, methodName));

      // add protected Method for Builder
      ASTCDMethod protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED, builderType, "_" + methodName);
      builderMethodsList.add(protectedMethod);
      this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", TypesPrinter.printType(builderType)));
    }

    return builderMethodsList;
  }

  protected List<ASTCDMethod> addSuperBuilderMethods(List<CDSymbol> superSymbolList) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    //get super symbols
    for (CDSymbol superSymbol : superSymbolList) {
      Optional<ASTNode> astNode = superSymbol.getAstNode();
      if (astNode.isPresent() && astNode.get() instanceof ASTCDDefinition) {
        //get super cddefinition
        ASTCDDefinition superDefinition = (ASTCDDefinition) astNode.get();
        //filter out all abstract classes
        superDefinition.setCDClassList(superDefinition.getCDClassList()
            .stream()
            .filter(ASTCDClassTOP::isPresentModifier)
            .filter(x -> !x.getModifier().isAbstract())
            .collect(Collectors.toList()));

        for (ASTCDClass superClass : superDefinition.getCDClassList()) {
          if (!service.isClassOverwritten(superClass, astcdDefinition.getCDClassList())) {
            String packageName = superSymbol.getFullName().toLowerCase() + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
            ASTType superAstType = this.getCDTypeFacade().createSimpleReferenceType(packageName + superClass.getName() + BUILDER);
            String methodName = StringTransformations.uncapitalize(superClass.getName().replaceFirst("AST", "")) + BUILDER;

            //add builder method
            ASTCDMethod createDelegateMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, superAstType, methodName);
            if (!service.isMethodAlreadyDefined(createDelegateMethod.getName(), superMethods)) {
              this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("mill.BuilderDelegatorMethod", packageName + superSymbol.getName(), methodName));
              superMethods.add(createDelegateMethod);
            }
          }
        }
      }
    }
    return superMethods;
  }

}
