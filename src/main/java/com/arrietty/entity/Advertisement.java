package com.arrietty.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Advertisement implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.user_id
     *
     * @mbg.generated
     */
    private Long userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.is_textbook
     *
     * @mbg.generated
     */
    private Boolean isTextbook;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.tag_id
     *
     * @mbg.generated
     */
    private Long tagId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.image_ids
     *
     * @mbg.generated
     */
    private String imageIds;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.price
     *
     * @mbg.generated
     */
    private BigDecimal price;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.comment
     *
     * @mbg.generated
     */
    private String comment;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.number_of_taps
     *
     * @mbg.generated
     */
    private Integer numberOfTaps;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table advertisement
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.id
     *
     * @return the value of advertisement.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.id
     *
     * @param id the value for advertisement.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.user_id
     *
     * @return the value of advertisement.user_id
     *
     * @mbg.generated
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.user_id
     *
     * @param userId the value for advertisement.user_id
     *
     * @mbg.generated
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.is_textbook
     *
     * @return the value of advertisement.is_textbook
     *
     * @mbg.generated
     */
    public Boolean getIsTextbook() {
        return isTextbook;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.is_textbook
     *
     * @param isTextbook the value for advertisement.is_textbook
     *
     * @mbg.generated
     */
    public void setIsTextbook(Boolean isTextbook) {
        this.isTextbook = isTextbook;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.tag_id
     *
     * @return the value of advertisement.tag_id
     *
     * @mbg.generated
     */
    public Long getTagId() {
        return tagId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.tag_id
     *
     * @param tagId the value for advertisement.tag_id
     *
     * @mbg.generated
     */
    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.image_ids
     *
     * @return the value of advertisement.image_ids
     *
     * @mbg.generated
     */
    public String getImageIds() {
        return imageIds;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.image_ids
     *
     * @param imageIds the value for advertisement.image_ids
     *
     * @mbg.generated
     */
    public void setImageIds(String imageIds) {
        this.imageIds = imageIds == null ? null : imageIds.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.price
     *
     * @return the value of advertisement.price
     *
     * @mbg.generated
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.price
     *
     * @param price the value for advertisement.price
     *
     * @mbg.generated
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.comment
     *
     * @return the value of advertisement.comment
     *
     * @mbg.generated
     */
    public String getComment() {
        return comment;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.comment
     *
     * @param comment the value for advertisement.comment
     *
     * @mbg.generated
     */
    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.number_of_taps
     *
     * @return the value of advertisement.number_of_taps
     *
     * @mbg.generated
     */
    public Integer getNumberOfTaps() {
        return numberOfTaps;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.number_of_taps
     *
     * @param numberOfTaps the value for advertisement.number_of_taps
     *
     * @mbg.generated
     */
    public void setNumberOfTaps(Integer numberOfTaps) {
        this.numberOfTaps = numberOfTaps;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.create_time
     *
     * @return the value of advertisement.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.create_time
     *
     * @param createTime the value for advertisement.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table advertisement
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
        sb.append(", userId=").append(userId);
        sb.append(", isTextbook=").append(isTextbook);
        sb.append(", tagId=").append(tagId);
        sb.append(", imageIds=").append(imageIds);
        sb.append(", price=").append(price);
        sb.append(", comment=").append(comment);
        sb.append(", numberOfTaps=").append(numberOfTaps);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}