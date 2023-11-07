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
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.interpreter.InterpreterConstants.*;

public class InterpreterDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService visitorService;
  private final MCTypeFacade typeFacade;

  public InterpreterDecorator(GlobalExtensionManagement glex, VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
    typeFacade = MCTypeFacade.getInstance();
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(input, decoratedCD, VisitorConstants.VISITOR_PACKAGE);
    visitorPackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String interpreterName = getInterpreterName(visitorService.getCDName());
    ASTCDInterfaceUsage interfaceUsage = getSuperInterface();
    List<ASTCDAttribute> superInterpreters = getInterpreterAttributes();
    List<ASTCDMember> realThisComponents = getRealThisComponents();
    ASTCDMethod interpretMethod = getInterpretMethod();
    List<ASTCDMethod> interpretMethods = getInterpretMethods();
    List<ASTCDConstructor> constructors = getConstructors(interpreterName, superInterpreters);

    return CD4CodeMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(interpreterName)
        .setCDInterfaceUsage(interfaceUsage)
        .addAllCDMembers(superInterpreters)
        .addAllCDMembers(constructors)
        .addAllCDMembers(realThisComponents)
        .addCDMember(interpretMethod)
        .addAllCDMembers(interpretMethods)
        .build();
  }

  public List<ASTCDConstructor> getConstructors(String interpreterName, List<ASTCDAttribute> superInterpreters) {
    ASTCDParameter parameter = CDParameterFacade.getInstance().createParameter(
        typeFacade.createQualifiedType(MODELINTERPRETER_FULLNAME),
        "realThis");

    ASTCDConstructor constructorNoParams = CDConstructorFacade.getInstance().createConstructor(PUBLIC.build(), interpreterName);
    ASTCDConstructor constructorRealThis = CDConstructorFacade.getInstance().createConstructor(PUBLIC.build(), interpreterName, parameter);

    List<String> names = superInterpreters.stream().map(ASTCDAttribute::getName).collect(Collectors.toList());
    List<String> types = superInterpreters.stream().map(a -> a.getMCType().printType()).collect(Collectors.toList());

    replaceTemplate(EMPTY_BODY, constructorRealThis, new TemplateHookPoint("interpreter.ConstructorRealThis"));
    replaceTemplate(EMPTY_BODY, constructorNoParams, new TemplateHookPoint("interpreter.ConstructorNoParams", names, types));

    return List.of(constructorNoParams, constructorRealThis);
  }

  public List<ASTCDMethod> getInterpretMethods() {
    List<ASTCDMethod> methods = new ArrayList<>();
    for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(visitorService.getCDSymbol())) {
      List<String> superTypes = typeSymbol.getSuperTypesList()
          .stream()
          .filter(s -> ((TypeSymbolSurrogate) s.getTypeInfo()).checkLazyLoadDelegate())
          .map(s -> ((TypeSymbolSurrogate) s.getTypeInfo()).lazyLoadDelegate())
          .filter(t -> t instanceof CDTypeSymbol)
          .map(s -> visitorService.createASTFullName((CDTypeSymbol) s))
          .collect(Collectors.toList());

      methods.add(createInterpretMethod(typeSymbol, superTypes));
    }

    return methods;
  }

  public ASTCDMethod createInterpretMethod(CDTypeSymbol typeSymbol, List<String> superTypes) {
    ASTMCReturnType returnType = CD4CodeMill.mCReturnTypeBuilder()
        .setMCType(typeFacade.createQualifiedType(VALUE_FULLNAME)).build();
    ASTCDParameter parameter = CDParameterFacade.getInstance()
        .createParameter(visitorService.createASTFullName(typeSymbol), NODE_PARAMETER);

    ASTCDMethod method = CDMethodFacade.getInstance().createMethod(
        PUBLIC.build(),
        returnType,
        "interpret",
        List.of(parameter));

    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return null;"));

    return method;
  }

  public ASTCDMethod getInterpretMethod() {
    ASTMCReturnType returnType = CD4CodeMill.mCReturnTypeBuilder()
        .setMCType(typeFacade.createQualifiedType(VALUE_FULLNAME))
        .build();
    ASTCDParameter parameter = CDParameterFacade.getInstance()
        .createParameter(typeFacade.createQualifiedType(NODE_TYPE), "node");

    ASTCDMethod method = CDMethodFacade.getInstance().createMethod(
        PUBLIC.build(),
        returnType,
        "interpret",
        List.of(parameter));

    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return null;"));

    return method;
  }

  public List<ASTCDMember> getRealThisComponents() {
    List<ASTCDMember> components = new ArrayList<>();

    ASTCDAttribute realThisAttribute = CDAttributeFacade.getInstance()
        .createAttribute(
            PROTECTED.build(),
            typeFacade.createQualifiedType(MODELINTERPRETER_FULLNAME),
            "realThis");
    components.add(realThisAttribute);

    MethodDecorator methodDecorator = new MethodDecorator(glex, visitorService);
    components.addAll(methodDecorator.decorate(realThisAttribute));

    return components;
  }

  public List<ASTCDAttribute> getInterpreterAttributes() {
    List<ASTCDAttribute> interpreters = new ArrayList<>();
    for (DiagramSymbol symbol : visitorService.getSuperCDsTransitive()) {
      String interpreterName = getInterpreterName(symbol.getName());
      String packageName = symbol.getFullName().toLowerCase() + "." + VisitorConstants.VISITOR_PACKAGE;

      interpreters.add(CDAttributeFacade.getInstance().createAttribute(
          PROTECTED.build(),
          typeFacade.createQualifiedType(packageName + "." + interpreterName),
          uncapFirst(interpreterName)));
    }
    return interpreters;
  }

  public ASTCDInterfaceUsage getSuperInterface() {
    ASTCDInterfaceUsage interfaceUsage = CDInterfaceUsageFacade.getInstance()
        .createCDInterfaceUsage(MODELINTERPRETER_FULLNAME);
    interfaceUsage.addInterface(typeFacade.createQualifiedType(visitorService.getInterpreterInterfaceFullName()));
    return interfaceUsage;
  }

  public String getInterpreterName(String grammarName) {
    return grammarName + INTERPRETER_NAME_SUFFIX;
  }

  protected String uncapFirst(String s) {
    return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
  }
}
