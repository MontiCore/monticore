/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SIUnitLiteralsTypeVisitorTest extends AbstractTypeVisitorTest {

  @Test
  public void deriveTFromSIUnitLiteralsSimple() throws IOException {
    checkExpr("17s", "[s]<int>");
    checkExpr("1.0 1/m", "[1/m]<double>");
    checkExpr("30kg", "[kg]<int>");
    checkExpr("30km", "[km]<int>");
    checkExpr("30g", "[g]<int>");
    checkExpr("30 kg", "[kg]<int>");
    checkExpr("30.4 kg", "[kg]<double>");
    checkExpr("30.4kg", "[kg]<double>");
    checkExpr("30.4 kg^2", "[kg^2]<double>");
    checkExpr("30 g^2", "[g^2]<int>");
    checkExpr("30 km^2", "[km^2]<int>");
    checkExpr("30.4kg^2", "[kg^2]<double>");
    checkExpr("30.4s^3/kgm^2", "[s^3/kg^1m^2]<double>");
    checkExpr("30.4 s^3/kgm^2", "[s^3/kg^1m^2]<double>");
    checkExpr("30 kg/m", "[kg/m]<int>");
    checkExpr("1 h/min", "[h/min]<int>");
    checkExpr("30.4 rad", "[rad]<double>");
  }

  @Test
  public void deriveTFromSIUnitLiteralsLOrF() throws IOException {
    checkExpr("30.2f km", "[km]<float>");
    checkExpr("30.4F F", "[F]<float>");
    checkExpr("30.4F kg", "[kg]<float>");
    checkExpr("30L F", "[F]<long>");
    checkExpr("30.2 F", "[F]<double>");
    checkExpr("30F", "[F]<int>"); // questionable
    checkExpr("30 L", "[L]<int>");
    checkExpr("30.2L", "[L]<double>");
    checkExpr("30 l", "[l]<int>");
  }

  @Test
  public void deriveTFromNonSIUnitLiteralsLOrF() throws IOException {
    // they contain L or F, but shall NOT be considered SIUnitLiterals
    checkExpr("13.2F", "float");
    checkExpr("13.2f", "float");
    checkExpr("13L", "long");
    checkExpr("13l", "long");
  }



}
