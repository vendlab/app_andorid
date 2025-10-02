package com.marlodev.app_android.domain;

import java.util.List;

public class ProductVariants {
    private List<ProductVariantSize> sizes;
    private List<String> temperature;

    public ProductVariants() {
    }

    public List<ProductVariantSize> getSizes() {
        return sizes;
    }

    public void setSizes(List<ProductVariantSize> sizes) {
        this.sizes = sizes;
    }

    public List<String> getTemperature() {
        return temperature;
    }

    public void setTemperature(List<String> temperature) {
        this.temperature = temperature;
    }
}
