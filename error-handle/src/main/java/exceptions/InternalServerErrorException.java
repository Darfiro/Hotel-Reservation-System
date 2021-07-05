package exceptions;

public class InternalServerErrorException extends BaseException
{
    public InternalServerErrorException(String message)
    {
        super(message);
    }
}