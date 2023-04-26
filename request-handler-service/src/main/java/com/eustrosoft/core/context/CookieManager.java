package com.eustrosoft.core.context;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CookieManager {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private Map<String, List<String>> cookies;
    private String cookieName = "EustrosoftCookie";
    private int expirationTime = 24 * 60 * 60;
    private String path = "/";

    public CookieManager(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.cookies = new HashMap<>(4);
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

    public synchronized List<String> getCookieValues() {
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
        Cookie[] cookies = this.request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName() == null)
                    continue; // TODO: ??
                List<String> keyCookies = this.cookies.get(cookie.getName());
                if (keyCookies == null) {
                    keyCookies = new ArrayList<>();
                    this.cookies.put(cookie.getName(), keyCookies);
                }
                keyCookies.add(cookie.getValue());
            }
        }
    }
}
