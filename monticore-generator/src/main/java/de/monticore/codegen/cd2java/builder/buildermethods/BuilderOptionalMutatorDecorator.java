package de.monticore.codegen.cd2java.builder.buildermethods;

import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;


public class BuilderOptionalMutatorDecorator extends OptionalMutatorDecorator {

  private final ASTType builderType;

  public BuilderOptionalMutatorDecorator(final GlobalExtensionManagement glex,
                                         final ASTType builderType) {
    super(glex);
    this.builderType = builderType;
  }

  @Override
  protected ASTCDMethod createSetMethod(final ASTCDAttribute attribute) {
    String name = String.format(SET, naiveAttributeName);
    ASTType parameterType = TypesHelper.getSimpleReferenceTypeFromOptional(attribute.getType()).deepClone();
    ASTCDParameter parameter = this.getCDParameterFacade().createParameter(parameterType, attribute.getName());
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, name, parameter);
    method.setReturnType(this.builderType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("builder.opt.Set", attribute));
    return method;
  }


  @Override
  protected ASTCDMethod createSetOptMethod(final ASTCDAttribute attribute) {
    String name = String.format(SET_OPT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, name, this.getCDParameterFacade().createParameters(attribute));
    method.setReturnType(this.builderType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("builder.Set", attribute));
    return method;
  }


  @Override
  protected ASTCDMethod createSetAbsentMethod(final ASTCDAttribute attribute) {
    String name = String.format(SET_ABSENT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, name);
    method.setReturnType(this.builderType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("builder.opt.SetAbsent", attribute));
    return method;
  }
}
