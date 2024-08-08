package kr.giljabi.gateway.service;

import kr.giljabi.gateway.repository.BoothStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BoothStatusService {
    private final BoothStatusRepository boothStatusRepository;

    @Autowired
    public BoothStatusService(BoothStatusRepository boothStatusRepository) {
        this.boothStatusRepository = boothStatusRepository;
    }

}
