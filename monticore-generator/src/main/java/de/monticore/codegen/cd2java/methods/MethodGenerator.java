package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Generator;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

public class MethodGenerator implements Generator<ASTCDAttribute, List<ASTCDMethod>> {

  private final GlobalExtensionManagement glex;

  public MethodGenerator(
      final GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  protected GlobalExtensionManagement getGlex() {
    return this.glex;
  }

  @Override
  public List<ASTCDMethod> generate(final ASTCDAttribute ast) {
    MethodGeneratorStrategy methodGeneratorStrategy = determineMethodGeneratorStrategy(ast);
    return methodGeneratorStrategy.generate(ast);
  }

  private MethodGeneratorStrategy determineMethodGeneratorStrategy(final ASTCDAttribute ast) {
    //TODO: helper durch OO-Ansatz ersetzen (und vereinheitlichen)
    if (GeneratorHelper.isListType(ast.printType())) {
      return createListMethodGeneratorStrategy();
    }
    else if (GeneratorHelper.isOptional(ast)) {
      return createOptionalMethodGeneratorStrategy();
    }
    return createMandatoryMethodGeneratorStrategy();
  }

  protected MandatoryMethodGeneratorStrategy createMandatoryMethodGeneratorStrategy() {
    return new MandatoryMethodGeneratorStrategy(this.getGlex());
  }

  protected OptionalMethodGeneratorStrategy createOptionalMethodGeneratorStrategy() {
    return new OptionalMethodGeneratorStrategy(this.getGlex());
  }

  protected ListMethodGeneratorStrategy createListMethodGeneratorStrategy() {
    return new ListMethodGeneratorStrategy(this.getGlex(), createMandatoryMethodGeneratorStrategy());
  }
}
