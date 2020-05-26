/* (c) https://github.com/MontiCore/monticore */
package g2;

import g2._ast.ASTSBuilder;

public class G2Mill extends G2MillTOP {

  public static void initMe(G2Mill a) {
    G2MillTOP.initMe(a);
  }
    
  protected G2Mill () { super(); }

  protected ASTSBuilder _sBuilder() {
     ASTSBuilder sb = super._sBuilder();
     // add extra c:T objects
     sb.addC(tBuilder().build());
     sb.addC(tBuilder().build());
     sb.addC(tBuilder().build());
     return sb;
  } 

  public static g1._ast.ASTTBuilder tBuilder() { 
    return G2MillTOP.tBuilder();
  }
     
  public static void init() { 
    G2MillTOP.init();
  }

}

