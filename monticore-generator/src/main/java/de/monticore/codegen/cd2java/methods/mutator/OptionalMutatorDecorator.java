package de.monticore.codegen.cd2java.methods.mutator;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class OptionalMutatorDecorator implements Decorator<ASTCDAttribute, List<ASTCDMethod>> {

  private static final String SET = "set%s";

  private static final String SET_OPT = "set%sOpt";

  private static final String SET_ABSENT = "setAbsent%s";

  private final GlobalExtensionManagement glex;

  private final CDMethodFactory cdMethodFactory;

  private final CDParameterFactory cdParameterFactory;

  public OptionalMutatorDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdMethodFactory = CDMethodFactory.getInstance();
    this.cdParameterFactory = CDParameterFactory.getInstance();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    ASTCDMethod set = createSetMethod(ast);
    ASTCDMethod setOpt = createSetOptMethod(ast);
    ASTCDMethod setAbsent = createSetAbsentMethod(ast);
    return Arrays.asList(set, setOpt, setAbsent);
  }

  private ASTCDMethod createSetMethod(final ASTCDAttribute ast) {
    String name = String.format(SET, StringUtils.capitalize(ast.getName()));
    ASTType parameterType = TypesHelper.getSimpleReferenceTypeFromOptional(ast.getType()).deepClone();
    ASTCDParameter parameter = this.cdParameterFactory.createParameter(parameterType, ast.getName());
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, name, parameter);
    this.glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.Set", ast));
    return method;
  }

  private ASTCDMethod createSetOptMethod(final ASTCDAttribute ast) {
    String name = String.format(SET_OPT, StringUtils.capitalize(ast.getName()));
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, name, this.cdParameterFactory.createParameters(ast));
    this.glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Set", ast));
    return method;
  }

  private ASTCDMethod createSetAbsentMethod(final ASTCDAttribute ast) {
    String name = String.format(SET_ABSENT, StringUtils.capitalize(ast.getName()));
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, name);
    this.glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.SetAbsent", ast));
    return method;
  }
}
