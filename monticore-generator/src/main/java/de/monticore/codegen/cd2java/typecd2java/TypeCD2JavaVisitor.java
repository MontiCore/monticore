/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typecd2java;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static de.monticore.codegen.mc2cd.TransformationHelper.simpleName;

public class TypeCD2JavaVisitor implements MCBasicTypesVisitor2 {

  private static final String PACKAGE_SEPARATOR = "\\.";

  protected ICDBasisScope scope;

  public TypeCD2JavaVisitor(ICDBasisScope scope) {
    this.scope = scope;
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
      l.add(simpleName(node));
      node.getMCQualifiedName().setPartsList(l);
    }
    if(node.getNameList().size() <= 1){
      node.getMCQualifiedName().setPartsList(new ArrayList<>(Arrays.asList(node.getNameList().get(0).split(PACKAGE_SEPARATOR))));
    }
  }

}
