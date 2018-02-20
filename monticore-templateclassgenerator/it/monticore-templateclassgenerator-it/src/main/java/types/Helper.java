/* (c) https://github.com/MontiCore/monticore */
package types;

import java.util.List;

/**
 * Used for testing
 *
 * @author Jerome Pfeiffer
 * 
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
