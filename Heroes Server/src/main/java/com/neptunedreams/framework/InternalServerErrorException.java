package com.neptunedreams.framework;

import com.neptunedreams.framework.exception.ResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * InternalServerErrorException is for server bugs only. It should only be used when the server code throws a 
 * RuntimeException, which should only happen if there's a bug in the server. This should never be used when the 
 * server is intentionally returning a bad result as a result of invalid input.
 * <p>
 * The ResponseUtility.serve() method catches RuntimeExceptions and throws this exception. That's why this class is 
 * not public.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/5/18
 * <p>Time: 1:34 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class InternalServerErrorException extends ResponseException {
	InternalServerErrorException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
