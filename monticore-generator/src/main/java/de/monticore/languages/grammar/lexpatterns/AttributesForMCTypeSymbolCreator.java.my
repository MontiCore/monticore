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

package de.monticore.languages.grammar.visitors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;

import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.languages.grammar.MCAttributeSymbol;
import de.monticore.languages.grammar.MCExternalTypeSymbol;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCGrammarSymbolsFactory;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.monticore.languages.grammar.attributeinfos.MCAttributeInfo;
import de.monticore.languages.grammar.attributeinfos.MCAttributeInfoCalculator;
import de.monticore.languages.grammar.attributeinfos.MCAttributeInfoMap;
import de.monticore.languages.grammar.symbolreferences.MCExternalTypeSymbolReference;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

/**
 * This class is responsible for creating {@link MCAttributeSymbol}s for {@link MCTypeSymbol}s. For
 * example, the rule <code>A = r:D</code> defines the (implicit) type <code>A</code>, which
 * has the attribute <code>r</code> of the type <code>D</code>.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
class AttributesForMCTypeSymbolCreator {

  private AttributesForMCTypeSymbolCreator() {
  }


  static void createAttributes(ASTMCGrammar astGrammar, MCGrammarSymbol grammarSymbol,
      Scope currentScope, Grammar_WithConceptsPrettyPrinter prettyPrinter) {
    boolean foundError = false;

    // Consider ast rules
    for (ASTASTRule astRule : astGrammar.getASTRules()) {
      MCTypeSymbol typeSymbol = grammarSymbol.getTypeWithInherited(astRule.getType());
      if (typeSymbol == null) {
        Log.error("0xA0267 ast rule " + astRule.getType() + " refers to a non-existing rule or "
            + "type " + astRule.getType() + ". Pos: " + astRule.get_SourcePositionStart());
        foundError = true;
        continue;
      }

      for (ASTGenericType ast : astRule.getASTSuperClass()) {
        if (typeSymbol.getKindOfType().equals(MCTypeSymbol.KindType.CLASS)) {
          MCTypeSymbol superType = createSuperType(astRule, ast, grammarSymbol, currentScope);

          if (superType != null) {
            typeSymbol.addSuperClass(superType, true);
          }
          else {
            foundError = true;
          }
        }
        else if (typeSymbol.getKindOfType().equals(MCTypeSymbol.KindType.INTERFACE)) {
          MCTypeSymbol superType = createSuperType(astRule, ast, grammarSymbol, currentScope);

          if (superType != null) {
            typeSymbol.addSuperInterface(superType, true);
          }
          else {
            foundError = true;
          }

        }
      }
      for (ASTGenericType ast : astRule.getASTSuperInterface()) {
        if (typeSymbol.getKindOfType().equals(MCTypeSymbol.KindType.CLASS)) {
          MCTypeSymbol superType = createSuperType(astRule, ast, grammarSymbol, currentScope);

          if (superType != null) {
            typeSymbol.addSuperInterface(superType, true);
          }
          else {
            foundError = true;
          }

        }
      }

      for (ASTAttributeInAST ast : astRule.getAttributeInASTs()) {
        add(ast, typeSymbol, grammarSymbol, prettyPrinter);
      }

      astRule.getMethods().forEach(typeSymbol::add);
    }

    // Iterate over all rules and handle attributes
    // Add the attributes from the users grammar
    Map<String, MCAttributeInfoMap> allAttributes = new HashMap<>();

    for (ASTClassProd astClassProd : astGrammar.getClassProds()) {
      MCAttributeInfoMap attributes = MCAttributeInfoCalculator
          .calculateAttributes(astClassProd, grammarSymbol);

      String typeName = astClassProd.getName();
      allAttributes.put(typeName, attributes.alternate(allAttributes.get(typeName)));
    }

    for (Map.Entry<String, MCAttributeInfoMap> entry : allAttributes.entrySet()) {
      MCTypeSymbol typeEntry = grammarSymbol.getTypeWithInherited(entry.getKey());
      add(entry.getValue(), typeEntry, grammarSymbol);
    }

    // Iterate over all enum productions
    for (ASTEnumProd astEnumProd : astGrammar.getEnumProds()) {
      MCTypeSymbol typeEntry = grammarSymbol.getType(astEnumProd.getName());

      for (ASTConstant astConstant : astEnumProd.getConstants()) {
        typeEntry.addEnum(grammarSymbol.getConstantNameForConstant(astConstant),
            astConstant.getName());
      }
    }

    if (foundError) {
      Log.error("0xA1010 Found fatal error, stopped after step 5a!");
    }
  }

  private static MCTypeSymbol createSuperType(ASTASTRule rule, ASTGenericType ast, MCGrammarSymbol
      grammarSymbol, Scope currentScope) {
    MCTypeSymbol superType = grammarSymbol.getTypeWithInherited(ast.getTypeName());
    if (superType == null) {
      if (ast.isExternal()) {
        superType = getOrCreateExternalType(ast.getTypeName(), grammarSymbol, currentScope);
        grammarSymbol.addType(superType);
      }
      else {
        Log.error("0xA2059 ast rule " + rule.getType() + " refers to a non-existing rule or type "
            + "" + ast.getTypeName() + ". Pos: " + rule.get_SourcePositionStart());
      }
    }
    return superType;
  }

  private static MCExternalTypeSymbol getOrCreateExternalType(String name, MCGrammarSymbol
      grammarSymbol, Scope currentScope) {
    MCTypeSymbol type = grammarSymbol.getType(name);
    if (type instanceof MCExternalTypeSymbol) {
      return (MCExternalTypeSymbol) type;
    }

    return new MCExternalTypeSymbolReference(name, grammarSymbol, currentScope);
  }

  /**
   * Converts the attributes form the AttributeMap to attributes of <code>typeSymbol </code>
   */
  private static  void add(MCAttributeInfoMap map, MCTypeSymbol typeSymbol, MCGrammarSymbol grammarSymbol) {
    // Consider all attributes in the map
    for (Map.Entry<String, MCAttributeInfo> e : map.entrySetAttributes()) {

      MCAttributeSymbol attribute = typeSymbol.getAttribute(e.getValue().getName());
      final MCAttributeInfo attr = e.getValue();
      createEnumIfNecessary(typeSymbol, attr, grammarSymbol);

      // Check if concept classgen has already defined the attribute
      if (attribute == null) {

        // Determine the type of the attribute
        MCTypeSymbol referencedType = determineTypeAttribute(typeSymbol.getName(), attr, grammarSymbol);

        // Create Attribute and set values
        attribute = new MCAttributeSymbol(attr.getName(), referencedType, grammarSymbol);

        // implicit enums are not iterated by default
        // TODO GV:
        if (attribute.getType() == null) {
          throw new NullPointerException("0xA2034 Type of the attribute '" + attr.getName() + "' is null");
        }
        else if (!(attribute.getType().isImplicitEnum())) {
          attribute.setIterated((attr.getMax() > 1 || attr.getMax() == MCAttributeInfo.STAR));
        }
        attribute.setMax(attr.getMax());
        attribute.setMin(attr.getMin());

        typeSymbol.addAttribute(attribute);
      }
    }
  }


  /**
   * Determines the type of the <code>attribute</code> that is defined in
   * <code>definingTypeName</code>.
   *
   * @return the type of the <code>attribute</code> that is defined in
   * <code>definingTypeName</code>.
   */
  private static MCTypeSymbol determineTypeAttribute(String definingTypeName, MCAttributeInfo
      attributeInfo, MCGrammarSymbol grammarSymbol) {
    if (!attributeInfo.getReferencedRule().isEmpty() && !attributeInfo.getConstantValues().isEmpty()) {
      Log.error("0xA2022 The attribute '" + attributeInfo.getName() + "' of type '" + definingTypeName + "'"
          + " cannot be both: Enum and reference to other rule!");
      return null;
    }

    else if (attributeInfo.getReferencedRule().isEmpty() && !attributeInfo.isReferencesConstantTerminal()) {
      return grammarSymbol.getType(Names.getQualifiedName(definingTypeName, attributeInfo.getName()));
    }

    else if (attributeInfo.getReferencedRule().isEmpty()) {
      // TODO: replace with Java bootstrap type
      return MCGrammarSymbolsFactory.createMCExternalTypeSymbol("/String",
          grammarSymbol);
    }

    else {
      HashMultimap<MCTypeSymbol.KindType, MCTypeSymbol> m = HashMultimap.create();

      for (String t : attributeInfo.getReferencedRule()) {
        MCRuleSymbol ruleByName = grammarSymbol.getRuleWithInherited(t);
        if (ruleByName == null) {
          Log.error("0xA2021 Undefined rule: " + t);
        }
        else {
          MCTypeSymbol typeByName = ruleByName.getType();
          if (typeByName == null) {
            Log.error("0xA2033 Cannot find type: " + t);
          }
          else {
            m.put(typeByName.getKindOfType(), typeByName);
          }
        }
      }

      MCTypeSymbol findLeastType = findLeastType(m.values());
      if (findLeastType == null) {
        ambiguousTypeOfAttribute(attributeInfo.getName(), definingTypeName, m.values());
        return null;
      }

      return findLeastType;
    }
  }

  /**
   * Returns the type of the collection <code>types</code> that is the sub type of all other types
   * in this collection. Else, null is returned.
   *
   * @param types Collection of types
   * @return type that is subtype of all other types or null.
   */
  private static MCTypeSymbol findLeastType(Collection<MCTypeSymbol> types) {
    for (MCTypeSymbol t1 : types) {
      boolean isLeastType = true;

      for (MCTypeSymbol t2 : types) {
        if (!t2.isSubtypeOf(t1) && !t2.isSameType(t1)) {
          isLeastType = false;
          break;
        }
      }

      if (isLeastType) {
        return t1;
      }
    }

    return null;
  }

  private static void ambiguousTypeOfAttribute(String attrName, String nameUsedInGrammar,
      Collection<MCTypeSymbol> typeNames) {
    StringBuilder b = new StringBuilder();
    for (MCTypeSymbol t : typeNames) {
      b.append(t).append("; ");
    }
    Log.error("0xA4006 The production " + nameUsedInGrammar + " must not use the attribute name " + attrName +
            " for different nonterminals.");
  }



  private static void createEnumIfNecessary(MCTypeSymbol typeSymbol, MCAttributeInfo attr,
      MCGrammarSymbol grammarSymbol) {
    if (attr.getReferencedRule().isEmpty() && !attr.isReferencesConstantTerminal()) {
      String enumName = typeSymbol.getName() + "." + attr.getName();
      grammarSymbol.createEnum(attr, enumName);
    }
  }

  /**
   * Adds the data from the ast rules
   *
   */
  private static void add(ASTAttributeInAST ast, MCTypeSymbol typeSymbol, MCGrammarSymbol
      grammarSymbol, Grammar_WithConceptsPrettyPrinter prettyPrinter) {
    String name;
    if (ast.getName().isPresent()) {
      name = ast.getName().get();
    }
    else
    {
      name = StringTransformations.uncapitalize(ast.getGenericType().getTypeName());
    }

    MCAttributeSymbol attribute = typeSymbol.getAttribute(name);
    if (attribute == null) {
      MCTypeSymbol attrType = grammarSymbol.createType(ast.getGenericType(), typeSymbol.getSpannedScope());
      attribute = new MCAttributeSymbol(name, attrType, grammarSymbol);
      typeSymbol.addAttribute(attribute);

      if (ast.isDerived() && ast.getBody().isPresent()) {
        String content = prettyPrinter.prettyprint(ast.getBody().get());
        attribute.setDerived(content);
      }
    }

    attribute.setUnordered(ast.isUnordered());

    if (ast.getCard().isPresent() && ast.getCard().get().getMin().isPresent()) {
      attribute.setMin(ast.getCard().get().getMin().get());
      attribute.setMinCheckedDuringParsing(true);
    }
    if (ast.getCard().isPresent() && ast.getCard().get().getMax().isPresent()) {
      attribute.setMax(ast.getCard().get().getMax().get());
      attribute.setIterated(attribute.getMax()>1);
      attribute.setMaxCheckedDuringParsing(true);
    }
    if (ast.getCard().isPresent() && ast.getCard().get().isUnbounded()) {
      attribute.setIterated(true);
    }
  }

}
