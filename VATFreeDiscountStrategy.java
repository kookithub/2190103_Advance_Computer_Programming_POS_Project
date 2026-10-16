public class VATFreeDiscountStrategy implements discountStrategy {
    private final double vatRate; // e.g., 0.07 for 7%

    public VATFreeDiscountStrategy(double vatRate) {
        this.vatRate = vatRate;
    }

    @Override
    public double applyDiscount(double fullprice) {
        return fullprice / (1 + vatRate);
    }
}