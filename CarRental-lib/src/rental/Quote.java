package rental;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public class Quote implements Serializable {

    @Id @GeneratedValue
    private int id;
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Temporal(TemporalType.DATE)
    private Date endDate;
    private String carRenter;
    private String rentalCompany;
    private String carType;
    private double rentalPrice;
    
    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Quote() {
        
    }

    public Quote(String carRenter, Date start, Date end, String rentalCompany, String carType, double rentalPrice) {
        setCarRenter(carRenter);
        setStartDate(start);
        setEndDate(end);
        setCarRentalCompany(rentalCompany);
        setCarType(carType);
        setRentalPrice(rentalPrice);
    }

    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCarRenter() {
        return carRenter;
    }
    
    public void setCarRenter(String carRenter) {
        this.carRenter = carRenter;
    }

    public String getRentalCompany() {
        return rentalCompany;
    }
    
    public void setCarRentalCompany(String carRentalCompany) {
        this.rentalCompany = carRentalCompany;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }
    
    public void setRentalPrice(double rentalPrice) {
        this.rentalPrice = rentalPrice;
    }
    
    public String getCarType() {
	return carType;
    }
    
    public void setCarType(String carType) {
        this.carType = carType;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
        return String.format("Quote for %s from %s to %s at %s\nCar type: %s\tTotal price: %.2f", 
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getRentalPrice());
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((carRenter == null) ? 0 : carRenter.hashCode());
	result = prime * result + ((carType == null) ? 0 : carType.hashCode());
	result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
	result = prime * result + ((rentalCompany == null) ? 0 : rentalCompany.hashCode());
	long temp;
	temp = Double.doubleToLongBits(rentalPrice);
	result = prime * result + (int) (temp ^ (temp >>> 32));
	result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
	Quote other = (Quote) obj;
	if (carRenter == null) {
            if (other.carRenter != null)
                return false;
	} else if (!carRenter.equals(other.carRenter))
            return false;
	if (carType == null) {
            if (other.carType != null)
		return false;
	} else if (!carType.equals(other.carType))
            return false;
	if (endDate == null) {
            if (other.endDate != null)
		return false;
	} else if (!endDate.equals(other.endDate))
            return false;
	if (rentalCompany == null) {
            if (other.rentalCompany != null)
		return false;
	} else if (!rentalCompany.equals(other.rentalCompany))
            return false;
	if (Double.doubleToLongBits(rentalPrice) != Double.doubleToLongBits(other.rentalPrice))
            return false;
	if (startDate == null) {
            if (other.startDate != null)
		return false;
	} else if (!startDate.equals(other.startDate))
            return false;
	return true;
    }
}