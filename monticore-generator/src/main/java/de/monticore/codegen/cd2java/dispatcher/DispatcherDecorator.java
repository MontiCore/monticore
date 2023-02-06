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
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.ArrayList;
import java.util.List;

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
    List<ASTCDAttribute> booleanAttributes = createBooleanAttributes();
    List<ASTCDAttribute> optionalAttributes = createOptionalAttributes();
    ASTCDMethod resetMethod = createResetMethod(booleanAttributes, optionalAttributes);
    ASTCDConstructor constructor = createConstructor(resetMethod, visitorName);
    List<ASTCDMember> traverserElements = createTraverserElements();
    List<ASTCDMember> methods = createIsASTMethods(booleanAttributes);
    methods.addAll(createAsASTMethods(booleanAttributes, optionalAttributes));
    methods.addAll(createHandleMethods(booleanAttributes, optionalAttributes));

    return CD4CodeMill.cDClassBuilder()
            .setName(visitorName)
            .setModifier(PUBLIC.build())
            .setCDInterfaceUsage(traverserInterface)
            .addAllCDMembers(booleanAttributes)
            .addAllCDMembers(optionalAttributes)
            .addCDMember(resetMethod)
            .addCDMember(constructor)
            .addAllCDMembers(traverserElements)
            .addAllCDMembers(methods)
            .build();
  }

  public List<ASTCDAttribute> createBooleanAttributes() {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for(CDTypeSymbol typeSymbol: visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      attributes.add(CD4CodeMill.cDAttributeBuilder()
              .setModifier(PROTECTED.build())
              .setMCType(MCTypeFacade.getInstance().createBooleanType())
              .setName("is" + typeSymbol.getName())
              .build());
    }
    return attributes;
  }

  public List<ASTCDAttribute> createOptionalAttributes() {

    List<ASTCDAttribute> attributes = new ArrayList<>();
    for(CDTypeSymbol typeSymbol: visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
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

  private ASTCDMethod createResetMethod(List<ASTCDAttribute> booleanAttributes, List<ASTCDAttribute> optionalAttributes) {
    ASTCDMethod resetMethod = CD4CodeMill.cDMethodBuilder()
            .setModifier(PUBLIC.build())
            .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                    .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
                    .build())
            .setName("reset")
            .build();
    replaceTemplate(EMPTY_BODY, resetMethod,
            new TemplateHookPoint("dispatcher.Reset", booleanAttributes, optionalAttributes));

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

  public List<ASTCDMember> createIsASTMethods(List<ASTCDAttribute> attributes) {
    List<ASTCDMember> methods = new ArrayList<>();

    for(ASTCDAttribute attribute: attributes) {
      String name = attribute.getName();

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

  public List<ASTCDMember> createAsASTMethods(List<ASTCDAttribute> booleans, List<ASTCDAttribute> optionals) {
    List<ASTCDMember> methods = new ArrayList<>();

    for(int i = 0; i < booleans.size(); ++i) {
      String name = booleans.get(i).getName();

      String returnType = optionals.get(i).getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));

      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
              .setModifier(PUBLIC.build())
              .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                      .setMCType(MCTypeFacade.getInstance()
                              .createQualifiedType(returnType))
                      .build())
              .setName("a" + name.substring(1))
              .addCDParameter(CD4CodeMill.cDParameterBuilder()
                      .setMCType(MCTypeFacade.getInstance().createQualifiedType("de.monticore.ast.ASTNode"))
                      .setName("node")
                      .build())
              .build();

      replaceTemplate(EMPTY_BODY, method,
              new TemplateHookPoint("dispatcher.AsAST", name.substring(2)));

      methods.add(method);
    }
    return methods;
  }

  public List<ASTCDMember> createHandleMethods(List<ASTCDAttribute> booleans, List<ASTCDAttribute> optionals) {
    List<ASTCDMember> methods = new ArrayList<>();
    for(int i = 0; i < booleans.size(); ++i) {

      String parameterType = optionals.get(i).getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));

      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
              .setModifier(PUBLIC.build())
              .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder()
                      .setMCVoidType(MCTypeFacade.getInstance().createVoidType())
                      .build())
              .setName("handle")
              .addCDParameter(CD4CodeMill.cDParameterBuilder().setMCType(MCTypeFacade.getInstance().createQualifiedType(parameterType))
                      .setName("node")
                      .build())
              .build();

      replaceTemplate(EMPTY_BODY, method,
              new TemplateHookPoint("dispatcher.Handle", booleans.get(i).getName(), optionals.get(i).getName()));

      methods.add(method);
    }
    return methods;
  }

  public String getInheritanceVisitorName(String name) {
    return name + "Dispatcher";
  }

}
