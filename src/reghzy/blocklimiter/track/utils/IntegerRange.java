package reghzy.blocklimiter.track.utils;

public class IntegerRange {
    public final int min;
    public final int max;

    public IntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public boolean between(int value) {
        return value >= min && value <= max;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntegerRange) {
            IntegerRange range = (IntegerRange) obj;
            return range.min == this.min && range.max == this.max;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return min + (max << 15);
    }

    @Override
    public String toString() {
        return "IntegerRange{min:" + this.min + ",max:" + this.max + '}';
    }
}
