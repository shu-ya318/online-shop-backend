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

	/* USERS */
	public static final String API_REGISTER = "/public/users/register";
	public static final String API_LOGIN = "/public/users/login";
	public static final String API_OAUTH2_EXCHANGE_CODE = "/public/users/oauth2/exchange-code";
	public static final String API_LOGOUT = "/users/logout";
	public static final String API_REFRESH_TOKEN = "/users/refresh-token";
	public static final String API_CURRENT_USER = "/users/me";
	public static final String API_UPDATE_USER = "/users/me/profile";
	public static final String API_UPDATE_PASSWORD = "/users/me/password";

	/* PRODUCTS */
	public static final String API_PRODUCTS = "/products";
	public static final String API_PRODUCT_BY_UUID = "/products/{uuid}";

	/* CARTS */
	public static final String API_CURRENT_USER_CART = "/carts/me";
	public static final String API_CURRENT_USER_CART_ITEMS = "/carts/me/items";
	public static final String API_CURRENT_USER_CART_ITEM_BY_UUID = "/carts/me/items/{productUuid}";

	/* ORDERS */
	public static final String API_CURRENT_USER_ORDERS = "/orders/me";
	public static final String API_CURRENT_USER_ORDER_BY_UUID = "/orders/me/{uuid}";
}
