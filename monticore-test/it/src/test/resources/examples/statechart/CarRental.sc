/* (c) https://github.com/MontiCore/monticore */

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
