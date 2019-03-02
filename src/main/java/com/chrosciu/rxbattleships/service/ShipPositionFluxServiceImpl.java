package com.chrosciu.rxbattleships.service;

import com.chrosciu.rxbattleships.exception.NotImplementedException;
import com.chrosciu.rxbattleships.model.ShipPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ShipPositionFluxServiceImpl implements ShipPositionFluxService {
    private final ShipPlacementService shipPlacementService;
    private final ConstantsService constantsService;

    @Override
    public Flux<ShipPosition> getShipPositionFlux() {
        Stream<Integer> shipSizesStream = Arrays.stream(constantsService.getShipSizes())
            .boxed();
        return Flux.fromStream(shipSizesStream)
            .map(shipPlacementService::placeShip);
    }
}
