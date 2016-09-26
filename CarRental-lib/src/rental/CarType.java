package rental;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CarType implements Serializable{
    
    @Id
    private String id;
    
    private String name;
    private String companyName;
    private int nbOfSeats;
    private boolean smokingAllowed;
    private double rentalPricePerDay;
    //trunk space in liters
    private float trunkSpace;
    
    /***************
     * CONSTRUCTOR *
     ***************/
    
    public CarType() {
        
    }
    
    public CarType(String companyName, String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        setCompanyName(companyName);
        setName(name);
        setNbOfSeats(nbOfSeats);
        setTrunkSpace(trunkSpace);
        setRentalPricePerDay(rentalPricePerDay);
        setSmokingAllowed(smokingAllowed);
        
        setId();
    }
    
    public static String generateId(String companyName, String name) {
        return (companyName + "_" + name);
    }
    
    public String getId() {
        return id;
    }
    
    public void setId() {
        this.id = generateId(getCompanyName(),getName());
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getNbOfSeats() {
        return nbOfSeats;
    }
    
    public void setNbOfSeats(int nbOfSeats) {
        this.nbOfSeats = nbOfSeats;
    }
    
    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }
    
    public void setSmokingAllowed(boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }
    
    public void setRentalPricePerDay (double rentalPricePerDay) {
        this.rentalPricePerDay = rentalPricePerDay;
    }
    
    public float getTrunkSpace() {
    	return trunkSpace;
    }
    
    public void setTrunkSpace(float trunkSpace) {
        this.trunkSpace = trunkSpace;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
    	return String.format("Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]" , 
                getName(), getNbOfSeats(), getRentalPricePerDay(), isSmokingAllowed(), getTrunkSpace());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
	if (obj == null)
            return false;
	if (getClass() != obj.getClass())
            return false;
	CarType other = (CarType) obj;
	if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
	return true;
    }
}