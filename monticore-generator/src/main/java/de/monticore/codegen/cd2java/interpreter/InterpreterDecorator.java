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
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.interpreter.InterpreterConstants.*;

public class InterpreterDecorator
    extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService service;

  public InterpreterDecorator(GlobalExtensionManagement glex,
                              VisitorService service) {
    super(glex);
    this.service = service;
  }

  public void decorate(ASTCDCompilationUnit input,
                       ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(
        input, decoratedCD, VisitorConstants.VISITOR_PACKAGE);
    visitorPackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    return CD4CodeMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(service.getInterpreterSimpleName())
        .setCDInterfaceUsage(getSuperInterface())
        .addAllCDMembers(getInterpreterAttributes())
        .addAllCDMembers(getConstructors())
        .addAllCDMembers(getRealThisComponents())
        .addAllCDMembers(createMapMembers())
        .addAllCDMembers(getInterpretMethods())
        .build();
  }

  public List<ASTCDConstructor> getConstructors() {
    ASTCDParameter parameter = cdParameterFacade.createParameter(
        MODELINTERPRETER_FULLNAME, "realThis");

    String interpreterName = service.getInterpreterSimpleName();
    ASTCDConstructor constructorNoParams = cdConstructorFacade
        .createConstructor(PUBLIC.build(), interpreterName);
    ASTCDConstructor constructorRealThis = cdConstructorFacade
        .createConstructor(PUBLIC.build(), interpreterName, parameter);

    List<String> names = new ArrayList<>();
    List<String> types = new ArrayList<>();

    for (DiagramSymbol symbol : service.getSuperCDsTransitive()) {
      names.add(service.getInterpreterSimpleName(symbol));
      types.add(service.getInterpreterFullName(symbol));
    }

    replaceTemplate(EMPTY_BODY, constructorRealThis,
        new StringHookPoint("this.setRealThis(realThis);"));
    replaceTemplate(EMPTY_BODY, constructorNoParams,
        new TemplateHookPoint("interpreter.ConstructorNoParams",
            names, types));

    return List.of(constructorNoParams, constructorRealThis);
  }

  public List<ASTCDMethod> getInterpretMethods() {
    List<ASTCDMethod> methods = new ArrayList<>();
    ASTMCReturnType returnType = CD4CodeMill.mCReturnTypeBuilder()
        .setMCType(mcTypeFacade.createQualifiedType(VALUE_FULLNAME)).build();

    for (CDTypeSymbol typeSymbol : service.getAllCDTypes()) {
      if (typeSymbol.isIsClass() || typeSymbol.isIsInterface()) {
        ASTCDParameter parameter = cdParameterFacade
            .createParameter(service.createASTFullName(typeSymbol), NODE_PARAMETER);
        ASTCDMethod method = cdMethodFacade.createMethod(
            PUBLIC.build(), returnType, "interpret", parameter);
        this.replaceTemplate(
            EMPTY_BODY, method, new StringHookPoint("return node.evaluate(getRealThis());"));
        methods.add(method);
      }
    }

    for (DiagramSymbol diagramSymbol : service.getSuperCDsTransitive()) {
      if (diagramSymbol != service.getCDSymbol()) {
        String interpreterName = uncapFirst(service.getInterpreterSimpleName(diagramSymbol));
        for (CDTypeSymbol typeSymbol : service.getAllCDTypes(diagramSymbol)) {
          if (typeSymbol.isIsClass() || typeSymbol.isIsInterface()) {
            ASTCDParameter parameter = cdParameterFacade
                .createParameter(service.createASTFullName(typeSymbol), NODE_PARAMETER);
            ASTCDMethod method = cdMethodFacade.createMethod(
                PUBLIC.build(), returnType, "interpret", parameter);
            this.replaceTemplate(
                EMPTY_BODY, method, new StringHookPoint(
                    String.format("return %s.interpret(node);",
                        interpreterName)));
            methods.add(method);
          }
        }
      }
    }

    return methods;
  }

  public List<ASTCDMember> createMapMembers() {
    List<ASTCDMember> members = new ArrayList<>();
    
    members.add(cdAttributeFacade.createAttribute(
        PROTECTED.build(),
        mcTypeFacade.createBasicGenericTypeOf("java.util.Stack", INTERPRETER_SCOPE_FULLNAME),
        "scopeCallstack"));

    ASTCDParameter variableSymbolParameter = cdParameterFacade.createParameter(VARIABLE_SYMBOL_FULLNAME, "symbol");
    ASTCDParameter functionSymbolParameter = cdParameterFacade.createParameter(FUNCTION_SYMBOL_FULLNAME, "symbol");
    ASTCDParameter valueParameter = cdParameterFacade.createParameter(VALUE_FULLNAME, "value");
    ASTCDParameter functionValueParameter = cdParameterFacade.createParameter(FUNCTION_VALUE_FULLNAME, "value");
    
    ASTCDMethod declareFuncMethod = cdMethodFacade.createMethod(
        PUBLIC.build(), "declareFunction", functionSymbolParameter, functionValueParameter);
    this.replaceTemplate(EMPTY_BODY, declareFuncMethod, new StringHookPoint("getRealThis().getCurrentScope().declareFunction(symbol, value);"));
    members.add(declareFuncMethod);
    
    ASTCDMethod loadFuncMethod = cdMethodFacade.createMethod(PUBLIC.build(), VALUE_FULLNAME, "loadFunction", functionSymbolParameter);
    this.replaceTemplate(EMPTY_BODY, loadFuncMethod, new StringHookPoint("return getRealThis().getCurrentScope().loadFunction(symbol);"));
    members.add(loadFuncMethod);
    
    ASTCDMethod declareVarMethod = cdMethodFacade.createMethod(
        PUBLIC.build(), "declareVariable", variableSymbolParameter, valueParameter);
    this.replaceTemplate(EMPTY_BODY, declareVarMethod, new StringHookPoint("getRealThis().getCurrentScope().declareVariable(symbol, value);"));
    members.add(declareVarMethod);
    
    ASTCDMethod loadVarMethod = cdMethodFacade.createMethod(PUBLIC.build(), VALUE_FULLNAME, "loadVariable", variableSymbolParameter);
    this.replaceTemplate(EMPTY_BODY, loadVarMethod, new StringHookPoint("return getRealThis().getCurrentScope().loadVariable(symbol);"));
    members.add(loadVarMethod);
    
    ASTCDMethod storeVarMethod = cdMethodFacade.createMethod(
        PUBLIC.build(), "storeVariable", variableSymbolParameter, valueParameter);
    this.replaceTemplate(EMPTY_BODY, storeVarMethod, new StringHookPoint("getRealThis().getCurrentScope().storeVariable(symbol, value);"));
    members.add(storeVarMethod);

    ASTCDMethod getter = cdMethodFacade.createMethod(
        PUBLIC.build(),
        mcTypeFacade.createQualifiedType(INTERPRETER_SCOPE_FULLNAME),
        "getCurrentScope");
    this.replaceTemplate(EMPTY_BODY, getter, new StringHookPoint("return this.scopeCallstack.peek();"));
    members.add(getter);
    
    ASTCDParameter scopeParameter = cdParameterFacade.createParameter(INTERPRETER_SCOPE_FULLNAME, "scope");
    ASTCDMethod pushScopeMethod = cdMethodFacade.createMethod(PUBLIC.build(), "pushScope", scopeParameter);
    this.replaceTemplate(EMPTY_BODY, pushScopeMethod, new StringHookPoint("this.scopeCallstack.push(scope);"));
    members.add(pushScopeMethod);
    
    ASTCDMethod popScopeMethod = cdMethodFacade.createMethod(PUBLIC.build(), "popScope");
    this.replaceTemplate(EMPTY_BODY, popScopeMethod, new StringHookPoint("this.scopeCallstack.pop();"));
    members.add(popScopeMethod);

    return members;
  }

  public List<ASTCDMember> getRealThisComponents() {
    List<ASTCDMember> components = new ArrayList<>();

    ASTCDAttribute realThisAttribute = cdAttributeFacade
        .createAttribute(
            PROTECTED.build(),
            mcTypeFacade.createQualifiedType(MODELINTERPRETER_FULLNAME),
            "realThis");
    components.add(realThisAttribute);

    MethodDecorator methodDecorator = new MethodDecorator(glex, service);
    components.addAll(methodDecorator.decorate(realThisAttribute));

    return components;
  }

  public List<ASTCDAttribute> getInterpreterAttributes() {
    return service.getSuperCDsTransitive()
        .stream()
        .map(s -> cdAttributeFacade.createAttribute(
            PROTECTED.build(), service.getInterpreterType(s),
            uncapFirst(service.getInterpreterSimpleName(s))))
        .collect(Collectors.toList());
  }

  public ASTCDInterfaceUsage getSuperInterface() {
    return CDInterfaceUsageFacade.getInstance()
        .createCDInterfaceUsage(service.getInterpreterInterfaceSimpleName());
  }

  protected String uncapFirst(String s) {
    return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
  }
}
