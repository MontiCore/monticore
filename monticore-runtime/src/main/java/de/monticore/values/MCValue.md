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