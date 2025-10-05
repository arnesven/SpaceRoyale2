package model.tracks;

import util.MyStrings;

public enum HealthCategory {
    HEALTHY,
    DECLINE,
    DEATH;

    @Override
    public String toString() {
        return "(" + MyStrings.capitalize(super.toString().toLowerCase()) + ")";
    }
}
