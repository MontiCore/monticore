/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

public interface AssocCardinality {
  
  boolean isMult();
  
  boolean isOne();
  
  boolean isAtLeastOne();
  
  boolean isOpt();
  
  boolean isOther();
  
  void setMult(boolean x);
  
  void setOne(boolean x);
  
  void setAtLeastOne(boolean x);
  
  void setOpt(boolean x);
  
  void setOther(boolean x);
  
  default String serializeCard() {
    String res = "";
    if (isMult()) {
      res = "[*]";
    }
    else if (isOne()) {
      res = "[1]";
    }
    else if (isAtLeastOne()) {
      res = "[1..*]";
    }
    else if (isOpt()) {
      res = "[0..1]";
    }
    else if (isOther()) {
      res = "[*]";
    }
    
    return res;
  }
  
  static AssocCardinality deserializeCard(String src) {
    AssocCardinality card = new AssocCardinality() {
      boolean isMult = false;
      boolean isOne = false;
      boolean isAtLeastOne = false;
      boolean isOpt = false;
      boolean isOther = false;
      
      @Override
      public boolean isMult() {
        return isMult;
      }
      
      @Override
      public boolean isOne() {
        return isOne;
      }
      
      @Override
      public boolean isAtLeastOne() {
        return isAtLeastOne;
      }
      
      @Override
      public boolean isOpt() {
        return isOpt;
      }
      
      @Override
      public boolean isOther() {
        return isOther;
      }
      
      @Override
      public void setMult(boolean x) {
        isMult = x;
      }
      
      @Override
      public void setOne(boolean x) {
        isOne = x;
      }
      
      @Override
      public void setAtLeastOne(boolean x) {
        isAtLeastOne = x;
      }
      
      @Override
      public void setOpt(boolean x) {
        isOpt = x;
      }
      
      @Override
      public void setOther(boolean x) {
        isOther = x;
      }
    };
    
    switch (src) {
      case "[*]":
        card.setMult(true);
        break;
      case "[1]":
        card.setOne(true);
        break;
      case "[1..*]":
        card.setAtLeastOne(true);
        break;
      case "[0..1]":
        card.setOpt(true);
        break;
      default:
        card.setOther(true);
        break;
    }
    
    return card;
  }
  
}
