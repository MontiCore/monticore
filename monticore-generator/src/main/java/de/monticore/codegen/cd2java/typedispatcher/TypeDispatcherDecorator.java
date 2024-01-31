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
      names.add(new AttributeName(
          visitorService.getCDName() + typeSymbol.getName(),
          visitorService.createASTFullName(typeSymbol),
          NODE_TYPE, NODE_PARAMETER));
    }

    for (String symbol : symbolTableService.retrieveSymbolNamesFromCD()) {

      names.add(new AttributeName(
          visitorService.getCDName() + symbolTableService.getSimpleNameFromSymbolName(symbol),
          symbol, SYMBOL_TYPE, SYMBOL_PARAMETER
      ));
    }

    names.add(new AttributeName(
        visitorService.getCDName() + symbolTableService.getScopeInterfaceSimpleName(),
        symbolTableService.getScopeInterfaceFullName(),
        SCOPE_TYPE, SCOPE_PARAMETER
    ));

    names.add(new AttributeName(
        visitorService.getCDName() + symbolTableService.getArtifactScopeInterfaceSimpleName(),
        symbolTableService.getArtifactScopeInterfaceFullName(),
        SCOPE_TYPE, SCOPE_PARAMETER
    ));

    names.add(new AttributeName(
        visitorService.getCDName() + symbolTableService.getGlobalScopeInterfaceSimpleName(),
        symbolTableService.getGlobalScopeInterfaceFullName(),
        SCOPE_TYPE, SCOPE_PARAMETER
    ));

    for (DiagramSymbol diagramSymbol : visitorService.getSuperCDsTransitive()) {
      for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(diagramSymbol)) {
        names.add(new AttributeName(
            diagramSymbol.getName() + typeSymbol.getName(),
            visitorService.createASTFullName(typeSymbol),
            NODE_TYPE, NODE_PARAMETER));
      }

      for (String symbol : symbolTableService.retrieveSymbolNamesFromCD(diagramSymbol)) {
        names.add(new AttributeName(
            diagramSymbol.getName() + symbolTableService.getSimpleNameFromSymbolName(symbol),
            symbol, SYMBOL_TYPE, SYMBOL_PARAMETER
        ));
      }

      names.add(new AttributeName(
          diagramSymbol.getName() + symbolTableService.getScopeInterfaceSimpleName(diagramSymbol),
          symbolTableService.getScopeInterfaceFullName(diagramSymbol),
          SCOPE_TYPE, SCOPE_PARAMETER
      ));

      names.add(new AttributeName(
          diagramSymbol.getName() + symbolTableService.getArtifactScopeInterfaceSimpleName(diagramSymbol),
          symbolTableService.getArtifactScopeInterfaceFullName(diagramSymbol),
          SCOPE_TYPE, SCOPE_PARAMETER
      ));

      names.add(new AttributeName(
          diagramSymbol.getName() + symbolTableService.getGlobalScopeInterfaceSimpleName(diagramSymbol),
          symbolTableService.getGlobalScopeInterfaceFullName(diagramSymbol),
          SCOPE_TYPE, SCOPE_PARAMETER
      ));
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
    return cdAttributeFacade.createAttribute(PROTECTED.build(),
        mcTypeFacade.createOptionalTypeOf(name.fullName),
        String.format("opt%s", name.simpleName));
  }

  protected ASTCDMethod createResetMethod(List<ASTCDAttribute> optAttributes) {
    ASTCDMethod resetMethod = cdMethodFacade.createMethod(
        PUBLIC.build(),
        CD4CodeMill.mCReturnTypeBuilder()
            .setMCVoidType(mcTypeFacade.createVoidType())
            .build(),
        "reset");

    replaceTemplate(EMPTY_BODY, resetMethod,
        new TemplateHookPoint("dispatcher.Reset", optAttributes));

    return resetMethod;
  }

  protected List<ASTCDMethod> createIsAsMethods(List<AttributeName> names) {
    List<ASTCDMethod> methods = new ArrayList<>();

    for (AttributeName name : names) {
      ASTCDMethod isMethod = cdMethodFacade.createMethod(
          PUBLIC.build(),
          mcTypeFacade.createBooleanType(),
          String.format("is%s", name.simpleName),
          cdParameterFacade.createParameter(name.type, name.parameterName));

      replaceTemplate(EMPTY_BODY, isMethod, new TemplateHookPoint(
          "dispatcher.IsMethod", name.simpleName, name.parameterName));

      methods.add(isMethod);

      ASTCDMethod asMethod = cdMethodFacade.createMethod(
          PUBLIC.build(),
          mcTypeFacade.createQualifiedType(name.fullName),
          String.format("as%s", name.simpleName),
          cdParameterFacade.createParameter(name.type, name.parameterName));

      replaceTemplate(EMPTY_BODY, asMethod, new TemplateHookPoint(
          "dispatcher.AsMethod", name.simpleName, name.parameterName));

      methods.add(asMethod);
    }

    return methods;
  }

  protected List<ASTCDMethod> createHandleMethods() {
    List<ASTCDMethod> methods = new ArrayList<>();

    methods.add(handleMethod(
        visitorService.getCDName() + symbolTableService.getArtifactScopeInterfaceSimpleName(),
        symbolTableService.getArtifactScopeInterfaceFullName(), SCOPE_PARAMETER,
        symbolTableService.getSuperCDsTransitive().stream()
            .map(symbolTableService::getArtifactScopeInterfaceFullName)
            .collect(Collectors.toList())));

    methods.add(handleMethod(
        visitorService.getCDName() + symbolTableService.getGlobalScopeInterfaceSimpleName(),
        symbolTableService.getGlobalScopeInterfaceFullName(), SCOPE_PARAMETER,
        symbolTableService.getSuperCDsTransitive().stream()
            .map(symbolTableService::getGlobalScopeInterfaceFullName)
            .collect(Collectors.toList())));

    methods.add(handleMethod(
        visitorService.getCDName() + symbolTableService.getScopeInterfaceSimpleName(),
        symbolTableService.getScopeInterfaceFullName(), SCOPE_PARAMETER,
        symbolTableService.getSuperCDsTransitive().stream()
            .map(symbolTableService::getScopeInterfaceFullName)
            .collect(Collectors.toList())));

    for (String symbol : symbolTableService.retrieveSymbolNamesFromCD()) {
      methods.add(handleMethod(
          visitorService.getCDName() + symbolTableService.getSimpleNameFromSymbolName(symbol),
          symbol, SYMBOL_PARAMETER,
          List.of(symbolTableService.getCommonSymbolInterfaceFullName(), SYMBOL_TYPE)));
    }

    for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes()) {
      List<String> superTypes = getSuperTypes(typeSymbol);
      methods.add(handleMethod(
          visitorService.getCDName() + typeSymbol.getName(),
          visitorService.createASTFullName(typeSymbol),
          NODE_PARAMETER, superTypes));
    }

    for (DiagramSymbol diagramSymbol : visitorService.getSuperCDsTransitive()) {
      methods.add(handleMethod(
          diagramSymbol.getName() + symbolTableService.getArtifactScopeInterfaceSimpleName(diagramSymbol),
          symbolTableService.getArtifactScopeInterfaceFullName(diagramSymbol), SCOPE_PARAMETER,
          symbolTableService.getSuperCDsTransitive(diagramSymbol).stream()
              .map(symbolTableService::getArtifactScopeInterfaceFullName)
              .collect(Collectors.toList())));

      methods.add(handleMethod(
          diagramSymbol.getName() + symbolTableService.getGlobalScopeInterfaceSimpleName(diagramSymbol),
          symbolTableService.getGlobalScopeInterfaceFullName(diagramSymbol), SCOPE_PARAMETER,
          symbolTableService.getSuperCDsTransitive(diagramSymbol).stream()
              .map(symbolTableService::getGlobalScopeInterfaceFullName)
              .collect(Collectors.toList())));

      methods.add(handleMethod(
          diagramSymbol.getName() + symbolTableService.getScopeInterfaceSimpleName(diagramSymbol),
          symbolTableService.getScopeInterfaceFullName(diagramSymbol), SCOPE_PARAMETER,
          symbolTableService.getSuperCDsTransitive(diagramSymbol).stream()
              .map(symbolTableService::getScopeInterfaceFullName)
              .collect(Collectors.toList())));

      for (String symbol : symbolTableService.retrieveSymbolNamesFromCD(diagramSymbol)) {
        methods.add(handleMethod(
            diagramSymbol.getName() + symbolTableService.getSimpleNameFromSymbolName(symbol),
            symbol, SYMBOL_PARAMETER,
            List.of(symbolTableService.getCommonSymbolInterfaceFullName(diagramSymbol), SYMBOL_TYPE)));
      }

      for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(diagramSymbol)) {
        List<String> superTypes = getSuperTypes(typeSymbol);
        methods.add(handleMethod(
            diagramSymbol.getName() + typeSymbol.getName(),
            visitorService.createASTFullName(typeSymbol),
            NODE_PARAMETER, superTypes));
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
    ASTCDMethod method = cdMethodFacade.createMethod(
        PUBLIC.build(),
        CD4CodeMill.mCReturnTypeBuilder()
            .setMCVoidType(mcTypeFacade.createVoidType()).build(), "handle",
        cdParameterFacade.createParameter(type, parameterName));

    replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(
            "dispatcher.Handle",
            typeName, superTypes, parameterName));

    return method;
  }

  protected ASTCDConstructor createConstructor(String name) {
    ASTCDConstructor constructor = cdConstructorFacade.createConstructor(PUBLIC.build(), name);

    List<String> superCDs = visitorService.getSuperCDsTransitive()
        .stream()
        .map(DiagramSymbol::getName)
        .collect(Collectors.toList());

    replaceTemplate(EMPTY_BODY, constructor,
        new TemplateHookPoint("dispatcher.Constructor",
            visitorService.getTraverserInterfaceFullName(),
            visitorService.getMillFullName(),
            visitorService.getCDName(),
            superCDs));

    return constructor;
  }

  protected List<ASTCDMember> addTraverserElements() {
    List<ASTCDMember> traverserElements = new ArrayList<>();

    traverserElements.add(cdAttributeFacade.createAttribute(PROTECTED.build(),
        mcTypeFacade.createQualifiedType(visitorService.getTraverserInterfaceFullName()),
        "traverser"));

    ASTCDMethod getter = cdMethodFacade.createMethod(PUBLIC.build(), mcTypeFacade
        .createQualifiedType(visitorService.getTraverserInterfaceFullName()), "getTraverser");
    this.glex.replaceTemplate(EMPTY_BODY, getter, new StringHookPoint("return this.traverser;"));
    traverserElements.add(getter);

    ASTCDMethod setter = cdMethodFacade.createMethod(
        PUBLIC.build(),
        CD4CodeMill.mCReturnTypeBuilder()
            .setMCVoidType(mcTypeFacade.createVoidType())
            .build(),
        "setTraverser",
        cdParameterFacade
            .createParameter(mcTypeFacade.createQualifiedType(visitorService.getTraverserInterfaceFullName()),
                "traverser"));
    this.glex.replaceTemplate(EMPTY_BODY, setter, new StringHookPoint("this.traverser = traverser;"));
    traverserElements.add(setter);

    for (DiagramSymbol diagram : visitorService.getSuperCDsTransitive()) {
      ASTCDMethod superSetter = cdMethodFacade.createMethod(
          PUBLIC.build(),
          CD4CodeMill.mCReturnTypeBuilder().setMCVoidType(mcTypeFacade.createVoidType()).build(),
          "setTraverser",
          cdParameterFacade
              .createParameter(
                  mcTypeFacade.createQualifiedType(visitorService.getTraverserInterfaceFullName(diagram)),
                      "traverser"));

    //  this.glex.replaceTemplate(EMPTY_BODY, superSetter, new StringHookPoint("this.traverser = traverser;"));
      traverserElements.add(superSetter);
    }

    return traverserElements;
  }

  protected String getTypeDispatcherName(String name) {
    return name + TYPE_DISPATCHER_SUFFIX;
  }

  protected ASTCDInterfaceUsage getInterfaceUsage() {
    return CDInterfaceUsageFacade.getInstance()
        .createCDInterfaceUsage(
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