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

statechart CarRental {

	state idle <<initial>>;
	
	state booking {
		state checkCars <<initial>> {
			entry: { carList = getAvailableCars(); }
		}	
		
		state noCars <<final>>;
		
		state selectCar {
			entry: { Car car = showDialog("Select car", carList); }
		}
		
		checkCars -> noCars: [carList == null]/{
			showMsg("No cars available");
		};
		
		checkCars -> selectCar: [carList != null];
	}
	
	state cancelled;
	
	state booked <<final>> {
		entry: { book(car); }
	}
	
	idle -> booking : start;
	
	booking -> cancelled: cancel;

	selectCar -> booked: confirmBooking;
	
	code {
	
		java.util.List carList;
	
		Car car; 
		
		public java.util.ArrayList getAvailableCars() throws RuntimeException {
			return null;
		}
		
		public void book(Car c) throws RuntimeException {}
		
		public Car showDialog(String msg, java.util.List carList) throws RuntimeException {
			return null;
		}
		
		public void showMsg(String msg) throws RuntimeException {
		}
		
	}
}
