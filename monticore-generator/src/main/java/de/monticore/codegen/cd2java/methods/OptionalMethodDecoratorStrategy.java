package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDTypeFactory.BOOLEAN_TYPE;

public class OptionalMethodDecoratorStrategy implements MethodDecoratorStrategy {

  private static final String GET_PREFIX = "get";

  private static final String SET_PREFIX = "set";

  private static final String OPT_SUFFIX = "Opt";

  private static final String IS_PRESENT_PREFIX = "isPresent";

  private static final String SET_ABSENT_PREFIX = "setAbsent";

  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDMethodFactory cdMethodFactory;

  private final CDParameterFactory cdParameterFactory;

  protected OptionalMethodDecoratorStrategy(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdMethodFactory = CDMethodFactory.getInstance();
    this.cdParameterFactory = CDParameterFactory.getInstance();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    ASTCDMethod get = createGetMethod(ast);
    ASTCDMethod getOpt = createGetOptMethod(ast);
    ASTCDMethod isPresent = createIsPresentMethod(ast);
    ASTCDMethod set = createSetMethod(ast);
    ASTCDMethod setOpt = createSetOptMethod(ast);
    ASTCDMethod setAbsent = createSetAbsentMethod(ast);
    return Arrays.asList(get, getOpt, isPresent, set, setOpt, setAbsent);
  }

  private ASTCDMethod createGetMethod(final ASTCDAttribute ast) {
    String name = GET_PREFIX + StringUtils.capitalize(ast.getName());
    ASTType type = TypesHelper.getSimpleReferenceTypeFromOptional(ast.getType().deepClone());
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, type, name);
    this.glex.replaceTemplate(EMPTY_BODY, method, createGetImplementation(ast));
    return method;
  }

  private HookPoint createGetImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("methods.opt.Get", ast);
  }

  private ASTCDMethod createGetOptMethod(final ASTCDAttribute ast) {
    String name = GET_PREFIX + StringUtils.capitalize(ast.getName()) + OPT_SUFFIX;
    ASTType type = ast.getType().deepClone();
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, type, name);
    this.glex.replaceTemplate(EMPTY_BODY, method, createGetOptImplementation(ast));
    return method;
  }

  private HookPoint createGetOptImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("methods.Get", ast);
  }

  private ASTCDMethod createIsPresentMethod(final ASTCDAttribute ast) {
    String name = IS_PRESENT_PREFIX + StringUtils.capitalize(ast.getName());
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, BOOLEAN_TYPE, name);
    this.glex.replaceTemplate(EMPTY_BODY, method, createIsPresentImplementation(ast));
    return method;
  }

  private HookPoint createIsPresentImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("methods.opt.IsPresent", ast);
  }



  protected ASTCDMethod createSetMethod(final ASTCDAttribute ast) {
    String name = SET_PREFIX + StringUtils.capitalize(ast.getName());
    ASTType parameterType = TypesHelper.getSimpleReferenceTypeFromOptional(ast.getType()).deepClone();
    ASTCDParameter parameter = this.cdParameterFactory.createParameter(parameterType, ast.getName());
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, name, parameter);
    this.glex.replaceTemplate(EMPTY_BODY, method, createSetImplementation(ast));
    return method;
  }

  protected HookPoint createSetImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("methods.opt.Set", ast);
  }

  protected ASTCDMethod createSetOptMethod(final ASTCDAttribute ast) {
    String name = SET_PREFIX + StringUtils.capitalize(ast.getName()) + OPT_SUFFIX;
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, name, this.cdParameterFactory.createParameters(ast));
    this.glex.replaceTemplate(EMPTY_BODY, method, createSetOptImplementation(ast));
    return method;
  }

  protected HookPoint createSetOptImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("methods.Set", ast);
  }

  protected ASTCDMethod createSetAbsentMethod(final ASTCDAttribute ast) {
    String name = SET_ABSENT_PREFIX + StringUtils.capitalize(ast.getName());
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, name);
    this.glex.replaceTemplate(EMPTY_BODY, method, createSetAbsentImplementation(ast));
    return method;
  }

  protected HookPoint createSetAbsentImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("methods.opt.SetAbsent", ast);
  }
}
