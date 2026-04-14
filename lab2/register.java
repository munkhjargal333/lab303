//Үндсэн асуудлууд?
//SRP 2р үйлдэл хийж байна. шалгалт хадгалалалт
//DRY 2р класс яг адилхан шалгалт хийж байна.

public class UserRegistrationService {
    private final EmailValidator emailValidator;

    public void registerUser(String email, String password) {

        emailValidator.validate(email);
        User user = new User(email, password);
        userRepository.save(user);
    }
}
public class PartnerService {
    private final EmailValidator emailValidator;

    public void addPartner(String companyEmail, String companyName) {

        emailValidator.validate(companyEmail);
        Partner partner = new Partner(companyEmail, companyName);
        partnerRepository.save(partner);
    }
}

public class EmailValidator {
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 50;

    public void validate(String email) {
        if (email == null || !email.contains("@")) {
            throw new InvalidEmailException("Email must contain @");
        }
        if (email.length() < MIN_LENGTH) {
            throw new InvalidEmailException("Email too short");
        }
        if (email.length() > MAX_LENGTH) {
            throw new InvalidEmailException("Email too long");
        }
    }
}