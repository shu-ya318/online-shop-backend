package com.project.demo.data;

public class PathConstantData {
	/* LOCAL & PORT */
	public static final String API_VUE = "http://localhost:5173";
	public static final String API_SPRING = "http://localhost:8081";

	/* DNS */
	public static final String API_DNS = "https://real-domain.com";

	/* PUBLIC */
	public static final String API_PUBLIC = "/public";
	public static final String API_PUBLIC_ALL = "/public/**";

	/* USER */
	public static final String API_REGISTER = "/public/user/register";
	public static final String API_LOGIN = "/public/user/login";
	public static final String API_OAUTH2_EXCHANGE_CODE = "/public/user/oauth2/exchange-code";
	public static final String API_LOGOUT = "/user/logout";
	public static final String API_REFRESH_TOKEN = "/user/refresh-token";
	public static final String API_CURRENT_USER = "/user/me";
	public static final String API_UPDATE_USER = "/user/me/profile";
	public static final String API_UPDATE_PASSWORD = "/user/me/password";

	/* PRODUCT */
	public static final String API_PRODUCTS = "/products";
	public static final String API_PRODUCT_BY_UUID = "/products/{uuid}";
}
