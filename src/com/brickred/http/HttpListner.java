package com.brickred.http;

/**
 * @author ravindrap
 *HttpListener is super class of which set the HTTP or  HTTPS request 
 */
public interface HttpListner {
    /**
     * @param http
     * Http handler object. when http request is completed then is call in HttpHandler class
     */
    void notifyHTTPRespons(HttpHandler http);
}
