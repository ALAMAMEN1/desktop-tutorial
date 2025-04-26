package com.example.myapplication;

public class ModuleAssignment {
    public String module, formation, section, year, group;

    public ModuleAssignment(String module, String formation, String section, String year, String group) {
        this.module = module;
        this.formation = formation;
        this.section = section;
        this.year = year;
        this.group = group;
    }

    @Override
    public String toString() {
        return module + " - " + formation + "/" + section + "/" + year + "/" + group;
    }
}
