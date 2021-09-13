/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.util;

import de.monticore.dstlgen.ruletranslation.DSTLGenInheritanceHelper;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;

import java.util.Optional;

import static de.se_rwth.commons.StringTransformations.capitalize;

/**
 * Created by
 *
 */
public class DSTLUtil {

  public static String getNameForConstant(ASTConstantGroup constantGroup){
    if (constantGroup.isPresentUsageName()) {
      return capitalize(constantGroup.getUsageName());
    } else if(constantGroup.getConstant(0).isPresentUsageName()){
      return capitalize(constantGroup.getConstant(0).getUsageName());
    } else {
      // use the name of the first constant
      return capitalize(constantGroup.getConstant(0).getName());

    }
  }

  public static boolean isEmptyProduction(ProdSymbol typeSymbol) {
    if(typeSymbol.isIsAbstract() || typeSymbol.isIsInterface()){
      return false;
    }
    if(typeSymbol.isIsLexerProd()){
      return true;
    }
    if(typeSymbol.isClass()){
      // Prepare traverser
      DetermineOptionalRuleComponentsVisitor determineOptionalRuleComponentsVisitor
              = new DetermineOptionalRuleComponentsVisitor();
      GrammarTraverser traverser = GrammarMill.traverser();
      traverser.setGrammarHandler(determineOptionalRuleComponentsVisitor);

      ASTClassProd p = (ASTClassProd) typeSymbol.getAstNode();
      for(ASTAlt a : p.getAltList()){
        // Are all components of this alt optional?
        boolean empty = true;
        for(ASTRuleComponent c : a.getComponentList()) {
          determineOptionalRuleComponentsVisitor.setOptional(false);
          c.accept(traverser);
          empty = empty && determineOptionalRuleComponentsVisitor.isOptional();
        }
        if(empty){
          return empty;
        }
      }
      return false;
    }
    return true;
  }
  
  public static String getUsageNameForConstant(ASTConstantGroup group) {
    String usageName = getNameForConstant(group);
    if(usageName.equals("class")|| usageName.equals("Class")){
      return "clazz";
    }
    return usageName;
  }

  public static boolean isFromSupportedGrammar(ASTNonTerminal node, MCGrammarSymbol grammarSymbol) {
    if (node.isPresentSymbol() && node.getSymbol().getReferencedProd().isPresent()) {
      String grammarName = node.getSymbol().getReferencedProd().get().getEnclosingScope().getName();
      DSTLGenInheritanceHelper inheritanceHelper = new DSTLGenInheritanceHelper();
      return !inheritanceHelper.isCommonSuperGrammar(grammarName);
    } else {
      Optional<ProdSymbol> prod = grammarSymbol.getProdWithInherited(node.getName());
      if(prod.isPresent()){
        DSTLGenInheritanceHelper inheritanceHelper = new DSTLGenInheritanceHelper();
        return !inheritanceHelper.isCommonSuperGrammar(prod.get().getEnclosingScope().getName());
      }
    }
    return true;
  }
}
