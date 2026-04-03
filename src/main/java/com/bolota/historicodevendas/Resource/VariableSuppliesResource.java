package com.bolota.historicodevendas.Resource;

import com.bolota.historicodevendas.Entities.PersistentEntities.SuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.SuppliesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariableSuppliesResource extends JpaRepository<SuppliesEntityPersistent,Long> {
    boolean existsByName(String name);
    boolean existsByUUID(String name);
    SuppliesEntityPersistent getByName(String i);

    SuppliesEntityPersistent getByUUID(String uuid);

    void removeByUUID(String UUID);
}
