package com.arrietty.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.user_id
     *
     * @mbg.generated
     */
    private Long userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.net_id
     *
     * @mbg.generated
     */
    private String netId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.first_name
     *
     * @mbg.generated
     */
    private String firstName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.last_name
     *
     * @mbg.generated
     */
    private String lastName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.last_login_time
     *
     * @mbg.generated
     */
    private Date lastLoginTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.account_status
     *
     * @mbg.generated
     */
    private String accountStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table user
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.user_id
     *
     * @return the value of user.user_id
     *
     * @mbg.generated
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.user_id
     *
     * @param userId the value for user.user_id
     *
     * @mbg.generated
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.net_id
     *
     * @return the value of user.net_id
     *
     * @mbg.generated
     */
    public String getNetId() {
        return netId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.net_id
     *
     * @param netId the value for user.net_id
     *
     * @mbg.generated
     */
    public void setNetId(String netId) {
        this.netId = netId == null ? null : netId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.first_name
     *
     * @return the value of user.first_name
     *
     * @mbg.generated
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.first_name
     *
     * @param firstName the value for user.first_name
     *
     * @mbg.generated
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName == null ? null : firstName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.last_name
     *
     * @return the value of user.last_name
     *
     * @mbg.generated
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.last_name
     *
     * @param lastName the value for user.last_name
     *
     * @mbg.generated
     */
    public void setLastName(String lastName) {
        this.lastName = lastName == null ? null : lastName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.last_login_time
     *
     * @return the value of user.last_login_time
     *
     * @mbg.generated
     */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.last_login_time
     *
     * @param lastLoginTime the value for user.last_login_time
     *
     * @mbg.generated
     */
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.account_status
     *
     * @return the value of user.account_status
     *
     * @mbg.generated
     */
    public String getAccountStatus() {
        return accountStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.account_status
     *
     * @param accountStatus the value for user.account_status
     *
     * @mbg.generated
     */
    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus == null ? null : accountStatus.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", netId=").append(netId);
        sb.append(", firstName=").append(firstName);
        sb.append(", lastName=").append(lastName);
        sb.append(", lastLoginTime=").append(lastLoginTime);
        sb.append(", accountStatus=").append(accountStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}