package com.example.segrentify;

import android.util.Patterns;

public class Account{
    // Variables to store account details
    private String firstName;  // Stores the user's first name
    private String lastName;   // Stores the user's last name
    private String email;      // Stores the user's email address (used for login)
    private String password;   // Stores the user's password for account security
    private String role;       // Stores the role of the user (e.g., Admin, Lessor, or Renter)
    private boolean disabled;
    private String id;


    public  Account(){
        disabled = false;
    }
    // Method to set the role of the user (Admin, Lessor, or Renter)
    public void setRole(String role) {
        this.role = role;
    }

    // Method to set the first name of the user, ensures only letters are used
    public void setFirstName(String firstName) throws  IllegalArgumentException{
        if(firstName.length()<2){
            throw new IllegalArgumentException("Invalid first name");
        }
        for(int i = 0; i < firstName.length(); i++) {
            if(!Character.isLetter(firstName.charAt(i))) {
               throw new IllegalArgumentException("Invalid first name");
            }
        }
        // Capitalizes the first letter of the name and makes the rest lowercase
        this.firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
    }

    // Method to set the last name of the user, ensures only letters are used
    public void setLastName(String lastName)throws  IllegalArgumentException {
        if(lastName.length()<2){
            throw new IllegalArgumentException("Invalid last name");
        }
        for(int i = 0; i < lastName.length(); i++) {
            if(!Character.isLetter(lastName.charAt(i))) {
                throw new IllegalArgumentException("Invalid last name");
            }
        }
        // Capitalizes the first letter of the name and makes the rest lowercase
        this.lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
    }

    // Method to set the password for the user, ensures no spaces are present
    // Returns true if the password is valid, false if it contains spaces
    public void setPassword(String password) throws IllegalArgumentException{
        if(password.length()<2){
            throw new IllegalArgumentException("Invalid password");
        }
        for(int i = 0; i < password.length(); i++) {
            if(password.substring(i, i + 1).equals(" ")) {
                throw new IllegalArgumentException("Invalid password");
            }
        }
        this.password = password; // Sets the password if valid
    }
    // Method to set the email for the user, ensures the email is of proper format
    // Returns true if the email is valid, false if it is not
    public void setEmail(String email) throws IllegalArgumentException{
        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() ){
            this.email = email;
            return;
        }
        throw new IllegalArgumentException("Invalid email");
    }

    public void changeDisable(){
        disabled = !disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }
    // Getter methods to retrieve user details
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Method to retrieve the role of the user (Admin, Lessor, or Renter)
    public String getRole() {
        return role;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }


}
