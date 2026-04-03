package com.bolota.historicodevendas.Resource;

import com.bolota.historicodevendas.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserResource extends JpaRepository<UserEntity, Long> {
    boolean existsByLogin(String login);
    UserEntity getByLogin(String login);
}
