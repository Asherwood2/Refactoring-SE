package edu.uca.registration;

import edu.uca.registration.exception.ValidationException;
import edu.uca.registration.util.Validator;
import org.junit.Test;

public class ValidatorTest {
    
    @Test
    public void testValidBannerId() {
        // IDs should not throw excep.
        Validator.validateBannerId("B001");
        Validator.validateBannerId("B123");
    }
    
    @Test(expected = ValidationException.class)
    public void testInvalidBannerId() {
        Validator.validateBannerId("invalid");
    }
    
    @Test
    public void testValidEmail() {
        Validator.validateEmail("test@uca.edu");
    }
    
    @Test(expected = ValidationException.class)
    public void testInvalidEmail() {
        Validator.validateEmail("notanemail");
    }
    
    @Test
    public void testValidCapacity() {
        Validator.validateCapacity(1);
        Validator.validateCapacity(50);
        Validator.validateCapacity(500);
    }
    
    @Test(expected = ValidationException.class)
    public void testInvalidCapacityTooLow() {
        Validator.validateCapacity(0);
    }
    
    @Test(expected = ValidationException.class)
    public void testInvalidCapacityTooHigh() {
        Validator.validateCapacity(501);
    }
}