/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package freemarker.core;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class Argument {
  
  String type;
  
  String name;
  
  /**
   * Constructor for freemarker.core.Argument
   * 
   * @param type
   * @param name
   */
  public Argument(String type, String name) {
    super();
    this.type = type;
    this.name = name;
  }
  
  /**
   * @return type
   */
  public String getType() {
    return this.type;
  }
  
  /**
   * @return name
   */
  public String getName() {
    return this.name;
  }
  
}
