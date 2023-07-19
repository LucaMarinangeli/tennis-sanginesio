package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuestController {

	
	@GetMapping("/galleria") 
	public String goToGalleria () {
		return "guest/galleria.html";
	}
	
}
