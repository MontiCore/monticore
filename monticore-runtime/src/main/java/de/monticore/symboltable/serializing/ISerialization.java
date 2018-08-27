/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public interface ISerialization<T> extends JsonSerializer<T>, JsonDeserializer<T>{
  
  public Class<T> getSerializedClass();
  
}
