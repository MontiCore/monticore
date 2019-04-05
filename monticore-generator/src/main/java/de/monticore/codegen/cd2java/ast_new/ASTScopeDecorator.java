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

public class ASTScopeDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private final MethodDecorator methodDecorator;

  private final SymbolTableService symbolTableService;

  public ASTScopeDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator,
      final SymbolTableService symbolTableService) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass clazz) {
    if (symbolTableService.isScopeClass(clazz)) {
      ASTType scopeType = this.getCDTypeFactory().createOptionalTypeOf(symbolTableService.getScopeType());
      String attributeName = StringUtils.uncapitalize(symbolTableService.getCDName()) + SymbolTableConstants.SCOPE_SUFFIX;
      ASTCDAttribute scopeAttribute = this.getCDAttributeFactory().createAttribute(PROTECTED, scopeType, attributeName);
      clazz.addCDAttribute(scopeAttribute);
      clazz.addAllCDMethods(methodDecorator.decorate(scopeAttribute));
    }
    return clazz;
  }
}