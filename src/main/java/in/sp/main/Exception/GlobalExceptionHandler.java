package in.sp.main.Exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler({RoomAlreadyExistException.class,invalidRoomIdException.class,EmailAllredyExistException.class,AccessDeniedException.class,passwordMismatchException.class,KeynotExistException.class,InvalidEmailEception.class,OtpExpireException.class,InvlaidOtpException.class,EmailNotVerifiedException.class,EmailNotExistException.class,InvlaidOtpTokenException.class,NullPointerException.class})
	public ResponseEntity<Map<String,Object>> AlReadyExistsException(Exception e){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("Time",LocalDateTime.now());
		map.put("message",e.getMessage());
		return new ResponseEntity<Map<String,Object>>(map,HttpStatus.BAD_REQUEST);
	}
}
