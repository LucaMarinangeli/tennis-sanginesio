package it.uniroma3.siw.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Booking;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.BookingService;
import it.uniroma3.siw.service.UserService;

@Controller
public class AdminController {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private UserService userService;

	@GetMapping ("/admin/prenotazioni")
	public String goToPrenotazioni(Model model) {
		List<Integer> hours = bookingService.getHoursList();
		model.addAttribute("hoursList", hours);
		List<String> days = bookingService.getDaysList();
		model.addAttribute("daysList", days);
		List<List<List<Object>>> week = bookingService.createCalendar();
		model.addAttribute("week", week);
		return "admin/prenotazioni.html";
	}

	@PostMapping("/admin/crea-prenotazione/{year}/{month}/{day}/{hour}/{courtId}")
	public String createBookingByAdmin(@PathVariable("year")Integer year, @PathVariable("month")Integer month, 
			                           @PathVariable("day")Integer day, @PathVariable("hour")Integer hour, 
			                           @PathVariable("courtId")Long courtId , Model model) {
		Booking bookingCreated = bookingService.createBooking(year, month, day, hour, courtId);
		model.addAttribute("booking", bookingCreated);
		LocalDateTime dateLDT = bookingCreated.getDate().toLocalDateTime();
		model.addAttribute("date", dateLDT);
		model.addAttribute("guestName", new String());
		model.addAttribute("guestTelephone", new String());
		return "admin/aggiungi-note.html";
	}

	@PostMapping("/admin/aggiungi-note/{bookingId}")
	public String aggiungiNote(@RequestParam String guestName, @RequestParam String guestTelephone,  @PathVariable ("bookingId") Long bookingId, Model model) {
		bookingService.saveBookingGuestName(bookingId, guestName);
		bookingService.saveBookingGuestTelephone(bookingId, guestTelephone);
		Booking booking = bookingService.findBookingById(bookingId);		
		model.addAttribute("booking", booking);
		LocalDateTime dateLDT = booking.getDate().toLocalDateTime();
		model.addAttribute("date", dateLDT);
		return "admin/prenotazione-effettuata.html";
	}

	@PostMapping("/admin/rimuovi-prenotazione/{bookingId}")
	public String removeBooking (@PathVariable("bookingId") Long bookingId, Model model) {
		if((bookingService.removeBooking(bookingId))== true) {
			List<Integer> hours = bookingService.getHoursList();
			model.addAttribute("hoursList", hours);
			List<String> days = bookingService.getDaysList();
			model.addAttribute("daysList", days);
			List<List<List<Object>>> week = bookingService.createCalendar();
			model.addAttribute("week", week);
			return "admin/prenotazioni.html";
		}
		else {
			return "admin/pagina-errore.html";
		}
	}

	@GetMapping("/admin/profilo-utente/{userId}")
	public String goToProfiloUtente(@PathVariable("userId") Long userId, Model model ) {
		User user = userService.getUser(userId);
		model.addAttribute(user);
		return "admin/profilo-utente.html";
	}
	
	@GetMapping("/admin/utenti")
	public String goToUtenti(Model model) {
		List<User> users = userService.getAllUsers();
		model.addAttribute("users", users);
		return "admin/utenti.html";
	}
	
	@PostMapping("/admin/rimuovi-utente/{userId}")
	public String removeUser(@PathVariable("userId") Long userId, Model model) {
		userService.removeUserByAdmin(userId);
		List<User> users = userService.getAllUsers();
		model.addAttribute("users", users);
		return "admin/utenti.html";
		
	}
}





