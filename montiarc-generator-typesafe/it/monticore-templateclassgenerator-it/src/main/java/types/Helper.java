/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package types;

import java.util.List;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class Helper {
  
  public static String printAttributes(List<Attribute> attributes) {
    String ret = "";
    
    for(Attribute a : attributes){
      ret += a.getType()+" "+a.getName()+", ";
    }
    if(ret.contains(",")){
      ret = ret.substring(0,ret.lastIndexOf(","));
    }
    return ret;
    
  }
}
