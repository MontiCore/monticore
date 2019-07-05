package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.se_rwth.commons.StringTransformations;

import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;

public class DataEmfDecorator extends DataDecorator {

  public DataEmfDecorator(GlobalExtensionManagement glex, MethodDecorator methodDecorator, AbstractService service, DataDecoratorUtil dataDecoratorUtil) {
    super(glex, methodDecorator, service, dataDecoratorUtil);
  }

  @Override
  protected void addAttributeDefaultValues(ASTCDAttribute attribute) {
    if (GeneratorHelper.isListType(attribute.printType())) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint(calculateListType(attribute.getType(),
          service.getCDName(), clazzName, attribute.getName())));

    } else if (GeneratorHelper.isOptional(attribute)) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    }
  }

  private String calculateListType(ASTType attributeType, String grammarName, String classname, String attributeName) {
    if (attributeType instanceof ASTSimpleReferenceType && ((ASTSimpleReferenceType) attributeType).getTypeArguments().sizeTypeArguments() == 1) {
      String simpleAttributeType = TypesPrinter.printType((ASTSimpleReferenceType) ((ASTSimpleReferenceType) attributeType).getTypeArguments().getTypeArgument(0));
      return "= new EObjectContainmentEList<" + simpleAttributeType + ">(" + simpleAttributeType +
          ".class, this, " + grammarName + "Package." + classname + "_" + StringTransformations.capitalize(attributeName) + ")";
    }
    return "";
  }
}
