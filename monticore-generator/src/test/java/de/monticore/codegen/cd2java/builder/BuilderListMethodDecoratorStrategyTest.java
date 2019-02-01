package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.builder.BuilderDecoratorConstants.BUILDER_SUFFIX;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class BuilderListMethodDecoratorStrategyTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();
    ASTCDAttribute attribute = CDAttributeFactory.getInstance().createAttributeByDefinition("protected List<String> a;");
    ASTType builderType = CDTypeFactory.getInstance().createTypeByDefinition("A" + BUILDER_SUFFIX);
    BuilderMandatoryMethodDecoratorStrategy mandatoryDecoratorStrategy = new BuilderMandatoryMethodDecoratorStrategy(glex, builderType);
    BuilderListMethodDecoratorStrategy decoratorStrategy = new BuilderListMethodDecoratorStrategy(glex, mandatoryDecoratorStrategy, builderType);
    this.methods = decoratorStrategy.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(34, methods.size());
  }
}
