/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typedispatcher;

import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;


public class TypeDispatcherDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  public TypeDispatcherDecorator(final GlobalExtensionManagement glex,
                                 final VisitorService visitorService,
                                 final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(input, decoratedCD, "_util");
    visitorPackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {

    String handlerName = getTypeDispatcherName(visitorService.getCDName());

    List<ASTCDAttribute> optASTAttributes = optASTAttributes();
    List<ASTCDAttribute> boolASTAttributes = boolASTAttributes();
    List<ASTCDAttribute> optScopeAttributes = optScopeAttributes();
    List<ASTCDAttribute> boolScopeAttributes = boolScopeAttributes();
    List<ASTCDAttribute> optSymbolAttributes = optSymbolAttributes();
    List<ASTCDAttribute> boolSymbolAttributes = boolSymbolAttributes();

    List<ASTCDAttribute> superDispatchers = getDispatcherAttributes();

    List<ASTCDMethod> methods = new ArrayList<>();

    methods.add(createResetMethod(superDispatchers,
        optASTAttributes,
        boolASTAttributes,
        optScopeAttributes,
        boolScopeAttributes,
        optSymbolAttributes,
        boolSymbolAttributes));

    methods.addAll(isMethodsForAttributes(boolASTAttributes, "de.monticore.ast.ASTNode", "node"));
    methods.addAll(isMethodsForAttributes(boolScopeAttributes, "de.monticore.symboltable.IScope", "scope"));
    methods.addAll(isMethodsForAttributes(boolSymbolAttributes, "de.monticore.symboltable.ISymbol", "symbol"));

    methods.addAll(asMethodsForAttributes(optASTAttributes, "de.monticore.ast.ASTNode", "node"));
    methods.addAll(asMethodsForAttributes(optScopeAttributes, "de.monticore.symboltable.IScope", "scope"));
    methods.addAll(asMethodsForAttributes(optSymbolAttributes, "de.monticore.symboltable.ISymbol", "symbol"));

    methods.addAll(isMethodsForSuperLanguages(methods));
    methods.addAll(asMethodsForSuperLanguages(methods));

    methods.addAll(createHandleMethods());

    ASTCDClass dispatcher = CD4CodeMill.cDClassBuilder()
        .setName(handlerName)
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsage())
        .addAllCDMembers(superDispatchers)
        .addAllCDMembers(optASTAttributes)
        .addAllCDMembers(boolASTAttributes)
        .addAllCDMembers(optScopeAttributes)
        .addAllCDMembers(boolScopeAttributes)
        .addAllCDMembers(optSymbolAttributes)
        .addAllCDMembers(boolSymbolAttributes)
        .addCDMember(createConstructor(handlerName, superDispatchers))
        .addAllCDMembers(addTraverserElements())
        .addAllCDMembers(methods)
        .build();

    addGettersAndSetters(boolASTAttributes, dispatcher);
    addGettersAndSetters(boolScopeAttributes, dispatcher);
    addGettersAndSetters(boolSymbolAttributes, dispatcher);
    addGettersAndSetters(optASTAttributes, dispatcher);
    addGettersAndSetters(optScopeAttributes, dispatcher);
    addGettersAndSetters(optSymbolAttributes, dispatcher);

    return dispatcher;
  }

  protected List<ASTCDAttribute> getDispatcherAttributes() {
    List<ASTCDAttribute> dispatchers = new ArrayList<>();

    for (DiagramSymbol type : visitorService.getSuperCDsTransitive()) {
      String pkg = type.getFullName().toLowerCase() + "._util";
      String superName = type.getName() + "TypeDispatcher";

      dispatchers.add(CD4CodeMill.cDAttributeBuilder()
          .setModifier(PROTECTED.build())
          .setMCType(MCTypeFacade.getInstance().createQualifiedType(pkg + "." + superName))
          .setName(uncapFirst(superName))
          .build());
    }

    return dispatchers;
  }

  protected List<ASTCDAttribute> optASTAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    Set<String> attributeNames = new HashSet<>();
    for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      ASTCDAttribute optional = createOptional(visitorService.createASTFullName(typeSymbol), typeSymbol.getName());
      if (!attributeNames.contains(optional.getName())) {
        attributes.add(optional);
        attributeNames.add(optional.getName());
      }
    }
    return attributes;
  }

  protected List<ASTCDAttribute> boolASTAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    Set<String> attributeNames = new HashSet<>();
    for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      ASTCDAttribute aBoolean = createBoolean(typeSymbol.getName());
      if (!attributeNames.contains(aBoolean.getName())) {
        attributes.add(aBoolean);
        attributeNames.add(aBoolean.getName());
      }
    }
    return attributes;
  }

  protected List<ASTCDAttribute> optScopeAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();

    attributes.add(createOptional(symbolTableService.getArtifactScopeInterfaceFullName(),
        symbolTableService.getArtifactScopeInterfaceSimpleName()));
    attributes.add(createOptional(symbolTableService.getGlobalScopeInterfaceFullName(),
        symbolTableService.getGlobalScopeInterfaceSimpleName()));
    attributes.add(createOptional(symbolTableService.getScopeInterfaceFullName(),
        symbolTableService.getScopeInterfaceSimpleName()));

    attributes.add(createOptional(symbolTableService.getScopeClassFullName(),
        symbolTableService.getScopeClassSimpleName()));
    attributes.add(createOptional(symbolTableService.getArtifactScopeFullName(),
        symbolTableService.getArtifactScopeSimpleName()));
    attributes.add(createOptional(symbolTableService.getGlobalScopeFullName(),
        symbolTableService.getGlobalScopeSimpleName()));

    return attributes;
  }

  protected List<ASTCDAttribute> boolScopeAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();

    attributes.add(createBoolean(symbolTableService.getArtifactScopeInterfaceSimpleName()));
    attributes.add(createBoolean(symbolTableService.getGlobalScopeInterfaceSimpleName()));
    attributes.add(createBoolean(symbolTableService.getScopeInterfaceSimpleName()));

    attributes.add(createBoolean(symbolTableService.getScopeClassSimpleName()));
    attributes.add(createBoolean(symbolTableService.getArtifactScopeSimpleName()));
    attributes.add(createBoolean(symbolTableService.getGlobalScopeSimpleName()));

    return attributes;
  }

  protected List<ASTCDAttribute> optSymbolAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (String symbol : symbolTableService.retrieveSymbolNamesFromCD(symbolTableService.getCDSymbol())) {
      attributes.add(createOptional(symbol, symbolTableService.getSimpleNameFromSymbolName(symbol)));
    }
    return attributes;
  }

  protected List<ASTCDAttribute> boolSymbolAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (String symbol : symbolTableService.retrieveSymbolNamesFromCD(symbolTableService.getCDSymbol())) {
      attributes.add(createBoolean(symbolTableService.getSimpleNameFromSymbolName(symbol)));
    }
    return attributes;
  }

  protected ASTCDAttribute createOptional(String type, String name) {
    return CD4CodeMill.cDAttributeBuilder()
        .setModifier(PROTECTED.build())
        .setMCType(MCTypeFacade
            .getInstance()
            .createOptionalTypeOf(type))
        .setName("opt" + name)
        .build();
  }

  protected ASTCDAttribute createBoolean(String type) {
    return CD4CodeMill.cDAttributeBuilder()
        .setModifier(PROTECTED.build())
        .setMCType(MCTypeFacade.getInstance().createBooleanType())
        .setName("is" + type)
        .build();
  }

  protected ASTCDMethod createResetMethod(List<ASTCDAttribute> superDispatchers,
                                          List<ASTCDAttribute> optASTAttributes,
                                          List<ASTCDAttribute> boolASTAttributes,
                                          List<ASTCDAttribute> optScopeAttributes,
                                          List<ASTCDAttribute> boolScopeAttributes,
                                          List<ASTCDAttribute> optSymbolAttributes,
                                          List<ASTCDAttribute> boolSymbolAttributes) {

    ASTCDMethod resetMethod = CD4CodeMill.cDMethodBuilder()
        .setModifier(PUBLIC.build())
        .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
            .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
            .build())
        .setName("reset")
        .build();

    List<ASTCDAttribute> optionals = new ArrayList<>();
    List<ASTCDAttribute> booleans = new ArrayList<>();

    optionals.addAll(optASTAttributes);
    optionals.addAll(optScopeAttributes);
    optionals.addAll(optSymbolAttributes);

    booleans.addAll(boolASTAttributes);
    booleans.addAll(boolScopeAttributes);
    booleans.addAll(boolSymbolAttributes);

    replaceTemplate(EMPTY_BODY, resetMethod,
        new TemplateHookPoint("dispatcher.Reset",
            booleans, optionals, superDispatchers));

    return resetMethod;
  }


  protected List<ASTCDMethod> isMethodsForAttributes(List<ASTCDAttribute> attributes, String parameterType, String parameterName) {
    List<ASTCDMethod> methods = new ArrayList<>();
    List<String> names = attributes.stream().map(ASTCDAttribute::getName).collect(Collectors.toList());

    for (String name : names) {
      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
          .setModifier(PUBLIC.build())
          .setName(name)
          .addCDParameter(CD4CodeMill.cDParameterBuilder()
              .setMCType(MCTypeFacade.getInstance().createQualifiedType(parameterType))
              .setName(parameterName)
              .build())
          .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
              .setMCType(MCTypeFacade.getInstance().createBooleanType())
              .build())
          .build();

      replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("dispatcher.IsAST",
          name,
          parameterName,
          "this"));

      methods.add(method);
    }
    return methods;
  }

  protected List<ASTCDMethod> isMethodsForSuperLanguages(List<ASTCDMethod> presentMethods) {
    List<ASTCDMethod> methods = new ArrayList<>();
    List<String> methodNames = presentMethods.stream().map(ASTCDMethod::getName).collect(Collectors.toList());

    for (DiagramSymbol superLanguage : visitorService.getSuperCDsTransitive()) {
      for (TypeSymbol typeSymbol : visitorService.getAllCDTypes(superLanguage)) {
        String name = "is" + typeSymbol.getName();
        if (!methodNames.contains(name)) {
          methodNames.add(name);
          methods = isMethodsForSuperLanguages(methods, name, "de.monticore.ast.ASTNode", "node", superLanguage);
        }
      }

      for (String symbol : symbolTableService.retrieveSymbolNamesFromCD(symbolTableService.getCDSymbol())) {
        String name = "is" + symbolTableService.getSimpleNameFromSymbolName(symbol);
        if (!methodNames.contains(name)) {
          methods = isMethodsForSuperLanguages(methods, name, "de.monticore.symboltable.ISymbol", "symbol", superLanguage);
          methodNames.add(name);
        }
      }
      if (!methodNames.contains("is" + symbolTableService.getArtifactScopeInterfaceSimpleName())) {
        methods = isMethodsForSuperLanguages(methods, "is" + symbolTableService.getArtifactScopeInterfaceSimpleName(),
            "de.monticore.symboltable.IScope", "scope", superLanguage);
        methodNames.add("is" + symbolTableService.getArtifactScopeInterfaceSimpleName());
      }
      if (!methodNames.contains("is" + symbolTableService.getGlobalScopeInterfaceSimpleName())) {
        methods = isMethodsForSuperLanguages(methods, "is" + symbolTableService.getGlobalScopeInterfaceSimpleName(),
            "de.monticore.symboltable.IScope", "scope", superLanguage);
        methodNames.add("is" + symbolTableService.getGlobalScopeInterfaceSimpleName());
      }
      if (!methodNames.contains("is" + symbolTableService.getScopeInterfaceSimpleName())) {
        methods = isMethodsForSuperLanguages(methods, "is" + symbolTableService.getScopeInterfaceSimpleName(),
            "de.monticore.symboltable.IScope", "scope", superLanguage);
        methodNames.add("is" + symbolTableService.getScopeInterfaceSimpleName());
      }
      if (!methodNames.contains("is" + symbolTableService.getScopeClassSimpleName())) {
        methods = isMethodsForSuperLanguages(methods, "is" + symbolTableService.getScopeClassSimpleName(),
            "de.monticore.symboltable.IScope", "scope", superLanguage);
        methodNames.add("is" + symbolTableService.getScopeClassSimpleName());
      }
      if (!methodNames.contains("is" + symbolTableService.getArtifactScopeSimpleName())) {
        methods = isMethodsForSuperLanguages(methods, "is" + symbolTableService.getArtifactScopeSimpleName(),
            "de.monticore.symboltable.IScope", "scope", superLanguage);
        methodNames.add("is" + symbolTableService.getArtifactScopeSimpleName());
      }
      if (!methodNames.contains("is" + symbolTableService.getGlobalScopeSimpleName())) {
        methods = isMethodsForSuperLanguages(methods, "is" + symbolTableService.getGlobalScopeSimpleName(),
            "de.monticore.symboltable.IScope", "scope", superLanguage);
        methodNames.add("is" + symbolTableService.getGlobalScopeSimpleName());
      }
    }

    return methods;
  }

  protected List<ASTCDMethod> isMethodsForSuperLanguages(List<ASTCDMethod> methods, String name,
                                                         String parameterType, String parameterName,
                                                         DiagramSymbol superLanguage) {
    ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
        .setModifier(PUBLIC.build())
        .setName(name)
        .addCDParameter(CD4CodeMill.cDParameterBuilder()
            .setMCType(MCTypeFacade.getInstance().createQualifiedType(parameterType))
            .setName(parameterName)
            .build())
        .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
            .setMCType(MCTypeFacade.getInstance().createBooleanType())
            .build())
        .build();

    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("dispatcher.IsAST",
        name,
        parameterName,
        getTypeDispatcherName(superLanguage.getName())));

    methods.add(method);

    return methods;
  }

  protected List<ASTCDMethod> asMethodsForAttributes(List<ASTCDAttribute> attributes, String parameterType, String parameterName) {
    List<ASTCDMethod> methods = new ArrayList<>();
    for (ASTCDAttribute attribute : attributes) {
      String name = attribute.getName();
      name = name.substring(name.indexOf("opt") + 3);
      String type = attribute.getMCType() instanceof ASTMCGenericType?
              CD4CodeMill.prettyPrint(((ASTMCGenericType) attribute.getMCType()).getMCTypeArgument(0), false):
              CD4CodeMill.prettyPrint(attribute.getMCType(), false);

      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
          .setModifier(PUBLIC.build())
          .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
              .setMCType(MCTypeFacade.getInstance()
                  .createQualifiedType(type))
              .build())
          .setName("as" + name)
          .addCDParameter(CD4CodeMill.cDParameterBuilder()
              .setMCType(MCTypeFacade.getInstance().createQualifiedType(parameterType))
              .setName(parameterName)
              .build())
          .build();

      replaceTemplate(EMPTY_BODY, method,
          new TemplateHookPoint("dispatcher.AsAST", name, parameterName, "this"));

      methods.add(method);
    }
    return methods;
  }

  protected List<ASTCDMethod> asMethodsForSuperLanguages(List<ASTCDMethod> presentMethods) {
    List<ASTCDMethod> methods = new ArrayList<>();
    List<String> methodNames = presentMethods.stream().map(ASTCDMethod::getName).collect(Collectors.toList());

    for (DiagramSymbol superLanguage : visitorService.getSuperCDsTransitive()) {
      for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(superLanguage)) {
        String name = typeSymbol.getName();
        if (!methodNames.contains("as" + name)) {
          methodNames.add("as" + name);
          methods = asMethodsForSuperLanguages(methods, name, visitorService.createASTFullName(typeSymbol),
              "de.monticore.ast.ASTNode", "node", superLanguage);
        }
      }

      for (String symbol : symbolTableService.retrieveSymbolNamesFromCD(symbolTableService.getCDSymbol())) {
        String name = symbolTableService.getSimpleNameFromSymbolName(symbol);
        if (!methodNames.contains("as" + name)) {
          methods = asMethodsForSuperLanguages(methods, name, symbol,
              "de.monticore.symboltable.ISymbol", "symbol", superLanguage);
          methodNames.add("as" + name);
        }
      }
      if (!methodNames.contains("as" + symbolTableService.getArtifactScopeInterfaceSimpleName())) {
        methods = asMethodsForSuperLanguages(methods, symbolTableService.getArtifactScopeInterfaceSimpleName(),
            symbolTableService.getArtifactScopeInterfaceFullName(), "de.monticore.symboltable.IScope",
            "scope", superLanguage);
        methodNames.add("as" + symbolTableService.getArtifactScopeInterfaceSimpleName());
      }
      if (!methodNames.contains("as" + symbolTableService.getGlobalScopeInterfaceSimpleName())) {
        methods = asMethodsForSuperLanguages(methods, symbolTableService.getGlobalScopeInterfaceSimpleName(),
            symbolTableService.getGlobalScopeInterfaceFullName(), "de.monticore.symboltable.IScope",
            "scope", superLanguage);
        methodNames.add("as" + symbolTableService.getGlobalScopeInterfaceSimpleName());
      }
      if (!methodNames.contains("as" + symbolTableService.getScopeInterfaceSimpleName())) {
        methods = asMethodsForSuperLanguages(methods, symbolTableService.getScopeInterfaceSimpleName(),
            symbolTableService.getScopeInterfaceFullName(), "de.monticore.symboltable.IScope",
            "scope", superLanguage);
        methodNames.add("as" + symbolTableService.getScopeInterfaceSimpleName());
      }
      if (!methodNames.contains("as" + symbolTableService.getScopeClassSimpleName())) {
        methods = asMethodsForSuperLanguages(methods, symbolTableService.getScopeClassSimpleName(),
            symbolTableService.getScopeClassFullName(), "de.monticore.symboltable.IScope",
            "scope", superLanguage);
        methodNames.add("as" + symbolTableService.getScopeClassSimpleName());
      }
      if (!methodNames.contains("as" + symbolTableService.getArtifactScopeSimpleName())) {
        methods = asMethodsForSuperLanguages(methods, symbolTableService.getArtifactScopeSimpleName(),
            symbolTableService.getArtifactScopeFullName(), "de.monticore.symboltable.IScope",
            "scope", superLanguage);
        methodNames.add("as" + symbolTableService.getArtifactScopeSimpleName());
      }
      if (!methodNames.contains("as" + symbolTableService.getGlobalScopeSimpleName())) {
        methods = asMethodsForSuperLanguages(methods, symbolTableService.getGlobalScopeSimpleName(),
            symbolTableService.getGlobalScopeFullName(), "de.monticore.symboltable.IScope",
            "scope", superLanguage);
        methodNames.add("as" + symbolTableService.getGlobalScopeSimpleName());
      }
    }

    return methods;
  }

  protected List<ASTCDMethod> asMethodsForSuperLanguages(List<ASTCDMethod> methods, String name, String type, String parameterType, String parameterName, DiagramSymbol superLanguage) {
    ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
        .setModifier(PUBLIC.build())
        .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
            .setMCType(MCTypeFacade.getInstance()
                .createQualifiedType(type))
            .build())
        .setName("as" + name)
        .addCDParameter(CD4CodeMill.cDParameterBuilder()
            .setMCType(MCTypeFacade.getInstance().createQualifiedType(parameterType))
            .setName(parameterName)
            .build())
        .build();

    replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("dispatcher.AsAST",
            name,
            parameterName,
            getTypeDispatcherName(superLanguage.getName())));

    methods.add(method);

    return methods;
  }

  protected List<ASTCDMethod> createHandleMethods() {
    List<ASTCDMethod> methods = new ArrayList<>();

    handleMethod(methods,
        symbolTableService.getArtifactScopeInterfaceSimpleName(),
        symbolTableService.getArtifactScopeInterfaceType(),
        symbolTableService.getSuperCDsTransitive().stream()
            .map(symbolTableService::getArtifactScopeInterfaceFullName)
            .collect(Collectors.toList()));

    handleMethod(methods,
        symbolTableService.getGlobalScopeInterfaceSimpleName(),
        symbolTableService.getGlobalScopeInterfaceType(),
        symbolTableService.getSuperCDsTransitive().stream()
            .map(symbolTableService::getGlobalScopeInterfaceFullName)
            .collect(Collectors.toList()));

    handleMethod(methods,
        symbolTableService.getScopeInterfaceSimpleName(),
        symbolTableService.getScopeInterfaceType(),
        symbolTableService.getSuperCDsTransitive().stream()
            .map(symbolTableService::getScopeInterfaceFullName)
            .collect(Collectors.toList()));

    handleMethod(methods,
        symbolTableService.getArtifactScopeSimpleName(),
        symbolTableService.getArtifactScopeType(),
        List.of(symbolTableService.getArtifactScopeInterfaceFullName()));

    for (String symbol : symbolTableService.retrieveSymbolNamesFromCD(symbolTableService.getCDSymbol())) {
      handleMethod(methods,
          symbolTableService.getSimpleNameFromSymbolName(symbol),
          MCTypeFacade.getInstance().createQualifiedType(symbol),
          List.of(symbolTableService.getCommonSymbolInterfaceFullName(), "de.monticore.symboltable.ISymbol"));
    }

    for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      List<String> superTypes = typeSymbol.getSuperTypesList()
          .stream()
          .filter(s -> ((TypeSymbolSurrogate) s.getTypeInfo()).checkLazyLoadDelegate())
          .map(s -> ((TypeSymbolSurrogate) s.getTypeInfo()).lazyLoadDelegate())
          .filter(t -> t instanceof CDTypeSymbol)
          .map(s -> visitorService.createASTFullName((CDTypeSymbol) s))
          .collect(Collectors.toList());

      handleMethod(methods,
          typeSymbol.getName(),
          MCTypeFacade.getInstance().createQualifiedType(visitorService.createASTFullName(typeSymbol)),
          superTypes);
    }
    return methods;
  }

  protected void handleMethod(List<ASTCDMethod> methods, String typeName, ASTMCType type, List<String> superTypes) {
    ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
        .setModifier(PUBLIC.build())
        .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
            .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
            .build())
        .setName("handle")
        .addCDParameter(CD4CodeMill.cDParameterBuilder()
            .setMCType(type)
            .setName("node")
            .build())
        .build();

    replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("dispatcher.Handle", typeName, superTypes, getTypeDispatcherName(visitorService.getCDName())));

    methods.add(method);
  }

  protected ASTCDConstructor createConstructor(String name, List<ASTCDAttribute> superDispatchers) {
    ASTCDConstructor constructor = CD4CodeMill.cDConstructorBuilder()
        .setModifier(PUBLIC.build())
        .setName(name)
        .build();

    replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("dispatcher.Constructor",
        visitorService.getTraverserInterfaceFullName(),
        visitorService.getMillFullName(),
        superDispatchers.stream().map(ASTCDAttribute::getName).collect(Collectors.toList()),
        superDispatchers.stream().map(ASTCDAttribute::printType).collect(Collectors.toList()),
        visitorService.getSuperCDsTransitive().stream().map(DiagramSymbol::getName).collect(Collectors.toList()),
        visitorService.getCDName()));

    return constructor;
  }

  protected List<ASTCDMember> addTraverserElements() {
    List<ASTCDMember> traverserElements = new ArrayList<>();

    traverserElements.add(CD4CodeMill.cDAttributeBuilder()
        .setModifier(PROTECTED.build())
        .setMCType(MCTypeFacade.getInstance().createQualifiedType(visitorService.getTraverserInterfaceFullName()))
        .setName("traverser")
        .build());

    ASTCDMethod getter = CD4CodeMill.cDMethodBuilder()
        .setModifier(PUBLIC.build())
        .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
            .setMCType(MCTypeFacade.getInstance().createQualifiedType(visitorService.getTraverserInterfaceFullName()))
            .build())
        .setName("getTraverser")
        .build();
    this.glex.replaceTemplate(EMPTY_BODY, getter, new StringHookPoint("return this.traverser;"));

    ASTCDMethod setter = CD4CodeMill.cDMethodBuilder()
        .setModifier(PUBLIC.build())
        .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
            .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
            .build())
        .setName("setTraverser")
        .addCDParameter(CD4CodeMill.cDParameterBuilder()
            .setMCType(MCTypeFacade.getInstance().createQualifiedType(visitorService.getTraverserInterfaceFullName()))
            .setName("traverser").build())
        .build();
    this.glex.replaceTemplate(EMPTY_BODY, setter, new StringHookPoint("this.traverser = traverser;"));

    traverserElements.add(getter);
    traverserElements.add(setter);
    return traverserElements;
  }

  protected String getTypeDispatcherName(String name) {
    return name + "TypeDispatcher";
  }

  protected ASTCDInterfaceUsage getInterfaceUsage() {
    return CDInterfaceUsageFacade.getInstance()
        .createCDInterfaceUsage(visitorService.getHandlerFullName());
  }

  protected void addGettersAndSetters(List<ASTCDAttribute> attributes, ASTCDType type) {
    MethodDecorator methodDecorator = new MethodDecorator(glex, visitorService);
    for (ASTCDAttribute attribute : attributes) {
      type.addAllCDMembers(methodDecorator.decorate(attribute));
    }
  }

  protected String uncapFirst(String s) {
    return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
  }

}