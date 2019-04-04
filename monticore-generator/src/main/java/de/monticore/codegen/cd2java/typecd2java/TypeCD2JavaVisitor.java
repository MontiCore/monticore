package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.codegen.cd2java.ast_new.ASTConstants;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TypeCD2JavaVisitor implements CD4AnalysisVisitor {

  private static final String PACKAGE_SEPARATOR = "\\.";

  private ASTCDCompilationUnit compilationUnit;

  public TypeCD2JavaVisitor(ASTCDCompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
  }

  @Override
  public void visit(ASTCDClass node) {
    if (node.getName().startsWith(ASTConstants.AST_PREFIX)) {
      node.setName(node.getName().substring(ASTConstants.AST_PREFIX.length()));
    }
  }

  @Override
  public void visit(ASTCDInterface node) {
    if (node.getName().startsWith(ASTConstants.AST_PREFIX)) {
      node.setName(node.getName().substring(ASTConstants.AST_PREFIX.length()));
    }
  }

  @Override
  public void visit(ASTSimpleReferenceType node) {
    //only take first one because at first the type has just one name which contains the complete qualified name
    //e.g. "de.monticore.Automaton.ASTAutomaton"
    Optional<CDTypeSymbol> typeSymbol = compilationUnit.getEnclosingScope().resolve(node.getName(0), CDTypeSymbol.KIND);
    if (typeSymbol.isPresent()) {
      String javaType = String.join(".", typeSymbol.get().getModelName().toLowerCase(), ASTConstants.AST_PACKAGE, typeSymbol.get().getName());
      node.setName(0, javaType);
    }
    node.setNameList(new ArrayList<>(Arrays.asList(node.getName(0).split(PACKAGE_SEPARATOR))));
  }

}
