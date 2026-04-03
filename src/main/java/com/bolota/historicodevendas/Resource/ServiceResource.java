package com.bolota.historicodevendas.Resource;

import com.bolota.historicodevendas.Entities.PersistentEntities.ServiceEntityPersistent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceResource extends JpaRepository<ServiceEntityPersistent, Long> {
    boolean existsByName(String name);
    boolean existsByUUID(String name);
    ServiceEntityPersistent getByName(String i);
    void deleteByUUID(String UUID);
    ServiceEntityPersistent getByUUID(String UUID);
}
