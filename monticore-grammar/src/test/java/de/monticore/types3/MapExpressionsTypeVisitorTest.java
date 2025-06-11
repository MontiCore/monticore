package de.monticore.types3;

import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.util.DefsVariablesForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MapExpressionsTypeVisitorTest extends AbstractTypeVisitorTest {
  
  @BeforeEach
  public void setup() {
    MCCollectionSymTypeRelations.init();
    DefsVariablesForTests.setup();
  }
  
  @ParameterizedTest
  @MethodSource
  public void deriveFromMap(String exprStr, String expectTypeStr) throws IOException {
    checkExpr(exprStr, expectTypeStr);
  }
  
  protected static Stream<Arguments> deriveFromMap() {
    return Stream.of(
        // examples with int keys
        arguments("[1 :> 2]", "Map<int,int>"), arguments("[1 :> 2,2 :> 3,3 :> 4]", "Map<int,int>"),
        arguments("[1 :> 2.3]", "Map<int,double>"),
        arguments("[1 :> 2.3,3 :> 4.5]", "Map<int,double>"),
        arguments("[1 :> 2.3,3 :> 4.5,5 :> 6]", "Map<int,double>"),
        arguments("[1 :> 2.3f,3 :> 4.5,5 :> 6]", "Map<int,double>"),
        arguments("[1 :> 'a',2 :> 'b']", "Map<int,char>"),
        arguments("[1 :> (char)1, 2 :> (byte)1, 3 :> (short)1, 4 :> (int)1, 5 :> (float)1]",
            "Map<int,float>"), arguments("[1 :> \"1\",2 :> 1]", "Map<int,(R\"1\" | int)>"),
        arguments("[1 :> \"1\", 2 :> varPerson]", "Map<int,(Person | R\"1\")>"),
        arguments("[1 :> varPerson, 2 :> varCar]", "Map<int,(Car | Person)>"),
        // TODO: Enable when wildcard capturer is fixed
        //arguments("[1 :> () -> 5]", "Map<int,(() -> int)>"),
        //arguments("[1 :> (int x) -> 2*x]", "Map<int,(int -> int)>"),
        
        // examples with complex keys
        // TODO: Enable when wildcard capturer is fixed
        //arguments("[(int x) -> 2*x :> varCar]", "Map<(int -> int),Car>"),
        arguments("[varPerson :> varCar]", "Map<Person,Car>"),
        arguments("[true?1:2 :> false?3:4]", "Map<int,int>"),
        
        // examples with nested collections
        arguments("[1 :> [9 :> 8,7 :> 6], 2 :> [5 :> 4]]", "Map<int,Map<int,int>>"),
        arguments("[1 :> [9,8,7], 2 :> [6,5,4]]", "Map<int,List<int>>"),
        arguments("[[1,2] :> [3,4], [5,6] :> [7,8]]", "Map<List<int>,List<int>>")
        
        // currently not supported -> to complex
        //arguments("[1 :> [\"A\" :> 9,\"B\" :> 8], 2 :> [\"C\" :> 7]]", "Map<int,(Map<(R\"A\" | R\"B\"),int> | Map<(R\"C\"),int>>"),
        
        // currently not supported -> SetExpressions currently do not support lambdas
        // TODO: Enable when wildcard capturer is fixed
        //arguments("[1 :> [() -> 42], 2 :> [(int x) -> 2*x]]", "Map<int,((()->int)|(int->int)))>")
    );
  }
  
  @ParameterizedTest
  @MethodSource
  public void deriveFromMapCTTI(String exprStr, String targetTypeStr, String expectTypeStr)
      throws IOException {
    checkExpr(exprStr, targetTypeStr, expectTypeStr);
  }
  
  protected static Stream<Arguments> deriveFromMapCTTI() {
    return Stream.of(
        // without values
        arguments("Map[]", "Map<int,int>", "Map<int,int>"),
        arguments("Map[]", "Map<int,Person>", "Map<int,Person>"),
        arguments("Map[]", "Map<Person,int>", "Map<Person,int>"),
        arguments("Map[]", "Map<Car,Person>", "Map<Car,Person>"),
        arguments("Map[]", "Map<Car,? extends Person>", "Map<Car,Person>"),
        arguments("Map[]", "Map<Car,? super Person>", "Map<Car,Person>"),
        arguments("[1 :> Map[]]", "Map<int,Map<int,int>>", "Map<int,Map<int,int>>"),
        arguments("[1 :> Map[]]", "Map<int,Map<Car,Person>>", "Map<int,Map<Car,Person>>"),
        // with values
        arguments("[1 :> 2]", "Map<int,int>", "Map<int,int>"),
        arguments("[1 :> 2]", "Map<int,float>", "Map<int,float>"));
  }
  
  protected static Stream<Arguments> deriveFromMapWithBoxingCTTI() {
    return Stream.of(
        arguments("[1 :> 2]", "Map<int, java.lang.Integer>", "Map<int,java.lang.Integer>"),
        arguments("[1 :> 2]", "Map<java.lang.Float, int>", "Map<java.lang.Float,int>"));
  }
  
  @ParameterizedTest
  @MethodSource
  public void deriveFromMapWithBoxingCTTI(String exprStr, String targetTypeStr,
      String expectTypeStr) throws IOException {
    checkExpr(exprStr, targetTypeStr, expectTypeStr);
  }
}
