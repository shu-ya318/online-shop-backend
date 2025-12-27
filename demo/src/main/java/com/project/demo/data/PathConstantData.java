package com.project.demo.data;

public class PathConstantData {

	/* DNS */
	public static final String API_DNS = "http://localhost:8080";

	/* PUBLIC */
	public static final String API_PUBLIC = "/public";
	public static final String[] API_PUBLIC_ALL = new String[] {
			"/public/**",
			"/login/oauth2/**",
			"/oauth2/**",
			"/actuator/**",
	};

	/* USERS */
	// AUTH
	public static final String API_REGISTER = "/public/users/register";
	public static final String API_LOGIN = "/public/users/login";
	public static final String API_OAUTH2_EXCHANGE_CODE = "/public/users/oauth2/exchange-code";
	public static final String API_LOGOUT = "/public/users/logout";
	public static final String API_REFRESH_TOKENS = "/public/users/refresh-tokens";
	// CURRENT USER
	public static final String API_CURRENT_USER = "/users/me";
	public static final String API_CURRENT_USER_UPDATE_PROFILE = "/users/me/profile";
	public static final String API_CURRENT_USER_UPDATE_PASSWORD = "/users/me/password";

	/* PRODUCTS */
	public static final String API_PRODUCTS = "/products";
	public static final String API_PRODUCT_BY_UUID = "/products/{productUuid}";

	/* CARTS */
	public static final String API_CURRENT_USER_CART = "/carts/me";
	public static final String API_CURRENT_USER_CART_ITEMS = "/carts/me/items";
	public static final String API_CURRENT_USER_CART_ITEM_BY_UUID = "/carts/me/items/{cartItemUuid}";

	/* ORDERS */
	public static final String API_CURRENT_USER_ORDERS = "/orders/me";
	public static final String API_CURRENT_USER_ORDER_BY_UUID = "/orders/me/{orderUuid}";

	/* PAYMENTS */
	public static final String API_CURRENT_USER_PAYMENTS = "/payments/me";
	public static final String API_CURRENT_USER_PAYMENTS_CAPTURE = "/payments/me/capture";
}
