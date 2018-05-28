package de.kenkou.jobtest.server.model;

import lombok.Data;

/**
 * User follow.
 * 
 * @author Dmitry Ognyannikov
 */
@Data
public class Follow {
    private final int fromUserId;
    private final int toUserId;
    
    public Follow(int fromUserId, int toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Follow)) return false;
        Follow key = (Follow) o;
        return fromUserId == key.fromUserId && toUserId == key.toUserId;
    }

    @Override
    public int hashCode() {
        int result = fromUserId;
        result = 31 * result + toUserId;
        return result;
    }
}
