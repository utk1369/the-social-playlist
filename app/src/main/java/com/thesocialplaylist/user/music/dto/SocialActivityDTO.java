package com.thesocialplaylist.user.music.dto;

import com.thesocialplaylist.user.music.enums.SocialActivityDomain;
import com.thesocialplaylist.user.music.enums.SocialActivityType;

import java.util.Date;
import java.util.List;

/**
 * Created by user on 06-09-2016.
 */
public class SocialActivityDTO {

    private SocialActivityType activityType;

    private SocialActivityDomain domain;

    private List<String> recipientUserIds;

    private ExternalLinksDTO externalLinksDTO;

    private Date timestamp;

    public ExternalLinksDTO getExternalLinksDTO() {
        return externalLinksDTO;
    }

    public void setExternalLinksDTO(ExternalLinksDTO externalLinksDTO) {
        this.externalLinksDTO = externalLinksDTO;
    }

    public SocialActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(SocialActivityType activityType) {
        this.activityType = activityType;
    }

    public SocialActivityDomain getDomain() {
        return domain;
    }

    public void setDomain(SocialActivityDomain domain) {
        this.domain = domain;
    }

    public List<String> getRecipientUserIds() {
        return recipientUserIds;
    }

    public void setRecipientUserIds(List<String> recipientUserIds) {
        this.recipientUserIds = recipientUserIds;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
