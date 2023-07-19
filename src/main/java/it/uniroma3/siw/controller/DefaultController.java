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
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.BookingService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;


@Controller
public class DefaultController {

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private BookingService bookingService;

	@Autowired
	UserService userService;

	@GetMapping ("default/prenota-un-campo")
	public String goToPrenotaUnCampo(Model model) {
		List<Integer> hours = bookingService.getHoursList();
		model.addAttribute("hoursList", hours);
		List<String> days = bookingService.getDaysList();
		model.addAttribute("daysList", days);
		List<List<List<Object>>> week = bookingService.createCalendar();
		model.addAttribute("week", week);
		User user = credentialsService.getCredentials().getUser();
		model.addAttribute("user", user);
		return "default/prenota-un-campo.html";
	}

	@GetMapping ("/default/le-mie-prenotazioni")
	public String goToLeMiePrenotazioni(Model model) {
		bookingService.deletePastBookings();
		List<List<Object>> list = bookingService.getUserBookings();
		model.addAttribute("list", list);
		return "default/le-mie-prenotazioni.html";
	}

	@GetMapping("/default/il-mio-profilo")
	public String goToIlMioProfilo(Model model) {
		Credentials credentials = credentialsService.getCredentials();
		model.addAttribute("credentials", credentials);
		return "default/il-mio-profilo.html";
	}

	@PostMapping("/default/crea-prenotazione/{year}/{month}/{day}/{hour}/{courtId}")
	public String createBookingByUser(@PathVariable("year")Integer year, @PathVariable("month")Integer month, 
			@PathVariable("day")Integer day, @PathVariable("hour")Integer hour, 
			@PathVariable("courtId")Long courtId , Model model) {
		Booking booking = bookingService.createBooking(year, month, day, hour, courtId);
		if(booking != null) {
			model.addAttribute("booking", booking);
			LocalDateTime dateLDT = booking.getDate().toLocalDateTime();
			model.addAttribute("date", dateLDT);
			return "default/prenotazione-effettuata.html";
		}
		else {
			return "default/pagina-errore.html";
		}
	}

	@PostMapping("/default/rimuovi-prenotazione-da-tabella/{bookingId}")
	public String removeBookingFromTable (@PathVariable("bookingId") Long bookingId , Model model) {
		if((bookingService.removeBooking(bookingId))== true) {
			List<Integer> hours = bookingService.getHoursList();
			model.addAttribute("hoursList", hours);
			List<String> days = bookingService.getDaysList();
			model.addAttribute("daysList", days);
			List<List<List<Object>>> week = bookingService.createCalendar();
			model.addAttribute("week", week);
			User user = credentialsService.getCredentials().getUser();
			model.addAttribute("user", user);
			return "default/prenota-un-campo.html";
		}
		else {
			return "default/pagina-errore.html";
		}
	}

	@PostMapping("/default/rimuovi-prenotazione-da-lista/{bookingId}")
	public String removeBookingFromList (@PathVariable("bookingId") Long bookingId , Model model) {
		if((bookingService.removeBooking(bookingId))== true) {
			bookingService.deletePastBookings();
			List<List<Object>> list = bookingService.getUserBookings();
			model.addAttribute("list", list);
			return "default/le-mie-prenotazioni.html";
		}
		else {
			return "default/pagina-errore.html";
		}
	}

	@PostMapping("/default/rimuovi-prenotazione-effettuata/{bookingId}")
	public String removeBookingCreated (@PathVariable("bookingId") Long bookingId , Model model) {
		if((bookingService.removeBooking(bookingId))== true) {
			List<Integer> hours = bookingService.getHoursList();
			model.addAttribute("hoursList", hours);
			List<String> days = bookingService.getDaysList();
			model.addAttribute("daysList", days);
			List<List<List<Object>>> week = bookingService.createCalendar();
			model.addAttribute("week", week);
			User user = credentialsService.getCredentials().getUser();
			model.addAttribute("user", user);
			return "default/prenota-un-campo.html";
		}
		else {
			return "default/pagina-errore.html";
		}
	}

	@GetMapping("/default/form-nuovo-telefono/{userId}")
	public String goToFormUpdateTelephoneNumber(@PathVariable("userId") Long userId, Model model) {
		model.addAttribute("telephone", new String());
		model.addAttribute("credentials", credentialsService.getCredentials());
		return "default/form-nuovo-telefono.html";
	}

	@PostMapping("/default/nuovo-telefono/{userId}")
	public String updateTelephoneNumber(@PathVariable("userId") Long userId, @RequestParam String telephone) {
		if(credentialsService.telephoneAlreadyExist(telephone)) {
			return "default/telefono-esistente.html";
		}
		else {
			userService.changeTelephone(userId, telephone);
			return "operazione-effettuata.html";
		}
	}

	@GetMapping("/default/form-nuovo-nome/{userId}")
	public String goToFormUpdateName(@PathVariable("userId") Long userId, Model model) {
		model.addAttribute("nome", new String());
		model.addAttribute("credentials", credentialsService.getCredentials());
		return "default/form-nuovo-nome.html";
	}

	@PostMapping("/default/nuovo-nome/{userId}")
	public String updateName(@PathVariable("userId") Long userId, @RequestParam String name) {
		userService.changeName(userId, name);
		return "operazione-effettuata.html";
	}

	@GetMapping("/default/form-nuovo-cognome/{userId}")
	public String goToFormUpdateSurname(@PathVariable("userId") Long userId, Model model) {
		model.addAttribute("cognome", new String());
		model.addAttribute("credentials", credentialsService.getCredentials());
		return "default/form-nuovo-cognome.html";
	}

	@PostMapping("/default/nuovo-cognome/{userId}")
	public String updateSurname(@PathVariable("userId") Long userId, @RequestParam String surname) {
		userService.changeSurname(userId, surname);
		return "operazione-effettuata.html";
	}

	@GetMapping("/default/form-nuova-email/{userId}")
	public String goToFormUpdateEmail(@PathVariable("userId") Long userId, Model model) {
		model.addAttribute("email", new String());
		model.addAttribute("credentials", credentialsService.getCredentials());
		return "default/form-nuova-email.html";
	}

	@PostMapping("/default/nuova-email/{userId}")
	public String updateEmail(@PathVariable("userId") Long userId, @RequestParam String email) {
		if(credentialsService.emailAlreadyExist(email)) {
			return "default/email-esistente.html";
		}
		else {
			userService.changeEmail(userId, email);
			return "operazione-effettuata.html";
		}
	}
	/*
	@PostMapping("/default/rimuovi-utente")
	public String removeUser(){
		userService.removeUserByDefault();
		http.logout();
		}
	 */





}
