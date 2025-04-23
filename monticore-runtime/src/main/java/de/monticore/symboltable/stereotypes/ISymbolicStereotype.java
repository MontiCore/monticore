/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.stereotypes;

import de.monticore.symboltable.ISymbol;

import java.util.List;

public interface ISymbolicStereotype extends ISymbol {

  Class<? extends ISymbol> getAnnotatedElement();

  List<StereoValueType> getAllowedValueTypesList();
}
