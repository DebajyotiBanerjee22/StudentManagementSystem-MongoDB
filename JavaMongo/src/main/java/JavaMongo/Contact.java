package JavaMongo;
public class Contact {
    private String rollNo;
    private String regNo;
    private String phone;
    private String email;

    public Contact(String rollNo, String regNo, String phone, String email) {
        this.rollNo = rollNo;
        this.regNo = regNo;
        this.phone = phone;
        this.email = email;
    }

    public String getRollNo() {
        return rollNo;
    }
    public String getRegNo() {
        return regNo;
    }
    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }
    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "rollNo='" + rollNo + '\'' +
                ", regNo='" + regNo + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}