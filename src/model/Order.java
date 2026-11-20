package model;

import java.math.BigDecimal;

public class Order {

    private int orderId;

    private int userId;

    private BigDecimal totalAmount;

    private String orderDate;

    public Order(){}

    public Order(int orderId,
                 int userId,
                 BigDecimal totalAmount,
                 String orderDate){

        this.orderId=orderId;
        this.userId=userId;
        this.totalAmount=totalAmount;
        this.orderDate=orderDate;

    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId=orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId=userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount=totalAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate=orderDate;
    }

}