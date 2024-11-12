package net.media.autotemplate.bean;

/*
    Created by shubham-ar
    on 18/10/17 2:04 PM   
*/
public class DateRange {
    private final String startDate;
    private final String endDate;

    public DateRange(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "DateRange{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
