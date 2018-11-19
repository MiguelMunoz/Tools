package com.neptunedreams.framework;

import com.neptunedreams.framework.exception.ResponseException;
import org.springframework.http.HttpStatus;

/**
 * Works with the {@code ResponseUtility} to implement REST services. This method is implemented by each REST service
 * to return the data provided by the service. A companion {@code FunctionalInterface} called {@code ServiceVoidMethod}
 * exists to implement REST services that do not return a value.
 * @see ResponseUtility
 * @see ResponseUtility#serve(HttpStatus, ServiceMethod)
 * @see ServiceVoidMethod
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 3/8/18
 * <p>Time: 3:14 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@FunctionalInterface
public interface ServiceMethod<T> {
  T doService() throws ResponseException;
}
