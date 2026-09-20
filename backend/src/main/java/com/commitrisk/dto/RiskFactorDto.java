package com.commitrisk.dto;

public class RiskFactorDto {

    private String name;
    private String description;
    private int impact;

    public RiskFactorDto() {}

    public RiskFactorDto(String name, String description, int impact) {
        this.name = name;
        this.description = description;
        this.impact = impact;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getImpact() { return impact; }
    public void setImpact(int impact) { this.impact = impact; }
}
