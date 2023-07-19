package it.uniroma3.siw.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import it.uniroma3.siw.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	
	public List<Booking> findByOrderByDateAsc();
	
	public List<Booking> findByUserIdOrderByDateAsc(Long userId);
	
	public Booking findOneByDate(Timestamp date);
	
	public List<Booking> findByDateBefore(Timestamp today);
	
}
