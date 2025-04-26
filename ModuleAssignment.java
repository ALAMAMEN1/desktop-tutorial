package com.example.myapplication;

public class ModuleAssignment {
    private String moduleName;
    private String formation;
    private String section;
    private String year;
    private String group;

    public ModuleAssignment(String moduleName, String formation, String section, String year, String group) {
        this.moduleName = moduleName;
        this.formation = formation;
        this.section = section;
        this.year = year;
        this.group = group;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getFormation() {
        return formation;
    }

    public String getSection() {
        return section;
    }

    public String getYear() {
        return year;
    }

    public String getGroup() {
        return group;
    }
}
