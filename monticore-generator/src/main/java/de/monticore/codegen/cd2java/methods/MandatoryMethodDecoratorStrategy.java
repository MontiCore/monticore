package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MandatoryMethodDecoratorStrategy implements MethodDecoratorStrategy {

  private static final String GETTER_PREFIX = "get";

  private static final String SETTER_PREFIX = "set";

  private final GlobalExtensionManagement glex;

  private final CDMethodFactory cdMethodFactory;

  private final CDParameterFactory cdParameterFactory;

  protected MandatoryMethodDecoratorStrategy(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdMethodFactory = CDMethodFactory.getInstance();
    this.cdParameterFactory = CDParameterFactory.getInstance();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    ASTCDMethod getter = createGetter(ast);
    ASTCDMethod setter = createSetter(ast);
    return new ArrayList<>(Arrays.asList(getter, setter));
  }

  private ASTCDMethod createGetter(final ASTCDAttribute ast) {
    String name = GETTER_PREFIX + StringUtils.capitalize(ast.getName());
    ASTType type = ast.getType().deepClone();
    return this.cdMethodFactory.createPublicMethod(type, name);
  }

  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    String name = SETTER_PREFIX + StringUtils.capitalize(ast.getName());
    return this.cdMethodFactory.createPublicVoidMethod(name, this.cdParameterFactory.createParameters(ast));
  }
}
