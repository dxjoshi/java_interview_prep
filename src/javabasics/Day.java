package javabasics;

public enum Day {
    SUNDAY("Weekend", 0),
    MONDAY("Weekday", 1),
    TUESDAY("Weekday", 2),
    WEDNESDAY("Weekday", 3),
    THURSDAY("Weekday", 4),
    FRIDAY("Weekend", 5),
    SATURDAY("Weekend", 6);

    Integer dayOfWeek;
    String alias;

    Day(String alias, Integer dayOfWeek) {
        this.alias = alias;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Day{");
        sb.append("dayOfWeek=").append(dayOfWeek);
        sb.append(", alias='").append(alias).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

