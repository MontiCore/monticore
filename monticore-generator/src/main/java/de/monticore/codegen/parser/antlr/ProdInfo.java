package de.monticore.codegen.parser.antlr;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTProd;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProdInfo {
  public final ASTProd prod;
  public final Map<ASTNode, String> tmpNames = new LinkedHashMap<>();
  public final Map<InterfaceInliningAlt, List<String>> alternativeToNames = new LinkedHashMap<>();

  protected ProdInfo(ASTProd prod) {
    this.prod = prod;
  }

  public Map<InterfaceInliningAlt, List<String>> getAlternativeToNames() {
    return alternativeToNames;
  }
}
