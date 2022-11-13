/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TypeCheckSettings {

  protected BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);
  protected BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);

  protected BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
  protected BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

  protected BigDecimal MAX_DOUBLE = BigDecimal.valueOf(Double.MAX_VALUE);
  protected BigDecimal MIN_DOUBLE = BigDecimal.valueOf(Double.MIN_VALUE);

  protected BigDecimal MAX_FLOAT = BigDecimal.valueOf(Float.MAX_VALUE);
  protected BigDecimal MIN_FLOAT = BigDecimal.valueOf(Float.MIN_VALUE);

  protected BigInteger MAX_SHORT = BigInteger.valueOf(Short.MAX_VALUE);
  protected BigInteger MIN_SHORT = BigInteger.valueOf(Short.MIN_VALUE);

  protected BigInteger MAX_BYTE = BigInteger.valueOf(Byte.MAX_VALUE);
  protected BigInteger MIN_BYTE = BigInteger.valueOf(Byte.MIN_VALUE);

  protected BigInteger MAX_CHAR = BigInteger.valueOf(Character.MAX_VALUE);
  protected BigInteger MIN_CHAR = BigInteger.valueOf(Character.MIN_VALUE);

  public void setMAX_INT(BigInteger MAX_INT) {
    this.MAX_INT = MAX_INT;
  }

  public BigInteger getMAX_INT() {
    return MAX_INT;
  }

  public void setMIN_INT(BigInteger MIN_INT) {
    this.MIN_INT = MIN_INT;
  }

  public BigInteger getMIN_INT() {
    return MIN_INT;
  }

  public void setMAX_BYTE(BigInteger MAX_BYTE) {
    this.MAX_BYTE = MAX_BYTE;
  }

  public BigInteger getMAX_BYTE() {
    return MAX_BYTE;
  }

  public void setMIN_BYTE(BigInteger MIN_BYTE) {
    this.MIN_BYTE = MIN_BYTE;
  }

  public BigInteger getMIN_BYTE() {
    return MIN_BYTE;
  }

  public void setMAX_CHAR(BigInteger MAX_CHAR) {
    this.MAX_CHAR = MAX_CHAR;
  }

  public BigInteger getMAX_CHAR() {
    return MAX_CHAR;
  }

  public void setMIN_CHAR(BigInteger MIN_CHAR) {
    this.MIN_CHAR = MIN_CHAR;
  }

  public BigInteger getMIN_CHAR() {
    return MIN_CHAR;
  }

  public void setMAX_DOUBLE(BigDecimal MAX_DOUBLE) {
    this.MAX_DOUBLE = MAX_DOUBLE;
  }

  public BigDecimal getMAX_DOUBLE() {
    return MAX_DOUBLE;
  }

  public void setMIN_DOUBLE(BigDecimal MIN_DOUBLE) {
    this.MIN_DOUBLE = MIN_DOUBLE;
  }

  public BigDecimal getMIN_DOUBLE() {
    return MIN_DOUBLE;
  }

  public void setMAX_FLOAT(BigDecimal MAX_FLOAT) {
    this.MAX_FLOAT = MAX_FLOAT;
  }

  public BigDecimal getMAX_FLOAT() {
    return MAX_FLOAT;
  }

  public void setMIN_FLOAT(BigDecimal MIN_FLOAT) {
    this.MIN_FLOAT = MIN_FLOAT;
  }

  public BigDecimal getMIN_FLOAT() {
    return MIN_FLOAT;
  }

  public void setMAX_LONG(BigInteger MAX_LONG) {
    this.MAX_LONG = MAX_LONG;
  }

  public BigInteger getMAX_LONG() {
    return MAX_LONG;
  }

  public void setMIN_LONG(BigInteger MIN_LONG) {
    this.MIN_LONG = MIN_LONG;
  }

  public BigInteger getMIN_LONG() {
    return MIN_LONG;
  }

  public void setMAX_SHORT(BigInteger MAX_SHORT) {
    this.MAX_SHORT = MAX_SHORT;
  }

  public BigInteger getMAX_SHORT() {
    return MAX_SHORT;
  }

  public void setMIN_SHORT(BigInteger MIN_SHORT) {
    this.MIN_SHORT = MIN_SHORT;
  }

  public BigInteger getMIN_SHORT() {
    return MIN_SHORT;
  }
}
