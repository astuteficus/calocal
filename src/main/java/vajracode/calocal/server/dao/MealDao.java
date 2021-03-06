package vajracode.calocal.server.dao;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.criteria.*;

import org.springframework.stereotype.Component;

import vajracode.calocal.server.dto.MealDTO;
import vajracode.calocal.server.dto.UserDTO;

/**
 * Meal data access object
 *
 */
@Component
public class MealDao extends Dao<MealDTO> {
	
	public MealDTO getById(long id) {
		return em.find(MealDTO.class, id);
	}

	public List<MealDTO> getList(LocalDateTime fromDate, LocalDateTime toDate, LocalTime fromTime, LocalTime toTime, 
			UserDTO user, int offset, int limit) {		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MealDTO> query = cb.createQuery(MealDTO.class);
		Root<MealDTO> root = query.from(MealDTO.class);
		query.where(getListExpression(cb, root, fromDate, toDate, fromTime, toTime, user));
		query.orderBy(cb.desc(root.get("consumed")));
		return em.createQuery(query)		
			.setFirstResult(offset)
			.setMaxResults(limit)
			.getResultList();
	}	

	public long getListSize(LocalDateTime fromDate, LocalDateTime toDate, LocalTime fromTime, LocalTime toTime, UserDTO user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<MealDTO> root = query.from(MealDTO.class);
		query.select(cb.count(root));
		query.where(getListExpression(cb, root, fromDate, toDate, fromTime, toTime, user));
		return em.createQuery(query).getSingleResult();
	}

	private Predicate getListExpression(CriteriaBuilder cb, Root<MealDTO> root, 
			LocalDateTime fromDate, LocalDateTime toDate, 
			LocalTime fromTime, LocalTime toTime, UserDTO user) {		
		Predicate p = cb.equal(root.get("user"), user);
		if (fromDate != null)
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get("consumed"), java.sql.Timestamp.valueOf(fromDate)));
		if (toDate != null)
			p = cb.and(p, cb.lessThanOrEqualTo(root.get("consumed"), java.sql.Timestamp.valueOf(toDate)));
		if (fromTime != null && toTime != null) {
			if (fromTime.isBefore(toTime)) {
				p = cb.and(p, cb.greaterThanOrEqualTo(cb.function("time", 
					Time.class, root.get("consumed")), Time.valueOf(fromTime)));
				p = cb.and(p, cb.lessThanOrEqualTo(cb.function("time", 
						Time.class, root.get("consumed")), Time.valueOf(toTime)));
			} else {
				Predicate or = cb.or(
						cb.lessThanOrEqualTo(cb.function("time", 
							Time.class, root.get("consumed")), Time.valueOf(toTime)), 
						cb.greaterThanOrEqualTo(cb.function("time", 
							Time.class, root.get("consumed")), Time.valueOf(fromTime)));
				p = cb.and(p, or);
			}
		}
		
			
		return p;
	}

}
