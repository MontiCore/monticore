/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

public interface IDeSer <T> {

  String serialize (T toSerialize);

  T deserialize (String serialized);

}
