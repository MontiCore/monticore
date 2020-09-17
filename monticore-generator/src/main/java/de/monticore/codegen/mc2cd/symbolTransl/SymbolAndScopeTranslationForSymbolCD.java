// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.transl.SymbolAndScopeTranslation;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

public class SymbolAndScopeTranslationForSymbolCD extends SymbolAndScopeTranslation {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> links) {
    for (Link<ASTClassProd, ASTCDClass> link : links.getLinks(ASTClassProd.class, ASTCDClass.class)) {
      final ASTClassProd astClassProd = link.source();
      final ASTCDClass astcdClass = link.target();
      addSymbolStereotypes(astClassProd, astcdClass);
      addScopeStereotypes(astClassProd.getSymbolDefinitionList(), astcdClass);
      addSymbolInheritedProperty(astClassProd, astcdClass);
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : links.getLinks(ASTAbstractProd.class, ASTCDClass.class)) {
      final ASTAbstractProd astClassProd = link.source();
      final ASTCDClass astcdClass = link.target();
      addSymbolStereotypes(astClassProd, astcdClass);
      addScopeStereotypes(astClassProd.getSymbolDefinitionList(), astcdClass);
      addSymbolInheritedProperty(astClassProd, astcdClass);
    }

    // link between interfaceProd and CDClass, because of interfaces Symbol classes are generated
    for (Link<ASTInterfaceProd, ASTCDClass> link : links.getLinks(ASTInterfaceProd.class, ASTCDClass.class)) {
      final ASTInterfaceProd astInterfaceProd = link.source();
      final ASTCDClass astcdClass = link.target();
      addSymbolStereotypes(astInterfaceProd, astcdClass);
      addScopeStereotypes(astInterfaceProd.getSymbolDefinitionList(), astcdClass);
      addSymbolInheritedProperty(astInterfaceProd, astcdClass);
    }
    return links;
  }
}
