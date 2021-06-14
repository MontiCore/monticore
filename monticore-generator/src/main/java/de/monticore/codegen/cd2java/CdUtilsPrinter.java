/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import java.util.List;
import java.util.Optional;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.grammar.grammarfamily._visitor.GrammarFamilyTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLModifierPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.umlmodifier._ast.ASTModifier;

import static com.google.common.base.Preconditions.checkNotNull;

public class CdUtilsPrinter {
  
  /**
   * Prints an ASTType.
   *
   * @param type An input ASTType
   * @return String representation of the ASTType
   */
  public String printType(ASTMCType type) {
    return type.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
  }
  
  /**
   * Shallowly visits the modifier with an attached printer to print the
   * attributes of an optional modifier.
   * 
   * @param modifier The input modifier
   * @return The printed modifier attributes as String if present or an empty
   *         String, otherwise
   */
  public String printSimpleModifier(Optional<ASTModifier> modifier) {
    if (!modifier.isPresent()) {
      return "";
    }
    else {
      return printSimpleModifier(modifier.get());
    }
  }
  
  /**
   * Shallowly visits the modifier with an attached printer to print the
   * attributes of the modifier.
   * 
   * @param modifier The input modifier
   * @return The printed modifier attributes as String
   */
  public String printSimpleModifier(ASTModifier modifier) {
    GrammarFamilyTraverser t = GrammarFamilyMill.traverser();
    IndentPrinter printer = new IndentPrinter();
    
    UMLModifierPrettyPrinter umlPrinter = new UMLModifierPrettyPrinter(printer);
    t.add4UMLModifier(umlPrinter);
    t.setUMLModifierHandler(umlPrinter);
    modifier.accept(t);
    
    return printer.getContent();
  }
  
}
