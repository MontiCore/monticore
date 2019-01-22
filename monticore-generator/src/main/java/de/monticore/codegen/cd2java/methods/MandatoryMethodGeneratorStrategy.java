package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class MandatoryMethodGeneratorStrategy implements MethodGeneratorStrategy {

  private static final String GETTER_PREFIX = "get";

  private static final String SETTER_PREFIX = "set";

  private final CDMethodFactory cdMethodFactory;

  public MandatoryMethodGeneratorStrategy(CDMethodFactory cdMethodFactory) {
    this.cdMethodFactory = cdMethodFactory;
  }

  @Override
  public List<ASTCDMethod> generate(final ASTCDAttribute ast) {
    ASTCDMethod getter = createGetter(ast);
    ASTCDMethod setter = createSetter(ast);
    return Arrays.asList(getter, setter);
  }

  private ASTCDMethod createGetter(final ASTCDAttribute ast) {
    String name = GETTER_PREFIX + StringUtils.capitalize(ast.getName());
    ASTType type = ast.getType().deepClone();
    return this.cdMethodFactory.createPublicMethod(type, name);
  }

  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    String name = SETTER_PREFIX + StringUtils.capitalize(ast.getName());
    return this.cdMethodFactory.createPublicVoidMethod(name, ast);
  }
}
