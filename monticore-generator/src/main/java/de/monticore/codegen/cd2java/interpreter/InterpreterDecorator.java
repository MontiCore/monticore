/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd.facade.*;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.interpreter.InterpreterConstants.*;

public class InterpreterDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService service;
  private final MCTypeFacade typeFacade;

  public InterpreterDecorator(GlobalExtensionManagement glex, VisitorService service) {
    super(glex);
    this.service = service;
    typeFacade = MCTypeFacade.getInstance();
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(input, decoratedCD, VisitorConstants.VISITOR_PACKAGE);
    visitorPackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    ASTCDInterfaceUsage interfaceUsage = getSuperInterface();
    List<ASTCDAttribute> superInterpreters = getInterpreterAttributes();
    List<ASTCDConstructor> constructors = getConstructors(superInterpreters);
    List<ASTCDMember> realThisComponents = getRealThisComponents();
    List<ASTCDMethod> interpretMethods = getInterpretMethods();

    return CD4CodeMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(service.getInterpreterSimpleName())
        .setCDInterfaceUsage(interfaceUsage)
        .addAllCDMembers(superInterpreters)
        .addAllCDMembers(constructors)
        .addAllCDMembers(realThisComponents)
        .addAllCDMembers(interpretMethods)
        .build();
  }

  public List<ASTCDConstructor> getConstructors(List<ASTCDAttribute> superInterpreters) {
    ASTCDParameter parameter = CDParameterFacade.getInstance().createParameter(
        typeFacade.createQualifiedType(MODELINTERPRETER_FULLNAME), "realThis");

    String interpreterName = service.getInterpreterSimpleName();
    ASTCDConstructor constructorNoParams = CDConstructorFacade.getInstance().createConstructor(PUBLIC.build(), interpreterName);
    ASTCDConstructor constructorRealThis = CDConstructorFacade.getInstance().createConstructor(PUBLIC.build(), interpreterName, parameter);

    List<String> names = superInterpreters.stream().map(ASTCDAttribute::getName).collect(Collectors.toList());
    List<String> types = superInterpreters.stream().map(a -> a.getMCType().printType()).collect(Collectors.toList());

    replaceTemplate(EMPTY_BODY, constructorRealThis, new StringHookPoint("this.setRealThis(realThis);"));
    replaceTemplate(EMPTY_BODY, constructorNoParams, new TemplateHookPoint("interpreter.ConstructorNoParams", names, types));

    return List.of(constructorNoParams, constructorRealThis);
  }

  public List<ASTCDMethod> getInterpretMethods() {
    List<ASTCDParameter> parameters = new ArrayList<>();

    for (CDTypeSymbol typeSymbol: service.getAllCDTypes()) {
      parameters.add(CDParameterFacade.getInstance()
          .createParameter(service.createASTFullName(typeSymbol), NODE_PARAMETER));
    }

    for (DiagramSymbol diagramSymbol: service.getSuperCDsTransitive()) {
      for (CDTypeSymbol typeSymbol : service.getAllCDTypes(diagramSymbol)) {
        parameters.add(CDParameterFacade.getInstance()
            .createParameter(service.createASTFullName(typeSymbol), NODE_PARAMETER));
      }
    }

    parameters.add(CDParameterFacade.getInstance()
        .createParameter(typeFacade.
            createQualifiedType(NODE_TYPE), NODE_PARAMETER));

    ASTMCReturnType returnType = CD4CodeMill.mCReturnTypeBuilder()
        .setMCType(typeFacade.createQualifiedType(VALUE_FULLNAME)).build();

    List<ASTCDMethod> methods = parameters.stream()
        .map(parameter -> CDMethodFacade.getInstance().createMethod(
            PUBLIC.build(), returnType, "interpret", parameter))
        .collect(Collectors.toList());

    methods.forEach(method ->
        this.replaceTemplate(
            EMPTY_BODY, method, new StringHookPoint(
                "return new " + NOT_A_VALUE_FULLNAME + "();")));

    return methods;
  }

  public List<ASTCDMember> getRealThisComponents() {
    List<ASTCDMember> components = new ArrayList<>();

    ASTCDAttribute realThisAttribute = CDAttributeFacade.getInstance()
        .createAttribute(
            PROTECTED.build(),
            typeFacade.createQualifiedType(MODELINTERPRETER_FULLNAME),
            "realThis");
    components.add(realThisAttribute);

    MethodDecorator methodDecorator = new MethodDecorator(glex, service);
    components.addAll(methodDecorator.decorate(realThisAttribute));

    return components;
  }

  public List<ASTCDAttribute> getInterpreterAttributes() {
    List<ASTCDAttribute> interpreters = new ArrayList<>();
    for (DiagramSymbol symbol : service.getSuperCDsTransitive()) {
      interpreters.add(CDAttributeFacade.getInstance().createAttribute(
          PROTECTED.build(),
          typeFacade.createQualifiedType(service.getInterpreterFullName(symbol)),
          uncapFirst(service.getInterpreterSimpleName(symbol))));
    }
    return interpreters;
  }

  public ASTCDInterfaceUsage getSuperInterface() {
    return CDInterfaceUsageFacade.getInstance()
        .createCDInterfaceUsage(
            MODELINTERPRETER_FULLNAME,
            service.getInterpreterInterfaceFullName());
  }

  protected String uncapFirst(String s) {
    return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
  }
}
