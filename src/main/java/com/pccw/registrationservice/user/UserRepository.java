package com.pccw.registrationservice.user;

import com.pccw.registrationservice.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
