package de.monticore.codegen.cd2java.builder.buildermethods;

import de.monticore.codegen.cd2java.methods.mutator.ListMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class BuilderListMutatorDecorator extends ListMutatorDecorator {

  private final ASTType builderType;

  public BuilderListMutatorDecorator(GlobalExtensionManagement glex, final ASTType builderType) {
    super(glex);
    this.builderType = builderType;
  }

  @Override
  protected List<ASTCDMethod> createSetter(ASTCDAttribute attribute){
    disableTemplates();
    List<ASTCDMethod> methods = super.createSetter(attribute);
    enableTemplates();
    for (ASTCDMethod m : methods) {
      m.setReturnType(builderType);
      String methodName = m.getName().substring(0, m.getName().length() - attribute.getName().length());
      String parameterCall = m.getCDParameterList().stream()
          .map(ASTCDParameter::getName)
          .collect(Collectors.joining(", "));
      this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("builder.MethodDelegate", attribute.getName(), methodName, parameterCall));
    }
    return methods;
  }

  @Override
  protected ASTCDMethod createSetListMethod(ASTCDAttribute ast) {
    String signature = String.format(SET_LIST, capitalizedAttributeName, attributeType);
    ASTCDMethod method = this.getCDMethodFacade().createMethodByDefinition(signature);
    method.setReturnType(builderType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("builder.Set", ast));
    return method;
  }
}
