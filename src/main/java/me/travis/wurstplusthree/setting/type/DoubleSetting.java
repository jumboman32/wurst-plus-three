package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

public class DoubleSetting extends Setting<Double> {

    private final double min;
    private final double max;

    public DoubleSetting(String name, Double value, Double min, Double max, Hack parent) {
        super(name, value, parent);
        this.min = min;
        this.max = max;
    }

    public Double getValue() {
        return this.value;
    }

    public Double getMax() {
        return this.max;
    }

    public Double getMin() {
        return this.min;
    }

    public double getNumber() {
        return this.value;
    }

    public void setNumber(double value) {
        this.value = value;
    }

    public double getMaximumValue() {
        return this.max;
    }

    public double getMinimumValue() {
        return this.min;
    }

    public int getPrecision() {
        return 2;
    }

    @Override
    public String getType() {
        return "double";
    }
}
