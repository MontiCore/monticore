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
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.interpreter.InterpreterConstants.*;

public class InterpreterDecorator
    extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {
  
  protected final ASTService astService;

  protected final VisitorService visitorService;

  public InterpreterDecorator(GlobalExtensionManagement glex,
                              ASTService astService,
                              VisitorService visitorService) {
    super(glex);
    this.astService = astService;
    this.visitorService = visitorService;
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
        .setName(visitorService.getInterpreterSimpleName())
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

    String interpreterName = visitorService.getInterpreterSimpleName();
    ASTCDConstructor constructorNoParams = cdConstructorFacade
        .createConstructor(PUBLIC.build(), interpreterName);
    ASTCDConstructor constructorRealThis = cdConstructorFacade
        .createConstructor(PUBLIC.build(), interpreterName, parameter);

    List<String> names = new ArrayList<>();
    List<String> types = new ArrayList<>();

    for (DiagramSymbol symbol : visitorService.getSuperCDsTransitive()) {
      names.add(visitorService.getInterpreterSimpleName(symbol));
      types.add(visitorService.getInterpreterFullName(symbol));
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

    for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes()) {
      if (typeSymbol.isIsClass() || typeSymbol.isIsInterface()) {
        ASTCDParameter parameter = cdParameterFacade
            .createParameter(visitorService.createASTFullName(typeSymbol), NODE_PARAMETER);
        ASTCDMethod method = cdMethodFacade.createMethod(
            PUBLIC.build(), returnType, "interpret", parameter);
        
        String errorCode = astService.getGeneratedErrorCode(typeSymbol.getFullName());
        
        this.replaceTemplate(EMPTY_BODY, method,
            new TemplateHookPoint("interpreter.NoImplementation",
                typeSymbol.getFullName(), errorCode));
        methods.add(method);
      }
    }

    for (DiagramSymbol diagramSymbol : visitorService.getSuperCDsTransitive()) {
      if (diagramSymbol != visitorService.getCDSymbol()) {
        String interpreterName = uncapFirst(visitorService.getInterpreterSimpleName(diagramSymbol));
        for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(diagramSymbol)) {
          if (typeSymbol.isIsClass() || typeSymbol.isIsInterface()) {
            ASTCDParameter parameter = cdParameterFacade
                .createParameter(visitorService.createASTFullName(typeSymbol), NODE_PARAMETER);
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
    
    ASTMCType scopeStackType = mcTypeFacade.createBasicGenericTypeOf("java.util.Stack", INTERPRETER_SCOPE_FULLNAME);
    
    members.add(cdAttributeFacade.createAttribute(PROTECTED.build(), scopeStackType, "scopeCallstack"));

    ASTCDMethod getScopeCallstackMethod = cdMethodFacade.createMethod(PUBLIC.build(), scopeStackType,
            "getScopeCallstack");
    this.replaceTemplate(EMPTY_BODY, getScopeCallstackMethod, new StringHookPoint("return scopeCallstack;"));
    members.add(getScopeCallstackMethod);

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

    MethodDecorator methodDecorator = new MethodDecorator(glex, visitorService);
    components.addAll(methodDecorator.decorate(realThisAttribute));

    return components;
  }

  public List<ASTCDAttribute> getInterpreterAttributes() {
    return visitorService.getSuperCDsTransitive()
        .stream()
        .map(s -> cdAttributeFacade.createAttribute(
            PROTECTED.build(), visitorService.getInterpreterType(s),
            uncapFirst(visitorService.getInterpreterSimpleName(s))))
        .collect(Collectors.toList());
  }

  public ASTCDInterfaceUsage getSuperInterface() {
    return CDInterfaceUsageFacade.getInstance()
        .createCDInterfaceUsage(visitorService.getInterpreterInterfaceSimpleName());
  }

  protected String uncapFirst(String s) {
    return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
  }
}
