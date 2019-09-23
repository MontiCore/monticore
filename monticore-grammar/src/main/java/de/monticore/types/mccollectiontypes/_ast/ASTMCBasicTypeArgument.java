/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTMCBasicTypeArgument extends ASTMCBasicTypeArgumentTOP {

  public ASTMCBasicTypeArgument(){
    super();
  }
  
  // TODO RE: Kann doch gar nicht optional sein: Methode & Klasse entfernen!
  // TODO BR: Optional ergibt sich aus dem ASTMCTypeArgument Interface, dass
  // wiederrum ist notwendig weil die größten Types die Implementierung
  // ASTMCWildcardTypeArgument liefern, und hier ist der Typ tatsächlich optional
  // sähe dann im Java Code so aus: List<?>
  public Optional<ASTMCType> getMCTypeOpt(){
    return Optional.ofNullable(getMCQualifiedType());
  }
}
