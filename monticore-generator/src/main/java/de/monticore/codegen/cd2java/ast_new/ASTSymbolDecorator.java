package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableConstants;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class ASTSymbolDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private final MethodDecorator methodDecorator;

  private final SymbolTableService symbolTableService;

  public ASTSymbolDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator,
      final SymbolTableService symbolTableService) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass clazz) {
    if (symbolTableService.isSymbolClass(clazz)) {
      ASTType symbolType = this.getCDTypeFactory().createOptionalTypeOf(symbolTableService.getSymbolType(clazz));
      String attributeName = StringUtils.uncapitalize(clazz.getName()) + SymbolTableConstants.SYMBOL_SUFFIX;
      ASTCDAttribute symbolAttribute = this.getCDAttributeFactory().createAttribute(PROTECTED, symbolType, attributeName);
      clazz.addCDAttribute(symbolAttribute);
      clazz.addAllCDMethods(methodDecorator.decorate(symbolAttribute));
    }
    return clazz;
  }
}