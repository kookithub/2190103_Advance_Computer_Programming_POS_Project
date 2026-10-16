public class CouponDiscountStrategy implements discountStrategy {
    private final double couponAmount;
    private final double minimumSpend;

    public CouponDiscountStrategy(double couponAmount, double minimumSpend) {
        this.couponAmount = couponAmount;
        this.minimumSpend = minimumSpend;
    }

    @Override
    public double applyDiscount(double fullprice) {
        if (fullprice >= minimumSpend) {
            return Math.max(0, fullprice - couponAmount);
        } else {
            System.out.printf("Coupon not applied. Minimum spend is $%.2f\n", minimumSpend);
            return fullprice;
        }
    }
}