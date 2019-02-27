package de.monticore.codegen.cd2java.methods.mutator;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class MandatoryMutatorDecorator implements Decorator<ASTCDAttribute, List<ASTCDMethod>> {

  private static final String SET = "set%s";

  protected final GlobalExtensionManagement glex;

  private final CDMethodFactory cdMethodFactory;

  private final CDParameterFactory cdParameterFactory;

  public MandatoryMutatorDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdMethodFactory = CDMethodFactory.getInstance();
    this.cdParameterFactory = CDParameterFactory.getInstance();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    return new ArrayList<>(Arrays.asList(createSetter(ast)));
  }

  private ASTCDMethod createSetter(final ASTCDAttribute ast) {
    String name = String.format(SET, StringUtils.capitalize(ast.getName()));
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, name, this.cdParameterFactory.createParameters(ast));
    this.glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Set", ast));
    return method;
  }
}
