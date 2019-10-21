/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

import java.util.List;

public interface ASTMCType extends ASTMCTypeTOP {

  // TODO: Beschreiben was die Funktionen berechnen und liefern sollen
  // und wozu was verwendet werden kann
  // Verdacht: Manchmal sind das Values aus dem Argument zB beim Array
  // und manchmal kann da gar nichts verwertbares rauskommen, zB bei anonymen Typen
  // (anononymen java Klassen)
  
  // TODO: Zweck & nutzbarkeit erklären
  public List<String> getNameList();
  
  // TODO: Zweck & nutzbarkeit erklären
  public String getBaseName();
  
  // TODO: Völlig illegale verwebung von einer sehr abstrakten Klasse einer abstrakten Grammatik
  // mit einer ganz speziellen Grammatik, die man ja u.U. gar nicht haben und laden will.
  // Das sind genau die Arten von Querreferenzen, die Modularität völlig kaputt machen.
  // MCFullGenericTypesPrettyPrinter hat hier nichts zu suchen
  // es gibt lösungen, das zu beheben --> Rücksprache
  default public String printType() {
    
    // TODO: Es werden für jedes auszugebende Objekt jeweils eigene IndentPrinter UND MCFullGenericTypesPrettyPrinter erzeugt
    // das ist hochgradig ineffizient --> das können wir besser
    IndentPrinter printer = new IndentPrinter();

    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    this.accept(vi);
    return vi.getPrinter().getContent();
  }
  
  // TODO: Zweck & nutzbarkeit erklären
  default public String getName(){
    return String.join(".",getNameList());
  }
}
