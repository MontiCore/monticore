/* (c) https://github.com/MontiCore/monticore */
statechart sc {
  state Green {
    -intern> : timeout / { beep(); };
  }
}
