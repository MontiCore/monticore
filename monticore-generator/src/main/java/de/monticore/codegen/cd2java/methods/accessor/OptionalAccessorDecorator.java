package de.monticore.codegen.cd2java.methods.accessor;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDTypeFactory.BOOLEAN_TYPE;

public class OptionalAccessorDecorator implements Decorator<ASTCDAttribute, List<ASTCDMethod>> {

  private static final String GET = "get%s";

  private static final String GET_OPT = "get%sOpt";

  private static final String IS_PRESENT = "isPresent%s";

  private final GlobalExtensionManagement glex;

  private final CDMethodFactory cdMethodFactory;

  public OptionalAccessorDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    ASTCDMethod get = createGetMethod(ast);
    ASTCDMethod getOpt = createGetOptMethod(ast);
    ASTCDMethod isPresent = createIsPresentMethod(ast);
    return new ArrayList<>(Arrays.asList(get, getOpt, isPresent));
  }

  private ASTCDMethod createGetMethod(final ASTCDAttribute ast) {
    String name = String.format(GET, StringUtils.capitalize(ast.getName()));
    ASTType type = TypesHelper.getSimpleReferenceTypeFromOptional(ast.getType().deepClone());
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, type, name);
    this.glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.Get", ast));
    return method;
  }

  private ASTCDMethod createGetOptMethod(final ASTCDAttribute ast) {
    String name = String.format(GET_OPT, StringUtils.capitalize(ast.getName()));
    ASTType type = ast.getType().deepClone();
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, type, name);
    this.glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Get", ast));
    return method;
  }

  private ASTCDMethod createIsPresentMethod(final ASTCDAttribute ast) {
    String name = String.format(IS_PRESENT, StringUtils.capitalize(ast.getName()));
    ASTCDMethod method = this.cdMethodFactory.createMethod(PUBLIC, BOOLEAN_TYPE, name);
    this.glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.IsPresent", ast));
    return method;
  }
}
