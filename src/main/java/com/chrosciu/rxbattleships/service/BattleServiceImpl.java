package com.chrosciu.rxbattleships.service;

import com.chrosciu.rxbattleships.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class BattleServiceImpl implements BattleService {
    private final ShipPositionFluxService shipPositionFluxService;
    private final FieldFluxService fieldFluxService;

    private List<Ship> ships = new ArrayList<>();

    @Override
    public Mono<Void> getShipsReadyMono() {
        return shipPositionFluxService.getShipPositionFlux()
            .map(ShipBuilder::buildShip)
            .doOnNext(ships::add)
            .then();
    }

    @Override
    public Flux<Stamp> getStampFlux() {
        return fieldFluxService.getFieldFlux()
            .takeUntil(field -> isAllSunk())
            .flatMap(this::getAffectedStamps);
    }

    private Flux<Stamp> getAffectedStamps(Field field) {
        return ships.stream()
            .filter(ship -> isHit(field, ship))
            .findAny()
            .map(ship -> hitStamps(ship, field))
            .map(Flux::fromStream)
            .orElse(Flux.just(new Stamp(field, ShotResult.MISSED)));
    }

    private static boolean isHit(Field field, Ship ship) {
        ShotResult shotResult = ship.takeShot(field);
        return ShotResult.HIT.equals(shotResult) || ShotResult.SUNK.equals(shotResult);
    }

    private static Stream<Stamp> hitStamps(Ship ship, Field field) {
        ShotResult shotResult = ship.takeShot(field);
        switch (shotResult) {
            case SUNK:
                return ship.getAllFields().stream().map(shipField -> new Stamp(shipField, shotResult));
            case HIT:
                return Stream.of(new Stamp(field, shotResult));
            default:
                throw new IllegalStateException(String.format("Illegal shot result type (%s)", shotResult));
        }
    }

    private boolean isAllSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }
}
