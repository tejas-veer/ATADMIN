package net.media.autotemplate.bean.autoasset;

import net.media.autotemplate.enums.ATRequestState;

import java.util.Objects;


public class AATaskDetail {
    private long taskId;
    private long requestId;
    private ATRequestState state;
    private String failureReason;
    private int isActive;
    private long adminId;
    private String taskInputDetails;
    private String taskOutputDetails;

    public AATaskDetail(long taskId, long requestId, String taskInputDetails, String taskOutputDetails, ATRequestState state, int isActive, Long adminId) {
        this.taskId = taskId;
        this.requestId = requestId;
        this.taskInputDetails = taskInputDetails;
        this.taskOutputDetails = taskOutputDetails;
        this.state = state;
        this.isActive = isActive;
        this.adminId = adminId;
    }

    public AATaskDetail(String taskInputDetails) {
        this.taskInputDetails = taskInputDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AATaskDetail that = (AATaskDetail) o;
        return taskId == that.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }

    public long getTaskId() {
        return taskId;
    }

    public String getTaskInputDetails() {
        return taskInputDetails;
    }

    public String getTaskOutputDetails() {
        return taskOutputDetails;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public ATRequestState getState() {
        return state;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setState(ATRequestState state) {
        this.state = state;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public void setTaskInputDetails(String taskInputDetails) {
        this.taskInputDetails = taskInputDetails;
    }

    public void setTaskOutputDetails(String taskOutputDetails) {
        this.taskOutputDetails = taskOutputDetails;
    }

    public Long getAdminId() {
        return adminId;
    }
}
