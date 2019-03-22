package de.monticore.codegen.cd2java.enums;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.CONSTANT;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class EnumDecorator extends AbstractDecorator<ASTCDEnum, ASTCDEnum> {

  public EnumDecorator(GlobalExtensionManagement glex, final AccessorDecorator accessorDecorator, final String grammarName) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.grammarName = grammarName;
  }

  private final String grammarName;

  private final AccessorDecorator accessorDecorator;


  private static final String INT_VALUE = "intValue";

  @Override
  public ASTCDEnum decorate(ASTCDEnum input) {
    String enumName = input.getName();
    ASTCDAttribute intValueAttribute = getIntValueAttribute();
    List<ASTCDMethod> intValueMethod = accessorDecorator.decorate(intValueAttribute);
    List<ASTCDEnumConstant> constants = input.getCDEnumConstantList().stream()
        .map(ASTCDEnumConstant::deepClone)
        .collect(Collectors.toList());
    for (ASTCDEnumConstant constant : constants) {
      this.replaceTemplate(EMPTY_BODY, constant, new TemplateHookPoint(CONSTANT, constant.getName(), grammarName));
    }
    return CD4AnalysisMill.cDEnumBuilder()
        .setName(enumName)
        .addAllCDEnumConstants(constants)
        .addCDConstructor(getLiteralsConstructor(enumName))
        .addCDAttribute(intValueAttribute)
        .addAllCDMethods(intValueMethod)
        .build();
  }

  protected ASTCDAttribute getIntValueAttribute() {
    ASTType intType = getCDTypeFactory().createIntType();
    return getCDAttributeFactory().createAttribute(PROTECTED, intType, INT_VALUE);
  }

  protected ASTCDConstructor getLiteralsConstructor(String enumName) {
    ASTType intType = getCDTypeFactory().createIntType();
    ASTCDParameter intParameter = getCDParameterFactory().createParameter(intType, INT_VALUE);
    ASTCDConstructor constructor = getCDConstructorFactory().createConstructor(PRIVATE.build(), enumName, intParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + INT_VALUE + " = " + INT_VALUE + ";"));
    return constructor;
  }

}
