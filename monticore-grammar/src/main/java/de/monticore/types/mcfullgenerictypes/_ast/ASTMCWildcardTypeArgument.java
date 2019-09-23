/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfullgenerictypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTMCWildcardTypeArgument extends ASTMCWildcardTypeArgumentTOP {
  public ASTMCWildcardTypeArgument(){
    super();
  }

  // TODO RE: Mir ist nicht klar wozu diese Methode dient, aber auch hier
  // wäre eigentlich manchmal ein Type vorhanden.-
  // Warum da "empty" zurück kommt ist mir schleierhaft.
  // entfernbar?
  // (dann könte man sich nämlich viele der TOP Klassen sparen) 
  public Optional<ASTMCType> getMCTypeOpt() {
    return Optional.empty();
  }
}
