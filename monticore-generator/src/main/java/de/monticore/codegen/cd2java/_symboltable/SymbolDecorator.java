package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class SymbolDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  protected final VisitorService visitorService;

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
  public ASTCDClass decorate(ASTCDClass input) {
    String scopeInterface = symbolTableService.getScopeInterfaceTypeName();
    String artifactScope = symbolTableService.getArtifactScopeTypeName();
    String globalScopeInterface = symbolTableService.getGlobalScopeInterfaceTypeName();
    String symbolName = symbolTableService.getSymbolName(input);

    List<ASTCDAttribute> symbolAttributes = createSymbolAttributes(input, scopeInterface);
    List<ASTCDMethod> symbolMethods = createSymbolMethods(symbolAttributes);


    ASTCDParameter constructorParam = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), "name");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolName, constructorParam);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this.name = name;"));


    ASTCDClass symbolClass = CD4AnalysisMill.cDClassBuilder()
        .setName(symbolName)
        .setModifier(PUBLIC.build())
        .addInterface(getCDTypeFacade().createQualifiedType(symbolTableService.getCommonSymbolInterfaceName()))
        .addCDConstructor(constructor)
        .addAllCDAttributes(symbolAttributes)
        .addAllCDMethods(symbolMethods)
        .addCDMethod(createAcceptMethod())
        .addCDMethod(createDeterminePackageName(scopeInterface, artifactScope))
        .addCDMethod(createDetermineFullName(scopeInterface, artifactScope, globalScopeInterface))
        .build();

    // add only for scope spanning symbols
    if (input.isPresentModifier() && symbolTableService.hasScopeStereotype(input.getModifier())) {
      ASTCDAttribute spannedScopeAttribute = createSpannedScopeAttribute();
      List<ASTCDMethod> spannedScopeMethods = createSpannedScopeMethods(spannedScopeAttribute);
      symbolClass.addCDAttribute(spannedScopeAttribute);
      symbolClass.addAllCDMethods(spannedScopeMethods);
      symbolClass.addInterface(getCDTypeFacade().createQualifiedType(I_SCOPE_SPANNING_SYMBOL));
    }

    return symbolClass;
  }

  protected List<ASTCDAttribute> createSymbolAttributes(ASTCDClass astcdClass, String scopeInterface) {
    ASTCDAttribute name = this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, "name");

    ASTCDAttribute fullName = this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, FULL_NAME);

    ASTCDAttribute enclosingScope = this.getCDAttributeFacade().createAttribute(PROTECTED, scopeInterface, "enclosingScope");

    ASTMCOptionalType optionalTypeOfASTNode = getCDTypeFacade().createOptionalTypeOf(symbolTableService.getASTPackage() + "." + astcdClass.getName());
    ASTCDAttribute node = this.getCDAttributeFacade().createAttribute(PROTECTED, optionalTypeOfASTNode, AST_NODE_VARIABLE);

    ASTCDAttribute packageName = this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, PACKAGE_NAME);

    ASTCDAttribute accessModifier = this.getCDAttributeFacade().createAttribute(PROTECTED, ACCESS_MODIFIER, "accessModifier");
    this.replaceTemplate(VALUE, accessModifier, new StringHookPoint("= de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION"));

    return new ArrayList<>(Arrays.asList(name, fullName, enclosingScope, node, packageName, accessModifier));
  }

  protected List<ASTCDMethod> createSymbolMethods(List<ASTCDAttribute> symbolAttributes) {

    List<ASTCDMethod> symbolMethods = new ArrayList<>();
    for (ASTCDAttribute symbolAttribute : symbolAttributes) {
      if (symbolAttribute.getName().equals(PACKAGE_NAME)) {
        symbolMethods.addAll(createNameMethods(symbolAttribute));
      } else if (symbolAttribute.getName().equals(FULL_NAME)) {
        symbolMethods.addAll(createNameMethods(symbolAttribute));
      } else if (symbolAttribute.getName().equals(AST_NODE_VARIABLE)) {
        // todo: remove this if statement when runtime is changed
        symbolMethods.addAll(createAstNodeMethods(symbolAttribute));
      } else {
        symbolMethods.addAll(methodDecorator.decorate(symbolAttribute));
      }
    }
    return symbolMethods;
  }

  protected List<ASTCDMethod> createNameMethods(ASTCDAttribute symbolAttribute) {
    // create getter and setter separately
    // setter is not changed
    List<ASTCDMethod> symbolMethods = new ArrayList<>(mutatorDecorator.decorate(symbolAttribute));

    accessorDecorator.disableTemplates();

    // template for getter mus be changed
    List<ASTCDMethod> packageNameMethod = accessorDecorator.decorate(symbolAttribute);
    packageNameMethod.forEach(m -> this.replaceTemplate(EMPTY_BODY, m,
        new TemplateHookPoint("_symboltable.symbol.NameSetter", PACKAGE_NAME)));
    symbolMethods.addAll(packageNameMethod);

    accessorDecorator.enableTemplates();
    return symbolMethods;
  }


  /**
   * @deprecated remove when runtime method 'public Optional<? extends ASTNode> getAstNode();' is changed
   * to ' public ASTNode getAstNode();'
   * because interface ISymbol from runtime must be implemented correctly for now, the get method return type must be changed manually
   */
  @Deprecated
  protected List<ASTCDMethod> createAstNodeMethods(ASTCDAttribute symbolAttribute) {
    // create getter and setter separately
    // setter is not changed
    List<ASTCDMethod> symbolMethods = new ArrayList<>(mutatorDecorator.decorate(symbolAttribute));

    // template for getter mus be changed
    List<ASTCDMethod> astNodeMethods = accessorDecorator.decorate(symbolAttribute);
    // remove getAstNode because of wrong return type
    symbolMethods.addAll(astNodeMethods
        .stream()
        .filter(m -> !m.getName().equals("getAstNode"))
        .collect(Collectors.toList()));

    // create getAstNode with Optional return Type
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(symbolAttribute.getMCType()).build();
    ASTCDMethod getAstNodeMethod = getCDMethodFacade().createMethod(PUBLIC, returnType, "getAstNode");
    this.replaceTemplate(EMPTY_BODY, getAstNodeMethod, new StringHookPoint("return this.astNode;"));
    symbolMethods.add(getAstNodeMethod);

    return symbolMethods;
  }

  protected ASTCDAttribute createSpannedScopeAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, symbolTableService.getScopeInterfaceType(), String.format(SPANNED_SCOPE, ""));
  }

  protected List<ASTCDMethod> createSpannedScopeMethods(ASTCDAttribute spannedScopeAttribute) {
    List<ASTCDMethod> methodList = new ArrayList<>(accessorDecorator.decorate(spannedScopeAttribute));
    mutatorDecorator.disableTemplates();
    List<ASTCDMethod> setter = mutatorDecorator.decorate(spannedScopeAttribute);
    setter.forEach(m -> this.replaceTemplate(EMPTY_BODY, m, new StringHookPoint("this.spannedScope = spannedScope;\n" +
        "    getSpannedScope().setSpanningSymbol(this);")));
    methodList.addAll(setter);
    mutatorDecorator.enableTemplates();
    return methodList;
  }

  protected ASTCDMethod createAcceptMethod() {
    ASTMCQualifiedType symbolVisitorType = getCDTypeFacade().createQualifiedType(visitorService.getSymbolVisitorFullTypeName());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(symbolVisitorType, VISITOR_PREFIX);
    ASTCDMethod acceptMethod = getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, parameter);
    this.replaceTemplate(EMPTY_BODY, acceptMethod, new StringHookPoint("visitor.handle(this);"));
    return acceptMethod;
  }

  protected ASTCDMethod createDeterminePackageName(String scopeInterface, String artifactScope) {
    ASTMCType stringType = getCDTypeFacade().createStringType();
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(stringType).build();

    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, returnType, DETERMINE_PACKAGE_NAME_METHOD);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.symbol.DeterminePackageName",
        scopeInterface, artifactScope));
    return method;
  }

  protected ASTCDMethod createDetermineFullName(String scopeInterface, String artifactScope, String globalScope) {
    ASTMCType stringType = getCDTypeFacade().createStringType();
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(stringType).build();

    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, returnType, DETERMINE_FULL_NAME_METHOD);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.symbol.DetermineFullName",
        scopeInterface, artifactScope, globalScope));
    return method;
  }
}
