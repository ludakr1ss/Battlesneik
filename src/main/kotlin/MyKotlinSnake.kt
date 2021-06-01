/*
 * Copyright © 2021 Paul Ambrose (pambrose@mac.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction")

import io.battlesnake.core.*
import io.ktor.application.*
import io.ktor.http.*

object MyKotlinSnake : AbstractBattleSnake<MyKotlinSnake.MySnakeContext>() {

    override fun gameStrategy(): GameStrategy<MySnakeContext> =
        strategy(verbose = true) {

            onDescribe { call: ApplicationCall ->
                DescribeResponse("Ludakr1ss", "#fff000", "pixel", "pixel")
            }

//            onStart { context: MySnakeContext, request: StartRequest ->
//                fun originPath(x: Int, y: Int): List<MoveResponse> =
//                    buildList {
//                        repeat(y) { add(DOWN) }
//                        repeat(x) { add(LEFT) }
//                    }
//
//                val you = request.you
//                val board = request.board
//                val pos = you.headPosition
//
//                context.moves = originPath(you.headPosition.x, you.headPosition.y).iterator()
//
//                logger.info { "Position: ${pos.x},${pos.y} game id: ${request.gameId}" }
//                logger.info { "Board: ${board.width}x${board.height} game id: ${request.gameId}" }
//            }

            onMove { context: MySnakeContext, request: MoveRequest ->
                fun moveTo(request: MoveRequest, position: Position): MoveResponse =
                    when {
                        request.headPosition.x > position.x -> LEFT
                        request.headPosition.x < position.x -> RIGHT
                        request.headPosition.y > position.y -> DOWN
                        else -> UP
                    }


                fun nearestFood(head: Position, foodlist: List<Food>): Food =
                    foodlist.maxByOrNull { head - it.position }!!

                if (request.isFoodAvailable)
                    moveTo(request, nearestFood(request.headPosition, request.foodList).position)
                else
                    moveTo(request, request.boardCenter)

                fun perimeterPath(width: Int, height: Int): List<MoveResponse> =
                    buildList {
                        repeat(height - 1) { add(UP) }
                        repeat(width - 1) { add(RIGHT) }
                        repeat(height - 1) { add(DOWN) }
                        repeat(width - 1) { add(LEFT) }
                    }

                if (request.isAtOrigin)
                    context.moves = perimeterPath(request.board.width, request.board.height).iterator()

                context.moves.next()

            }
        }

    override fun snakeContext(): MySnakeContext = MySnakeContext()

    class MySnakeContext : SnakeContext() {
        lateinit var moves: Iterator<MoveResponse>
    }

    @JvmStatic
    fun main(args: Array<String>) {
        run()
    }
}