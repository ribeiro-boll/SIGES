package com.bolota.historicodevendas.Resource;

import com.bolota.historicodevendas.Entities.PersistentEntities.FixedSuppliesEntityPersistent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedSuppliesResource extends JpaRepository<FixedSuppliesEntityPersistent, Long> {
    FixedSuppliesEntityPersistent getByUUID(String productUUID);

    void removeByUUID(String productUUID);

    boolean existsByUUID(String s);
}