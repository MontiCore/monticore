/* (c) https://github.com/MontiCore/monticore */
statechart Substates {

    state Top {

      state One;
      state Two;
      state Three;

      A -> B : method();
      B -> C : method();
      C -> D : method();
    }
}
