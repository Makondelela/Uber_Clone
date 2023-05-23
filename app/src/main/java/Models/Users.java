package Models;

public class Users {

    String firstName,lastName,userName, email, password;

    public Users(String firstName, String lastName, String userName, String email, String password){
        this.firstName = firstName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.lastName = lastName;

    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

