/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;

/**
 * creates a Symbol class from a grammar
 */
public class SymbolDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  protected final VisitorService visitorService;

  /**
   * flag added to define if the ArtifactScope class was overwritten with the TOP mechanism
   * if top mechanism was used, must use setter to set flag true, before the decoration
   * is needed for different accept method implementations
   */
  protected boolean isSymbolTop = false;

  protected static final String TEMPLATE_PATH = "_symboltable.symbol.";

  public SymbolDecorator(final GlobalExtensionManagement glex,
                         final SymbolTableService symbolTableService,
                         final VisitorService visitorService,
                         final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
    this.accessorDecorator = methodDecorator.getAccessorDecorator();
    this.mutatorDecorator = methodDecorator.getMutatorDecorator();
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass symbolInput) {
    String scopeInterface = symbolTableService.getScopeInterfaceFullName();
    String symbolName = symbolTableService.getNameWithSymbolSuffix(symbolInput);
    boolean hasInheritedSymbol = symbolTableService.hasInheritedSymbolStereotype(symbolInput.getModifier());
    ASTModifier modifier = symbolInput.isPresentModifier() ?
            symbolTableService.createModifierPublicModifier(symbolInput.getModifier()) :
            PUBLIC.build();

    // uses symbol rule methods and attributes
    List<ASTCDAttribute> symbolRuleAttributes = symbolInput.deepClone().getCDAttributeList()
            .stream().filter(attr -> !symbolTableService.isInheritedAttribute(attr)).collect(Collectors.toList());
    symbolRuleAttributes.forEach(a -> getDecorationHelper().addAttributeDefaultValues(a, this.glex));
    List<ASTCDMethod> symbolRuleAttributeMethods = symbolRuleAttributes
            .stream()
            .map(methodDecorator::decorate)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    List<ASTCDMethod> symbolRuleMethods = symbolInput.deepClone().getCDMethodList();

    List<ASTCDAttribute> symbolAttributes = Lists.newArrayList();
    List<ASTCDMethod> symbolMethods = Lists.newArrayList();
    List<ASTCDAttribute> symbolNameAttributes = Lists.newArrayList();
    List<ASTCDMethod> symbolNameMethods = Lists.newArrayList();
    if (hasInheritedSymbol) {
      symbolMethods.addAll(createOverridingSymbolMethods(symbolInput.getName(), scopeInterface));
    } else {
      symbolAttributes = createSymbolAttributes(symbolInput.getName(), scopeInterface);
      symbolNameAttributes = createSymbolNameAttributes();
      symbolMethods = symbolAttributes
              .stream()
              .map(methodDecorator::decorate)
              .flatMap(List::stream)
              .collect(Collectors.toList());
      symbolNameMethods = symbolNameAttributes
              .stream()
              .map(this::createNameMethods)
              .flatMap(List::stream)
              .collect(Collectors.toList());
    }

    ASTCDParameter constructorParam = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), NAME_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolName, constructorParam);
    if (hasInheritedSymbol) {
      this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("super(" + NAME_VAR + ");"));
    } else {
      this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + NAME_VAR + " = " + NAME_VAR + ";"));
    }

    ASTCDClass symbolClass = CD4AnalysisMill.cDClassBuilder()
            .setName(symbolName)
            .setModifier(modifier)
            .addInterface(getMCTypeFacade().createQualifiedType(symbolTableService.getCommonSymbolInterfaceFullName()))
            .addAllInterfaces(symbolInput.getInterfaceList())
            .addCDConstructor(constructor)
            .addAllCDAttributes(symbolAttributes)
            .addAllCDAttributes(symbolNameAttributes)
            .addAllCDAttributes(symbolRuleAttributes)
            .addAllCDMethods(symbolRuleAttributeMethods)
            .addAllCDMethods(symbolRuleMethods)
            .addAllCDMethods(symbolMethods)
            .addAllCDMethods(symbolNameMethods)
            .addCDMethod(createAcceptMethod(symbolName))
            .addCDMethod(createDeterminePackageName(scopeInterface))
            .addCDMethod(createDetermineFullName(scopeInterface))
            .build();

    // add only for scope spanning symbols
    if (symbolInput.isPresentModifier() &&
            (symbolTableService.hasScopeStereotype(symbolInput.getModifier()) || symbolTableService.hasInheritedScopeStereotype(symbolInput.getModifier()))) {
      ASTCDAttribute spannedScopeAttribute = createSpannedScopeAttribute();
      if (!symbolTableService.hasInheritedScopeStereotype(symbolInput.getModifier())) {
        symbolClass.addCDAttribute(spannedScopeAttribute);
      }
      symbolClass.addAllCDMethods(createSpannedScopeMethods(scopeInterface));
      symbolClass.addInterface(getMCTypeFacade().createQualifiedType(I_SCOPE_SPANNING_SYMBOL));
    }
    if (hasInheritedSymbol) {
      Map<ASTCDClass, String> values = symbolTableService.getInheritedSymbolPropertyClasses(Lists.newArrayList(symbolInput));
      String value = values.getOrDefault(symbolInput, "");
      if (!value.isEmpty()) {
        symbolClass.setSuperclass(getMCTypeFacade().createQualifiedType(value));
      }
    } else if (symbolInput.isPresentSuperclass()) {
      symbolClass.setSuperclass(symbolInput.getSuperclass());
    }
    return symbolClass;
  }

  protected List<ASTCDMethod> createOverridingSymbolMethods(String astClassName, String scopeInterface) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    // getEnclosingScope
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(scopeInterface), "getEnclosingScope");
    String errorCode = symbolTableService.getGeneratedErrorCode(scopeInterface+"getScope");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "GetScope",
            ENCLOSING_SCOPE_VAR, scopeInterface, errorCode));
    methods.add(method);

    // setEnclosingScope
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), "scope");
    method = getCDMethodFacade().createMethod(PUBLIC, "setEnclosingScope", parameter);
    this.replaceTemplate(EMPTY_BODY,method, new StringHookPoint("enclosingScope = scope;"));
    methods.add(method);

    // getASTNode
    ASTMCQualifiedType retType = getMCTypeFacade().createQualifiedType(symbolTableService.getASTPackage() + "." + AST_PREFIX + astClassName);
    method = getCDMethodFacade().createMethod(PUBLIC, retType, "getASTNode");
    this.replaceTemplate(EMPTY_BODY,method, new StringHookPoint("return ("
            + retType.printType(new MCFullGenericTypesPrettyPrinter(new IndentPrinter()))
            + ") super.getASTNode();"));
    methods.add(method);
    return methods;
  }

  protected List<ASTCDAttribute> createSymbolAttributes(String astClassName, String scopeInterface) {
    ASTCDAttribute name = this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, NAME_VAR);

    ASTCDAttribute enclosingScope = this.getCDAttributeFacade().createAttribute(PROTECTED, scopeInterface, ENCLOSING_SCOPE_VAR);

    ASTMCOptionalType optionalTypeOfASTNode = getMCTypeFacade().createOptionalTypeOf(symbolTableService.getASTPackage() + "." + AST_PREFIX + astClassName);
    ASTCDAttribute node = this.getCDAttributeFacade().createAttribute(PROTECTED, optionalTypeOfASTNode, AST_NODE_VAR);
    getDecorationHelper().addAttributeDefaultValues(node, glex);

    ASTCDAttribute accessModifier = this.getCDAttributeFacade().createAttribute(PROTECTED, ACCESS_MODIFIER, "accessModifier");
    this.replaceTemplate(VALUE, accessModifier, new StringHookPoint("= " + ACCESS_MODIFIER_ALL_INCLUSION));

    return new ArrayList<>(Arrays.asList(name, enclosingScope, node, accessModifier));
  }

  protected List<ASTCDAttribute> createSymbolNameAttributes() {
    // own method because these attributes will have different methods
    ASTCDAttribute fullName = this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, FULL_NAME_VAR);
    ASTCDAttribute packageName = this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, PACKAGE_NAME_VAR);
    return new ArrayList<>(Arrays.asList(fullName, packageName));
  }

  protected List<ASTCDMethod> createNameMethods(ASTCDAttribute symbolAttribute) {
    // create getter and setter separately
    // setter is not changed
    List<ASTCDMethod> symbolMethods = new ArrayList<>(mutatorDecorator.decorate(symbolAttribute));

    accessorDecorator.disableTemplates();
    // template for getter mus be changed
    List<ASTCDMethod> packageNameMethod = accessorDecorator.decorate(symbolAttribute);
    packageNameMethod.forEach(m -> this.replaceTemplate(EMPTY_BODY, m,
            new TemplateHookPoint(TEMPLATE_PATH + "NameSetter", symbolAttribute.getName())));
    symbolMethods.addAll(packageNameMethod);
    accessorDecorator.enableTemplates();
    return symbolMethods;
  }

  protected ASTCDAttribute createSpannedScopeAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, symbolTableService.getScopeInterfaceType(), String.format(SPANNED_SCOPE_VAR, ""));
  }

  protected List<ASTCDMethod> createSpannedScopeMethods(String scopeInterface) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    // getSpannedScope
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(scopeInterface), "getSpannedScope");
    String errorCode = symbolTableService.getGeneratedErrorCode(scopeInterface+"getSpannedScope");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "GetScope",
            SPANNED_SCOPE_VAR, scopeInterface, errorCode));
    methods.add(method);

    // setSpannedScope
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), "scope");
    method = getCDMethodFacade().createMethod(PUBLIC, "setSpannedScope", parameter);
    this.replaceTemplate(EMPTY_BODY,method, new TemplateHookPoint(TEMPLATE_PATH + "SetSpannedScope"));
    methods.add(method);

    return methods;

  }

  protected ASTCDMethod createAcceptMethod(String symbolName) {
    ASTMCQualifiedType visitorType = getMCTypeFacade().createQualifiedType(visitorService.getVisitorFullName());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(visitorType, VISITOR_PREFIX);
    ASTCDMethod acceptMethod = getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, parameter);
    if (!isSymbolTop()) {
      this.replaceTemplate(EMPTY_BODY, acceptMethod, new StringHookPoint("visitor.handle(this);"));
    } else {
      String errorCode = symbolTableService.getGeneratedErrorCode(symbolName + ACCEPT_METHOD);
      this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint(
              "_symboltable.AcceptTop", symbolName, errorCode));
    }
    return acceptMethod;
  }

  protected ASTCDMethod createDeterminePackageName(String scopeInterface) {
    ASTMCType stringType = getMCTypeFacade().createStringType();

    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, stringType, "determinePackageName");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "DeterminePackageName",
            scopeInterface));
    return method;
  }

  protected ASTCDMethod createDetermineFullName(String scopeInterface) {
    ASTMCType stringType = getMCTypeFacade().createStringType();

    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, stringType, "determineFullName");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "DetermineFullName",
            scopeInterface));
    return method;
  }

  public boolean isSymbolTop() {
    return isSymbolTop;
  }

  public void setSymbolTop(boolean symbolTop) {
    isSymbolTop = symbolTop;
  }
}
