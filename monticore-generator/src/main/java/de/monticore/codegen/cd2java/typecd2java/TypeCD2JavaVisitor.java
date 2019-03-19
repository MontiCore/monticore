package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TypeCD2JavaVisitor implements CD4AnalysisVisitor {

  private static final String PACKAGE_SEPARATOR = "\\.";


  private String package_suffix;

  private ASTCDCompilationUnit compilationUnit;

  public TypeCD2JavaVisitor(String package_suffix, ASTCDCompilationUnit compilationUnit) {
    this.package_suffix = package_suffix;
    this.compilationUnit = compilationUnit;
  }

  @Override
  public void visit(ASTSimpleReferenceType node) {
    transform(node);
  }

  private void transform(ASTSimpleReferenceType node) {
    //only take first one because at first the type has just one name which contains the complete qualified name
    //e.g. "de.monticore.Automaton.ASTAutomaton"
    Optional<CDTypeSymbol> typeSymbol = compilationUnit.getEnclosingScope().resolve(node.getName(0), CDTypeSymbol.KIND);
    if (typeSymbol.isPresent()) {
      String javaType = typeSymbol.get().getModelName().toLowerCase() + package_suffix + typeSymbol.get().getName();
      node.setName(0, javaType);
    }
    node.setNameList(splitName(node.getName(0)));
  }

  private List<String> splitName(String name) {
    String[] names = name.split(PACKAGE_SEPARATOR);
    return new ArrayList<>(Arrays.asList(names));
  }

}
