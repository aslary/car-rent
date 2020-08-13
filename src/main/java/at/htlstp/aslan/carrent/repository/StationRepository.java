package at.htlstp.aslan.carrent.repository;

import at.htlstp.aslan.carrent.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Integer> {
}
