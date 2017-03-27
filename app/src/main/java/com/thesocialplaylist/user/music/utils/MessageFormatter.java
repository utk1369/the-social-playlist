package com.thesocialplaylist.user.music.utils;

import android.text.TextUtils;

import com.thesocialplaylist.user.music.dto.SocialActivityDTO;
import com.thesocialplaylist.user.music.dto.SongMetadataDTO;
import com.thesocialplaylist.user.music.dto.UserDTO;
import com.thesocialplaylist.user.music.enums.SocialActivityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 27-03-2017.
 */

public class MessageFormatter {

    public static String generateHeadlineForActivity(SocialActivityDTO socialActivityDTO, Map<String, UserDTO> idToUserDetailsMap) {
        String headline = "";
        UserDTO postedBy = idToUserDetailsMap.get(socialActivityDTO.getPostedBy());
        String postedByUserName = (postedBy == null) ? "..." : postedBy.getName();
        headline += postedByUserName;
        if(socialActivityDTO.getActivityType().equals(SocialActivityType.SHARE)) {
           headline += " shared this song.";
            return headline;
        }

        List<String> recipientNames = new ArrayList<>();

        for(String recipient: socialActivityDTO.getRecipientUserIds()) {
            if(idToUserDetailsMap.containsKey(recipient)) {
                recipientNames.add(idToUserDetailsMap.get(recipient).getName());
            }
        }

        String recipientsString = "";
        if(recipientNames.size() == 0) {
            recipientsString += socialActivityDTO.getRecipientUserIds().size() + "friend(s).";
        } else if(recipientNames.size() < socialActivityDTO.getRecipientUserIds().size()) {
            recipientsString += TextUtils.join(", ", recipientNames);
            recipientsString += " and " + (socialActivityDTO.getRecipientUserIds().size() - recipientNames.size()) + " others.";
        } else {
            if(recipientNames.size() == 1) {
                recipientsString += recipientNames.get(0);
            } else {
                for(int i=0; i<recipientNames.size() - 1; i++) {
                    recipientsString += ((i == 0) ? "": ", ") + recipientNames.get(i);
                }
                recipientsString += " and " + recipientNames.get(recipientNames.size() - 1);
            }
        }

        if(socialActivityDTO.getActivityType().equals(SocialActivityType.DEDICATE)) {
            return headline + " dedicated this song to " + recipientsString;
        }
        if(socialActivityDTO.getActivityType().equals(SocialActivityType.RECOMMEND)) {
            return headline + " recommended this song to " + recipientsString;
        }
        return null;
    }

    public static String getSongInfo(SongMetadataDTO songMetadataDTO) {
        String songInfoMsg = songMetadataDTO.getTitle() + " by " + songMetadataDTO.getArtist() +
                " from the album " + songMetadataDTO.getAlbum();
        return songInfoMsg;
    }
}
