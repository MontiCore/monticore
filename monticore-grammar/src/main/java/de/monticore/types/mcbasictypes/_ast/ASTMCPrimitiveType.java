
package de.monticore.types.mcbasictypes._ast;


import com.google.common.collect.Lists;

import java.util.List;

public  class ASTMCPrimitiveType extends ASTMCPrimitiveTypeTOP {

  public ASTMCPrimitiveType() {
  }

  public ASTMCPrimitiveType(int primitive) {
    super(primitive);
  }

  public String toString(){
    if (isBoolean()){
      return "boolean";
    }
    if (isByte()){
      return "byte";
    }
    if (isChar()){
      return "char";
    }
    if (isShort()){
      return "short";
    }
    if (isInt()){
      return "int";
    }
    if (isFloat()){
      return "float";
    }
    if (isLong()){
      return "long";
    }
    if (isDouble()){
      return "double";
    }
    return "";
  }
  public boolean isBoolean(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.BOOLEAN;
  }
  public boolean isByte(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.BYTE;
  }
  public boolean isChar(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.CHAR;
  }
  public boolean isShort(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.SHORT;
  }
  public boolean isInt(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.INT;
  }
  public boolean isFloat(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.FLOAT;
  }
  public boolean isLong(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.LONG;
  }
  public boolean isDouble(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.DOUBLE;
  }

  public String getBaseName() {
    return toString();
  }

  public List<String> getNameList() {
    return Lists.newArrayList(toString());
  }
}
