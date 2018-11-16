package com.chrosciu.rxbattleships;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@ToString
public class ShipHits {
    @NonNull private final Ship ship;
    private int hits = 0;

    public void takeShot() {
        if (!isSunk()) {
            ++hits;
        }
    }

    public boolean isSunk() {
        return hits >= ship.size;
    }

    public List<Shot> getShotsToReport(Shot shot) {
        if (isSunk()) {
            return IntStream.range(0, ship.size).mapToObj(value -> ship.horizontal ?
                    Shot.builder().x(ship.x + value).y(ship.y).build()
                    : Shot.builder().x(ship.x).y(ship.y + value).build()).collect(Collectors.toList());
        } else {
            return Collections.singletonList(shot);
        }
    }

    public boolean isHit(Shot shot) {
        if (ship.horizontal) {
            return ship.y == shot.y && shot.x >= ship.x && shot.x < ship.x + ship.size;
        } else {
            return ship.x == shot.x && shot.y >= ship.y && shot.y < ship.y + ship.size;
        }
    }







}
