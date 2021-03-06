package com.imc.rps.game.service

import com.imc.rps.game.model.GameResultEnum
import com.imc.rps.game.model.GameSymbolEnum
import spock.lang.Specification
import spock.lang.Unroll

class GameResultServiceImplTest extends Specification {

    def gameResultService = new GameResultServiceImpl()

    @Unroll
    def "should return correct game result"() {

        given:

        when: "invoking gameResult service"
        GameResultEnum computedResult = gameResultService.computeResult(player, computer)

        then:
        computedResult == result

        where:
        player                  | computer                | result
        GameSymbolEnum.PAPER    | GameSymbolEnum.PAPER    | GameResultEnum.DRAW
        GameSymbolEnum.PAPER    | GameSymbolEnum.ROCK     | GameResultEnum.WIN
        GameSymbolEnum.PAPER    | GameSymbolEnum.SCISSORS | GameResultEnum.LOSE
        GameSymbolEnum.ROCK     | GameSymbolEnum.ROCK     | GameResultEnum.DRAW
        GameSymbolEnum.ROCK     | GameSymbolEnum.PAPER    | GameResultEnum.LOSE
        GameSymbolEnum.ROCK     | GameSymbolEnum.SCISSORS | GameResultEnum.WIN
        GameSymbolEnum.SCISSORS | GameSymbolEnum.SCISSORS | GameResultEnum.DRAW
        GameSymbolEnum.SCISSORS | GameSymbolEnum.PAPER    | GameResultEnum.WIN
        GameSymbolEnum.SCISSORS | GameSymbolEnum.ROCK     | GameResultEnum.LOSE

    }
}