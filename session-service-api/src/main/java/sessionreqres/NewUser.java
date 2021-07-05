package sessionreqres;


import lombok.Getter;
import lombok.Setter;

public class NewUser
{
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String role;
}