package com.believer.licar.transport.repository;

import com.believer.licar.transport.entity.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransportRepository extends JpaRepository<Transport, Integer> {
    Optional<Transport> findFirstByOrderByTransportIdDesc();

    Optional<Transport> findFirstByRobotIdOrderByTransportIdDesc(int robotId);
}
