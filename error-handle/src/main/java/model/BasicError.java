package model;

import lombok.Getter;
import lombok.Setter;

public class BasicError
{
    @Getter
    @Setter
    private String message;

    public BasicError(String message)
    {
        this.message = message;
    }
}
