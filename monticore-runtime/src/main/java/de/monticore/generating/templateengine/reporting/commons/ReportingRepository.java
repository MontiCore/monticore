/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.Map;
import java.util.Set;

/**
 * ReportingRepository holds all used formatted ASTNode strings. All string representations for a
 * ASTNode should be retrieved from this repository (getASTNodeNameFormatted method).
 */
public class ReportingRepository {

  protected IASTNodeIdentHelper astNodeIdentHelper;

  // save objects that have no position
  protected Map<Object, Integer> node2Ident = Maps.newHashMap();

  protected Map<Object, String> node2Name = Maps.newHashMap();

  // save nodes that have a position
  protected Map<String, Integer> name2maxidSourcePos = Maps.newHashMap();

  protected Map<Object, Integer> nodeWithSource2Ident = Maps.newHashMap();

  protected Map<Object, String> nodeWithSource2Name = Maps.newHashMap();

  // save current maxID for aSTNode string
  protected Map<String, Integer> name2maxid = Maps.newHashMap();

  protected Set<String> allHWJavaNames = Sets.newLinkedHashSet();

  protected Set<String> allHWTemplateNames = Sets.newLinkedHashSet();

  public ReportingRepository(IASTNodeIdentHelper astNodeIdentHelper) {
    this.astNodeIdentHelper = astNodeIdentHelper;
  }

  protected String getNameFormatted(Object obj, String out, SourcePosition sourcePos) {
    String pos = Layouter.sourcePos(sourcePos);
    // node has a source position
    if (!sourcePos.equals(
        SourcePosition.getDefaultSourcePosition())) {
      if (!nodeWithSource2Ident.containsKey(obj)) {
        // set output name for node
        nodeWithSource2Name.put(obj, out + pos);
        // name map has no identifier
        if (!name2maxidSourcePos.containsKey(out + pos)) {
          // init map
          name2maxidSourcePos.merge(out + pos, 1, Integer::sum); // value is 1
        }
        nodeWithSource2Ident.put(obj, name2maxidSourcePos.get(out + pos));
        name2maxidSourcePos.merge(out + pos, 1, Integer::sum); // increase current
                                                                        // value
      }
      // do not print <...>!1!
      if (nodeWithSource2Ident.get(obj) != 1) {
        return nodeWithSource2Name.get(obj).replace(Layouter.END_TAG,
            "!" + nodeWithSource2Ident.get(obj) + Layouter.END_TAG);
      }
      // instead <<...>> if identifier is '1'
      else {
        return nodeWithSource2Name.get(obj);
      }
    }
    // first time this node
    if (!node2Ident.containsKey(obj)) {
      // set output name for node
      node2Name.put(obj, out);
      // name map has no identifier
      if (!name2maxid.containsKey(out)) {
        // init map
        name2maxid.merge(out, 1, Integer::sum);
      }
      node2Ident.put(obj, name2maxid.get(out));
      name2maxid.merge(out, 1, Integer::sum);
    }

    // do not print <<...>>!1!
    if (node2Ident.get(obj) != 1) {
      return node2Name.get(obj) + Layouter.START_TAG + "!" + node2Ident.get(obj) + Layouter.END_TAG;
    }
    // instead <<...>> if identifier is '1'
    else {
      return node2Name.get(obj);
    }
  }

  /**
   * Method that converts the ASTNode into a formatted string with a source position if this is
   * possible. The structure of the string is
   *
   * @nodeName!nodeType(x,y) or @nodeName!nodeType(!ID).
   * @param a that should be converted into unique String
   * @return representation of the ASTNode that contains either the position or a unique
   * identification number for the object
   */
  public String getASTNodeNameFormatted(ASTNode a) {
    String out = astNodeIdentHelper.getIdent(a);
    return getNameFormatted(a, out, a.get_SourcePositionStart());
  }

  /**
   * Method that converts the Symbol into a formatted string with a source position if this is
   * possible. The structure of the string is
   *
   * @symbolName!symbolType(x,y) or @symbolName!symbolType(!ID).
   * @param symbol The symbol that should be converted into unique String
   * @return representation of the ASTNode that contains either the position or a unique
   * identification number for the object
   */
  public String getSymbolNameFormatted(ISymbol symbol) {
    String name = astNodeIdentHelper.getIdent(symbol);
    return getNameFormatted(symbol, name, symbol.getSourcePosition());
  }

  /**
   * Method that converts the Symbol into a formatted string with a source position if this is
   * possible. The structure of the string is
   *
   * @symbolName!symbolType(x,y) or @symbolName!symbolType(!ID).
   * @param scope The scope that should be converted into unique String
   * @return representation of the ASTNode that contains either the position or a unique
   * identification number for the object
   */
  public String getScopeNameFormatted(IScope scope) {
    String name = astNodeIdentHelper.getIdent(scope);
    return getNameFormatted(scope, name, SourcePosition.getDefaultSourcePosition());
  }

  public Set<String> getAllHWJavaNames() {
    return allHWJavaNames;
  }

  public Set<String> getAllHWTemplateNames() {
    return allHWTemplateNames;
  }

}
