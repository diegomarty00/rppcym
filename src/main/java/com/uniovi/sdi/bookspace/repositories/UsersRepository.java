package com.uniovi.sdi.bookspace.repositories;

import com.uniovi.sdi.bookspace.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, Long>{
    User findByDni(String dni);
}