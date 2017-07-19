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

// this is a very user friendly console appender
appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%-7([%level]) %message%n%exception{0}"
  }
}

// this is a rather technically detailed file appender
appender("MAIN", FileAppender) {
  file = "target/test.log"
  append = false
  encoder(PatternLayoutEncoder) {
    pattern = "%date{yyyy-MM-dd HH:mm:ss} %-7([%level]) %logger{26} %message%n"
  }
}

// everything with level >= DEBUG is logged to the file (see above)
root(DEBUG, ["MAIN"])

// the dedicated logger called "USER" logs everything with level >= TRACE to the console
logger("USER", TRACE, ["CONSOLE"])
