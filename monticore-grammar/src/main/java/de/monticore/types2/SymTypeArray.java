package de.monticore.types2;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Arrays of a certain dimension (>= 1)
 */
public class SymTypeArray extends SymTypeExpression {
  
  /**
   * An arrayType has a dimension
   */
  protected int dim;
  
  /**
   * An Array has an argument Type
   */
  protected SymTypeExpression argument;
  
  public SymTypeArray(int dim, SymTypeExpression argument) {
    this.dim = dim;
    this.argument = argument;
    this.setTypeInfo(arrayTypeSymbol);
  }
  
  /**
   * This is a predefined Symbol describing the
   * externally accessible Fields, Methods, etc. of an array
   *
   * We deliberately choose Java 10 here and take methods from:
   * https://docs.oracle.com/javase/10/docs/api/java/util/Arrays.html
   *
   * TODO : 1) Besseres Verfahren überlegen, wie man Vordefinierte Signaturen einliest
   *          (zB durch handgeschriebene Symtabs, die man der RTE beilegt,
   *          oder auch doch durch Java Code wie den hier?)
   *          Das untenstehende beispiel ist grausam schlecht)
   *
   * TODO : 2) Die Liste ist unvollständig --> irgendwie vervollstänndigen
   *       wenige übersetzte beispiele:
   *           int i[]; i.length; i.toString(); i.equals(Object i); i.wait(long 3,int 3);
   *
   * TODO : 2) Konfigurierbarkeit mit der Target-Java-Version (denn die Checks
   *        müssen nicht nach Java 10 gehen, sondern evtl. eine frühere Version ...)
   *
   */
  public static final TypeSymbol arrayTypeSymbol;
  
  static {
    TypeSymbolBuilder tb = TypeSymbolsSymTabMill.typeSymbolBuilder()
            .setName("ArrayType")           // should be unused
            .setFullName("ArrayType")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            // ?Array ist eigentlich generisch?
            .setTypeParameter(new ArrayList<>());

    // toString()
    List<MethodSymbol> methods = new ArrayList<>();
    methods.add(TypeSymbolsSymTabMill.methodSymbolBuilder()
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setName("toString")
            .setFullName("Object.toString")
            .setParameter(new ArrayList<>())
            .setReturnType(SymTypeExpressionFactory.createTypeConstant("int")) // "XXX:EigentlichObjectTypeString"))
            .build());

    // equals(Object)
    List<FieldSymbol> fieldsE = new ArrayList<>();
    fieldsE.add(TypeSymbolsSymTabMill.fieldSymbolBuilder()
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setFullName("o")
            .setName("o")
            .setType(SymTypeExpressionFactory.createTypeConstant("XXX:EigentlichObjectTypeString"))
            .build());
    methods.add(TypeSymbolsSymTabMill.methodSymbolBuilder()
            .setName("equals")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setFullName("java.lang.Object.equals")
            .setParameter(fieldsE)
            .setReturnType(SymTypeExpressionFactory.createTypeConstant("boolean"))
            .build());
    tb.setMethods(methods);
  
    List<FieldSymbol> fieldsAttr = new ArrayList<>();
    fieldsAttr.add(TypeSymbolsSymTabMill.fieldSymbolBuilder()
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setFullName("length")
            .setName("length")
            .setType(SymTypeExpressionFactory.createTypeConstant("int"))
            .build());

    tb.setFields(fieldsAttr);
    
    arrayTypeSymbol = tb.build();
  }

  // ------------------------------------------------------------------ Functions
  
  public int getDim() {
    return dim;
  }
  
  public void setDim(int dim) {
    this.dim = dim;
  }
  
  public SymTypeExpression getArgument() {
    return argument;
  }
  
  public void setArgument(SymTypeExpression argument) {
    this.argument = argument;
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    StringBuffer r = new StringBuffer(getArgument().print());
    for(int i = 1; i<=dim;i++){
      r.append("[]");
    }
    return r.toString();
  }
  
  
  // --------------------------------------------------------------------------
  
  @Override @Deprecated // and not implemented yet
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    return false;
  }
  
  @Override @Deprecated // and not implemented yet
  public SymTypeExpression deepClone() {
    return null;
  }
  
}
