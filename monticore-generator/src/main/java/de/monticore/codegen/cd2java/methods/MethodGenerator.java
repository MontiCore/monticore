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

  public List<ASTCDMethod> generate(final ASTCDAttribute ast) {
    MethodGeneratorStrategy methodGeneratorStrategy = determineMethodGeneratorStrategy(ast);
    return methodGeneratorStrategy.generate(ast);
  }

  private MethodGeneratorStrategy determineMethodGeneratorStrategy(final ASTCDAttribute ast) {
    CDTypeFactory cdTypeFactory = CDTypeFactory.getInstance();
    CDMethodFactory cdMethodFactory = CDMethodFactory.getInstance();
    CDParameterFactory cdParameterFactory = CDParameterFactory.getInstance();

    //TODO: helper durch OO-Ansatz ersetzen (und vereinheitlichen)
    if (GeneratorHelper.isListType(ast.printType())) {
      return createListMethodGeneratorStrategy(cdMethodFactory);
    }
    else if (GeneratorHelper.isOptional(ast)) {
      return createOptionalMethodGeneratorStrategy(cdTypeFactory, cdMethodFactory, cdParameterFactory);
    }
    return createMandatoryMethodGeneratorStrategy(cdMethodFactory);
  }

  protected MandatoryMethodGeneratorStrategy createMandatoryMethodGeneratorStrategy(final CDMethodFactory cdMethodFactory) {
    return new MandatoryMethodGeneratorStrategy(cdMethodFactory);
  }

  protected OptionalMethodGeneratorStrategy createOptionalMethodGeneratorStrategy(final CDTypeFactory cdTypeFactory, final CDMethodFactory cdMethodFactory,
      final CDParameterFactory cdParameterFactory) {
    return new OptionalMethodGeneratorStrategy(cdTypeFactory, cdMethodFactory, cdParameterFactory);
  }

  protected ListMethodGeneratorStrategy createListMethodGeneratorStrategy(final CDMethodFactory cdMethodFactory) {
    return new ListMethodGeneratorStrategy(cdMethodFactory, new MandatoryMethodGeneratorStrategy(cdMethodFactory));
  }
}
