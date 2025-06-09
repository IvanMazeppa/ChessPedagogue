package com.chesspedagogue.configurator.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

public class SliderWithLabel extends VBox {
    private final Label label;
    private final Slider slider;
    private final Label valueLabel;
    private final DoubleProperty value = new SimpleDoubleProperty();
    
    public SliderWithLabel(String labelText, double min, double max, double initialValue) {
        this.label = new Label(labelText);
        this.slider = new Slider(min, max, initialValue);
        this.valueLabel = new Label(formatValue(initialValue));
        
        setupSlider();
        setupLayout();
        bindProperties();
    }
    
    public SliderWithLabel(String labelText, double min, double max, double initialValue, String suffix) {
        this(labelText, min, max, initialValue);
        this.valueLabel.setText(formatValue(initialValue) + " " + suffix);
        
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            this.valueLabel.setText(formatValue(newVal.doubleValue()) + " " + suffix);
        });
    }
    
    private void setupSlider() {
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((slider.getMax() - slider.getMin()) / 4);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement(1);
    }
    
    private void setupLayout() {
        setSpacing(5);
        setPadding(new Insets(5));
        setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(label, slider, valueLabel);
    }
    
    private void bindProperties() {
        value.bind(slider.valueProperty());
        
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            valueLabel.setText(formatValue(newVal.doubleValue()));
        });
    }
    
    private String formatValue(double val) {
        if (val == (int) val) {
            return String.valueOf((int) val);
        }
        return String.format("%.1f", val);
    }
    
    public DoubleProperty valueProperty() {
        return value;
    }
    
    public double getValue() {
        return value.get();
    }
    
    public void setValue(double val) {
        slider.setValue(val);
    }
    
    public Slider getSlider() {
        return slider;
    }
    
    public Label getLabel() {
        return label;
    }
    
    public Label getValueLabel() {
        return valueLabel;
    }
    
    /**
     * Set tooltip for the slider component
     */
    public void setTooltip(Tooltip tooltip) {
        slider.setTooltip(tooltip);
    }
    
    /**
     * Set minimum value for the slider
     */
    public void setMin(double min) {
        slider.setMin(min);
    }
    
    /**
     * Set maximum value for the slider
     */
    public void setMax(double max) {
        slider.setMax(max);
    }
    
    /**
     * Get minimum value of the slider
     */
    public double getMin() {
        return slider.getMin();
    }
    
    /**
     * Get maximum value of the slider
     */
    public double getMax() {
        return slider.getMax();
    }
    
    /**
     * Set whether to show tick marks
     */
    public void setShowTickMarks(boolean show) {
        slider.setShowTickMarks(show);
    }
    
    /**
     * Set whether to show tick labels
     */
    public void setShowTickLabels(boolean show) {
        slider.setShowTickLabels(show);
    }
    
    /**
     * Set the major tick unit
     */
    public void setMajorTickUnit(double unit) {
        slider.setMajorTickUnit(unit);
    }
    
    /**
     * Set the minor tick count
     */
    public void setMinorTickCount(int count) {
        slider.setMinorTickCount(count);
    }
}