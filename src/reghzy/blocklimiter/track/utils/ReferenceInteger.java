package reghzy.blocklimiter.track.utils;

public class ReferenceInteger {
    private int value;

    public ReferenceInteger(int value) {
        this.value = value;
    }

    public int get() {
        return this.value;
    }

    public void set(int value) {
        this.value = value;
    }

    public void increment() {
        this.value++;
    }

    public void decrement() {
        this.value--;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReferenceInteger && ((ReferenceInteger) obj).value == this.value;
    }

    @Override
    public int hashCode() {
        return this.value;
    }
}
