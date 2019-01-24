package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Generator;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

public class MethodGenerator implements Generator<ASTCDAttribute, List<ASTCDMethod>> {

  private final CDTypeFactory cdTypeFactory;

  private final CDMethodFactory cdMethodFactory;

  private final CDParameterFactory cdParameterFactory;

  public MethodGenerator(final CDTypeFactory cdTypeFactory, final CDMethodFactory cdMethodFactory, final CDParameterFactory cdParameterFactory) {
    this.cdTypeFactory = cdTypeFactory;
    this.cdMethodFactory = cdMethodFactory;
    this.cdParameterFactory = cdParameterFactory;
  }

  protected CDTypeFactory getCDTypeFactory() {
    return this.cdTypeFactory;
  }

  protected CDMethodFactory getCDMethodFactory() {
    return this.cdMethodFactory;
  }

  protected CDParameterFactory getCDParameterFactory() {
    return this.cdParameterFactory;
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
    return new MandatoryMethodGeneratorStrategy(this.getCDMethodFactory());
  }

  protected OptionalMethodGeneratorStrategy createOptionalMethodGeneratorStrategy() {
    return new OptionalMethodGeneratorStrategy(this.getCDTypeFactory(), this.getCDMethodFactory(), this.getCDParameterFactory());
  }

  protected ListMethodGeneratorStrategy createListMethodGeneratorStrategy() {
    return new ListMethodGeneratorStrategy(this.getCDMethodFactory(), createMandatoryMethodGeneratorStrategy());
  }
}
