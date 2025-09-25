public class RollingAverage {
    private double[] values;
    private double lastReported;
    private int rollingIndex;
    private boolean filled;

    public RollingAverage(int size) {
        values = new double[size];
        rollingIndex = 0;
        lastReported = -1;
        filled = false;
    }

    public void addValue(double newValue) {
        values[rollingIndex] = newValue;
        rollingIndex++;
        if (rollingIndex > values.length-1) {
            filled = true;
            rollingIndex = 0;
            double sum = 0;
            for (double n: values) {
                sum += n;
            }
            lastReported = sum / values.length;
        }
    }

    public double currentAverage() {
        double sum = 0;
        if (!filled) {
            for (int i = 0; i < rollingIndex; i++) {
                sum += values[i];
            }
            return sum/rollingIndex;
        }
        
        return lastReported;
    }
}
