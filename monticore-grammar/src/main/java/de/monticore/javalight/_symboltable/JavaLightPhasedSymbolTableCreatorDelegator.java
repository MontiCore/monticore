package de.monticore.javalight._symboltable;

import de.monticore.javalight._visitor.JavaLightTraverser;

public class JavaLightPhasedSymbolTableCreatorDelegator extends JavaLightPhasedSymbolTableCreatorDelegatorTOP {

  public JavaLightPhasedSymbolTableCreatorDelegator(IJavaLightGlobalScope globalScope){
    super(globalScope);
    this.priorityList.add(new JavaLightSTCompleteTypesDelegator().getTraverser());
  }

  public JavaLightPhasedSymbolTableCreatorDelegator(){
    super();
    this.priorityList.add(new JavaLightSTCompleteTypesDelegator().getTraverser());
  }
}
