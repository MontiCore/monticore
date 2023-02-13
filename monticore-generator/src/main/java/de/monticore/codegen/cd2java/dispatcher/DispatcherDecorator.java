/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.dispatcher;

import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;


public class DispatcherDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService visitorService;

  public DispatcherDecorator(final GlobalExtensionManagement glex,
                             final VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(input, decoratedCD, "_util");
    visitorPackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {

    String visitorName = getInheritanceVisitorName(input.getCDDefinition().getName());
    ASTCDInterfaceUsage traverserInterface = getInterfaceUsage();
    List<ASTCDAttribute> attributes = createAllAttributes();
    ASTCDMethod resetMethod = createResetMethod();
    ASTCDConstructor constructor = createConstructor(resetMethod, visitorName);
    List<ASTCDMember> traverserElements = createTraverserElements();
    List<ASTCDMember> methods = createIsASTMethods();
    methods.addAll(createAsASTMethods());
    methods.addAll(createHandleMethods());
    methods.add(resetMethod);

    return CD4CodeMill.cDClassBuilder()
            .setName(visitorName)
            .setModifier(PUBLIC.build())
            .setCDInterfaceUsage(traverserInterface)
            .addAllCDMembers(attributes)
            .addCDMember(constructor)
            .addAllCDMembers(traverserElements)
            .addAllCDMembers(methods)
            .build();
  }

  public List<ASTCDAttribute> createAllAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for(CDTypeSymbol typeSymbol: visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      attributes.add(CD4CodeMill.cDAttributeBuilder()
              .setModifier(PROTECTED.build())
              .setMCType(MCTypeFacade.getInstance().createBooleanType())
              .setName("is" + typeSymbol.getName())
              .build());

      attributes.add(CD4CodeMill.cDAttributeBuilder()
              .setModifier(PROTECTED.build())
              .setMCType(MCTypeFacade
                      .getInstance()
                      .createOptionalTypeOf(visitorService.createASTFullName(typeSymbol)))
              .setName("opt" + typeSymbol.getName())
              .build());
    }
    return attributes;
  }

  public ASTCDMethod createResetMethod() {
    ASTCDMethod resetMethod = CD4CodeMill.cDMethodBuilder()
            .setModifier(PUBLIC.build())
            .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                    .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
                    .build())
            .setName("reset")
            .build();

    replaceTemplate(EMPTY_BODY, resetMethod,
            new TemplateHookPoint("dispatcher.Reset",
                    visitorService.getAllCDTypes(visitorService.getCDSymbol()).stream().map(CDTypeSymbol::getName).collect(Collectors.toList())));

    return resetMethod;
  }

  public ASTCDConstructor createConstructor(ASTCDMethod resetMethod, String name) {
    ASTCDConstructor constructor = CD4CodeMill.cDConstructorBuilder()
            .setModifier(PUBLIC.build())
            .setName(name)
            .build();
    replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint(resetMethod.getName() + "();"));
    return constructor;
  }

  public ASTCDInterfaceUsage getInterfaceUsage() {
    return CDInterfaceUsageFacade.getInstance()
            .createCDInterfaceUsage(visitorService.getTraverserInterfaceFullName());
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

  public List<ASTCDMember> createIsASTMethods() {
    List<ASTCDMember> methods = new ArrayList<>();

    for(CDTypeSymbol typeSymbol: visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      String name = typeSymbol.getName();

      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
              .setModifier(PUBLIC.build())
              .setName("is"+name)
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

  public List<ASTCDMember> createAsASTMethods() {
    List<ASTCDMember> methods = new ArrayList<>();

    for(CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      String name = typeSymbol.getName();

      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
              .setModifier(PUBLIC.build())
              .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                      .setMCType(MCTypeFacade.getInstance()
                              .createQualifiedType(visitorService.createASTFullName(typeSymbol)))
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
    for(CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      List<String> superTypes = typeSymbol.getSuperTypesList()
              .stream()
              .filter(s -> ((TypeSymbolSurrogate)s.getTypeInfo()).checkLazyLoadDelegate())
              .map(s -> ((TypeSymbolSurrogate)s.getTypeInfo()).lazyLoadDelegate())
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

  public String getInheritanceVisitorName(String name) {
    return name + "Dispatcher";
  }

}
