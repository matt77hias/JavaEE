package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {
    
    @PersistenceContext
    private EntityManager entityManager;

    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        List<String> resultList = this.entityManager.createNamedQuery("allCarRentalCompanyNames")
                .getResultList()
                ;
        
        if (resultList == null) {
            return new HashSet<String>();
        }
        
        return new HashSet<String>(resultList);
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List<CarRentalCompany> resultList = this.entityManager.createNamedQuery("allCarRentalCompanies")
                .getResultList()
                ;
        
        if (resultList == null) {
            resultList = new ArrayList<CarRentalCompany>();
        }
        
        List<CarType> availableCarTypes = new LinkedList<CarType>();
        for(CarRentalCompany crc : resultList) {
            for(CarType ct : crc.getAvailableCarTypes(start, end)) {
                if(!availableCarTypes.contains(ct))
                    availableCarTypes.add(ct);
            }
        }
        return availableCarTypes;
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) 
            throws ReservationException {
        CarRentalCompany carRentalCompany = entityManager.find(CarRentalCompany.class, company);
        
        if (carRentalCompany == null) {
            throw new ReservationException("Company " + company + " doesn't exist");
        }
        
        Quote out = carRentalCompany.createQuote(constraints, renter);
        quotes.add(out);
        return out;
    }
    
    @Resource
    private SessionContext context;

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            for (Quote quote : quotes) {
                CarRentalCompany carRentalCompany = entityManager.find(CarRentalCompany.class, quote.getRentalCompany());
                done.add(carRentalCompany.confirmQuote(quote));
            }
        } catch (ReservationException e) {
            context.setRollbackOnly();
            throw e;
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("The name of this renter is already set.");
        }
        renter = name;
    }
}