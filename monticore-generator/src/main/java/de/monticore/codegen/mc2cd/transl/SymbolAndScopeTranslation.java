/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.utils.Link;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKAGE;

public class SymbolAndScopeTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> links) {
    for (Link<ASTClassProd, ASTCDClass> link : links.getLinks(ASTClassProd.class, ASTCDClass.class)) {
      final ASTClassProd astClassProd = link.source();
      final ASTCDClass astcdClass = link.target();
      addSymbolAndScopeStereotypes(astClassProd, astcdClass);
      addSymbolInheritedProperty(astClassProd, astcdClass);
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : links.getLinks(ASTAbstractProd.class, ASTCDClass.class)) {
      final ASTAbstractProd astClassProd = link.source();
      final ASTCDClass astcdClass = link.target();
      addSymbolAndScopeStereotypes(astClassProd, astcdClass);
      addSymbolInheritedProperty(astClassProd, astcdClass);
    }

    for (Link<ASTInterfaceProd, ASTCDInterface> link : links.getLinks(ASTInterfaceProd.class, ASTCDInterface.class)) {
      final ASTInterfaceProd astInterfaceProd = link.source();
      final ASTCDInterface astcdInterface = link.target();
      addSymbolAndScopeStereotypes(astInterfaceProd, astcdInterface);
      addSymbolInheritedProperty(astInterfaceProd, astcdInterface);
    }
    return links;
  }

  protected void addSymbolAndScopeStereotypes(ASTProd grammarProd, ASTCDType cdType) {
    for (ASTSymbolDefinition symbolDefinition : grammarProd.getSymbolDefinitionList()) {
      if (symbolDefinition.isGenSymbol()) {
        final Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
            .getMCGrammarSymbol(grammarProd.getEnclosingScope());
        if (symbolDefinition.isPresentSymbolName()
            && grammarSymbol.isPresent()) {
          //extra information into stereotype value if a symboltype is already defined in the grammar
          String symbolName = symbolDefinition.getSymbolName();
          String qualifiedName;
          Optional<ProdSymbol> symbolType = grammarProd.getEnclosingScope().resolveProd(symbolName);
          if (symbolType.isPresent()) {
            String packageName = symbolType.get().getFullName().substring(0, symbolType.get().getFullName().lastIndexOf(".")).toLowerCase();
            qualifiedName = packageName + "." + SYMBOL_TABLE_PACKAGE + "." + symbolName;
          } else {
            qualifiedName = grammarSymbol.get().getFullName().toLowerCase() + "." +
                SYMBOL_TABLE_PACKAGE + "." + symbolName;
          }
          TransformationHelper.addStereoType(cdType,
              MC2CDStereotypes.SYMBOL.toString(), qualifiedName + SYMBOL_SUFFIX);
        } else {
          TransformationHelper.addStereoType(cdType,
              MC2CDStereotypes.SYMBOL.toString());
        }
      } else {
        if (symbolDefinition.isGenScope()) {
          TransformationHelper.addStereoType(cdType,
              MC2CDStereotypes.SCOPE.toString());
        }
      }
    }
  }

  protected void addSymbolInheritedProperty(ASTParserProd astClassProd, ASTCDType astcdClass) {
    final Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(astClassProd.getEnclosingScope());
    if (grammarSymbol.isPresent() &&
        astcdClass.getModifierOpt().isPresent() && !hasStereotype(MC2CDStereotypes.SYMBOL, astcdClass.getModifierOpt().get())) {
      for (ASTRuleReference astRuleReference : astClassProd.getSuperInterfaceRuleList()) {
        Optional<ProdSymbol> prodSymbol = grammarSymbol.get().getProdWithInherited(astRuleReference.getName());
        if (prodSymbol.isPresent()) {
          if (prodSymbol.get().isIsSymbolDefinition()) {
            String packageName = prodSymbol.get().getFullName().substring(0, prodSymbol.get().getFullName().lastIndexOf(".")).toLowerCase();
            String qualifiedName = packageName + "." + SYMBOL_TABLE_PACKAGE + "." + prodSymbol.get().getName() + SYMBOL_SUFFIX;
            TransformationHelper.addStereoType(astcdClass,
                MC2CDStereotypes.INHERITED_SYMBOL.toString(), qualifiedName);
          }
          if (prodSymbol.get().isIsScopeSpanning()) {
            TransformationHelper.addStereoType(astcdClass,
                MC2CDStereotypes.INHERITED_SCOPE.toString());
          }
          if (prodSymbol.get().getAstNode() instanceof ASTParserProd) {
            addSymbolInheritedProperty((ASTParserProd) prodSymbol.get().getAstNode(), astcdClass);
          }
        }
      }
      addInheritsSymbolPropertyThroughOverwriting(grammarSymbol.get(), astcdClass);
    }
  }

  protected void addSymbolInheritedProperty(ASTAbstractProd astClassProd, ASTCDType astcdClass) {
    final Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(astClassProd.getEnclosingScope());
    if (grammarSymbol.isPresent() &&
        astcdClass.getModifierOpt().isPresent() && !hasStereotype(MC2CDStereotypes.SYMBOL, astcdClass.getModifierOpt().get())) {
      for (ASTRuleReference astRuleReference : astClassProd.getSuperInterfaceRuleList()) {
        Optional<ProdSymbol> prodSymbol = grammarSymbol.get().getProdWithInherited(astRuleReference.getName());
        if (prodSymbol.isPresent()) {
          if (prodSymbol.get().isIsSymbolDefinition()) {
            TransformationHelper.addStereoType(astcdClass,
                MC2CDStereotypes.INHERITED_SYMBOL.toString(), getSymbolName(prodSymbol.get()));
          }
          if (prodSymbol.get().isIsScopeSpanning()) {
            TransformationHelper.addStereoType(astcdClass,
                MC2CDStereotypes.INHERITED_SCOPE.toString());
          }
          if (prodSymbol.get().getAstNode() instanceof ASTParserProd) {
            addSymbolInheritedProperty((ASTParserProd) prodSymbol.get().getAstNode(), astcdClass);
          }
        }
      }
      addInheritsSymbolPropertyThroughOverwriting(grammarSymbol.get(), astcdClass);
    }
  }

  protected String getSymbolName(ProdSymbol prodSymbol) {
    String packageName = prodSymbol.getFullName().substring(0, prodSymbol.getFullName().lastIndexOf(".")).toLowerCase();
    return packageName + "." + SYMBOL_TABLE_PACKAGE + "." + prodSymbol.getName() + SYMBOL_SUFFIX;
  }

  protected boolean hasStereotype(MC2CDStereotypes stereotype, ASTModifier modifier) {
    return modifier.isPresentStereotype() && modifier.getStereotype().getValueList()
        .stream()
        .anyMatch(v -> v.getName().equals(stereotype.toString()));
  }

  /**
   * returns true for e.g. for Foo in B in: A { symbol Foo; }  grammar B extends A { Foo; }
   */
  protected void addInheritsSymbolPropertyThroughOverwriting(MCGrammarSymbol grammarSymbol, ASTCDType prod) {
    List<MCGrammarSymbol> superGrammarSymbols = grammarSymbol.getSuperGrammarSymbols();
    for (MCGrammarSymbol superGrammar : superGrammarSymbols) {
      String prodName = prod.getName().startsWith(AST_PREFIX) ? prod.getName().replaceFirst(AST_PREFIX, "") : prod.getName();
      Optional<ProdSymbol> superProd = superGrammar.getProd(prodName);
      if (superProd.isPresent()) {
        if (superProd.get().isIsSymbolDefinition()) {
          TransformationHelper.addStereoType(prod,
              MC2CDStereotypes.INHERITED_SYMBOL.toString(), getSymbolName(superProd.get()));
        }
      }
    }
  }
}
