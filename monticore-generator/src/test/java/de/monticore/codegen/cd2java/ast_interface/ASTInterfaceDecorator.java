package de.monticore.codegen.cd2java.ast_interface;

import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Before;

public class ASTInterfaceDecorator extends DecoratorTestCase {

  private ASTCDClass constantClass;

  private GlobalExtensionManagement glex;

  private CDTypeFactory cdTypeFactory;

  @Before
  public void setUp() {
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
//    ASTInterfaceDecorator decorator = new ASTInterfaceDecorator(this.glex);
//    this.constantClass = decorator.decorate(compilationUnit);
  }

}
