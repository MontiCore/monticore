/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import static de.monticore.codegen.mc2cd.TransformationHelper.createSimpleReference;
import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static de.monticore.grammar.Multiplicity.determineMultiplicity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.Multiplicity;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;


/**
 * Checks for the multiplicity implied by the Monticore AST and modifies the type of the resulting
 * attribute accordingly. Keep in mind that since nonterminals can be enclosed by blocks
 * (parentheses) multiple multiplicities can apply. The rules for this operation, sorted from
 * highest to lowest priority:
 * <li>Star or Plus indicates a List
 * <li>Question mark implies an  Optional
 * <li>the default is just a normal reference
 *
 * @author Sebastian Oberhoff
 */
public class MultiplicityTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    Map<ASTCDAttribute, Multiplicity> cdAttributesToMaxMultiplicities =
        mapCDAttributesToMaxMultiplicities(rootLink.getLinks(ASTNode.class, ASTCDAttribute.class));

    cdAttributesToMaxMultiplicities.entrySet().stream()
        .forEach(entry -> {
          ASTCDAttribute cdAttribute = entry.getKey();
          Multiplicity multiplicity = entry.getValue();
          cdAttribute.setType(createNewType(cdAttribute.getType(), multiplicity));
        });

    return rootLink;
  }

  /**
   * Groups all the links with identical targets and maps them to their maximum Multiplicities
   */
  private Map<ASTCDAttribute, Multiplicity> mapCDAttributesToMaxMultiplicities(
      Set<Link<ASTNode, ASTCDAttribute>> cdAttributeLinks) {
    Map<ASTCDAttribute, List<Link<ASTNode, ASTCDAttribute>>> cdAttributesToLinks =
        cdAttributeLinks.stream().collect(Collectors.groupingBy(Link::target));

    return Maps.transformValues(cdAttributesToLinks,
        linkList -> linkList.stream()
            .map(link -> determineMultiplicity(link.rootLink().source(), link.source()))
            .reduce(BinaryOperator.maxBy(Multiplicity::compareTo))
            .get());
  }

  private static ASTType createNewType(ASTType oldType, Multiplicity multiplicity) {
    if (oldType instanceof ASTPrimitiveType) {
      if (multiplicity == Multiplicity.LIST) {
        return createNewListType(changePrimitiveType(((ASTPrimitiveType) oldType).getPrimitive()));
      }
    } else {
      if (multiplicity == Multiplicity.LIST) {
        return createNewListType(typeToString(oldType));
      }
      else if (multiplicity == Multiplicity.OPTIONAL) {
        return createSimpleReference("Optional", typeToString(oldType));
      }
    }
    return oldType;
  }

  private static ASTSimpleReferenceType createNewListType(String oldTypeName) {
    return createSimpleReference("java.util.List", oldTypeName);
    // TODO GV, MB
    /*
    if (Names.getSimpleName(oldTypeName).startsWith(TransformationHelper.AST_PREFIX)) {
      return createSimpleReference(oldTypeName + "List");
    }
    else {
      return createSimpleReference("java.util.List", oldTypeName);
    }*/
  }
  
  private static String changePrimitiveType(int primType) {
    switch (primType) {
      case ASTConstantsTypes.INT:
        return "Integer";
      case ASTConstantsTypes.BOOLEAN:
        return "Boolean";
      case ASTConstantsTypes.DOUBLE:
        return "Double";
      case ASTConstantsTypes.FLOAT:
        return "Float";
      case ASTConstantsTypes.CHAR:
        return "Char";
      case ASTConstantsTypes.BYTE:
        return "Byte";
      case ASTConstantsTypes.SHORT:
        return "Short";
      case ASTConstantsTypes.LONG:
        return "Long";
      default:
        return "Object";
    }
  }
  
}
