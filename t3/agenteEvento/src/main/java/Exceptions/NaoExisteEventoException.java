package Exceptions;

public class NaoExisteEventoException extends Exception{
    public NaoExisteEventoException (){
        super();
    }

    public NaoExisteEventoException (String message){
        super(message);
    }
}


