package com.ant;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.handlers.AsyncHandler;

import javax.naming.Context;

public class Handler implements AsyncHandler {
    public void myHandler(String input, Context context) {
        Main.main(new String[]{});
    }

    /**
     * Invoked after an asynchronous request
     *
     * @param exception
     */
    @Override
    public void onError(Exception exception) {

    }

    /**
     * Invoked after an asynchronous request has completed successfully. Callers
     * have access to the original request object and the returned response
     * object.
     *
     * @param request The initial request created by the caller
     * @param o
     */
    @Override
    public void onSuccess(AmazonWebServiceRequest request, Object o) {

    }

}