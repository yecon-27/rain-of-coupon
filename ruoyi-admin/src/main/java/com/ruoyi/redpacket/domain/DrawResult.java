package com.ruoyi.redpacket.domain;

/**
 * 抽奖结果实体
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
public class DrawResult {
    
    /** 是否中奖 */
    private boolean isWin;
    
    /** 奖品名称 */
    private String prizeName;
    
    /** 奖品ID */
    private Long prizeId;
    
    /** 奖品面值 */
    private String prizeValue;
    
    /** 消息提示 */
    private String message;
    
    public DrawResult() {}
    
    public DrawResult(boolean isWin, String message) {
        this.isWin = isWin;
        this.message = message;
    }
    
    public DrawResult(boolean isWin, String prizeName, Long prizeId, String prizeValue, String message) {
        this.isWin = isWin;
        this.prizeName = prizeName;
        this.prizeId = prizeId;
        this.prizeValue = prizeValue;
        this.message = message;
    }
    
    public boolean isWin() {
        return isWin;
    }
    
    public void setWin(boolean win) {
        isWin = win;
    }
    
    public String getPrizeName() {
        return prizeName;
    }
    
    public void setPrizeName(String prizeName) {
        this.prizeName = prizeName;
    }
    
    public Long getPrizeId() {
        return prizeId;
    }
    
    public void setPrizeId(Long prizeId) {
        this.prizeId = prizeId;
    }
    
    public String getPrizeValue() {
        return prizeValue;
    }
    
    public void setPrizeValue(String prizeValue) {
        this.prizeValue = prizeValue;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "DrawResult{" +
                "isWin=" + isWin +
                ", prizeName='" + prizeName + '\'' +
                ", prizeId=" + prizeId +
                ", prizeValue='" + prizeValue + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}