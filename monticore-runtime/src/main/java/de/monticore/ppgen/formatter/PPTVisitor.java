/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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

package de.monticore.ppgen.formatter;

/**
 * This visitor traverse pretty printing trees with visitor clients that extend
 * the class PPTConcreteVisitor. It is used to traverse and format the PPT.
 * 
 * @author diego
 */
//STATE SMELL? needed for pretty printer generation, maybe this should be part of generator project.
@Deprecated
public class PPTVisitor {
  private PPTConcreteVisitor client;
  
  /**
   * Convenient method to start traversing the tree with a concrete visitor.
   * 
   * @param client Concrete visitor with the methods to visit each node.
   * @param n PPT node to start the traversing from.
   */
  public static void run(PPTConcreteVisitor client, PPTNode n) {
    PPTVisitor visitor = new PPTVisitor(client);
    visitor.startVisit(n);
  }
  
  /**
   * Constructor.
   * 
   * @param client Concrete visitor with the methods to visit each node.
   */
  public PPTVisitor(PPTConcreteVisitor client) {
    this.client = client;
  }
  
  /**
   * Calls the traverse method on the node.
   * 
   * @param n Node to start to visit.
   */
  public void startVisit(PPTNode n) {
    n.traverse(this);
  }
  
  /**
   * Calls the corresponding visit methods in the concrete visitor for the node.
   * 
   * @param n Node to visit.
   */
  public void visit(PPTNode n) {
    if (n instanceof PPTRule)
      client.visit((PPTRule) n);
    else if (n instanceof PPTLexRule)
      client.visit((PPTLexRule) n);
    else if (n instanceof PPTTerminal)
      client.visit((PPTTerminal) n);
    else if (n instanceof PPTConstantGroup)
      client.visit((PPTConstantGroup) n);
    else if (n instanceof PPTVariable)
      client.visit((PPTVariable) n);
    else if (n instanceof PPTComment)
      client.visit((PPTComment) n);
    else if (n instanceof PPTText)
      client.visit((PPTText) n);
  }
}
