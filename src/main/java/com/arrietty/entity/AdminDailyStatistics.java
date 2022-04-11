package com.arrietty.entity;

import java.io.Serializable;
import java.util.Date;

public class AdminDailyStatistics implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.total_user_num
     *
     * @mbg.generated
     */
    private Integer totalUserNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.login_user_num
     *
     * @mbg.generated
     */
    private Integer loginUserNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.ad_upload_num
     *
     * @mbg.generated
     */
    private Integer adUploadNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.ad_edit_num
     *
     * @mbg.generated
     */
    private Integer adEditNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.ad_delete_num
     *
     * @mbg.generated
     */
    private Integer adDeleteNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.total_ad_num
     *
     * @mbg.generated
     */
    private Integer totalAdNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.tap_request_num
     *
     * @mbg.generated
     */
    private Integer tapRequestNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.mark_request_num
     *
     * @mbg.generated
     */
    private Integer markRequestNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.unmark_request_num
     *
     * @mbg.generated
     */
    private Integer unmarkRequestNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.search_request_num
     *
     * @mbg.generated
     */
    private Integer searchRequestNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column admin_daily_statistics.date
     *
     * @mbg.generated
     */
    private Date date;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table admin_daily_statistics
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.id
     *
     * @return the value of admin_daily_statistics.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.id
     *
     * @param id the value for admin_daily_statistics.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.total_user_num
     *
     * @return the value of admin_daily_statistics.total_user_num
     *
     * @mbg.generated
     */
    public Integer getTotalUserNum() {
        return totalUserNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.total_user_num
     *
     * @param totalUserNum the value for admin_daily_statistics.total_user_num
     *
     * @mbg.generated
     */
    public void setTotalUserNum(Integer totalUserNum) {
        this.totalUserNum = totalUserNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.login_user_num
     *
     * @return the value of admin_daily_statistics.login_user_num
     *
     * @mbg.generated
     */
    public Integer getLoginUserNum() {
        return loginUserNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.login_user_num
     *
     * @param loginUserNum the value for admin_daily_statistics.login_user_num
     *
     * @mbg.generated
     */
    public void setLoginUserNum(Integer loginUserNum) {
        this.loginUserNum = loginUserNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.ad_upload_num
     *
     * @return the value of admin_daily_statistics.ad_upload_num
     *
     * @mbg.generated
     */
    public Integer getAdUploadNum() {
        return adUploadNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.ad_upload_num
     *
     * @param adUploadNum the value for admin_daily_statistics.ad_upload_num
     *
     * @mbg.generated
     */
    public void setAdUploadNum(Integer adUploadNum) {
        this.adUploadNum = adUploadNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.ad_edit_num
     *
     * @return the value of admin_daily_statistics.ad_edit_num
     *
     * @mbg.generated
     */
    public Integer getAdEditNum() {
        return adEditNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.ad_edit_num
     *
     * @param adEditNum the value for admin_daily_statistics.ad_edit_num
     *
     * @mbg.generated
     */
    public void setAdEditNum(Integer adEditNum) {
        this.adEditNum = adEditNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.ad_delete_num
     *
     * @return the value of admin_daily_statistics.ad_delete_num
     *
     * @mbg.generated
     */
    public Integer getAdDeleteNum() {
        return adDeleteNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.ad_delete_num
     *
     * @param adDeleteNum the value for admin_daily_statistics.ad_delete_num
     *
     * @mbg.generated
     */
    public void setAdDeleteNum(Integer adDeleteNum) {
        this.adDeleteNum = adDeleteNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.total_ad_num
     *
     * @return the value of admin_daily_statistics.total_ad_num
     *
     * @mbg.generated
     */
    public Integer getTotalAdNum() {
        return totalAdNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.total_ad_num
     *
     * @param totalAdNum the value for admin_daily_statistics.total_ad_num
     *
     * @mbg.generated
     */
    public void setTotalAdNum(Integer totalAdNum) {
        this.totalAdNum = totalAdNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.tap_request_num
     *
     * @return the value of admin_daily_statistics.tap_request_num
     *
     * @mbg.generated
     */
    public Integer getTapRequestNum() {
        return tapRequestNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.tap_request_num
     *
     * @param tapRequestNum the value for admin_daily_statistics.tap_request_num
     *
     * @mbg.generated
     */
    public void setTapRequestNum(Integer tapRequestNum) {
        this.tapRequestNum = tapRequestNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.mark_request_num
     *
     * @return the value of admin_daily_statistics.mark_request_num
     *
     * @mbg.generated
     */
    public Integer getMarkRequestNum() {
        return markRequestNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.mark_request_num
     *
     * @param markRequestNum the value for admin_daily_statistics.mark_request_num
     *
     * @mbg.generated
     */
    public void setMarkRequestNum(Integer markRequestNum) {
        this.markRequestNum = markRequestNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.unmark_request_num
     *
     * @return the value of admin_daily_statistics.unmark_request_num
     *
     * @mbg.generated
     */
    public Integer getUnmarkRequestNum() {
        return unmarkRequestNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.unmark_request_num
     *
     * @param unmarkRequestNum the value for admin_daily_statistics.unmark_request_num
     *
     * @mbg.generated
     */
    public void setUnmarkRequestNum(Integer unmarkRequestNum) {
        this.unmarkRequestNum = unmarkRequestNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.search_request_num
     *
     * @return the value of admin_daily_statistics.search_request_num
     *
     * @mbg.generated
     */
    public Integer getSearchRequestNum() {
        return searchRequestNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.search_request_num
     *
     * @param searchRequestNum the value for admin_daily_statistics.search_request_num
     *
     * @mbg.generated
     */
    public void setSearchRequestNum(Integer searchRequestNum) {
        this.searchRequestNum = searchRequestNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column admin_daily_statistics.date
     *
     * @return the value of admin_daily_statistics.date
     *
     * @mbg.generated
     */
    public Date getDate() {
        return date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column admin_daily_statistics.date
     *
     * @param date the value for admin_daily_statistics.date
     *
     * @mbg.generated
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin_daily_statistics
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", totalUserNum=").append(totalUserNum);
        sb.append(", loginUserNum=").append(loginUserNum);
        sb.append(", adUploadNum=").append(adUploadNum);
        sb.append(", adEditNum=").append(adEditNum);
        sb.append(", adDeleteNum=").append(adDeleteNum);
        sb.append(", totalAdNum=").append(totalAdNum);
        sb.append(", tapRequestNum=").append(tapRequestNum);
        sb.append(", markRequestNum=").append(markRequestNum);
        sb.append(", unmarkRequestNum=").append(unmarkRequestNum);
        sb.append(", searchRequestNum=").append(searchRequestNum);
        sb.append(", date=").append(date);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}