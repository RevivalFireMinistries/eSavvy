package za.org.rfm.dao;

import org.springframework.data.repository.CrudRepository;
import za.org.rfm.model.Event;

import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/11
 * Time: 10:53 AM
 */
public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findByEventType(String eventType);
}
