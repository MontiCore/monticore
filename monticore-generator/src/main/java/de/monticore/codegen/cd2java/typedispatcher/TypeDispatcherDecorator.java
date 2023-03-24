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
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.ArrayList;
import java.util.List;
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

    String visitorName = getTypeDispatcherName(visitorService.getCDName());

    List<ASTCDAttribute> optASTAttributes = createOptASTAttributes();
    List<ASTCDAttribute> boolASTAttributes = createBoolASTAttributes();
    List<ASTCDAttribute> optScopeAttributes = createOptScopeAttributes();
    List<ASTCDAttribute> boolScopeAttributes = createBoolScopeAttributes();
    List<ASTCDAttribute> optSymbolAttributes = createOptSymbolAttributes();
    List<ASTCDAttribute> boolSymbolAttributes = createBoolSymbolAttributes();

    List<ASTCDMember> methods = new ArrayList<>();

    methods.add(createResetMethod(optASTAttributes,
            boolASTAttributes,
            optScopeAttributes,
            boolScopeAttributes,
            optSymbolAttributes,
            boolSymbolAttributes));

    methods.addAll(createIsMethods(boolASTAttributes, "de.monticore.ast.ASTNode", "node"));
    methods.addAll(createIsMethods(boolScopeAttributes, "de.monticore.symboltable.IScope", "scope"));
    methods.addAll(createIsMethods(boolSymbolAttributes, "de.monticore.symboltable.ISymbol", "symbol"));

    methods.addAll(createAsMethods(optASTAttributes, "de.monticore.ast.ASTNode", "node"));
    methods.addAll(createAsMethods(optScopeAttributes, "de.monticore.symboltable.IScope", "scope"));
    methods.addAll(createAsMethods(optSymbolAttributes, "de.monticore.symboltable.ISymbol", "symbol"));

    methods.addAll(createHandleMethods());

    return CD4CodeMill.cDClassBuilder()
            .setName(visitorName)
            .setModifier(PUBLIC.build())
            .setCDInterfaceUsage(getInterfaceUsage())
            .addAllCDMembers(optASTAttributes)
            .addAllCDMembers(boolASTAttributes)
            .addAllCDMembers(optScopeAttributes)
            .addAllCDMembers(boolScopeAttributes)
            .addAllCDMembers(optSymbolAttributes)
            .addAllCDMembers(boolSymbolAttributes)
            .addCDMember(createConstructor(visitorName))
            .addAllCDMembers(createTraverserElements())
            .addAllCDMembers(methods)
            .build();
  }

  protected List<ASTCDAttribute> createOptASTAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (CDTypeSymbol typeSymbol: visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      attributes.add(createOptional(visitorService.createASTFullName(typeSymbol), typeSymbol.getName()));
    }
    return attributes;
  }

  protected List<ASTCDAttribute> createBoolASTAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (CDTypeSymbol typeSymbol: visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      attributes.add(createBoolean(typeSymbol.getName()));
    }
    return attributes;
  }

  protected List<ASTCDAttribute> createOptScopeAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();

    attributes.add(createOptional(symbolTableService.getArtifactScopeInterfaceFullName(), symbolTableService.getArtifactScopeInterfaceSimpleName()));
    attributes.add(createOptional(symbolTableService.getGlobalScopeInterfaceFullName(), symbolTableService.getGlobalScopeInterfaceSimpleName()));
    attributes.add(createOptional(symbolTableService.getScopeInterfaceFullName(), symbolTableService.getScopeInterfaceSimpleName()));

    attributes.add(createOptional(symbolTableService.getScopeClassFullName(), symbolTableService.getScopeClassSimpleName()));
    attributes.add(createOptional(symbolTableService.getArtifactScopeFullName(), symbolTableService.getArtifactScopeSimpleName()));
    attributes.add(createOptional(symbolTableService.getGlobalScopeFullName(), symbolTableService.getGlobalScopeSimpleName()));

    return attributes;
  }

  protected List<ASTCDAttribute> createBoolScopeAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();

    attributes.add(createBoolean(symbolTableService.getArtifactScopeInterfaceSimpleName()));
    attributes.add(createBoolean(symbolTableService.getGlobalScopeInterfaceSimpleName()));
    attributes.add(createBoolean(symbolTableService.getScopeInterfaceSimpleName()));

    attributes.add(createBoolean(symbolTableService.getScopeClassSimpleName()));
    attributes.add(createBoolean(symbolTableService.getArtifactScopeSimpleName()));
    attributes.add(createBoolean(symbolTableService.getGlobalScopeSimpleName()));

    return attributes;
  }

  protected List<ASTCDAttribute> createOptSymbolAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (String symbol: symbolTableService.retrieveSymbolNamesFromCD(symbolTableService.getCDSymbol())) {
      attributes.add(createOptional(symbol, symbolTableService.getSimpleNameFromSymbolName(symbol)));
    }
    return attributes;
  }

  protected List<ASTCDAttribute> createBoolSymbolAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (String symbol: symbolTableService.retrieveSymbolNamesFromCD(symbolTableService.getCDSymbol())) {
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

  protected ASTCDMethod createResetMethod(List<ASTCDAttribute> optASTAttributes,
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
                    booleans, optionals));

    return resetMethod;
  }


  protected List<ASTCDMember> createIsMethods(List<ASTCDAttribute> attributes, String parameterType, String parameterName) {
    List<ASTCDMember> methods = new ArrayList<>();
    List<String> names = attributes.stream().map(ASTCDAttribute::getName).collect(Collectors.toList());
    for (String name: names) {
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
      replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("dispatcher.IsAST", name, parameterName));
      methods.add(method);
    }
    return methods;
  }

  protected List<ASTCDMember> createAsMethods(List<ASTCDAttribute> attributes, String parameterType, String parameterName) {
    List<ASTCDMember> methods = new ArrayList<>();
    for (ASTCDAttribute attribute: attributes) {
      String name = attribute.getName();
      name = name.substring(name.indexOf("opt") + 3);

      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
              .setModifier(PUBLIC.build())
              .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                      .setMCType(MCTypeFacade.getInstance()
                              .createQualifiedType(attribute.getMCType()
                                      .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()))))
                      .build())
              .setName("as" + name)
              .addCDParameter(CD4CodeMill.cDParameterBuilder()
                      .setMCType(MCTypeFacade.getInstance().createQualifiedType(parameterType))
                      .setName(parameterName)
                      .build())
              .build();

      replaceTemplate(EMPTY_BODY, method,
              new TemplateHookPoint("dispatcher.AsAST", name, parameterName));

      methods.add(method);
    }
    return methods;
  }

  protected List<ASTCDMember> createHandleMethods() {
    List<ASTCDMember> methods = new ArrayList<>();

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

    for (String symbol: symbolTableService.retrieveSymbolNamesFromCD(symbolTableService.getCDSymbol())) {
      handleMethod(methods,
              symbolTableService.getSimpleNameFromSymbolName(symbol),
              MCTypeFacade.getInstance().createQualifiedType(symbol),
              List.of(symbolTableService.getCommonSymbolInterfaceFullName(), "de.monticore.symboltable.ISymbol"));
    }

    for (CDTypeSymbol typeSymbol: visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
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

  protected void handleMethod(List<ASTCDMember> methods, String typeName, ASTMCType type, List<String> superTypes) {
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
            new TemplateHookPoint("dispatcher.Handle", typeName, superTypes));

    methods.add(method);
  }

  protected ASTCDConstructor createConstructor(String name) {
    ASTCDConstructor constructor = CD4CodeMill.cDConstructorBuilder()
            .setModifier(PUBLIC.build())
            .setName(name)
            .build();
    replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("reset();"));
    return constructor;
  }

  protected List<ASTCDMember> createTraverserElements() {
    List<ASTCDMember> traverserElements = new ArrayList<>();

    traverserElements.add(CD4CodeMill.cDAttributeBuilder()
            .setModifier(PROTECTED.build())
            .setName("traversedElements")
            .setMCType(MCTypeFacade.getInstance().createSetTypeOf("Object"))
            .build());

    ASTCDMethod getter = CD4CodeMill.cDMethodBuilder()
            .setModifier(PUBLIC.build())
            .setName("getTraversedElements")
            .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                    .setMCType(MCTypeFacade.getInstance().createSetTypeOf("Object"))
                    .build())
            .build();

    replaceTemplate(EMPTY_BODY, getter,
            new StringHookPoint("return traversedElements;"));

    ASTCDMethod setter = CD4CodeMill.cDMethodBuilder()
            .setModifier(PUBLIC.build())
            .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                    .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
                    .build())
            .setName("setTraversedElements")
            .addCDParameter(CD4CodeMill.cDParameterBuilder()
                    .setMCType(MCTypeFacade.getInstance().createSetTypeOf("Object"))
                    .setName("traversedElements")
                    .build())
            .build();

    replaceTemplate(EMPTY_BODY, setter,
            new StringHookPoint("this.traversedElements = traversedElements;"));

    traverserElements.add(getter);
    traverserElements.add(setter);

    return traverserElements;
  }

  protected String getTypeDispatcherName(String name) {
    return name + "TypeDispatcher";
  }

  protected ASTCDInterfaceUsage getInterfaceUsage() {
    return CDInterfaceUsageFacade.getInstance()
            .createCDInterfaceUsage(visitorService.getTraverserInterfaceFullName());
  }

}