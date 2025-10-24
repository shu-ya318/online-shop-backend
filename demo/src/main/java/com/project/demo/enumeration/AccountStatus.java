package com.project.demo.enumeration;

public enum AccountStatus {
	REGISTERED,   // Registered (Not verified or activated)
    ACTIVE,       // Active (Normal usage)
    SUSPENDED,    // Suspended (Can be due to violations or management operations)
    LOCKED,       // Locked (Continuous login failure)
    DEACTIVATED,  // Deactivated (User closed or long-term inactivity)
    BANNED        // Banned (Severe violations block)
}
