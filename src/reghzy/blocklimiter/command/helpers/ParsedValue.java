package reghzy.blocklimiter.command.helpers;

public class ParsedValue<T> {
    public T value;
    public boolean failed;

    public ParsedValue(T value, boolean failed) {
        this.value = value;
        this.failed = failed;
    }
}
