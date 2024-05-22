/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._ast.builder.BuilderConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ASTEvaluateDecorator extends AbstractCreator<ASTCDType, List<ASTCDMethod>> {

  protected final VisitorService visitorService;

  public ASTEvaluateDecorator(GlobalExtensionManagement glex,
                              VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astPackage = getPackage(input, decoratedCD, ASTConstants.AST_PACKAGE);
    astPackage.streamCDElements()
        .filter(e -> e instanceof ASTCDType)
        .map(e -> (ASTCDType) e)
        .forEach(t -> t.addAllCDMembers(decorate(t)));
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDType input) {
    if (input.getName().endsWith(BuilderConstants.BUILDER_SUFFIX) ||
        input.getName().startsWith("ASTConstants") ||
        input.getName().endsWith(ASTConstants.NODE_SUFFIX) ||
        input.getName().endsWith("Literals")) {
      return new ArrayList<>();
    }

    List<ASTCDMethod> result = new ArrayList<>();
    result.add(createEvaluateInterpreterMethod(input));
    result.addAll(createEvaluateInterpreterSuperMethod(input));
    return result;
  }

  protected ASTCDMethod createEvaluateInterpreterMethod(ASTCDType cdType) {
    ASTCDParameter parameter = cdParameterFacade.createParameter(
        visitorService.getInterpreterInterfaceFullName(), "interpreter");
    String astName = cdType.getName().endsWith("TOP")
        ? cdType.getName().substring(0, cdType.getName().length() - 3)
        : cdType.getName();
    ASTCDMethod method = getCDMethodFacade().createMethod(
        PUBLIC.build(), InterpreterConstants.VALUE_FULLNAME, "evaluate",
        parameter);
    replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint(String.format("return interpreter.interpret((%s)this);", astName)));
    return method;
  }


  protected List<ASTCDMethod> createEvaluateInterpreterSuperMethod(ASTCDType cdType) {
    List<ASTCDMethod> methods = new ArrayList<>();
    List<ASTMCQualifiedType> types = visitorService.getAllInterpreterInterfacesTypesInHierarchy();

    types.remove(types.size() - 1);

    types.add(mcTypeFacade.createQualifiedType(InterpreterConstants.MODELINTERPRETER_FULLNAME));

    for (ASTMCQualifiedType type : types) {
      ASTCDParameter parameter = cdParameterFacade.createParameter(type, "interpreter");
      ASTCDMethod method = cdMethodFacade.createMethod(
          PUBLIC.build(), InterpreterConstants.VALUE_FULLNAME, "evaluate", parameter);
      replaceTemplate(EMPTY_BODY, method,
          new TemplateHookPoint("_ast.ast_class.Evaluate",
              cdType, visitorService.getInterpreterInterfaceFullName()));
      methods.add(method);
    }
    return methods;
  }

}
