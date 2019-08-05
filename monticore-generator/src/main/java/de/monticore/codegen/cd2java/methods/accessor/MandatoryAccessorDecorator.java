package de.monticore.codegen.cd2java.methods.accessor;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class MandatoryAccessorDecorator extends AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> {

  private static final String GET = "get%s";

  private static final String IS = "is%s";

  public MandatoryAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }


  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    return new ArrayList<>(Arrays.asList(createGetter(ast)));
  }

  private ASTCDMethod createGetter(final ASTCDAttribute ast) {
    String getterPrefix;
    if (getCDTypeFacade().isBooleanType(ast.getType())) {
      getterPrefix = IS;
    } else {
      getterPrefix = GET;
    }
    //todo find better util than the DecorationHelper
    String name = String.format(getterPrefix, StringUtils.capitalize(DecorationHelper.getNativeAttributeName(ast.getName())));
    ASTType type = ast.getType().deepClone();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, type, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Get", ast));
    return method;
  }
}
