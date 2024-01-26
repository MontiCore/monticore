/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typedispatcher;

import de.monticore.cd.facade.*;
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
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.MCTypeFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.typedispatcher.TypeDispatcherConstants.*;

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
    ASTCDPackage visitorPackage = getPackage(input, decoratedCD, UTILS_PACKAGE);
    visitorPackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {

    String handlerName = getTypeDispatcherName(visitorService.getCDName());

    List<AttributeName> attributeNames = getAttributeNames();
    List<ASTCDAttribute> optAttributes = createAttributesFromNames(attributeNames);

    List<ASTCDMethod> methods = new ArrayList<>();

    methods.add(createResetMethod(optAttributes));
    methods.addAll(createIsAsMethods(attributeNames));
    methods.addAll(createHandleMethods());

    ASTCDClass dispatcher = CD4CodeMill.cDClassBuilder()
        .setName(handlerName)
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsage())
        .addAllCDMembers(optAttributes)
        .addCDMember(createConstructor(handlerName))
        .addAllCDMembers(addTraverserElements())
        .addAllCDMembers(methods)
        .build();

    addGettersAndSetters(optAttributes, dispatcher);

    return dispatcher;
  }

  protected List<AttributeName> getAttributeNames() {
    List<AttributeName> names = new ArrayList<>();

    for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes()) {
      if (names.stream().map(n -> n.simpleName)
          .noneMatch(n -> n.equals(typeSymbol.getName()))) {
        names.add(new AttributeName(
            typeSymbol.getName(),
            visitorService.createASTFullName(typeSymbol),
            NODE_TYPE, NODE_PARAMETER));
      }
    }

    for (String symbol : symbolTableService.retrieveSymbolNamesFromCD()) {
      if (names.stream().map(n -> n.simpleName)
          .noneMatch(n -> n.equals(symbolTableService.getSimpleNameFromSymbolName(symbol)))) {
        names.add(new AttributeName(
            symbolTableService.getSimpleNameFromSymbolName(symbol),
            symbol, SYMBOL_TYPE, SYMBOL_PARAMETER
        ));
      }
    }

    if (names.stream().map(n -> n.simpleName)
        .noneMatch(n -> n.equals(symbolTableService.getScopeInterfaceFullName()))) {
      names.add(new AttributeName(
          symbolTableService.getScopeInterfaceSimpleName(),
          symbolTableService.getScopeInterfaceFullName(),
          SCOPE_TYPE, SCOPE_PARAMETER
      ));
    }

    if (names.stream().map(n -> n.simpleName)
        .noneMatch(n -> n.equals(symbolTableService.getArtifactScopeInterfaceFullName()))) {
      names.add(new AttributeName(
          symbolTableService.getArtifactScopeInterfaceSimpleName(),
          symbolTableService.getArtifactScopeInterfaceFullName(),
          SCOPE_TYPE, SCOPE_PARAMETER
      ));
    }

    if (names.stream().map(n -> n.simpleName)
        .noneMatch(n -> n.equals(symbolTableService.getGlobalScopeInterfaceFullName()))) {
      names.add(new AttributeName(
          symbolTableService.getGlobalScopeInterfaceSimpleName(),
          symbolTableService.getGlobalScopeInterfaceFullName(),
          SCOPE_TYPE, SCOPE_PARAMETER
      ));
    }

    for (DiagramSymbol diagramSymbol : visitorService.getSuperCDsTransitive()) {
      for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(diagramSymbol)) {
        if (names.stream().map(n -> n.simpleName)
            .noneMatch(n -> n.equals(typeSymbol.getName()))) {
          names.add(new AttributeName(
              typeSymbol.getName(),
              visitorService.createASTFullName(typeSymbol),
              NODE_TYPE, NODE_PARAMETER));
        }
      }

      for (String symbol : symbolTableService.retrieveSymbolNamesFromCD(diagramSymbol)) {
        if (names.stream().map(n -> n.simpleName)
            .noneMatch(n -> n.equals(symbolTableService.getSimpleNameFromSymbolName(symbol)))) {
          names.add(new AttributeName(
              symbolTableService.getSimpleNameFromSymbolName(symbol),
              symbol, SYMBOL_TYPE, SYMBOL_PARAMETER
          ));
        }
      }

      if (names.stream().map(n -> n.simpleName)
          .noneMatch(n -> n.equals(symbolTableService.getScopeInterfaceFullName()))) {
        names.add(new AttributeName(
            symbolTableService.getScopeInterfaceSimpleName(diagramSymbol),
            symbolTableService.getScopeInterfaceFullName(diagramSymbol),
            SCOPE_TYPE, SCOPE_PARAMETER
        ));
      }

      if (names.stream().map(n -> n.simpleName)
          .noneMatch(n -> n.equals(symbolTableService.getArtifactScopeInterfaceFullName()))) {
        names.add(new AttributeName(
            symbolTableService.getArtifactScopeInterfaceSimpleName(diagramSymbol),
            symbolTableService.getArtifactScopeInterfaceFullName(diagramSymbol),
            SCOPE_TYPE, SCOPE_PARAMETER
        ));
      }

      if (names.stream().map(n -> n.simpleName)
          .noneMatch(n -> n.equals(symbolTableService.getGlobalScopeInterfaceFullName()))) {
        names.add(new AttributeName(
            symbolTableService.getGlobalScopeInterfaceSimpleName(diagramSymbol),
            symbolTableService.getGlobalScopeInterfaceFullName(diagramSymbol),
            SCOPE_TYPE, SCOPE_PARAMETER
        ));
      }
    }

    return names;
  }

  protected List<ASTCDAttribute> createAttributesFromNames(List<AttributeName> names) {
    List<ASTCDAttribute> attributes = new ArrayList<>();

    for (AttributeName name : names) {
      attributes.add(createOptional(name));
    }

    return attributes;
  }

  protected ASTCDAttribute createOptional(AttributeName name) {
    return CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(),
        MCTypeFacade.getInstance().createOptionalTypeOf(name.fullName),
        String.format("opt%s", name.simpleName));
  }

  protected ASTCDMethod createResetMethod(List<ASTCDAttribute> optAttributes) {
    ASTCDMethod resetMethod = CDMethodFacade.getInstance().createMethod(
        PUBLIC.build(),
        CD4CodeMill.mCReturnTypeBuilder()
            .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
            .build(),
        "reset");

    replaceTemplate(EMPTY_BODY, resetMethod,
        new TemplateHookPoint("dispatcher.Reset", optAttributes));

    return resetMethod;
  }

  protected List<ASTCDMethod> createIsAsMethods(List<AttributeName> names) {
    List<ASTCDMethod> methods = new ArrayList<>();

    CDMethodFacade methodFacade = CDMethodFacade.getInstance();
    CDParameterFacade parameterFacade = CDParameterFacade.getInstance();
    MCTypeFacade typeFacade = MCTypeFacade.getInstance();

    for (AttributeName name : names) {
      ASTCDMethod isMethod = methodFacade.createMethod(
          PUBLIC.build(),
          typeFacade.createBooleanType(),
          String.format("is%s", name.simpleName),
          parameterFacade.createParameter(name.type, name.parameterName));

      replaceTemplate(EMPTY_BODY, isMethod, new TemplateHookPoint(
          "dispatcher.IsMethod", name.simpleName, name.parameterName));

      methods.add(isMethod);

      ASTCDMethod asMethod = methodFacade.createMethod(
          PUBLIC.build(),
          typeFacade.createQualifiedType(name.fullName),
          String.format("as%s", name.simpleName),
          parameterFacade.createParameter(name.type, name.parameterName));

      replaceTemplate(EMPTY_BODY, asMethod, new TemplateHookPoint(
          "dispatcher.AsMethod", name.simpleName, name.parameterName));

      methods.add(asMethod);
    }

    return methods;
  }

  protected List<ASTCDMethod> createHandleMethods() {
    List<ASTCDMethod> methods = new ArrayList<>();
    List<String> handleParameter = new ArrayList<>();

    methods.add(handleMethod(
        symbolTableService.getArtifactScopeInterfaceSimpleName(),
        symbolTableService.getArtifactScopeInterfaceFullName(), SCOPE_PARAMETER,
        symbolTableService.getSuperCDsTransitive().stream()
            .map(symbolTableService::getArtifactScopeInterfaceFullName)
            .collect(Collectors.toList())));
    handleParameter.add(symbolTableService.getArtifactScopeInterfaceSimpleName());

    methods.add(handleMethod(
        symbolTableService.getGlobalScopeInterfaceSimpleName(),
        symbolTableService.getGlobalScopeInterfaceFullName(), SCOPE_PARAMETER,
        symbolTableService.getSuperCDsTransitive().stream()
            .map(symbolTableService::getGlobalScopeInterfaceFullName)
            .collect(Collectors.toList())));
    handleParameter.add(symbolTableService.getGlobalScopeInterfaceSimpleName());

    methods.add(handleMethod(
        symbolTableService.getScopeInterfaceSimpleName(),
        symbolTableService.getScopeInterfaceFullName(), SCOPE_PARAMETER,
        symbolTableService.getSuperCDsTransitive().stream()
            .map(symbolTableService::getScopeInterfaceFullName)
            .collect(Collectors.toList())));
    handleParameter.add(symbolTableService.getScopeInterfaceSimpleName());

    for (String symbol : symbolTableService.retrieveSymbolNamesFromCD()) {
      methods.add(handleMethod(
          symbolTableService.getSimpleNameFromSymbolName(symbol),
          symbol, SYMBOL_PARAMETER,
          List.of(symbolTableService.getCommonSymbolInterfaceFullName(), SYMBOL_TYPE)));
      handleParameter.add(symbolTableService.getSimpleNameFromSymbolName(symbol));
    }

    for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes()) {
      List<String> superTypes = getSuperTypes(typeSymbol);
      methods.add(handleMethod(
          typeSymbol.getName(),
          visitorService.createASTFullName(typeSymbol),
          NODE_PARAMETER, superTypes));
      handleParameter.add(typeSymbol.getName());
    }

    for (DiagramSymbol diagramSymbol : visitorService.getSuperCDsTransitive()) {
      if (!handleParameter.contains(symbolTableService.getArtifactScopeInterfaceSimpleName(diagramSymbol))) {
        methods.add(handleMethod(
            symbolTableService.getArtifactScopeInterfaceSimpleName(diagramSymbol),
            symbolTableService.getArtifactScopeInterfaceFullName(diagramSymbol), SCOPE_PARAMETER,
            symbolTableService.getSuperCDsTransitive(diagramSymbol).stream()
                .map(symbolTableService::getArtifactScopeInterfaceFullName)
                .collect(Collectors.toList())));
        handleParameter.add(symbolTableService.getArtifactScopeInterfaceSimpleName(diagramSymbol));
      }

      if (!handleParameter.contains(symbolTableService.getGlobalScopeInterfaceSimpleName(diagramSymbol))) {
        methods.add(handleMethod(
            symbolTableService.getGlobalScopeInterfaceSimpleName(diagramSymbol),
            symbolTableService.getGlobalScopeInterfaceFullName(diagramSymbol), SCOPE_PARAMETER,
            symbolTableService.getSuperCDsTransitive(diagramSymbol).stream()
                .map(symbolTableService::getGlobalScopeInterfaceFullName)
                .collect(Collectors.toList())));
        handleParameter.add(symbolTableService.getGlobalScopeInterfaceSimpleName(diagramSymbol));
      }

      if (!handleParameter.contains(symbolTableService.getScopeInterfaceSimpleName(diagramSymbol))) {
        methods.add(handleMethod(
            symbolTableService.getScopeInterfaceSimpleName(diagramSymbol),
            symbolTableService.getScopeInterfaceFullName(diagramSymbol), SCOPE_PARAMETER,
            symbolTableService.getSuperCDsTransitive(diagramSymbol).stream()
                .map(symbolTableService::getScopeInterfaceFullName)
                .collect(Collectors.toList())));
        handleParameter.add(symbolTableService.getScopeInterfaceSimpleName(diagramSymbol));
      }

      for (String symbol : symbolTableService.retrieveSymbolNamesFromCD(diagramSymbol)) {
        if (!handleParameter.contains(symbolTableService.getSimpleNameFromSymbolName(symbol))) {
          methods.add(handleMethod(
              symbolTableService.getSimpleNameFromSymbolName(symbol),
              symbol, SYMBOL_PARAMETER,
              List.of(symbolTableService.getCommonSymbolInterfaceFullName(diagramSymbol), SYMBOL_TYPE)));
          handleParameter.add(symbolTableService.getSimpleNameFromSymbolName(symbol));
        }
      }

      for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(diagramSymbol)) {
        if (!handleParameter.contains(typeSymbol.getName())) {
          List<String> superTypes = getSuperTypes(typeSymbol);
          methods.add(handleMethod(
              typeSymbol.getName(),
              visitorService.createASTFullName(typeSymbol),
              NODE_PARAMETER, superTypes));
          handleParameter.add(typeSymbol.getName());
        }
      }
    }

    return methods;
  }

  protected List<String> getSuperTypes(CDTypeSymbol typeSymbol) {
    return typeSymbol.getSuperTypesList()
        .stream()
        .filter(s -> ((TypeSymbolSurrogate) s.getTypeInfo()).checkLazyLoadDelegate())
        .map(s -> ((TypeSymbolSurrogate) s.getTypeInfo()).lazyLoadDelegate())
        .filter(t -> t instanceof CDTypeSymbol)
        .map(s -> visitorService.createASTFullName((CDTypeSymbol) s))
        .collect(Collectors.toList());
  }

  protected ASTCDMethod handleMethod(String typeName,
                                     String type,
                                     String parameterName,
                                     List<String> superTypes) {
    ASTCDMethod method = CDMethodFacade.getInstance().createMethod(
        PUBLIC.build(),
        CD4CodeMill.mCReturnTypeBuilder()
            .setMCVoidType(MCTypeFacade.getInstance().createVoidType()).build(), "handle",
        CDParameterFacade.getInstance().createParameter(type, parameterName));

    replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(
            "dispatcher.Handle",
            typeName, superTypes, parameterName));

    return method;
  }

  protected ASTCDConstructor createConstructor(String name) {
    ASTCDConstructor constructor = CDConstructorFacade.getInstance().createConstructor(PUBLIC.build(), name);

    replaceTemplate(EMPTY_BODY, constructor,
        new TemplateHookPoint("dispatcher.Constructor",
            visitorService.getTraverserInterfaceFullName(),
            visitorService.getMillFullName(),
            visitorService.getCDName()));

    return constructor;
  }

  protected List<ASTCDMember> addTraverserElements() {
    List<ASTCDMember> traverserElements = new ArrayList<>();

    traverserElements.add(CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(),
        MCTypeFacade.getInstance().createQualifiedType(visitorService.getTraverserInterfaceFullName()),
        "traverser"));

    ASTCDMethod getter = CDMethodFacade.getInstance().createMethod(PUBLIC.build(), MCTypeFacade.getInstance()
        .createQualifiedType(visitorService.getTraverserInterfaceFullName()), "getTraverser");
    this.glex.replaceTemplate(EMPTY_BODY, getter, new StringHookPoint("return this.traverser;"));

    ASTCDMethod setter = CDMethodFacade.getInstance().createMethod(PUBLIC.build(), CD4CodeMill.mCReturnTypeBuilder()
        .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
        .build(), "setTraverser", CDParameterFacade.getInstance()
        .createParameter(MCTypeFacade.getInstance().createQualifiedType(visitorService.getTraverserInterfaceFullName()),
            "traverser"));
    this.glex.replaceTemplate(EMPTY_BODY, setter, new StringHookPoint("this.traverser = traverser;"));

    traverserElements.add(getter);
    traverserElements.add(setter);
    return traverserElements;
  }

  protected String getTypeDispatcherName(String name) {
    return name + TYPE_DISPATCHER_SUFFIX;
  }

  protected ASTCDInterfaceUsage getInterfaceUsage() {
    return CDInterfaceUsageFacade.getInstance()
        .createCDInterfaceUsage(
            visitorService.getHandlerFullName(),
            String.format("I%s", getTypeDispatcherName(visitorService.getCDName())));
  }

  protected void addGettersAndSetters(List<ASTCDAttribute> attributes, ASTCDType type) {
    MethodDecorator methodDecorator = new MethodDecorator(glex, visitorService);
    for (ASTCDAttribute attribute : attributes) {
      type.addAllCDMembers(methodDecorator.decorate(attribute));
    }
  }

  protected static class AttributeName {

    protected String simpleName;
    protected String fullName;

    protected String type;

    protected String parameterName;

    public AttributeName(String simpleName, String fullName, String type, String parameterName) {
      this.simpleName = simpleName;
      this.fullName = fullName;
      this.type = type;
      this.parameterName = parameterName;
    }
  }
}