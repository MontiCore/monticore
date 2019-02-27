package de.monticore.codegen.cd2java.methods.accessor;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class MandatoryAccessorDecorator implements Decorator<ASTCDAttribute, List<ASTCDMethod>> {

  private static final String GET = "get%s";

  protected final GlobalExtensionManagement glex;

  private final CDMethodFactory cdMethodFactory;

  public MandatoryAccessorDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    return new ArrayList<>(Arrays.asList(createGetter(ast)));
  }

  private ASTCDMethod createGetter(final ASTCDAttribute ast) {
    String name = String.format(GET, StringUtils.capitalize(ast.getName()));
    ASTType type = ast.getType().deepClone();
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, type, name);
    this.glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Get", ast));
    return method;
  }
}
