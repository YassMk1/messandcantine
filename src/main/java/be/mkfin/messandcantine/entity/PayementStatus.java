package be.mkfin.messandcantine.entity;

public enum PayementStatus {

    INITIATED,PAYPAL_TENTATIVE,FAILED, CONFIRMED, REJECTED, REFUND_REQUESTED, REFUNDED;

    public boolean isFailed() {

        return this == INITIATED || this == PAYPAL_TENTATIVE ||  this == FAILED;
    }
}
