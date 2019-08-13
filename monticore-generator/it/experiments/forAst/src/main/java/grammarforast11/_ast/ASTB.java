/* (c) https://github.com/MontiCore/monticore */
/* handcoded Extension in order to Implement the Observer Interface */

package grammarforast11._ast;

import java.util.Optional;
import de.se_rwth.commons.logging.Log;
import java.util.Iterator;
import java.util.Observable;

import java.util.*;

public class ASTB extends ASTBTOP
{
//   protected  ASTB () 
   //{
   	//ASTBTOP.super()
   //}
//

@Override
public void update(Observable o, Object arg) {
  // TODO Auto-generated method stub
  
} 
  
protected  ASTB _construct()   {      
  return new ASTB();
}

}
