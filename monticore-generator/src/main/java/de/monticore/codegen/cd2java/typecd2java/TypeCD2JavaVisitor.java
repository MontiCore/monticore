package de.monticore.codegen.cd2java.typecd2java;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class TypeCD2JavaVisitor implements CD4AnalysisVisitor {

  private static final String PACKAGE_SEPARATOR = "\\.";

  protected ICD4AnalysisScope scope;

  public TypeCD2JavaVisitor(ICD4AnalysisScope scope) {
    this.scope = scope;
  }

  private TypeCD2JavaVisitor() {
  }

  @Override
  public void visit(ASTMCQualifiedType node) {
    //only take first one because at first the type has just one name which contains the complete qualified name
    //e.g. "de.monticore.Automaton.ASTAutomaton"
    Optional<CDTypeSymbol> typeSymbol = scope.resolveCDType(String.join(".", node.getNameList()));
    if (typeSymbol.isPresent()) {
      ArrayList<String> l = Lists.newArrayList();
      for (String name: node.getNameList()) {
        l.add(name.toLowerCase());
      }
      l.remove(node.getNameList().size()-1);
      l.add( ASTConstants.AST_PACKAGE);
      l.add(node.getBaseName()
      );
      node.getMCQualifiedName().setPartList(l);
    }
    if(node.getNameList().size() <= 1){
      node.getMCQualifiedName().setPartList(new ArrayList<>(Arrays.asList(node.getNameList().get(0).split(PACKAGE_SEPARATOR))));
    }
  }
}
