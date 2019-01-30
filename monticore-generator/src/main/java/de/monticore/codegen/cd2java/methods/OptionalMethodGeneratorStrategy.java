package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class OptionalMethodGeneratorStrategy implements MethodGeneratorStrategy {

  private static final String GET_PREFIX = "get";

  private static final String SET_PREFIX = "set";

  private static final String OPT_SUFFIX = "Opt";

  private static final String IS_PRESENT_PREFIX = "isPresent";

  private static final String SET_ABSENT_PREFIX = "setAbsent";

  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDMethodFactory cdMethodFactory;

  private final CDParameterFactory cdParameterFactory;

  protected OptionalMethodGeneratorStrategy(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdMethodFactory = CDMethodFactory.getInstance();
    this.cdParameterFactory = CDParameterFactory.getInstance();
  }

  @Override
  public List<ASTCDMethod> generate(final ASTCDAttribute ast) {
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
    return this.cdMethodFactory.createPublicMethod(type, name);
  }

  private ASTCDMethod createGetOptMethod(final ASTCDAttribute ast) {
    String name = GET_PREFIX + StringUtils.capitalize(ast.getName()) + OPT_SUFFIX;
    ASTType type = ast.getType().deepClone();
    return this.cdMethodFactory.createPublicMethod(type, name);
  }

  private ASTCDMethod createIsPresentMethod(final ASTCDAttribute ast) {
    String name = IS_PRESENT_PREFIX + StringUtils.capitalize(ast.getName());
    ASTType type = this.cdTypeFactory.createBooleanType();
    return this.cdMethodFactory.createPublicMethod(type, name);
  }

  protected ASTCDMethod createSetMethod(final ASTCDAttribute ast) {
    String name = SET_PREFIX + StringUtils.capitalize(ast.getName());
    ASTType parameterType = TypesHelper.getSimpleReferenceTypeFromOptional(ast.getType().deepClone());
    ASTCDParameter parameter = this.cdParameterFactory.createParameter(parameterType, ast.getName());
    return this.cdMethodFactory.createPublicVoidMethod(name, parameter);
  }

  protected ASTCDMethod createSetOptMethod(final ASTCDAttribute ast) {
    String name = SET_PREFIX + StringUtils.capitalize(ast.getName()) + OPT_SUFFIX;
    return this.cdMethodFactory.createPublicVoidMethod(name, ast);
  }

  protected ASTCDMethod createSetAbsentMethod(final ASTCDAttribute ast) {
    String name = SET_ABSENT_PREFIX + StringUtils.capitalize(ast.getName());
    return this.cdMethodFactory.createPublicVoidMethod(name);
  }
}
