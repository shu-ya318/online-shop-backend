package com.project.demo.enumeration;

public enum OrderStatus {
    PENDING, // 建立訂單，尚未選擇支付方式 或 外部支付尚未成功
    PROCESSING, // 已選擇貨到付款 或 外部支付成功，進入備貨
    ON_THE_WAY, // 備貨完成，配送中
    COMPLETED, // 取貨完成
    CANCELLED       
}
