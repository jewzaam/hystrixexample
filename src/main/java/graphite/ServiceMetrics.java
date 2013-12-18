package graphite;

import java.util.Set;

public interface ServiceMetrics {

    /**
     * Increment counter by name. If the counter doesn't exist, a new counter is
     * created with initial value of 1 and is registered.
     */
    public Number incrementCounter(String name);

    /**
     * Get names of all registered counters.
     * 
     * @return
     */
    public Set<String> getCounterNames();

    /**
     * Get value of counter by name. If counter doesn't exist returns -1.
     * 
     * @param name
     * @return
     */
    public Number getCounterValue(String name);

    /**
     * Increment gauge. Creates gauge with zero (0) value if it doesn't exist.
     * 
     * @param name
     *            - the name of the gauge
     */
    public Number incrementGauge(String name);

    /**
     * Decrement gauge. Creates gauge with zero (0) value if it doesn't exist.
     * 
     * @param name
     *            - the name of the gauge
     */
    public Number decrementGauge(String name);

    /**
     * Sets the value of a gauge. Creates gauge with zero (0) value if it
     * doesn't exist.
     * 
     * @param name
     *            - the name of the gauge
     * @param value
     *            - the new value of the gauge
     */
    public Number setGauge(String name, int value);

    public Set<String> getGaugeNames();

    public Number getGaugeValue(String name);
}