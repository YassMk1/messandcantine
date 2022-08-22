package be.mkfin.messandcantine.service;

import be.mkfin.messandcantine.entity.Availability;

public interface AvailabilityService {

    Availability save(Availability availability);

    Availability findAvailabilityById(Long id);
}
