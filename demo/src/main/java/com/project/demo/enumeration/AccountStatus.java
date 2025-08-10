package com.project.demo.enumeration;

public enum AccountStatus {
	REGISTERED,   // 已註冊（尚未驗證或啟用）
    ACTIVE,       // 已啟用（正常使用中）
    SUSPENDED,    // 暫停（可因違規或管理操作）
    LOCKED,       // 鎖定（如連續登入失敗）
    DEACTIVATED,  // 已停用（使用者自己關閉或長期不使用）
    BANNED        // 封禁（嚴重違規封鎖）
}
