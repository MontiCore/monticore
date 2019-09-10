package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class SymbolDecorator extends AbstractCreator<ASTCDType, ASTCDClass> {

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
  public ASTCDClass decorate(ASTCDType input) {
    String scopeInterface = symbolTableService.getScopeInterfaceFullName();
    String artifactScope = symbolTableService.getArtifactScopeFullName();
    String globalScopeInterface = symbolTableService.getGlobalScopeInterfaceFullName();
    String symbolName = symbolTableService.getNameWithSymbolSuffix(input);

    List<ASTCDAttribute> symbolAttributes = createSymbolAttributes(input.getName(), scopeInterface);
    List<ASTCDMethod> symbolMethods = createSymbolMethods(symbolAttributes);

    ASTCDParameter constructorParam = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), "name");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolName, constructorParam);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this.name = name;"));

    ASTCDClass symbolClass = CD4AnalysisMill.cDClassBuilder()
        .setName(symbolName)
        .setModifier(PUBLIC.build())
        .addInterface(getCDTypeFacade().createQualifiedType(symbolTableService.getCommonSymbolInterfaceFullName()))
        .addCDConstructor(constructor)
        .addAllCDAttributes(symbolAttributes)
        .addAllCDMethods(symbolMethods)
        .addCDMethod(createAcceptMethod())
        .addCDMethod(createDeterminePackageName(scopeInterface, artifactScope))
        .addCDMethod(createDetermineFullName(scopeInterface, artifactScope, globalScopeInterface))
        .build();

    // add only for scope spanning symbols
    if (input.getModifierOpt().isPresent() && symbolTableService.hasScopeStereotype(input.getModifierOpt().get())) {
      ASTCDAttribute spannedScopeAttribute = createSpannedScopeAttribute();
      List<ASTCDMethod> spannedScopeMethods = createSpannedScopeMethods(spannedScopeAttribute);
      symbolClass.addCDAttribute(spannedScopeAttribute);
      symbolClass.addAllCDMethods(spannedScopeMethods);
      symbolClass.addInterface(getCDTypeFacade().createQualifiedType(I_SCOPE_SPANNING_SYMBOL));
    }

    return symbolClass;
  }

  protected List<ASTCDAttribute> createSymbolAttributes(String astClassName, String scopeInterface) {
    ASTCDAttribute name = this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, "name");

    ASTCDAttribute fullName = this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, FULL_NAME);

    ASTCDAttribute enclosingScope = this.getCDAttributeFacade().createAttribute(PROTECTED, scopeInterface, "enclosingScope");

    ASTMCOptionalType optionalTypeOfASTNode = getCDTypeFacade().createOptionalTypeOf(symbolTableService.getASTPackage() + "." + AST_PREFIX + astClassName);
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
        new TemplateHookPoint("_symboltable.symbol.NameSetter", symbolAttribute.getName())));
    symbolMethods.addAll(packageNameMethod);
    accessorDecorator.enableTemplates();
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
    ASTMCQualifiedType symbolVisitorType = getCDTypeFacade().createQualifiedType(visitorService.getSymbolVisitorFullName());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(symbolVisitorType, VISITOR_PREFIX);
    ASTCDMethod acceptMethod = getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, parameter);
    this.replaceTemplate(EMPTY_BODY, acceptMethod, new StringHookPoint("visitor.handle(this);"));
    return acceptMethod;
  }

  protected ASTCDMethod createDeterminePackageName(String scopeInterface, String artifactScope) {
    ASTMCType stringType = getCDTypeFacade().createStringType();

    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, stringType, DETERMINE_PACKAGE_NAME_METHOD);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.symbol.DeterminePackageName",
        scopeInterface, artifactScope));
    return method;
  }

  protected ASTCDMethod createDetermineFullName(String scopeInterface, String artifactScope, String globalScope) {
    ASTMCType stringType = getCDTypeFacade().createStringType();

    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, stringType, DETERMINE_FULL_NAME_METHOD);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.symbol.DetermineFullName",
        scopeInterface, artifactScope, globalScope));
    return method;
  }
}
