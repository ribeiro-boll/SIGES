package com.bolota.historicodevendas.Service;

import com.bolota.historicodevendas.Entities.PersistentEntities.FixedSuppliesEntityPersistent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuppliesService {
    public void editFixedSupply(FixedSuppliesEntityPersistent fsepLocalEntity, FixedSuppliesEntityPersistent fsep){
        fsepLocalEntity.setCounterInUseByServices(fsep.getCounterInUseByServices());
        fsepLocalEntity.setDescription(fsep.getDescription());
        fsepLocalEntity.setFixedSupplyDate(fsep.getFixedSupplyDate());
        fsepLocalEntity.setName(fsep.getName());
        fsepLocalEntity.setSupplyTotalCost(fsep.getSupplyTotalCost());
        fsepLocalEntity.setCondUpdatePopup(fsep.isCondUpdatePopup());
        fsepLocalEntity.generateCostPerMinute();
    }
    public static <T> Page<T> toPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        if (start > end) {
            return new PageImpl<>(List.of(), pageable, list.size());
        }
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
