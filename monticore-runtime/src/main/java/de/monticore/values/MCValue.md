<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

MCValues describe values during runtime,
e.g., the result of an expression in an interpreter.

## Given infrastructure in MontiCore

* [MCValue](MCValue.java)
  (represents values)
    * [MCValueFactory](MCValueFactory.java)
      (creates MCValues)
    * [MCValueBoolean](MCValueBoolean.java)
      (represents booleans)
    * [MCValueInt](MCValueInt.java)
      (represents integral values)
    * [MCValueDouble](MCValueDouble.java)
      (represents doubles)
    * [MCValueObject](MCValueObject.java)
      (represents native Java objects)
    * [MCValueFunction](MCValueFunction.java)
      (represents functions)
        * [MCValueVoid](MCValueVoid.java)
          (represents void, rarely used except for function returns)
    * [MCValueError](MCValueError.java)
      (An internal error occurred or an exception was thrown)

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library
  **](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
