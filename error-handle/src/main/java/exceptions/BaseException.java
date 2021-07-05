package exceptions;

import lombok.Getter;
import lombok.Setter;

public class BaseException extends Exception
{
    @Getter
    @Setter
    private String message;

    public BaseException(String message)
    {
        this.message = message;
    }
}
