package net.media.autotemplate.bean;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/*
    Created by shubham-ar
    on 26/10/17 1:54 PM   
*/
public class Admin {
    @SerializedName("admin_email")
    private String adminEmail;
    @SerializedName("admin_id")
    private final long adminId;
    @SerializedName("admin_name")
    private String adminName;

    public Admin(long adminId, String name) {
        this.adminId = adminId;
        this.adminName = name;
    }

    public Admin(String adminEmail, long adminId) {
        this.adminEmail = adminEmail;
        this.adminId = adminId;
    }

    public Admin(String adminEmail, long adminId, String name) {
        this.adminEmail = adminEmail;
        this.adminId = adminId;
        this.adminName = name;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public long getAdminId() {
        return adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", adminName='" + adminName + '\'' +
                ", adminEmail=" + adminEmail +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return adminId == admin.adminId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminId);
    }
}
