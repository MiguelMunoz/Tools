package com.neptunedreams.framework;

import com.neptunedreams.framework.exception.ResponseException;
import org.springframework.http.HttpStatus;

/**
 * Works with the {@code ResponseUtility} to implement REST services. This method is implemented by each REST service
 * that processes input but returns no data. A companion {@code FunctionalInterface} called {@code ServiceMethod} 
 * exists to implement REST services that do return a value.
 * @see ResponseUtility
 * @see ResponseUtility#serve(HttpStatus, ServiceMethod) 
 * @see ServiceMethod
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/17/18
 * <p>Time: 5:45 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@FunctionalInterface
public interface ServiceVoidMethod {
	void doService() throws ResponseException;
}
