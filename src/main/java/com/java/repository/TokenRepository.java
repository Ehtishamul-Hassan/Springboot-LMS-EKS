package com.java.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.java.token.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
	
	@Query("""
		    select t from Token t 
		    join t.user u
		    where u.id = :id and (t.expired = false or t.revoked = false)
		""")
	List<Token> findAllValidTokenByUser(Long id);

	Optional<Token> findByToken(String token);

}
