/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import java.util.List;

public interface ASTMCGenericType extends ASTMCGenericTypeTOP {

  // TODO RE: ? diese Funktion macht bei TypExpr. wenig Sinn: Entfernbar? 
    List<String> getNameList() ;

  /* TODO RE: Das ist leider nur eine Funktion (von 30 sinnvollen
   Wäre es besser, dies in der Grammatik hinzuzufügen?
   und in der Grammatik etwas in der Art zu evrankern:
   (( oder die Funktion einfach rauszuwerfen, weil sie eh nicht hierher gehört))
 
   interface MCGenericType extends MCObjectType = MCTypeArgument*;

   MCListType implements MCGenericType <200> =
       {next("List")}? Name "<" MCTypeArgument ">" ( | "XTHISSHALLNOTPARSEX" {MCTypeArgument || ","}* )
                           
  ---------- oder alternativ:

  ast MCLIstType  ... min=1, max = 1;

  ((Selbes für Map, Set, List, Optional)
  */  
    List<ASTMCTypeArgument> getMCTypeArgumentList() ;

}
