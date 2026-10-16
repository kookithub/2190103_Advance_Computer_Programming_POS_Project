public class PercentDiscountStrategy implements discountStrategy {
    private final double percent; 

    public PercentDiscountStrategy(double percent) {
        this.percent = percent;
    }

    @Override
    public double applyDiscount(double fullprice) {
        return fullprice * (100 - percent)/100;
    }
}