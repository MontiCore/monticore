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
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;


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

    List<ASTCDAttribute> attributes = createAllAttributes();

    List<ASTCDMember> methods = new ArrayList<>();
    methods.add(createResetMethod(new ArrayList<>(attributes)));
    methods.addAll(createIsASTMethods(new ArrayList<>(attributes)));
    methods.addAll(createAsASTMethods(new ArrayList<>(attributes)));
    methods.addAll(createHandleMethods());

    return CD4CodeMill.cDClassBuilder()
            .setName(visitorName)
            .setModifier(PUBLIC.build())
            .setCDInterfaceUsage(getInterfaceUsage())
            .addAllCDMembers(attributes)
            .addCDMember(createConstructor(visitorName))
            .addAllCDMembers(createTraverserElements())
            .addAllCDMembers(methods)
            .build();
  }

  public List<ASTCDAttribute> createAllAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();

    for(CDTypeSymbol typeSymbol: visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      booleanAttribute(attributes, typeSymbol.getName());
      optionalAttribute(attributes, visitorService.createASTFullName(typeSymbol), typeSymbol.getName());
    }

    booleanAttribute(attributes, symbolTableService.getArtifactScopeInterfaceSimpleName());
    optionalAttribute(attributes, symbolTableService.getArtifactScopeInterfaceFullName(), symbolTableService.getArtifactScopeInterfaceSimpleName());
    booleanAttribute(attributes, symbolTableService.getGlobalScopeInterfaceSimpleName());
    optionalAttribute(attributes, symbolTableService.getGlobalScopeInterfaceFullName(), symbolTableService.getGlobalScopeInterfaceSimpleName());
    booleanAttribute(attributes, symbolTableService.getScopeInterfaceSimpleName());
    optionalAttribute(attributes, symbolTableService.getScopeInterfaceFullName(), symbolTableService.getScopeInterfaceSimpleName());

    booleanAttribute(attributes, symbolTableService.getArtifactScopeSimpleName());
    optionalAttribute(attributes, symbolTableService.getArtifactScopeFullName(), symbolTableService.getArtifactScopeSimpleName());
    booleanAttribute(attributes, symbolTableService.getGlobalScopeSimpleName());
    optionalAttribute(attributes, symbolTableService.getGlobalScopeFullName(), symbolTableService.getGlobalScopeSimpleName());

    return attributes;
  }

  private void optionalAttribute(List<ASTCDAttribute> attributes, String type, String name) {
    attributes.add(CD4CodeMill.cDAttributeBuilder()
            .setModifier(PROTECTED.build())
            .setMCType(MCTypeFacade
                    .getInstance()
                    .createOptionalTypeOf(type))
            .setName("opt" + name)
            .build());
  }

  private static void booleanAttribute(List<ASTCDAttribute> attributes, String type) {
    attributes.add(CD4CodeMill.cDAttributeBuilder()
            .setModifier(PROTECTED.build())
            .setMCType(MCTypeFacade.getInstance().createBooleanType())
            .setName("is" + type)
            .build());
  }

  public ASTCDMethod createResetMethod(List<ASTCDAttribute> attributes) {
    ASTCDMethod resetMethod = CD4CodeMill.cDMethodBuilder()
            .setModifier(PUBLIC.build())
            .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                    .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
                    .build())
            .setName("reset")
            .build();

    List<String> booleans = attributes.stream()
            .map(ASTCDAttribute::getName)
            .filter(s -> s.startsWith("is"))
            .collect(Collectors.toList());

    List<String> optionals = attributes.stream()
            .map(ASTCDAttribute::getName)
            .filter(s -> s.startsWith("opt"))
            .collect(Collectors.toList());

    replaceTemplate(EMPTY_BODY, resetMethod,
            new TemplateHookPoint("dispatcher.Reset",
                    booleans, optionals));

    return resetMethod;
  }

  public ASTCDConstructor createConstructor(String name) {
    ASTCDConstructor constructor = CD4CodeMill.cDConstructorBuilder()
            .setModifier(PUBLIC.build())
            .setName(name)
            .build();
    replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("reset();"));
    return constructor;
  }

  public List<ASTCDMember> createIsASTMethods(List<ASTCDAttribute> attributes) {
    List<ASTCDMember> methods = new ArrayList<>();
    List<String> names = attributes.stream()
            .map(ASTCDAttribute::getName)
            .filter(s -> s.startsWith("is"))
            .collect(Collectors.toList());

    for(String name: names) {
      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
              .setModifier(PUBLIC.build())
              .setName(name)
              .addCDParameter(CD4CodeMill.cDParameterBuilder()
                      .setMCType(MCTypeFacade.getInstance().createQualifiedType("de.monticore.ast.ASTNode"))
                      .setName("node")
                      .build())
              .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                      .setMCType(MCTypeFacade.getInstance().createBooleanType())
                      .build())
              .build();

      replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("dispatcher.IsAST", name));
      methods.add(method);
    }
    return methods;
  }

  public List<ASTCDMember> createAsASTMethods(List<ASTCDAttribute> attributes) {
    List<ASTCDMember> methods = new ArrayList<>();
    attributes.removeIf(a -> !a.getName().startsWith("opt"));

    for(ASTCDAttribute attribute: attributes) {
      String name = attribute.getName();
      name = name.substring(name.indexOf("opt")+3);

      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
              .setModifier(PUBLIC.build())
              .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                      .setMCType(MCTypeFacade.getInstance()
                              .createQualifiedType(attribute.getMCType()
                                      .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()))))
                      .build())
              .setName("as" + name)
              .addCDParameter(CD4CodeMill.cDParameterBuilder()
                      .setMCType(MCTypeFacade.getInstance().createQualifiedType("de.monticore.ast.ASTNode"))
                      .setName("node")
                      .build())
              .build();

      replaceTemplate(EMPTY_BODY, method,
              new TemplateHookPoint("dispatcher.AsAST", name));

      methods.add(method);
    }
    return methods;
  }

  public List<ASTCDMember> createHandleMethods() {
    List<ASTCDMember> methods = new ArrayList<>();

    ASTCDMethod handleArtifactInterfaceScope = CD4CodeMill.cDMethodBuilder()
            .setModifier(PUBLIC.build())
            .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                    .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
                    .build())
            .setName("handle")
            .addCDParameter(CD4CodeMill.cDParameterBuilder()
                    .setMCType(symbolTableService.getArtifactScopeInterfaceType())
                    .setName("node")
                    .build())
            .build();

    replaceTemplate(EMPTY_BODY, handleArtifactInterfaceScope,
            new TemplateHookPoint("dispatcher.Handle",
                    symbolTableService.getArtifactScopeInterfaceSimpleName(),
                    symbolTableService.getSuperCDsTransitive().stream()
                            .map(symbolTableService::getArtifactScopeInterfaceFullName)
                            .collect(Collectors.toList())));

    methods.add(handleArtifactInterfaceScope);

    ASTCDMethod handleGlobalInterfaceScope = CD4CodeMill.cDMethodBuilder()
            .setModifier(PUBLIC.build())
            .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                    .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
                    .build())
            .setName("handle")
            .addCDParameter(CD4CodeMill.cDParameterBuilder()
                    .setMCType(symbolTableService.getGlobalScopeInterfaceType())
                    .setName("node")
                    .build())
            .build();

    replaceTemplate(EMPTY_BODY, handleGlobalInterfaceScope,
            new TemplateHookPoint("dispatcher.Handle",
                    symbolTableService.getGlobalScopeInterfaceSimpleName(),
                    symbolTableService.getSuperCDsTransitive().stream()
                            .map(symbolTableService::getGlobalScopeInterfaceFullName)
                            .collect(Collectors.toList())));

    methods.add(handleGlobalInterfaceScope);

    ASTCDMethod handleArtifactScope = CD4CodeMill.cDMethodBuilder()
            .setModifier(PUBLIC.build())
            .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                    .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
                    .build())
            .setName("handle")
            .addCDParameter(CD4CodeMill.cDParameterBuilder()
                    .setMCType(symbolTableService.getArtifactScopeType())
                    .setName("node")
                    .build())
            .build();

    replaceTemplate(EMPTY_BODY, handleArtifactScope,
            new TemplateHookPoint("dispatcher.Handle",
                    symbolTableService.getArtifactScopeSimpleName(),
                    symbolTableService.getSuperCDsTransitive().stream()
                            .map(symbolTableService::getArtifactScopeFullName)
                            .collect(Collectors.toList())));

    methods.add(handleArtifactScope);

    ASTCDMethod handleInterfaceScope = CD4CodeMill.cDMethodBuilder()
            .setModifier(PUBLIC.build())
            .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                    .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
                    .build())
            .setName("handle")
            .addCDParameter(CD4CodeMill.cDParameterBuilder()
                    .setMCType(symbolTableService.getScopeInterfaceType())
                    .setName("node")
                    .build())
            .build();

    replaceTemplate(EMPTY_BODY, handleInterfaceScope,
            new TemplateHookPoint("dispatcher.Handle",
                    symbolTableService.getScopeInterfaceSimpleName(),
                    symbolTableService.getSuperCDsTransitive().stream()
                            .map(symbolTableService::getScopeInterfaceFullName)
                            .collect(Collectors.toList())));

    methods.add(handleInterfaceScope);

    for(CDTypeSymbol typeSymbol: visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      List<String> superTypes = typeSymbol.getSuperTypesList()
              .stream()
              .filter(s -> ((TypeSymbolSurrogate) s.getTypeInfo()).checkLazyLoadDelegate())
              .map(s -> ((TypeSymbolSurrogate) s.getTypeInfo()).lazyLoadDelegate())
              .filter(t -> t instanceof CDTypeSymbol)
              .map(s -> visitorService.createASTFullName((CDTypeSymbol) s))
              .collect(Collectors.toList());

      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
              .setModifier(PUBLIC.build())
              .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                      .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
                      .build())
              .setName("handle")
              .addCDParameter(CD4CodeMill.cDParameterBuilder().setMCType(MCTypeFacade.getInstance()
                              .createQualifiedType(visitorService.createASTFullName(typeSymbol)))
                      .setName("node")
                      .build())
              .build();

      replaceTemplate(EMPTY_BODY, method,
              new TemplateHookPoint("dispatcher.Handle", typeSymbol.getName(), superTypes));

      methods.add(method);
    }
    return methods;
  }

  public List<ASTCDMember> createTraverserElements() {
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

  public String getTypeDispatcherName(String name) {
    return name + "TypeDispatcher";
  }
  public ASTCDInterfaceUsage getInterfaceUsage() {
    return CDInterfaceUsageFacade.getInstance()
            .createCDInterfaceUsage(visitorService.getTraverserInterfaceFullName());
  }
}
