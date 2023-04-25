package com.eustrosoft.core.context;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class CookieManager {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private Map<String, String> cookies;
    private String cookieName = "EustrosoftCookie";
    private int expirationTime = 24 * 60 * 60;
    private String path = "/";

    public CookieManager(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        setupCookies();
    }

    public CookieManager(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        this(request, response);
        this.cookieName = cookieName;
    }

    public synchronized void setCookie(String cookieValue, boolean httpOnly, boolean secure) {
        Cookie newCookie = new Cookie(this.cookieName, cookieValue);
        newCookie.setMaxAge(this.expirationTime);
        newCookie.setPath(this.path);
        newCookie.setHttpOnly(httpOnly);
        newCookie.setSecure(secure);
        this.response.addCookie(newCookie);
    }

    public synchronized String getCookieValue() {
        return this.cookies.get(this.cookieName);
    }

    public synchronized void deleteCookie() {
        Cookie toDeleteCookie = new Cookie(this.cookieName, "");
        toDeleteCookie.setMaxAge(0);
        this.response.addCookie(toDeleteCookie);
    }

    public synchronized void setCookieName(String newName) {
        this.cookieName = newName;
    }

    public synchronized void setExpirationTime(int time) {
        this.expirationTime = time;
    }

    public synchronized void setPath(String path) {
        this.path = path;
    }

    private synchronized void setupCookies() {
        this.cookies = Arrays.stream(this.request.getCookies())
                .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
    }
}
