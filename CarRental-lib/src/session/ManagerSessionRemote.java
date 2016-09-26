package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {
    
    public Set<String> getAllRentalCompanies();
    
    public Set<CarType> getAvailableCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
      
    public int getNumberOfReservationsBy(String renter);
    
    public void storeCarRentalCompany(CarRentalCompany carRentalCompany);
    
    public void storeCarAtCompany(Car car, String carRentalCompanyName);
    
    public String getMostPopularCompany()
            throws IllegalStateException;
    
    public CarType getMostPopularCarTypeOfCompany(String carRentalCompanyName)
            throws IllegalStateException;
}