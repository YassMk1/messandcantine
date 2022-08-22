package be.mkfin.messandcantine.service.impl;

import be.mkfin.messandcantine.entity.Availability;
import be.mkfin.messandcantine.repository.AvailabilityRepository;
import be.mkfin.messandcantine.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    @Autowired
    AvailabilityRepository availabilityRepository ;
    @Override
    public Availability save(Availability availability) {

        if (availability.getStartLocalDateTime() != null)
            availability.setStartTime(Timestamp.valueOf(availability.getStartLocalDateTime()));
        if (availability.getStartLocalDateTime() != null)
            availability.setEndTime(Timestamp.valueOf(availability.getEndLocalDateTime()));
        return availabilityRepository.save(availability);
    }

    @Override
    public Availability findAvailabilityById(Long id) {
        return availabilityRepository.findById(id).orElse(null);
    }
}
