package za.org.rfm.model.json;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Russel on 2014-12-11.
 */
@Getter
@Setter
public class J_Event {
    public String eventType, eventDateString,tithes,offerings,comment,assemblyId;
    public int attendance;
}
