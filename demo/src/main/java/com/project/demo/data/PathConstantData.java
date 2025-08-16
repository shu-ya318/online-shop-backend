package com.project.demo.data;

public class PathConstantData {
    /* LOCAL & PORT */
	public static final String API_VUE = "http://localhost:5173";
	public static final String API_SPRING = "http://localhost:8080";
	
	/* DNS */
	public static final String API_DNS = "https://real-domain.com"; 
	
	/* PUBLIC */
	public static final String API_PUBLIC = "/public";
	public static final String API_PUBLIC_ALL = "/public/**";
	
	/* USER */
	public static final String API_REGISTER = "/user/register";
	public static final String API_ACTIVATE = "/user/activate";
	public static final String API_ACTIVATE_LINK = "/user/activate?email=%s&code=%s";
}
