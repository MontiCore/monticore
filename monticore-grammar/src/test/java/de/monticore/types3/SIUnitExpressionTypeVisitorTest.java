package de.monticore.types3;

import org.junit.Test;

import java.io.IOException;

public class SIUnitExpressionTypeVisitorTest extends AbstractTypeVisitorTest{

    @Test
    public void deriveFromMetersExpression() throws IOException {
        checkExpr("1m+1m", "[m]<int>");
        checkExpr("1m+1m+3m", "[m]<int>");
        checkExpr("1m-1m-19m", "[m]<int>");
        checkExpr("1m-10m+1m", "[m]<int>");

        checkExpr("1m*1m", "[m^2]<int>");
        checkExpr("1m*1m*3m", "[m^3]<int>");

        checkExpr("1m^3 / 1m", "[m^2]<int>");
        checkExpr("1m^2 / 1m", "[m]<int>");
        checkExpr("2m/1m", "[]<int>");

        checkExpr("1m * 1.0f", "[m]<float>");
        checkExpr("1m^2 * 1.0f", "[m^2]<float>");
        checkExpr("1m^3 * 1.0f", "[m^3]<float>");

        checkExpr("1m / 1.0f", "[m]<float>");
        checkExpr("1m^2 / 1.0f", "[m^2]<float>");
        checkExpr("1m^3 / 1.0f", "[m^3]<float>");

        checkExpr("1m * 1", "[m]<int>");
        checkExpr("1m^2 * 1", "[m^2]<int>");
        checkExpr("1m^3  * 1", "[m^3 ]<int>");

        checkExpr("1m / 1", "[m]<int>");
        checkExpr("1m^2 / 1", "[m^2]<int>");
        checkExpr("1m^3  / 1", "[m^3 ]<int>");
    }

    @Test
    public void testInvalidMetersExpression() throws IOException {
        checkErrorExpr("1m + 1", "");
        checkErrorExpr("1m^2 + 1", "");
        checkErrorExpr("1m^3 + 1", "");

        checkErrorExpr("1m + 1.0f", "");
        checkErrorExpr("1m^2 + 1.0f", "");
        checkErrorExpr("1m^3 + 1.0f", "");

        checkErrorExpr("1m - 1", "");
        checkErrorExpr("1m^2 - 1", "");
        checkErrorExpr("1m^3 - 1", "");

        checkErrorExpr("1m - 1.0f", "");
        checkErrorExpr("1m^2 - 1.0f", "");
        checkErrorExpr("1m^3 - 1.0f", "");
    }

    @Test
    public void deriveFromSecondsExpression() throws IOException {
        checkExpr("1s+1s", "[s]<int>");
        checkExpr("1s+1s+3s", "[s]<int>");
        checkExpr("1s-1s-19s", "[s]<int>");
        checkExpr("1s-10s+1s", "[s]<int>");

        checkExpr("1s * 1.0f", "[s]<float>");
        checkExpr("1s / 1.0f", "[s]<float>");

        checkExpr("1s * 1", "[s]<int>");
        checkExpr("1s / 1", "[s]<int>");
        checkExpr("2s/1s", "[]<int>");
    }

    @Test
    public void testInvalidSecondsExpression() throws IOException {
        checkErrorExpr("1s*1s", "");
        checkErrorExpr("1s*1s*3s", "");
        checkErrorExpr("1s + 1", "");

        checkErrorExpr("1^s2 + 1", "");
        checkErrorExpr("1^s3 + 1", "");

        checkErrorExpr("1s + 1.0f", "");
        checkErrorExpr("1^s2 + 1.0f", "");
        checkErrorExpr("1^s3 + 1.0f", "");

        checkErrorExpr("1s - 1", "");
        checkErrorExpr("1s^2 - 1", "");
        checkErrorExpr("1s^3 - 1", "");

        checkErrorExpr("1s - 1.0f", "");
        checkErrorExpr("1s^2 - 1.0f", "");
        checkErrorExpr("1s^3 - 1.0f", "");
    }

    @Test
    public void deriveFromMetersSecondsExpression() throws IOException {
        checkExpr("1m/1s", "[m/s]<int>");
        checkExpr("1m^2 / 1s", "[m^2/s]<int>");
        checkExpr("1m^3 / 1s", "[m^3/s]<int>");

        checkExpr("1m/1s*1.0f", "[m/s]<float>");
        checkExpr("1m^2/1s*1.0f", "[m^2/s]<float>");
        checkExpr("1m^3/1s*1.0f", "[m^3/s]<float>");

        checkExpr("1m/s + 2m/s", "[m/s]<int>");
        checkExpr("1m/s - 2m/s", "[m/s]<int>");
        checkExpr("1m/s + 2m/s - 3m/s", "[m/s]<int>");

        checkExpr("1m^2/s + 2m^2/s", "[m^2/s]<int>");
        checkExpr("1m^2/s - 2m^2/s", "[m^2/s]<int>");
        checkExpr("1m^2/s + 2m^2/s - 3m^2/s", "[m^2/s]<int>");

        checkExpr("1m^3/s + 2m^3/s", "[m^3/s]<int>");
        checkExpr("1m^3/s - 2m^3/s", "[m^3/s]<int>");
        checkExpr("1m^3/s + 2m^3/s - 3m^3/s", "[m^3/s]<int>");

        checkExpr("1m^2/s + 1m^2/s", "[m^2/s]<int>");
        checkExpr("1m^3 / 1s", "[m^3/s]<int>");
        checkExpr("1m/1s*1.0f", "[m/s]<float>");
        checkExpr("1m^2/1s*1.0f", "[m^2/s]<float>");
        checkExpr("1m^3/1s*1.0f", "[m^3/s]<float>");

    }

    @Test
    public void testInvalidMetersSecondsExpression() throws IOException {
        checkErrorExpr("1m+1s+3s", "");
        checkErrorExpr("1s-1m-19s", "");
        checkErrorExpr("1m-10s+1s", "");
        checkErrorExpr("1m-10s+1s^2", "");
        checkErrorExpr("1m-10s+1s^3", "");

        checkErrorExpr("2s/1m", "");
        checkErrorExpr("2s^2/1m", "");
        checkErrorExpr("2s^3/1m", "");

        checkErrorExpr("2s/1m^2", "");
        checkErrorExpr("2s^2/1m^2", "");
        checkErrorExpr("2s^3/1m^2", "");

        checkErrorExpr("2s/1m^3", "");
        checkErrorExpr("2s^2/1m^3", "");
        checkErrorExpr("2s^3/1m^3", "");

        checkErrorExpr("2s*1m", "");
        checkErrorExpr("2s^2*1m", "");
        checkErrorExpr("2s^3*1m", "");

        checkErrorExpr("2s*1m^2", "");
        checkErrorExpr("2s^2*1m^2", "");
        checkErrorExpr("2s^3*1m^2", "");

        checkErrorExpr("2s*1m^3", "");
        checkErrorExpr("2s^2*1m^3", "");
        checkErrorExpr("2s^3*1m^3", "");

        checkErrorExpr("1m^3 * 1s + 1.0f", "");
        checkErrorExpr("1m^3 * 1s - 1.0f", "");
        checkErrorExpr("1m^3 * 1s * 1.0f", "");
        checkErrorExpr("1m^3 * 1s / 1.0f", "");

    }


}
