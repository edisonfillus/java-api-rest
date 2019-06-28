package org.project.example.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.project.example.dto.Notification;


public class NotificationDAO {
	
	private static Map<Long, Notification> memorydb = new HashMap<Long, Notification>();
	private static AtomicLong sequence = new AtomicLong(2);

	static {
		memorydb.put(1l, new Notification(1l, "New user created"));
		memorydb.put(2l, new Notification(2l, "New order created"));
	}
	
	public static Notification add(Notification notification) {
		long id = sequence.incrementAndGet();
		notification.setId(id);
		memorydb.put(id, notification);
		return notification;
	}
	
	public static Notification find(long id) {
		return memorydb.get(id);
	}
	
	public static Notification update(Notification notification) {
		return memorydb.put(notification.getId(), notification);
	}
	
	public static boolean remove(long id) {
		return (memorydb.remove(id) != null); //if null, not exist
	}
	
	public static List<Notification> findAll() {
		return new ArrayList<Notification>(memorydb.values());
	}
	
}
