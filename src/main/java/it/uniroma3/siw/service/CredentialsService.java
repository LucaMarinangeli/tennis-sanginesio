package it.uniroma3.siw.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class CredentialsService {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected CredentialsRepository credentialsRepository;

	@Autowired
	protected UserRepository userRepository;

	@Transactional
	public Credentials getCredentials(Long id) {
		Optional<Credentials> result = this.credentialsRepository.findById(id);
		return result.orElse(null);
	}

	@Transactional
	public Credentials getCredentials(String username) {
		Credentials result = null;
		result = this.credentialsRepository.findByUsername(username);
		return result;
	}

	@Transactional
	public Credentials saveCredentials(Credentials credentials) {
		credentials.setRole(Credentials.DEFAULT_ROLE);
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		return this.credentialsRepository.save(credentials);
	}

	public Credentials getCredentials() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = getCredentials(userDetails.getUsername());
		return credentials;
	}

	public boolean usernameAlreadyExist( String username ) {
		Credentials credentials = credentialsRepository.findByUsername(username);
		if(credentials != null) {
			return true;
		}
		else {
			return false;	
		}
	}

	public boolean emailAlreadyExist( String email ) {
		User user = userRepository.findByEmail(email);
		if(user != null) {
			return true;
		}
		else {
			return false;	
		}
	}

	public boolean telephoneAlreadyExist( String telephone) {
		User user = userRepository.findByTelephone(telephone);

		if(user != null) {
			return true;
		}
		else {
			return false;	
		}
	}


}
