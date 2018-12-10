/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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

import de.monticore.generating.templateengine.reporting.*;
import de.se_rwth.commons.logging.Log;

/**
 * Main class 
 *
 */
public class SomeCode {
  
  /**
   * 
   */
  public static void main(String[] args) {

    // use normal logging (no DEBUG, TRACE)
    Log.init();

    // do nothing
  }
  
  // Used in 14.errorhandling as fictive example
  void aMethod() {
    Reporting.reportToDetailed("Additional info");
  }

  // Used in 14.errorhandling as fictive example
  public void processModels() {
    // disable fail quick
    Log.enableFailQuick(false);
    // iterate and process many models
    // ...
  
    // re-enable fail quick
    Log.enableFailQuick(true);
  }

}
