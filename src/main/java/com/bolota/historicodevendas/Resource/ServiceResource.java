package com.bolota.historicodevendas.Resource;

import com.bolota.historicodevendas.Entities.PersistentEntities.ServiceEntityPersistent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface ServiceResource extends JpaRepository<ServiceEntityPersistent, Long> {

    List<ServiceEntityPersistent> findByUUIDInAndServiceDateBetween(List<String> uuids, LocalDate start, LocalDate end);
    boolean existsByUUID(String name);
    ServiceEntityPersistent getByName(String i);
    void deleteByUUID(String UUID);
    ServiceEntityPersistent getByUUID(String UUID);
}
