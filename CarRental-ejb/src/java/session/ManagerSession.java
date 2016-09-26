package session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager entityManager;
    
    @Override
    public Set<String> getAllRentalCompanies() {
        List<String> resultList = entityManager.createNamedQuery("allCarRentalCompanyNames")
                .getResultList()
                ;
        
        if (resultList == null) {
            return new HashSet<String>();
        }
        
        return new HashSet<String>(resultList);
    }

    @Override
    public Set<CarType> getAvailableCarTypes(String company) {
        List<CarType> resultList = entityManager.createNamedQuery("allCarTypesOfCompany")
                .setParameter("companyName", company)
                .getResultList()
                ;
        
        if (resultList == null) {
            return new HashSet<CarType>();
        }
        
        return new HashSet<CarType>(resultList);
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        List<Integer> resultList = entityManager.createNamedQuery("allCarIdsOfType")
                .setParameter("companyName", company)
                .setParameter("carTypeName", type)
                .getResultList()
                ;
        
        if (resultList == null) {
            return new HashSet<Integer>();
        }
        
        return new HashSet<Integer>(resultList);
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        List<Reservation> resultList = entityManager.createNamedQuery("allReservationsForCarId")
                .setParameter("companyName", company)
                .setParameter("carId", id)
                .getResultList()
                ;
        
        if (resultList == null) {
            return 0;
        }
        
        return resultList.size();
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        List<Reservation> resultList = entityManager.createNamedQuery("allReservationsForCarType")
                .setParameter("companyName", company)
                .setParameter("carTypeName", type)
                .getResultList()
                ;
        
        if (resultList == null) {
            return 0;
        }
        
        return resultList.size();
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        List<Reservation> resultList = entityManager.createNamedQuery("allReservationsForRenter")
                .setParameter("carRenter", renter)
                .getResultList()
                ;
        
        if (resultList == null) {
            return 0;
        }
        
        return resultList.size();
    }

    @Override
    public String getMostPopularCompany()
            throws IllegalStateException {
        List<Object[]> resultList = entityManager.createNamedQuery("mostPopularCarRentalCompanies")
                .getResultList()
                ;
        if (resultList == null || resultList.isEmpty()) {
            throw new IllegalStateException("no companies");
        }
        
        return (String) resultList.get(0)[0];
    }

    @Override
    public CarType getMostPopularCarTypeOfCompany(String carRentalCompanyName)
            throws IllegalStateException {
        List<Object[]> resultList = entityManager.createNamedQuery("mostPopularCarTypeOfCompany")
                .setParameter("companyName", carRentalCompanyName)
                .getResultList()
                ;
        if (resultList == null || resultList.isEmpty()) {
            throw new IllegalStateException("no cartypes at company " + carRentalCompanyName);
        }
        
        return (CarType) resultList.get(0)[0];
    }
    
    @Override
    public void storeCarRentalCompany(CarRentalCompany carRentalCompany) {
        List<Car> cars = carRentalCompany.getCars();
        carRentalCompany.setCars(new ArrayList<Car>());
        entityManager.persist(carRentalCompany);
        
        for (Car car : cars) {
            storeAtCompanyWithRef(car, carRentalCompany);
        }
    }
    
    @Override
    public void storeCarAtCompany(Car car, String carRentalCompanyName) {
        CarRentalCompany carRentalCompany = entityManager.find(CarRentalCompany.class, carRentalCompanyName);
        storeAtCompanyWithRef(car, carRentalCompany);
    }
    
    private void storeAtCompanyWithRef(Car car, CarRentalCompany carRentalCompany) {
        CarType carType = entityManager.find(CarType.class, car.getType().getName());
        if (carType != null) {
            car.setCarType(carType);
        }
        carRentalCompany.addCar(car);
    }   
}