/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.symboltable.mocks.languages.statechart.asts;

public interface StateChartLanguageBaseVisitor {

  public default void visit(ASTStateChartCompilationUnit node) {}
  public default void endVisit(ASTStateChartCompilationUnit node) {}

  public default void traverse(ASTStateChartCompilationUnit node) {
    visit(node);
    node.getStateChart().accept(this);
    endVisit(node);
  }

  public default void visit(ASTStateChart node) {}
  public default void endVisit(ASTStateChart node) {}

  public default void traverse(ASTStateChart node) {
    visit(node);

    node.get_Children().stream()
        .filter(child -> child instanceof ASTState)
        .forEach(child -> ((ASTState) child).accept(this));

    endVisit(node);
  }


  public default void visit(ASTState node) {}
  public default void endVisit(ASTState node) {}

  public default void traverse(ASTState node) {
    visit(node);
    // no children to traverse
    endVisit(node);

  }
}
