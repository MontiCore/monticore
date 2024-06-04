/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import com.google.common.collect.Lists;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.JavaDoc;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.mcbasics._prettyprint.MCBasicsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.codegen.CD2JavaTemplatesFix.JAVADOC;
import static de.monticore.cd.codegen.TopDecorator.TOP_SUFFIX;
import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.*;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._parser.ParserConstants.PARSER_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.mill.MillConstants.*;
import static de.monticore.codegen.cd2java.typedispatcher.TypeDispatcherConstants.TYPE_DISPATCHER_SUFFIX;
import static de.monticore.codegen.prettyprint.PrettyPrinterConstants.*;

/**
 * created mill class for a grammar
 */
public class MillDecorator extends AbstractCreator<List<ASTCDPackage>, ASTCDClass> {

  protected final SymbolTableService symbolTableService;
  protected final VisitorService visitorService;
  protected final ParserService parserService;

  public MillDecorator(final GlobalExtensionManagement glex,
                       final SymbolTableService symbolTableService,
                       final VisitorService visitorService,
                       final ParserService parserService) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
    this.parserService = parserService;
  }

  public ASTCDClass decorate(List<ASTCDPackage> packageList) {
    String millClassName = symbolTableService.getMillSimpleName();
    ASTMCType millType = this.getMCTypeFacade().createQualifiedType(millClassName);

    String fullDefinitionName = symbolTableService.getCDSymbol().getFullName();

    List<DiagramSymbol> superSymbolList = symbolTableService.getSuperCDsTransitive();

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED.build(), millClassName);

    ASTCDAttribute millAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC.build(), millType, MILL_INFIX);
    // add all standard methods
    ASTCDMethod getMillMethod = addGetMillMethods(millType);
    ASTCDMethod initMethod = addInitMethod(millType, superSymbolList, fullDefinitionName);

    ASTCDClass millClass = CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(millClassName)
        .addCDMember(millAttribute)
        .addCDMember(constructor)
        .addCDMember(getMillMethod)
        .addCDMember(initMethod)
        .build();

    // list of all classes needed for the reset and initMe method
    List<ASTCDClass> allClasses = new ArrayList<>();

    for (ASTCDPackage cd : packageList) {
      // filter out all classes that are abstract and only builder classes
      List<ASTCDClass> classList = cd.getCDElementList()
          .stream()
          .filter(x -> x instanceof ASTCDClass)
          .map(x -> (ASTCDClass) x)
          .filter(x -> !x.getModifier().isAbstract())
          .filter(this::checkIncludeInMill)
          .map(x -> x.deepClone())
          .collect(Collectors.toList());


      // filter out all classes that are abstract and end with the TOP suffix
      List<ASTCDClass> topClassList = cd.getCDElementList()
          .stream()
          .filter(x -> x instanceof ASTCDClass)
          .map(x -> (ASTCDClass) x)
          .filter(x -> x.getModifier().isAbstract())
          .filter(x -> x.getName().endsWith(TOP_SUFFIX))
          .collect(Collectors.toList());
      // remove TOP suffix
      topClassList.forEach(x -> x.setName(x.getName().substring(0, x.getName().length() - 3)));
      // check if builder classes
      topClassList = topClassList
          .stream()
          .filter(this::checkIncludeInMill)
          .collect(Collectors.toList());
      // add to classes which need a builder method
      classList.addAll(topClassList);

      // add to all class list for reset and initMe method
      allClasses.addAll(classList);

      // add mill attribute for each class
      List<ASTCDAttribute> attributeList = new ArrayList<>();
      for (String attributeName : getAttributeNameList(classList)) {
        attributeList.add(this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC.build(), millType, MILL_INFIX + attributeName));
      }
      // add pretty printer functionality
      List<ASTCDMember> prettyPrinterMembersList = new ArrayList<>();
      Optional<ASTCDClass> fullPrettyPrinterCandidate = classList.stream().filter(c -> c.getName().endsWith(FULLPRETTYPRINTER_SUFFIX)).findFirst();
      fullPrettyPrinterCandidate.ifPresent(astcdClass -> prettyPrinterMembersList.addAll(addPrettyPrinterMembers(astcdClass, cd)));

      // add static getter for type dispatcher singleton
      List<ASTCDMember> typeDispatcherMemberList = new ArrayList<>();
      Optional<ASTCDClass> typeDispatcherClass = cd.getCDElementList().stream()
          .filter(e -> e instanceof ASTCDClass)
          .map(e -> (ASTCDClass) e)
          .filter(c -> c.getName().endsWith(TYPE_DISPATCHER_SUFFIX))
          .findFirst();
      typeDispatcherClass.ifPresent(c -> typeDispatcherMemberList.addAll(addTypeDispatcherMembers(c, cd, millType)));

      //remove the methods that are generated in the code below the for-loop
      classList = classList.stream().filter(this::checkNotGeneratedSpecifically).collect(Collectors.toList());
      List<ASTCDMethod> builderMethodsList = addBuilderMethods(classList, cd);

      millClass.addAllCDMembers(attributeList);
      millClass.addAllCDMembers(builderMethodsList);
      millClass.addAllCDMembers(prettyPrinterMembersList);
      millClass.addAllCDMembers(typeDispatcherMemberList);
    }

    // decorate for traverser
    List<ASTCDMethod> traverserMethods = getAttributeMethods(visitorService.getTraverserSimpleName(),
        visitorService.getTraverserFullName(), TRAVERSER, visitorService.getTraverserInterfaceFullName());
    millClass.addAllCDMembers(traverserMethods);

    // decorate for traverser with InheritanceHandler
    List<ASTCDMethod> traverserInheritanceMethods = getInheritanceTraverserMethods(visitorService.getInheritanceHandlerSimpleName(),
        visitorService.getTraverserFullName(), INHERITANCE_TRAVERSER, visitorService.getTraverserInterfaceFullName());
    millClass.addAllCDMembers(traverserInheritanceMethods);

    // decorate for global scope
    //globalScope
    String globalScopeAttributeName = StringTransformations.uncapitalize(symbolTableService.getGlobalScopeSimpleName());
    ASTCDAttribute globalScopeAttribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), symbolTableService.getGlobalScopeInterfaceType(), globalScopeAttributeName);
    List<ASTCDMethod> globalScopeMethods = getGlobalScopeMethods(globalScopeAttribute);
    millClass.addCDMember(globalScopeAttribute);
    millClass.addAllCDMembers(globalScopeMethods);

    //artifactScope
    millClass.addAllCDMembers(getArtifactScopeMethods());


    if (!symbolTableService.hasComponentStereotype((ASTCDDefinition) symbolTableService.getCDSymbol().getAstNode())) {
      ASTCDAttribute parserAttribute = getCDAttributeFacade().createAttribute(PROTECTED_STATIC.build(), millType, MILL_INFIX + parserService.getParserClassSimpleName());
      List<ASTCDMethod> parserMethods = getParserMethods();
      millClass.addCDMember(parserAttribute);
      millClass.addAllCDMembers(parserMethods);
      allClasses.add(CD4AnalysisMill.cDClassBuilder()
          .setName(parserService.getParserClassSimpleName())
          .setModifier(CD4CodeMill.modifierBuilder().build()).build());
    }
    //scope
    millClass.addAllCDMembers(getScopeMethods());

    //decorate for scopesgenitor
    Optional<String> startProd = symbolTableService.getStartProdASTFullName();
    if (startProd.isPresent()) {
      millClass.addAllCDMembers(getScopesGenitorMethods());

      millClass.addAllCDMembers(getScopesGenitorDelegatorMethods());
    }

    // add builder methods for each class
    List<ASTCDMethod> superMethodsList = addSuperBuilderMethods(superSymbolList, allClasses);
    millClass.addAllCDMembers(superMethodsList);

    ASTCDMethod initMeMethod = addInitMeMethod(millType, allClasses);
    millClass.addCDMember(initMeMethod);

    ASTCDMethod resetMethod = addResetMethod(allClasses, superSymbolList);
    millClass.addCDMember(resetMethod);

    return millClass;
  }

  /**
   * checks if the class is generated specifically, i.e. there are special methods for the parser, scope, artifactScope and globalScope
   * These special methods can also be seen in this class, e.g. {@link #getScopeMethods()}
   * if a new class does not need a specific mill implementation, normal methods will be generated for it
   */
  protected boolean checkNotGeneratedSpecifically(ASTCDClass cdClass) {
    String name = cdClass.getName();
    String cdName = symbolTableService.getCDName();
    return !(name.endsWith(PARSER_SUFFIX)
        || name.endsWith(cdName + SCOPE_SUFFIX)
        || name.endsWith(cdName + ARTIFACT_PREFIX + SCOPE_SUFFIX)
        || name.endsWith(cdName + GLOBAL_SUFFIX + SCOPE_SUFFIX)
        || name.endsWith(SCOPES_GENITOR_SUFFIX)
        || name.endsWith(SCOPES_GENITOR_SUFFIX + DELEGATOR_SUFFIX)
        || name.endsWith(TRAVERSER_CLASS_SUFFIX)
        || name.endsWith(FULLPRETTYPRINTER_SUFFIX)
        || name.endsWith(INHERITANCE_SUFFIX + HANDLER_SUFFIX)
        || name.endsWith(TYPE_DISPATCHER_SUFFIX));
  }

  /**
   * checks if a class should be included in the mill, example: serialization classes should not be included
   * so they are not mentioned in this method
   */
  protected boolean checkIncludeInMill(ASTCDClass cdClass) {
    String name = cdClass.getName();
    String cdName = symbolTableService.getCDName();
    return name.endsWith(BUILDER_SUFFIX)
        || name.endsWith(SYMBOL_TABLE_CREATOR_SUFFIX)
        || name.endsWith(SYMBOL_TABLE_CREATOR_SUFFIX + DELEGATOR_SUFFIX)
        || name.endsWith(PARSER_SUFFIX)
        || name.endsWith(cdName + SCOPE_SUFFIX)
        || name.endsWith(cdName + ARTIFACT_PREFIX + SCOPE_SUFFIX)
        || name.endsWith(cdName + GLOBAL_SUFFIX + SCOPE_SUFFIX)
        || name.endsWith(SCOPES_GENITOR_SUFFIX)
        || name.endsWith(SCOPES_GENITOR_SUFFIX + DELEGATOR_SUFFIX)
        || name.endsWith(TRAVERSER_CLASS_SUFFIX)
        || name.endsWith(INHERITANCE_SUFFIX + HANDLER_SUFFIX)
        || name.endsWith(FULLPRETTYPRINTER_SUFFIX)
        || name.endsWith(TYPE_DISPATCHER_SUFFIX);
  }

  protected List<String> getAttributeNameList(List<ASTCDClass> astcdClasses) {
    List<String> attributeNames = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClasses) {
      attributeNames.add(astcdClass.getName());
    }
    return attributeNames;
  }

  protected ASTCDMethod addGetMillMethods(ASTMCType millType) {
    ASTCDMethod getMillMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), millType, GET_MILL);
    this.replaceTemplate(EMPTY_BODY, getMillMethod, new TemplateHookPoint("mill.GetMillMethod", millType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())));
    return getMillMethod;
  }

  protected ASTCDMethod addInitMeMethod(ASTMCType millType, List<ASTCDClass> astcdClassList) {
    ASTCDParameter astcdParameter = getCDParameterFacade().createParameter(millType, "a");
    ASTCDMethod initMeMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), INIT_ME, astcdParameter);
    this.replaceTemplate(EMPTY_BODY, initMeMethod, new TemplateHookPoint("mill.InitMeMethod", getAttributeNameList(astcdClassList)));
    return initMeMethod;
  }

  protected ASTCDMethod addInitMethod(ASTMCType millType, List<DiagramSymbol> superSymbolList, String fullDefinitionName) {
    ASTCDMethod initMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), INIT);
    this.replaceTemplate(EMPTY_BODY, initMethod, new TemplateHookPoint("mill.InitMethod", millType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()), superSymbolList, fullDefinitionName + "." + AUXILIARY_PACKAGE));
    this.replaceTemplate(JAVADOC, initMethod, JavaDoc.of("Initializes a languages Mill.",
            "This will also initialize the Mills of all languages it depends on.",
            "This ensures that all objects of this mill, such as builders, traversers, scopes, ..., deliver the element of the correct language.").asHP());
    return initMethod;
  }

  protected ASTCDMethod addResetMethod(List<ASTCDClass> astcdClassList, List<DiagramSymbol> superSymbolList) {
    ASTCDMethod resetMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), RESET);
    this.replaceTemplate(EMPTY_BODY, resetMethod, new TemplateHookPoint("mill.ResetMethod", getAttributeNameList(astcdClassList), superSymbolList));
    return resetMethod;
  }

  protected List<ASTCDMethod> addBuilderMethods(List<ASTCDClass> astcdClassList, ASTCDPackage cd) {
    List<ASTCDMethod> builderMethodsList = new ArrayList<>();

    for (ASTCDClass astcdClass : astcdClassList) {
      String astName = astcdClass.getName();
      String packageDef = cd.getName();
      ASTMCQualifiedType builderType = this.getMCTypeFacade().createQualifiedType(packageDef + "." + astName);
      String methodName = astName.startsWith(AST_PREFIX) ?
          StringTransformations.uncapitalize(astName.replaceFirst(AST_PREFIX, ""))
          : StringTransformations.uncapitalize(astName);

      // add public static Method for Builder
      ASTModifier modifier = PUBLIC_STATIC.build();
      ASTCDMethod builderMethod = this.getCDMethodFacade().createMethod(modifier, builderType, methodName);
      builderMethodsList.add(builderMethod);
      this.replaceTemplate(EMPTY_BODY, builderMethod, new TemplateHookPoint("mill.BuilderMethod", astName, methodName));

      // add protected Method for Builder
      ASTModifier protectedModifier = PROTECTED.build();
      ASTCDMethod protectedMethod = this.getCDMethodFacade().createMethod(protectedModifier, builderType, "_" + methodName);
      builderMethodsList.add(protectedMethod);
      this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", builderType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())));
    }

    return builderMethodsList;
  }

  protected List<ASTCDMember> addPrettyPrinterMembers(ASTCDClass astcdClass, ASTCDPackage cd) {
    // Similar to addBuilderMethods, just for FullPrettyPrinters (with the additional printComments parameter)
    List<ASTCDMember> prettyPrintMembersList = new ArrayList<>();

    String astName = astcdClass.getName();
    String packageDef = cd.getName();
    ASTMCQualifiedType fullPrettyPrinterType = this.getMCTypeFacade().createQualifiedType(packageDef + "." + astName);

    ASTCDParameter node = this.getCDParameterFacade().createParameter(AST_INTERFACE, "node");
    ASTCDParameter printComments = this.getCDParameterFacade().createParameter(
        this.getMCTypeFacade().createBooleanType(), "printComments");

    // add public static Method for pretty printing
    ASTCDMethod builderMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), getMCTypeFacade().createStringType(), "prettyPrint", node, printComments);
    prettyPrintMembersList.add(builderMethod);
    this.replaceTemplate(EMPTY_BODY, builderMethod, new TemplateHookPoint("mill.PrettyPrintBuilderMethod", astName));
    this.replaceTemplate(JAVADOC, builderMethod, JavaDoc.of("Uses the composed pretty printer to print an ASTNode of this language")
            .param("node", "The ASTNode to be printed")
            .param("printComments", "Whether comments should be printed")
            .block("return", "The pretty printer output")
            .asHP());

    // add protected Method for pretty printing
    ASTCDMethod protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED.build(), getMCTypeFacade().createStringType(), "_prettyPrint", node, printComments);
    prettyPrintMembersList.add(protectedMethod);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.PrettyPrintProtectedBuilderMethod", fullPrettyPrinterType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())));

    // attribute for caching the full pretty printer
    prettyPrintMembersList.add(this.getCDAttributeFacade().createAttribute(PROTECTED.build(), fullPrettyPrinterType, "fullPrettyPrinter"));

    return prettyPrintMembersList;
  }

  protected List<ASTCDMember> addTypeDispatcherMembers(ASTCDClass cdClass, ASTCDPackage cdPackage, ASTMCType millType) {
    List<ASTCDMember> typeDispatcherMembers = new ArrayList<>();

    String typeDispatcherName = cdClass.getName();
    String packageName = cdPackage.getName();
    String millDispatcherName = MILL_INFIX + typeDispatcherName;
    ASTMCQualifiedType typeDispatcherType = this.getMCTypeFacade().createQualifiedType(packageName + ".I" + typeDispatcherName);

    typeDispatcherMembers.add(this.getCDAttributeFacade().createAttribute(PROTECTED.build(), typeDispatcherType, "typeDispatcher"));

    ASTCDMethod staticGetter = this.getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), typeDispatcherType, "typeDispatcher");
    this.replaceTemplate(EMPTY_BODY, staticGetter,
        new TemplateHookPoint("mill.BuilderMethod",
            typeDispatcherName, "typeDispatcher"));
    typeDispatcherMembers.add(staticGetter);

    ASTCDMethod protectedGetter = this.getCDMethodFacade().createMethod(PROTECTED.build(), typeDispatcherType, "_typeDispatcher");
    this.replaceTemplate(EMPTY_BODY, protectedGetter,
        new TemplateHookPoint("mill.TypeDispatcherGetter",
            millDispatcherName,
            packageName + "." + typeDispatcherName));
    typeDispatcherMembers.add(protectedGetter);

    return typeDispatcherMembers;
  }

  /**
   * adds builder methods for the delegation to builders of super grammars
   */
  protected List<ASTCDMethod> addSuperBuilderMethods(List<DiagramSymbol> superSymbolList, List<ASTCDClass> classList) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    // get super symbols
    for (DiagramSymbol superSymbol : superSymbolList) {
      if (superSymbol.isPresentAstNode()) {
        for (CDTypeSymbol type : symbolTableService.getAllCDTypes(superSymbol)) {
          if (!type.isPresentAstNode()) {
            continue;
          }
          if (symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
            superMethods.addAll(getSuperSymbolMethods(superSymbol, type));
          }
          if (type.getAstNode() instanceof ASTCDClass && !type.getAstNode().getModifier().isAbstract() &&
              !symbolTableService.isClassOverwritten(type.getName() + BUILDER_SUFFIX, classList)) {
            superMethods.addAll(getSuperASTMethods(superSymbol, type, superMethods));
          }
        }
      }
    }
    return superMethods;
  }

  protected List<ASTCDMethod> getScopesGenitorDelegatorMethods() {
    String scopesGenitorSimpleName = symbolTableService.getScopesGenitorDelegatorSimpleName();
    String scopesGenitorFullName = symbolTableService.getScopesGenitorDelegatorFullName();
    List<ASTCDMethod> ret = getStaticAndProtectedMethods(StringTransformations.uncapitalize(SCOPES_GENITOR_SUFFIX + DELEGATOR_SUFFIX), scopesGenitorSimpleName, scopesGenitorFullName);
    this.replaceTemplate(JAVADOC, ret.get(0), JavaDoc.of("Returns a new ScopeGenitorDelegator.",
                    "Delegates to the ScopeGenitors of composed languages, used for instantiating symbol tables in the context of language composition",
                    "See the delegators #createFromAST method.")
            .asHP());
    return ret;
  }

  protected List<ASTCDMethod> getScopesGenitorMethods() {
    String scopesGenitorSimpleName = symbolTableService.getScopesGenitorSimpleName();
    String scopesGenitorFullName = symbolTableService.getScopesGenitorFullName();
    List<ASTCDMethod> ret = getStaticAndProtectedMethods(StringTransformations.uncapitalize(SCOPES_GENITOR_SUFFIX), scopesGenitorSimpleName, scopesGenitorFullName);
    this.replaceTemplate(JAVADOC, ret.get(0), JavaDoc.of("Returns a new ScopeGenitor.",
                    "ScopeGenitors are responsible for creating the scope structure of artifacts and linking it with the AST nodes.",
                    "Note: ScopeGenitors do NOT delegate to elements of composed languages",
                    "which is why you are most likely looking for {@link #scopesGenitorDelegator()}.")
            .asHP());
    return ret;
  }

  protected List<ASTCDMethod> getStaticAndProtectedMethods(String methodName, String name, String fullName) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    String staticMethodName = StringTransformations.uncapitalize(methodName);
    String protectedMethodName = "_" + staticMethodName;
    ASTMCType scopesGenitorType = getMCTypeFacade().createQualifiedType(fullName);

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), scopesGenitorType, staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", name, staticMethodName));
    methods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED.build(), scopesGenitorType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", fullName));
    methods.add(protectedMethod);
    return methods;
  }

  protected List<ASTCDMethod> getParserMethods() {
    List<ASTCDMethod> parserMethods = Lists.newArrayList();

    String parserName = parserService.getParserClassSimpleName();
    String staticMethodName = "parser";
    String protectedMethodName = "_" + staticMethodName;
    ASTMCType parserType = getMCTypeFacade().createQualifiedType(parserService.getParserClassFullName());

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), parserType, staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", parserName, staticMethodName));
    this.replaceTemplate(JAVADOC, staticMethod, JavaDoc.of("Returns a new instance of this languages parser.",
                    "Respects grammar composition by means of the Mill pattern.")
            .asHP());
    parserMethods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED.build(), parserType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedParserMethod", parserService.getParserClassFullName()));
    parserMethods.add(protectedMethod);

    return parserMethods;
  }

  protected List<ASTCDMethod> getGlobalScopeMethods(ASTCDAttribute globalScopeAttribute) {
    List<ASTCDMethod> globalScopeMethods = Lists.newArrayList();

    String attributeName = globalScopeAttribute.getName();
    String staticMethodName = "globalScope";
    String protectedMethodName = "_" + staticMethodName;

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), globalScopeAttribute.getMCType(), staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", StringTransformations.capitalize(attributeName), staticMethodName));
    globalScopeMethods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED.build(), globalScopeAttribute.getMCType(), protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedGlobalScopeMethod", attributeName, symbolTableService.getGlobalScopeFullName()));
    globalScopeMethods.add(protectedMethod);

    return globalScopeMethods;
  }

  protected List<ASTCDMethod> getArtifactScopeMethods() {
    String artifactScopeName = symbolTableService.getArtifactScopeSimpleName();
    ASTMCType returnType = symbolTableService.getArtifactScopeInterfaceType();
    ASTMCType scopeType = symbolTableService.getArtifactScopeType();
    return getStaticAndProtectedScopeMethods(StringTransformations.uncapitalize(ARTIFACT_PREFIX + SCOPE_SUFFIX), artifactScopeName, returnType, scopeType);
  }

  protected List<ASTCDMethod> getScopeMethods() {
    String scopeName = symbolTableService.getScopeClassSimpleName();
    ASTMCType returnType = symbolTableService.getScopeInterfaceType();
    ASTMCType scopeType = symbolTableService.getScopeType();
    return getStaticAndProtectedScopeMethods(StringTransformations.uncapitalize(SCOPE_SUFFIX), scopeName, returnType, scopeType);
  }

  protected List<ASTCDMethod> getStaticAndProtectedScopeMethods(String methodName, String scopeName, ASTMCType returnType, ASTMCType scopeType) {
    List<ASTCDMethod> scopeMethods = Lists.newArrayList();
    String staticMethodName = StringTransformations.uncapitalize(methodName);
    String protectedMethodName = "_" + staticMethodName;

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), returnType, staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", scopeName, staticMethodName));
    scopeMethods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED.build(), returnType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", scopeType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter())));
    scopeMethods.add(protectedMethod);

    return scopeMethods;
  }

  protected List<ASTCDMethod> getSuperSymbolMethods(DiagramSymbol superSymbol, CDTypeSymbol type) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    // for prod with symbol property create delegate builder method
    String symbolBuilderFullName = symbolTableService.getSymbolBuilderFullName(type.getAstNode(), superSymbol);
    String millFullName = symbolTableService.getMillFullName(superSymbol);
    String symbolBuilderSimpleName = StringTransformations.uncapitalize(symbolTableService.getSymbolBuilderSimpleName(type.getAstNode()));
    ASTCDMethod builderMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(),
        getMCTypeFacade().createQualifiedType(symbolBuilderFullName), symbolBuilderSimpleName);

    this.replaceTemplate(EMPTY_BODY, builderMethod, new StringHookPoint("return " + millFullName + "." + symbolBuilderSimpleName + "();"));
    superMethods.add(builderMethod);

    // create corresponding builder for symbolSurrogate
    String symbolSurrogateBuilderFullName = symbolTableService.getSymbolSurrogateBuilderFullName(type.getAstNode(), superSymbol);
    String symbolSurrogateBuilderSimpleName = StringTransformations.uncapitalize(symbolTableService.getSymbolSurrogateBuilderSimpleName(type.getAstNode()));
    ASTCDMethod builderLoaderMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(),
        getMCTypeFacade().createQualifiedType(symbolSurrogateBuilderFullName), symbolSurrogateBuilderSimpleName);

    this.replaceTemplate(EMPTY_BODY, builderLoaderMethod, new StringHookPoint("return " + millFullName + "." + symbolSurrogateBuilderSimpleName + "();"));
    superMethods.add(builderLoaderMethod);
    return superMethods;
  }


  protected List<ASTCDMethod> getSuperASTMethods(DiagramSymbol superSymbol, CDTypeSymbol type,
                                                 List<ASTCDMethod> alreadyDefinedMethods) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    String astPackageName = superSymbol.getFullName().toLowerCase() + "." + AST_PACKAGE + ".";
    ASTMCQualifiedType superAstType = this.getMCTypeFacade().createQualifiedType(astPackageName + type.getName() + BUILDER_SUFFIX);
    String methodName = StringTransformations.uncapitalize(type.getName().replaceFirst(AST_PREFIX, "")) + BUILDER_SUFFIX;

    // add builder method
    ASTCDMethod createDelegateMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), superAstType, methodName);
    if (!symbolTableService.isMethodAlreadyDefined(createDelegateMethod, alreadyDefinedMethods)) {
      String millPackageName = superSymbol.getFullName().toLowerCase() + ".";
      this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("mill.BuilderDelegatorMethod", millPackageName + superSymbol.getName(), methodName));
      superMethods.add(createDelegateMethod);
    }
    return superMethods;
  }

  /**
   * Creates the public accessor and protected internal method for a given
   * attribute. The attribute is specified by its simple name, its qualified
   * type, and the qualified return type of the methods. The return type of the
   * method may be equal to the attribute type or a corresponding super type.
   *
   * @param attributeName The name of the attribute
   * @param attributeType The qualified type of the attribute
   * @param methodName    The name of the method
   * @param methodType    The return type of the methods
   * @return The accessor and corresponding internal method for the attribute
   */
  protected List<ASTCDMethod> getInheritanceTraverserMethods(String attributeName, String attributeType, String methodName, String methodType) {
    List<ASTCDMethod> attributeMethods = Lists.newArrayList();

    // method names and return type
    String protectedMethodName = "_" + methodName;
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(methodType);

    // static accessor method
    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), returnType, methodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", StringTransformations.capitalize(attributeName), methodName));
    attributeMethods.add(staticMethod);

    // protected internal method
    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED.build(), returnType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod,
        new TemplateHookPoint("mill.InheritanceHandlerMethod", attributeType,
            visitorService.getAllCDs(), visitorService));

    attributeMethods.add(protectedMethod);

    return attributeMethods;
  }

  /**
   * Creates the public accessor and protected internal method for a given
   * attribute. The attribute is specified by its simple name, its qualified
   * type, and the qualified return type of the methods. The return type of the
   * method may be equal to the attribute type or a corresponding super type.
   *
   * @param attributeName The name of the attribute
   * @param attributeType The qualified type of the attribute
   * @param methodName    The name of the method
   * @param methodType    The return type of the methods
   * @return The accessor and corresponding internal method for the attribute
   */
  protected List<ASTCDMethod> getAttributeMethods(String attributeName, String attributeType, String methodName, String methodType) {
    List<ASTCDMethod> attributeMethods = Lists.newArrayList();

    // method names and return type
    String protectedMethodName = "_" + methodName;
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(methodType);

    // static accessor method
    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), returnType, methodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", StringTransformations.capitalize(attributeName), methodName));
    attributeMethods.add(staticMethod);

    // protected internal method
    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED.build(), returnType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new StringHookPoint("return new " + attributeType + "();"));
    attributeMethods.add(protectedMethod);

    return attributeMethods;
  }

}
