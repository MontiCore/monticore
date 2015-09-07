/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.languages.grammar;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTExternalProd;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.symboltable.Symbol;

/**
 * Factory to create symbols for the MontiCore grammar symbol table.
 * 
 * @author Volkova
 */
public class MCGrammarSymbolsFactory {
  
  protected static MCGrammarSymbolsFactory factory = null;
  
  private MCGrammarSymbolsFactory() {}
  
  protected <T extends Symbol> T setNode(T symbol, ASTNode node) {
    if (node != null) {
      symbol.setAstNode(node);
    }
    return symbol;
  }

  public static MCGrammarSymbolsFactory getInstance() {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory;
  }
  
  public static MCGrammarSymbol createMCGrammarSymbol(String name) {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory.doCreateMCGrammarSymbol(name);
  }

  public static MCClassRuleSymbol createMCClassProdSymbol(ASTClassProd classProd) {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory.doCreateMCClassProdSymbol(classProd);
  }
  
  public static MCTypeSymbol createMCTypeSymbol(String name, MCGrammarSymbol grammar, ASTNode node) {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory.setNode(factory.doCreateMCTypeSymbol(name, grammar), node);
  }
  
  public static MCExternalTypeSymbol createMCExternalTypeSymbol(String name, MCGrammarSymbol grammar) {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory.doCreateMCExternalTypeSymbol(name, grammar);
  }
  
  public static MCEnumRuleSymbol createMCEnumProdSymbol(ASTEnumProd astEnumProd) {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory.doCreateMCEnumProdSymbol(astEnumProd);
  }

  public static MCRuleSymbol createMCExternalProdSymbol(ASTExternalProd a, String name) {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory.doCreateMCExternalProdSymbol(a, name);
  }

  public static MCRuleSymbol createMCLexProdSymbol(String name) {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory.doCreateMCLexProdSymbol(name);
  }

  public static MCInterfaceOrAbstractRuleSymbol createMCInterfaceRuleOrAbstractProdSymbol(ASTProd astProd, String name, boolean isInterface) {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory.doCreateMCInterfaceRuleOrAbstractProdSymbol(astProd, name, isInterface);
  }

  public static MCLexRuleSymbol createLexProdSymbol(ASTLexProd astLexProd) {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory.doCreateMCLexProdSymbol(astLexProd);
  }

  public static MCRuleComponentSymbol createRuleComponentSymbol(String name) {
    if (factory == null) {
      factory = new MCGrammarSymbolsFactory();
    }
    return factory.doCreateMCRuleComponentSymbol(name);
  }

  protected MCClassRuleSymbol doCreateMCClassProdSymbol(ASTClassProd classProd) {
    return new MCClassRuleSymbol(classProd);
  }
  
  protected MCGrammarSymbol doCreateMCGrammarSymbol(String name) {
    return new MCGrammarSymbol(name);
  }
  
  protected MCTypeSymbol doCreateMCTypeSymbol(String name, MCGrammarSymbol grammar) {
    return new MCTypeSymbol(name, grammar);
  }
  
  protected MCExternalTypeSymbol doCreateMCExternalTypeSymbol(String name, MCGrammarSymbol grammar) {
    return new MCExternalTypeSymbol(name, grammar);
  }
  
  protected MCEnumRuleSymbol doCreateMCEnumProdSymbol(ASTEnumProd astEnumProd) {
    return new MCEnumRuleSymbol(astEnumProd);
  }

  protected MCRuleSymbol doCreateMCExternalProdSymbol(ASTExternalProd a, String name) {
    return new MCExternalRuleSymbol(a, name);
  }

  protected MCLexRuleSymbol doCreateMCLexProdSymbol(ASTLexProd astLexProd) {
    return new MCLexRuleSymbol(astLexProd);
  }
  
  protected MCRuleSymbol doCreateMCLexProdSymbol(String name) {
    return new MCLexRuleSymbol(name);
  }

  protected MCRuleComponentSymbol doCreateMCRuleComponentSymbol(String name) {
    return new MCRuleComponentSymbol(name);
  }

  protected MCInterfaceOrAbstractRuleSymbol doCreateMCInterfaceRuleOrAbstractProdSymbol(ASTProd
      astProd, String name, boolean isInterface) {
    return new MCInterfaceOrAbstractRuleSymbol(astProd, name, isInterface);
  }

}
