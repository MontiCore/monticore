// (c) https://github.com/MontiCore/monticore
package de.monticore.simplecd;

import de.monticore.simplecd.types3.SimpleCDTypeCheck3;

public class SimpleCDMill extends SimpleCDMillTOP {

  /** additionally inits the TypeCheck */
  public static void init() {
    SimpleCDMillTOP.init();
    SimpleCDTypeCheck3.init();
  }

}
