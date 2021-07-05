package sessionreqres;

import lombok.Getter;
import lombok.Setter;

public class GetUser
{
    @Getter
    @Setter
    private String userUid;
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    private String role;
}
