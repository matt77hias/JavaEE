package rental;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({
@NamedQuery(
    name = "allReservationsForRenter",
    query= "SELECT reservation FROM Reservation reservation "
        + "WHERE reservation.carRenter = :carRenter "
)
})

@Entity
public class Reservation extends Quote {

    private int carId;
    
    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Reservation() {
        
    }

    public Reservation(Quote quote, int carId) {
    	super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(), 
    		quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.carId = carId;
    }
    
    /******
     * ID *
     ******/
    
    public int getCarId() {
    	return carId;
    }
    
    public void setCar(int carId) {
        this.carId = carId;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f", 
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarId(), getRentalPrice());
    }	

    private static class Car {

        public Car() {
        }
    }
}