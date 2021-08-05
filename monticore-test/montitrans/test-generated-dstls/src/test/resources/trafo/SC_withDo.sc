/* (c) https://github.com/MontiCore/monticore */
statechart $sc {
    state $s {
      [ isInNormalMode() ]
      do : { foo(); }
    }
}
