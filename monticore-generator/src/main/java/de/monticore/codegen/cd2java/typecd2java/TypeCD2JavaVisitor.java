package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.codegen.cd2java.ast_new.ASTConstants;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class TypeCD2JavaVisitor implements CD4AnalysisVisitor {

  private static final String PACKAGE_SEPARATOR = "\\.";

  @Override
  public void visit(ASTSimpleReferenceType node) {
    //only take first one because at first the type has just one name which contains the complete qualified name
    //e.g. "de.monticore.Automaton.ASTAutomaton"
    Optional<CDTypeSymbol> typeSymbol = node.getEnclosingScope().resolve(node.getName(0), CDTypeSymbol.KIND);
    if (typeSymbol.isPresent()) {
      String javaType = String.join(".", typeSymbol.get().getModelName().toLowerCase(), ASTConstants.AST_PACKAGE, typeSymbol.get().getName());
      node.setName(0, javaType);
    }
    node.setNameList(new ArrayList<>(Arrays.asList(node.getName(0).split(PACKAGE_SEPARATOR))));
  }
}
