/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.*;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java.mill.MillConstants.*;
import static de.monticore.codegen.cd2java.top.TopDecorator.TOP_SUFFIX;

/**
 * created mill class for a grammar
 */
public class MillDecorator extends AbstractCreator<List<ASTCDCompilationUnit>, ASTCDClass> {

  protected final SymbolTableService service;

  public MillDecorator(final GlobalExtensionManagement glex,
                       final SymbolTableService service) {
    super(glex);
    this.service = service;
  }

  public ASTCDClass decorate(final List<ASTCDCompilationUnit> cdList) {
    String millClassName = service.getMillSimpleName();
    ASTMCType millType = this.getMCTypeFacade().createQualifiedType(millClassName);

    List<CDDefinitionSymbol> superSymbolList = service.getSuperCDsTransitive();

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED, millClassName);

    ASTCDAttribute millAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX);
    // add all standard methods
    ASTCDMethod getMillMethod = addGetMillMethods(millType);
    ASTCDMethod initMethod = addInitMethod(millType, superSymbolList);

    ASTCDClass millClass = CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(millClassName)
        .addCDAttribute(millAttribute)
        .addCDConstructor(constructor)
        .addCDMethod(getMillMethod)
        .addCDMethod(initMethod)
        .build();

    // list of all classes needed for the reset and initMe method
    List<ASTCDClass> allClasses = new ArrayList<>();

    for (ASTCDCompilationUnit cd : cdList) {
      // filter out all classes that are abstract and only builder classes
      List<ASTCDClass> classList = cd.getCDDefinition().deepClone().getCDClassList()
          .stream()
          .filter(ASTCDClass::isPresentModifier)
          .filter(x -> !x.getModifier().isAbstract())
          .filter(x -> x.getName().endsWith(BUILDER_SUFFIX))
          .collect(Collectors.toList());


      // filter out all classes that are abstract and end with the TOP suffix
      List<ASTCDClass> topClassList = cd.getCDDefinition().deepClone().getCDClassList()
          .stream()
          .filter(ASTCDClass::isPresentModifier)
          .filter(x -> x.getModifier().isAbstract())
          .filter(x -> x.getName().endsWith(TOP_SUFFIX))
          .collect(Collectors.toList());
      // remove TOP suffix
      topClassList.forEach(x -> x.setName(x.getName().substring(0, x.getName().length() - 3)));
      // check if builder classes
      topClassList = topClassList
          .stream()
          .filter(x -> x.getName().endsWith(BUILDER_SUFFIX))
          .collect(Collectors.toList());
      // add to classes which need a builder method
      classList.addAll(topClassList);

      // add to all class list for reset and initMe method
      allClasses.addAll(classList);

      // add mill attribute for each class
      List<ASTCDAttribute> attributeList = new ArrayList<>();
      for (String attributeName : getAttributeNameList(classList)) {
        attributeList.add(this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX + attributeName));
      }

      List<ASTCDMethod> builderMethodsList = addBuilderMethods(classList, cd);


      millClass.addAllCDAttributes(attributeList);
      millClass.addAllCDMethods(builderMethodsList);
    }

    // add builder methods for each class
    List<ASTCDMethod> superMethodsList = addSuperBuilderMethods(superSymbolList, allClasses);
    millClass.addAllCDMethods(superMethodsList);

    ASTCDMethod initMeMethod = addInitMeMethod(millType, allClasses);
    millClass.addCDMethod(initMeMethod);

    ASTCDMethod resetMethod = addResetMethod(allClasses, superSymbolList);
    millClass.addCDMethod(resetMethod);

    return millClass;
  }

  protected List<String> getAttributeNameList(List<ASTCDClass> astcdClasses) {
    List<String> attributeNames = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClasses) {
      attributeNames.add(astcdClass.getName());
    }
    return attributeNames;
  }

  protected ASTCDMethod addGetMillMethods(ASTMCType millType) {
    ASTCDMethod getMillMethod = this.getCDMethodFacade().createMethod(PROTECTED_STATIC, millType, GET_MILL);
    this.replaceTemplate(EMPTY_BODY, getMillMethod, new TemplateHookPoint("mill.GetMillMethod", millType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())));
    return getMillMethod;
  }

  protected ASTCDMethod addInitMeMethod(ASTMCType millType, List<ASTCDClass> astcdClassList) {
    ASTCDParameter astcdParameter = getCDParameterFacade().createParameter(millType, "a");
    ASTCDMethod initMeMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, INIT_ME, astcdParameter);
    this.replaceTemplate(EMPTY_BODY, initMeMethod, new TemplateHookPoint("mill.InitMeMethod", getAttributeNameList(astcdClassList)));
    return initMeMethod;
  }

  protected ASTCDMethod addInitMethod(ASTMCType millType, List<CDDefinitionSymbol> superSymbolList) {
    ASTCDMethod initMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, INIT);
    this.replaceTemplate(EMPTY_BODY, initMethod, new TemplateHookPoint("mill.InitMethod", millType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()), superSymbolList));
    return initMethod;
  }

  protected ASTCDMethod addResetMethod(List<ASTCDClass> astcdClassList, List<CDDefinitionSymbol> superSymbolList) {
    ASTCDMethod resetMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, RESET);
    this.replaceTemplate(EMPTY_BODY, resetMethod, new TemplateHookPoint("mill.ResetMethod", getAttributeNameList(astcdClassList), superSymbolList));
    return resetMethod;
  }

  protected List<ASTCDMethod> addBuilderMethods(List<ASTCDClass> astcdClassList, ASTCDCompilationUnit cd) {
    List<ASTCDMethod> builderMethodsList = new ArrayList<>();

    for (ASTCDClass astcdClass : astcdClassList) {
      String astName = astcdClass.getName();
      String packageDef = String.join(".", cd.getPackageList());
      ASTMCQualifiedType builderType = this.getMCTypeFacade().createQualifiedType(packageDef + "." + astName);
      String methodName = astName.startsWith(AST_PREFIX) ?
          StringTransformations.uncapitalize(astName.replaceFirst(AST_PREFIX, ""))
          : StringTransformations.uncapitalize(astName);

      // add public static Method for Builder
      ASTCDMethod builderMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, builderType, methodName);
      builderMethodsList.add(builderMethod);
      this.replaceTemplate(EMPTY_BODY, builderMethod, new TemplateHookPoint("mill.BuilderMethod", astName, methodName));

      // add protected Method for Builder
      ASTCDMethod protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED, builderType, "_" + methodName);
      builderMethodsList.add(protectedMethod);
      this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", builderType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())));
    }

    return builderMethodsList;
  }

  /**
   * adds builder methods for the delegation to builders of super grammars
   */
  protected List<ASTCDMethod> addSuperBuilderMethods(List<CDDefinitionSymbol> superSymbolList, List<ASTCDClass> classList) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    // get super symbols
    for (CDDefinitionSymbol superSymbol : superSymbolList) {
      if (superSymbol.isPresentAstNode()) {
        for (CDTypeSymbol type : superSymbol.getTypes()) {
          if (type.isPresentAstNode() && type.getAstNode().isPresentModifier()
              && service.hasSymbolStereotype(type.getAstNode().getModifier())) {
            superMethods.addAll(getSuperSymbolMethods(superSymbol, type));
          }
          if (type.isIsClass() && !type.isIsAbstract() && type.isPresentAstNode() &&
              !service.isClassOverwritten(type.getName() + BUILDER_SUFFIX, classList)) {
            superMethods.addAll(getSuperASTMethods(superSymbol, type, superMethods));
          }
        }
      }
    }
    return superMethods;
  }

  protected List<ASTCDMethod> getSuperSymbolMethods(CDDefinitionSymbol superSymbol, CDTypeSymbol type) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    // for prod with symbol property create delegate builder method
    String symbolBuilderFullName = service.getSymbolBuilderFullName(type.getAstNode(), superSymbol);
    String millFullName = service.getMillFullName(superSymbol);
    String symbolBuilderSimpleName = StringTransformations.uncapitalize(service.getSymbolBuilderSimpleName(type.getAstNode()));
    ASTCDMethod builderMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC,
        getMCTypeFacade().createQualifiedType(symbolBuilderFullName), symbolBuilderSimpleName);

    this.replaceTemplate(EMPTY_BODY, builderMethod, new StringHookPoint("return " + millFullName + "." + symbolBuilderSimpleName + "();"));
    superMethods.add(builderMethod);

    // create corresponding builder for symbolSurrogate
    String symbolSurrogateBuilderFullName = service.getSymbolSurrogateBuilderFullName(type.getAstNode(), superSymbol);
    String symbolSurrogateBuilderSimpleName = StringTransformations.uncapitalize(service.getSymbolSurrogateBuilderSimpleName(type.getAstNode()));
    ASTCDMethod builderLoaderMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC,
        getMCTypeFacade().createQualifiedType(symbolSurrogateBuilderFullName), symbolSurrogateBuilderSimpleName);

    this.replaceTemplate(EMPTY_BODY, builderLoaderMethod, new StringHookPoint("return " + millFullName + "." + symbolSurrogateBuilderSimpleName + "();"));
    superMethods.add(builderLoaderMethod);
    return superMethods;
  }


  protected List<ASTCDMethod> getSuperASTMethods(CDDefinitionSymbol superSymbol, CDTypeSymbol type,
                                                 List<ASTCDMethod> alreadyDefinedMethods) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    String astPackageName = superSymbol.getFullName().toLowerCase() + "." + AST_PACKAGE + ".";
    ASTMCQualifiedType superAstType = this.getMCTypeFacade().createQualifiedType(astPackageName + type.getName() + BUILDER_SUFFIX);
    String methodName = StringTransformations.uncapitalize(type.getName().replaceFirst(AST_PREFIX, "")) + BUILDER_SUFFIX;

    // add builder method
    ASTCDMethod createDelegateMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, superAstType, methodName);
    if (!service.isMethodAlreadyDefined(createDelegateMethod, alreadyDefinedMethods)) {
      String millPackageName = superSymbol.getFullName().toLowerCase() + ".";
      this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("mill.BuilderDelegatorMethod", millPackageName + superSymbol.getName(), methodName));
      superMethods.add(createDelegateMethod);
    }
    return superMethods;
  }

}
