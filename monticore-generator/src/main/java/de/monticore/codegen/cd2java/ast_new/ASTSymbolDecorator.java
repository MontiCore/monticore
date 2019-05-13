package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
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
      // add attributes
      ASTType symbolType = this.getCDTypeFacade().createOptionalTypeOf(symbolTableService.getSymbolType(clazz));
      String attributeName = StringUtils.uncapitalize(symbolTableService.getSymbolName(clazz));

      ASTCDAttribute symbolAttribute = createSymbolAttribute(symbolType, attributeName);
      clazz.addCDAttribute(symbolAttribute);
      ASTCDAttribute symbol2Attribute = createSymbol2Attribute(symbolType, attributeName);
      clazz.addCDAttribute(symbol2Attribute);

      // add getter and setter for attributes
      clazz.addAllCDMethods(methodDecorator.decorate(symbolAttribute));
      clazz.addAllCDMethods(methodDecorator.decorate(symbol2Attribute));
    }
    return clazz;
  }


  protected ASTCDAttribute createSymbolAttribute(ASTType symbolType, String attributeName) {
    //todo replace with symbol2 some day
    return this.getCDAttributeFacade().createAttribute(PROTECTED, symbolType, attributeName);
  }

  protected ASTCDAttribute createSymbol2Attribute(ASTType symbolType, String attributeName) {
    //todo better name with the grammar name in the attributeName, like it was before
//    attributeName += "2";
    attributeName = "symbol2";
    return this.getCDAttributeFacade().createAttribute(PROTECTED, symbolType, attributeName);
  }
}