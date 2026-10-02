package com.acos.auth.security;

import com.acos.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Loads ACOS users for Spring Security authentication. */
@Service
public class AcosUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Creates the user-details service.
   *
   * @param userRepository user persistence
   */
  public AcosUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) {
    return userRepository
        .findByEmail(username)
        .map(AcosUserDetails::from)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }
}
