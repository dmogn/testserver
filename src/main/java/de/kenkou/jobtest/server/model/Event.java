package de.kenkou.jobtest.server.model;

import lombok.Data;

/**
 * Stream event.
 * 
 * @author Dmitry Ognyannikov
 */
@Data
public class Event {
    
    private Integer sequenceId;
    private Integer fromUserId;
    private Integer toUserId;
    private String line;
    
    public enum TYPE {
        FOLLOW,
        UNFOLLOW,
        BROADCAST,
        PRIVATE_MSG,
        STATUS_UPDATE
    }
    
    private TYPE type;
}
