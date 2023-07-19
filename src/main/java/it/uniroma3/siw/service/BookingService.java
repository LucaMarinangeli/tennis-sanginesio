package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import it.uniroma3.siw.model.Booking;
import it.uniroma3.siw.model.Court;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.BookingRepository;
import it.uniroma3.siw.repository.CourtRepository;


@Service
public class BookingService {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private CourtRepository courtRepository;

	public Booking findBookingById(Long bookingId) {
		return bookingRepository.findById(bookingId).orElse(null);
	}

	public List<Booking> findBookingsByUser(){
		Long userId = credentialsService.getCredentials().getUser().getId();
		return bookingRepository.findByUserIdOrderByDateAsc(userId);
	}

	public Timestamp getCurrentTime() {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		return currentTime;
	}

	public List<LocalDateTime> getNextWeek(Timestamp currentTime){
		List<LocalDateTime> week = new ArrayList<>();
		LocalDateTime dayToAdd = currentTime.toLocalDateTime();    
		for (int i = 0; i <6; i++) {
			dayToAdd = dayToAdd.plusDays(1);         
			week.add(i,dayToAdd);	              
		}
		return week;
	}

	public List<Integer> getHoursList(){
		List<Integer> hoursList = new ArrayList<>();
		hoursList.add(0, 8 );
		hoursList.add(1, 9 );
		hoursList.add(2, 10);
		hoursList.add(3, 11);
		hoursList.add(4, 12);
		hoursList.add(5, 13);
		hoursList.add(6, 14);
		hoursList.add(7, 15);
		hoursList.add(8, 16);
		hoursList.add(9, 17);
		hoursList.add(10,18);
		hoursList.add(11,19);
		hoursList.add(12,20);
		return hoursList;
	}

	public List<String> getDaysList(){
		List<String> daysList = new ArrayList<>();
		LocalDateTime today = LocalDateTime.now();
		daysList.add(0,today.plusDays(0).getDayOfWeek().toString());
		daysList.add(1,today.plusDays(1).getDayOfWeek().toString());
		daysList.add(2,today.plusDays(2).getDayOfWeek().toString());
		daysList.add(3,today.plusDays(3).getDayOfWeek().toString());
		daysList.add(4,today.plusDays(4).getDayOfWeek().toString());
		daysList.add(5,today.plusDays(5).getDayOfWeek().toString());
		daysList.add(6,today.plusDays(6).getDayOfWeek().toString());
		return daysList;
	}

	@Transactional
	public Booking createBooking(Integer year, Integer month, Integer day, Integer hour, Long courtId) {
		LocalDateTime date = LocalDateTime.of(year, month, day, hour, 0);
		Timestamp dateConverted = Timestamp.valueOf(date);
		Booking bookingCreated = new Booking();
		Credentials credentials = credentialsService.getCredentials();
		User user = credentials.getUser();
		Court court = courtRepository.findById(courtId).orElse(null);
		bookingCreated.setDate(dateConverted);
		bookingCreated.setCourt(court);
		bookingCreated.setUser(user);
		bookingCreated.setGuestName(" ");
		bookingCreated.setGuestTelephone(" ");
		return saveBooking(bookingCreated);
	}

	@Transactional
	public Booking saveBooking(Booking booking) {
		return bookingRepository.save(booking);
	}

	@Transactional
	public Booking saveBookingGuestName(Long bookingId, String guestName) {
		Booking booking = findBookingById(bookingId);
		booking.setGuestName(guestName);
		return saveBooking(booking);
	}

	@Transactional
	public Booking saveBookingGuestTelephone(Long bookingId, String guestTelephone) {
		Booking booking = findBookingById(bookingId);
		booking.setGuestTelephone(guestTelephone);
		return saveBooking(booking);
	}

	public List<Booking> findAllBooking(){
		return bookingRepository.findByOrderByDateAsc();
	}

	@Transactional
	public boolean removeBooking(Long bookingId) {
		Booking bookingToDelete = findBookingById(bookingId);
		if(bookingToDelete != null) {
			bookingRepository.delete(bookingToDelete);
			return true;
		}
		else {
			return false;
		}
	}

	public boolean bookingExist(Integer year, Integer month, Integer day, Integer hour) {
		LocalDateTime dateLDT = LocalDateTime.of(year, month, day, hour, 0);
		Timestamp dateTS = Timestamp.valueOf(dateLDT);
		Booking booking = bookingRepository.findOneByDate(dateTS);
		if(Objects.isNull(booking)) {
			return false;
		}
		return true;
	}

	public void deletePastBookings() {
		LocalDateTime todayLDT = LocalDateTime.now().minusHours(1);
		Timestamp todayTS = Timestamp.valueOf(todayLDT);
		List<Booking> pastBookings = bookingRepository.findByDateBefore(todayTS);
		for (Booking booking : pastBookings) {
			removeBooking(booking.getId());
		}
	}
	
	@Transactional
	public List<List<List<Object>>> createCalendar(){
		LocalDateTime today = LocalDateTime.now();         
		List<List<List<Object>>> week = new ArrayList<>();
		for (int i = 0 ; i<=6 ; i++) {
			LocalDateTime date = today.plusDays(i);           
			List<List<Object>> day = new ArrayList<>();
			for(int j = 8 ; j <= 20; j++) {
				List<Object> slot = new ArrayList<>();
				LocalDateTime dateToSearch = LocalDateTime.of(date.getYear(), date.getMonthValue() , date.getDayOfMonth(), j, 0);
				if(dateToSearch.isBefore(LocalDateTime.now().minusHours(1))) {
					slot.add(0, null);
					slot.add(1, null);
				}
				else {
					Timestamp dateToSearchTS = Timestamp.valueOf(dateToSearch);
					slot.add(0, dateToSearch);
					if(bookingExist(date.getYear(), date.getMonthValue() , date.getDayOfMonth(), j)) {
						Booking bookingToAdd = bookingRepository.findOneByDate(dateToSearchTS);
						slot.add(1,bookingToAdd);	
					}
					else {
						slot.add(1,null);
					}
				}
				day.add(slot);
			}
			week.add(i,day);
		}
		return week;
	}

	@Transactional
	public List<List<Object>> getUserBookings() {
		List<Booking> bookingsFound = findBookingsByUser();
		List<List<Object>> list = new ArrayList<>();
		for(int i = 0; i < bookingsFound.size() ; i++) {
			List<Object> slot = new ArrayList<>();
			LocalDateTime date = bookingsFound.get(i).getDate().toLocalDateTime();
			Booking booking = bookingsFound.get(i);
			slot.add(0, date );
			slot.add(1, booking );
			list.add(slot);
		}
		return list;
	}
	
	public LocalDateTime translate(LocalDateTime date) {
	 //da implementare
		return date;
	}

}






