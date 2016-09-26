package rental;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;


@NamedQueries({
    
@NamedQuery(
    name = "allCarRentalCompanies",
    query= "SELECT company FROM CarRentalCompany company"
),
    
@NamedQuery(
    name = "allCarRentalCompanyNames",
    query= "SELECT company.name FROM CarRentalCompany company"
),
    
@NamedQuery(
    name = "allCarTypesOfCompany",
    query= "SELECT DISTINCT car.type FROM Car car, CarRentalCompany company "
        + "WHERE car MEMBER OF company.cars "
        + "AND company.name = :companyName"
),

@NamedQuery(
    name = "allCarIds",
    query= "SELECT car.id FROM Car car, CarRentalCompany company "
        + "WHERE company.name = :companyName "
        + "AND car MEMBER OF company.cars"
),

@NamedQuery(
    name = "allCarIdsOfType",
    query= "SELECT car.id FROM Car car, CarRentalCompany company "
        + "WHERE company.name = :companyName "
        + "AND car MEMBER OF company.cars "
        + "AND car.type.name = :carTypeName"
),

@NamedQuery(
    name = "allReservationsForCarId",
    query= "SELECT reservation FROM Reservation reservation, CarRentalCompany company, Car car "
        + "WHERE company.name = :companyName "
        + "AND car.id = :carId "
        + "AND reservation MEMBER OF car.reservations"
),

@NamedQuery(
    name = "allReservationsForCarType",
    query= "SELECT reservation FROM Reservation reservation, Car car "
        + "WHERE reservation.rentalCompany = :companyName "
        + "AND car.type.name = :carTypeName "
        + "AND reservation MEMBER OF car.reservations"
),

@NamedQuery(
    name = "mostPopularCarRentalCompanies",
    query= "SELECT company.name, COUNT(reservation) AS total FROM CarRentalCompany company, Reservation reservation "
        + "WHERE reservation.rentalCompany = company.name "
        + "GROUP BY company.name "
        + "ORDER BY total DESC"
),

@NamedQuery(
    name = "mostPopularCarTypeOfCompany",
    query= "SELECT carType, COUNT(carType) AS total FROM Reservation reservation, CarType carType "
        + "WHERE reservation.rentalCompany = :companyName "
            +"AND carType.companyName = :companyName "
        + "GROUP BY carType "
        + "ORDER BY total DESC"
)
})

@Entity
public class CarRentalCompany implements Serializable {

    private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
    @Id
    private String name;
    @OneToMany(cascade= CascadeType.ALL)
    private List<Car> cars = new ArrayList<Car>();
    @Transient
    private Set<CarType> carTypes = new HashSet<CarType>();

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public CarRentalCompany() {
    }
    
    public CarRentalCompany(String name, List<Car> cars) {
        logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
        setName(name);
        this.cars = cars;
        initializeCarTypes();
    }
    
    private void initializeCarTypes() {
        for (Car car : cars) {
            carTypes.add(car.getType());
        }
    }

    /********
     * NAME *
     ********/
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*************
     * CAR TYPES *
     *************/
    
    public Collection<CarType> getAllTypes() {
        return carTypes;
    }

    public CarType getType(String carTypeName) {
        for(CarType type:carTypes){
            if(type.getName().equals(carTypeName))
                return type;
        }
        throw new IllegalArgumentException("<" + carTypeName + "> No cartype of name " + carTypeName);
    }

    public boolean isAvailable(String carTypeName, Date start, Date end) {
        logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
        return getAvailableCarTypes(start, end).contains(getType(carTypeName));
    }

    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<CarType>();
        for (Car car : cars) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType());
            }
        }
        return availableCarTypes;
    }

    /*********
     * CARS *
     *********/
    
    public Car getCar(int uid) {
        for (Car car : cars) {
            if (car.getId() == uid) {
                return car;
            }
        }
        throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
    }

    public Set<Car> getCars(CarType type) {
        Set<Car> out = new HashSet<Car>();
        for (Car car : cars) {
            if (car.getType().equals(type)) {
                out.add(car);
            }
        }
        return out;
    }
    
     public Set<Car> getCars(String type) {
        Set<Car> out = new HashSet<Car>();
        for (Car car : cars) {
            if (type.equals(car.getType().getName())) {
                out.add(car);
            }
        }
        return out;
    }

    private List<Car> getAvailableCars(String carType, Date start, Date end) {
        List<Car> availableCars = new LinkedList<Car>();
        for (Car car : cars) {
            if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }
    public List<Car> getCars() {
        return cars;
    }
    
    public void setCars(List<Car> cars) {
        this.cars = cars;
        initializeCarTypes();
    }
    
    public void addCar(Car car) {
        cars.add(car);
        carTypes.add(car.getType());
    }

    /****************
     * RESERVATIONS *
     ****************/
    
    public Quote createQuote(ReservationConstraints constraints, String guest)
            throws ReservationException {
        logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
                new Object[]{name, guest, constraints.toString()});

        CarType type = getType(constraints.getCarType());

        if (!isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate())) {
            throw new ReservationException("<" + name
                    + "> No cars available to satisfy the given constraints.");
        }

        double price = calculateRentalPrice(type.getRentalPricePerDay(), constraints.getStartDate(), constraints.getEndDate());

        return new Quote(guest, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
    }
 
    // Implementation can be subject to different pricing strategies
    private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
        return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
                / (1000 * 60 * 60 * 24D));
    }

    public Reservation confirmQuote(Quote quote) throws ReservationException {
        logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
        List<Car> availableCars = getAvailableCars(quote.getCarType().toString(), quote.getStartDate(), quote.getEndDate());
        if (availableCars.isEmpty()) {
            throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
                    + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
        }
        Car car = availableCars.get((int) (Math.random() * availableCars.size()));

        Reservation res = new Reservation(quote, car.getId());
        car.addReservation(res);
        return res;
    }

    public void cancelReservation(Reservation res) {
        logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
        getCar(res.getCarId()).removeReservation(res);
    }
    
    public Set<Reservation> getReservationsBy(String renter) {
        logger.log(Level.INFO, "<{0}> Retrieving reservations by {1}", new Object[]{name, renter});
        Set<Reservation> out = new HashSet<Reservation>();
        for(Car c : cars) {
            for(Reservation r : c.getReservations()) {
                if(r.getCarRenter().equals(renter))
                    out.add(r);
            }
        }
        return out;
    }
}