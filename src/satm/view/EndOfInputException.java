package satm.view;


public class EndOfInputException extends RuntimeException {
    public EndOfInputException() {
        super("End of input");
    }
}
