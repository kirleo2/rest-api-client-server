package cz.fit.cvut.tjv.game_store.api;

import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityAlreadyExistsException;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityDoesNotExistsException;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.NotEnoughFundsException;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.OrderIsClosedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityAlreadyExistsException.class)
    protected ResponseEntity<Object> handleAlreadyExists(Exception exception, WebRequest webRequest){
        return handleExceptionInternal(exception, "This entity is already exists!", new HttpHeaders(), HttpStatus.CONFLICT, webRequest);
    }
    @ExceptionHandler({NullPointerException.class, NoSuchElementException.class})
    protected ResponseEntity<Object> handleNullElement(Exception exception, WebRequest webRequest){
        return handleExceptionInternal(exception, "Invalid entity state!\n" + exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
    }
    @ExceptionHandler(EntityDoesNotExistsException.class)
    protected ResponseEntity<Object> handleDoesNotExists(Exception exception, WebRequest webRequest){
        return handleExceptionInternal(exception, "Wrong Entity ID!", new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleValidation(Exception exception, WebRequest webRequest){
        return handleExceptionInternal(exception, "Unable to create the entity!\nYou did not fill all mandatory parameters.", new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
    }
    @ExceptionHandler(NotEnoughFundsException.class)
    protected ResponseEntity<Object> handleNotEnoughFunds(Exception exception, WebRequest webRequest){
        return handleExceptionInternal(exception, "Unable to create order!\nYou don't have enough funds on your balance", new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
    }
    @ExceptionHandler(OrderIsClosedException.class)
    protected ResponseEntity<Object> handleOrderIsClosed(Exception exception, WebRequest webRequest) {
        return handleExceptionInternal(exception, "Unable to edit order!\nIt is already complete.", new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
    }

}
